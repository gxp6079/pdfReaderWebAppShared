package main.webapp.Routes;

import com.google.gson.Gson;
import main.webapp.Application;
import main.webapp.Model.*;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class getDataFromTable implements Route {

    private static final Logger LOG = Logger.getLogger(getDataFromTable.class.getName());
    public static FileHandler fh;

    public getDataFromTable() {
        try{
            fh = new FileHandler("pdfReaderLogFiles/GetDataFromTable.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("Created");
        }
        catch (Exception e){

        }
        LOG.info("postTemplateRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String tableId = request.queryParams("tableId");
        String tokenString = request.queryParams("token");

        Token token = Application.getToken(tokenString, request);
        LOG.info("Got token = " + token);
        Template template = token.getTemplate();
        LOG.info("Got template = " + template);

        LOG.info("Getting table, factory = " + token.getTableFactory());
        Table table = TemplateReader.getTableWithId(tableId, template, token.getTableFactory(), LOG);
        LOG.info("Got table = " + table);
        HashMap<String, List<String>> values = new HashMap<>();

        LOG.info("got tamplate " + template);
        for (Field field : template.getFields().values()) {
            LOG.info("Comparing fieldTable: " + field.TABLE_ID + " and tableId: " + tableId);
            if (field.TABLE_ID.equals(tableId)) {
                LOG.info("Reading data for field: " + field.NAME);
                Map<String, String> dictionary = field.getWordLUT();
                List<String> value = field.getValue(table, LOG);
                LOG.info("Got " + value + "\nfrom table " + field.TABLE_ID + "\nwith header" + field.HEADER);
                if (dictionary.size() != 0) {
                    ArrayList<String> data = new ArrayList<>(value);
                    for (int i = 0; i < data.size(); i++) {
                        String curr = data.get(i);
                        if (dictionary.containsKey(curr)) data.set(i, dictionary.get(curr));
                    }
                    value = data;
                }
                values.put(field.NAME, value);
                LOG.info("added new field " + values);
            }
        }

        Gson gson = new Gson();

        LOG.info("table info printing complete with values " + values + "and Json " + gson.toJson(values));
        return gson.toJson(values);
    }
}
