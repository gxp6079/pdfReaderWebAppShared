package main.webapp.Model;

import com.google.gson.Gson;
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
        return DataBaseConnection.checkIfObjExists(templateName, institutionId);
    }


    public static String readExistingTemplate(String filename, String templateName, String institutionId, Logger LOG) throws IOException {
        Template template = null;
        try {
            template = readFromDB(templateName, institutionId, LOG);
            LOG.info("Template successfully retrieved");

            List<String[]> list = readAllLines(filename);

            Map<String, Table> tables = new HashMap<>();

            TableFactory tableFactory = new TableFactory(list);
            for (TableAttributes ta : template.getTables().values()) {
                tableFactory.initialize(ta.START, ta.END, ta.contains, ta.orientation);
                Table table = tableFactory.makeTable(ta.getOccurrence());
                if (table != null) tables.put(ta.tableId, table);
            }

            /**
             * name of field to value
             */
            HashMap<String, HashMap<String, List<String>>> values = new HashMap<>();

            for (Field field : template.getFields().values()) {
                HashMap<String, List<String>> valuesInTable;
                if(values.containsKey(field.TABLE_ID)){
                    valuesInTable = values.get(field.TABLE_ID);
                }
                else{
                    valuesInTable = new HashMap<>();
                    values.put(field.TABLE_ID, valuesInTable);
                }
                LOG.info("Reading data for field: " + field.NAME);
                Map<String, String> dictionary = field.getWordLUT();
                List<String> value = field.getValue(tables.get(field.TABLE_ID), LOG);
                LOG.info("Got " + value + "\nfrom table " + field.TABLE_ID + "\nwith header" + field.HEADER);
                if(dictionary.size() != 0) {
                    ArrayList<String> data = new ArrayList<>(value);
                    for (int i = 0; i < data.size(); i++) {
                        String curr = data.get(i);
                        if (dictionary.containsKey(curr)) data.set(i, dictionary.get(curr));
                    }
                    value = data;
                }
                valuesInTable.put(field.NAME, value);
                LOG.info("added new field " + valuesInTable);
            }

            Gson gson = new Gson();

            LOG.info("table info printing complete with values " + values + "and Json " + gson.toJson(values));
            return gson.toJson(values);
        }
        catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pr = new PrintWriter(sw);
            e.printStackTrace(pr);
            LOG.info(sw.toString());
        }
        return "";
    }

    public static HashMap<String, Table> getTables(Template template, TableFactory tableFactory, Logger LOG) throws IOException {

        LOG.info("getTables() method called in TemplateReader");
        HashMap<String, Table> tables = new HashMap<>();

        for(TableAttributes attributes : template.getTables().values()){
            LOG.info("looking for table with start, end: " + attributes.START + ", " + attributes.END);
            tableFactory.initialize(attributes.START, attributes.END, attributes.contains, attributes.orientation);
            Table table = tableFactory.makeTable(attributes.getOccurrence());
            LOG.info("Table found");
            tables.put(attributes.tableId, table);
        }

        return tables;
    }

    public static Table getTableWithId(String tableId, Template template, TableFactory tableFactory){
        TableAttributes attributes = template.getTables().get(tableId);
        tableFactory.initialize(attributes.START, attributes.END, attributes.contains, attributes.orientation);
        Table table = tableFactory.makeTable(attributes.getOccurrence());
        return table;
    }

    public static void printTables(HashMap<String, Table> tables, PrintWriter out, Logger LOG){
        LOG.info("Printing tables");
        for(String id : tables.keySet()){
            out.println(id);
            try {
                out.println(String.valueOf(tables.get(id)));
            } catch (Exception e) {
                LOG.info("Error while printing table");
                LOG.info(e.getMessage());
            }
            out.println("\n");
        }
    }

    public static void createTable(Template template, String start, String end, Boolean contains, String tableId, int instance, TableAttributes.Orientation orientation){

        TableAttributes attributes = new TableAttributes(start, end, contains, tableId, orientation);
        attributes.setOccurrence(instance);
        template.addTable(attributes);

    }


    public static Template readFromDB(String type, String institutionId, Logger LOG) throws SQLException, IOException {
        try {
            return (Template) DataBaseConnection.deSerializeJavaObjectFromDB(type, institutionId);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LOG.info(e.getMessage());
        }
        return null;
    }

    public static ArrayList<String> getTemplatesForInstitutionFromDB(String institutionId, Logger LOG) throws SQLException, IOException {
        try {
            return DataBaseConnection.getTemplatesForInstitution(institutionId, LOG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addToDB(Template template, String institutionId) throws SQLException {
        DataBaseConnection.serializeJavaObjectToDB(template, institutionId);
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
