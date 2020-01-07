package main.webapp.Model;

import com.opencsv.CSVReader;

import javax.servlet.ServletOutputStream;
import java.io.*;
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
    public static boolean checkIfExists(String templateName) throws SQLException {
        Connection connection = DataBaseConnection.makeConnection();
        return DataBaseConnection.checkIfObjExists(connection, templateName);
    }


    public static void readExistingTemplate(String filename, String templateName, PrintWriter out) throws IOException {
        Template template = null;
        try {
            template = readFromDB(templateName);
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
            List<String> value = field.getValue(tables.get(field.TABLE_ID));
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


    public static Template readFromDB(String type) throws SQLException, IOException {
        Connection connection = DataBaseConnection.makeConnection();
        try {
            return (Template) DataBaseConnection.deSerializeJavaObjectFromDB(
                    connection, type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addToDB(Template template) throws SQLException {
        Connection connection = DataBaseConnection.makeConnection();
        DataBaseConnection.serializeJavaObjectToDB(connection, template);
    }


    public static List<String[]> readAllLines(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> list = csvReader.readAll();
            reader.close();
            csvReader.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
