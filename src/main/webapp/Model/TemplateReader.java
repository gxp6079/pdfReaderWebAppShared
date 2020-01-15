package main.webapp.Model;

import com.opencsv.CSVReader;

import java.io.*;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class TemplateReader {

    /***
     * checks if a template exists in the database
     * @param templateName name of template to check
     * @return true if exists false otherwise
     * @throws SQLException thrown if cannot access database
     */
    public static boolean checkIfExists(String templateName, String institutionId) throws SQLException {
        Connection connection = DataBaseConnection.makeConnection();
        return DataBaseConnection.checkIfObjExists(connection, templateName, institutionId);
    }


    public static void readExistingTemplate(String filename, String templateName, String institutionId, PrintWriter out) throws IOException {
        Template template = null;
        try {
            template = readFromDB(templateName, institutionId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String[]> list = readAllLines(filename);

        Map<Integer, Table> tables = new HashMap<>();

        TableFactory tableFactory = new TableFactory(list);
        for (TableAttributes ta : template.getTables()) {
            tableFactory.initialize(ta.START, ta.END, ta.contains);
            Table table = tableFactory.makeTable(ta.getOccurrence());
            if (table != null) tables.put(table.hashCode(), table);
        }

        /**
         * name of field to value
         */
        Map<String, List<String>> values = new HashMap<>();

        for (Field field : template.getFields().values()) {
            Map<String, String> dictionary = field.getWordLUT();
            List<String> value = field.getValue(tables.get(field.TABLE_ID));
            if (dictionary.size() == 0) continue;
            else {
                ArrayList<String> data = new ArrayList<>(value);
                for (int i = 0; i < data.size(); i++) {
                    String curr = data.get(i);
                    if (dictionary.containsKey(curr)) data.set(i, dictionary.get(curr));
                }
                value = data;
            }
            values.put(field.NAME, value);
            out.println(field.NAME + " :" + String.join(" | ", value));
        }

    }

    public static HashMap<Integer, Table> getTables(Template template, TableFactory tableFactory, PrintWriter out, Logger LOG) throws IOException {

        LOG.info("getTables() method called in TemplateReader");
        HashMap<Integer, Table> tables = new HashMap<>();

        for(TableAttributes attributes : template.getTables()){
            LOG.info("looking for table with start, end: " + attributes.START + ", " + attributes.END);
            tableFactory.initialize(attributes.START, attributes.END, attributes.contains);
            Table table = tableFactory.makeTable(attributes.getOccurrence());
            LOG.info("Table found");
            tables.put(table.hashCode(), table);
        }

        LOG.info("Printing tables");
        for(Integer id : tables.keySet()){
            out.println(id);
            try {
                out.println(String.valueOf(tables.get(id)));
            } catch (Exception e) {
                LOG.info("Error while printing table");
                LOG.info(e.getMessage());
            }
            out.println("\n");
        }

        return tables;
    }

    public static void createTable(Template template, String start, String end, Boolean contains,int instance){

        TableAttributes attributes = new TableAttributes(start, end, contains);
        attributes.setOccurrence(instance);
        template.addTable(attributes);

    }


    public static Template readFromDB(String type, String institutionId) throws SQLException, IOException {
        Connection connection = DataBaseConnection.makeConnection();
        try {
            return (Template) DataBaseConnection.deSerializeJavaObjectFromDB(
                    connection, type, institutionId);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getTemplatesForInstitutionFromDB(String institutionId, Logger LOG) throws SQLException, IOException {
        Connection connection = DataBaseConnection.makeConnection();
        try {
            return DataBaseConnection.getTemplatesForInstitution(
                    connection, institutionId, LOG);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addToDB(Template template, String institutionId) throws SQLException {
        Connection connection = DataBaseConnection.makeConnection();
        DataBaseConnection.serializeJavaObjectToDB(connection, template, institutionId);
    }


    public static List<String[]> readAllLines(String filename) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filename);
            InputStreamReader reader = new InputStreamReader(fileInputStream, "UTF-8");
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> list = csvReader.readAll();
            fileInputStream.close();
            reader.close();
            csvReader.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
