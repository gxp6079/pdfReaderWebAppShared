package main.webapp.Routes;

import main.webapp.Application;
import main.webapp.Model.*;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * called to add a field data to a template that is being created
 * @redirect getMultipleInstancesRoute if multple instances of start/end were found
 *
 */
public class postTableInfoRoute implements Route {
    private static final Logger LOG = Logger.getLogger(postTableInfoRoute.class.getName());
    public static FileHandler fh;


    public postTableInfoRoute() {

        try{
            fh = new FileHandler("pdfReaderLogFiles/PostTableInfoRouteLog.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("Created");
        } catch (Exception e) {

        }

        LOG.info("postTableInfoRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {

        String tokenId = request.queryParams("token");
        Token token = Application.getToken(tokenId, request);

        Template currentTemplate = token.getTemplate();

        TableFactory factory = token.getTableFactory();

        String fieldName = request.queryParams("field").trim().toLowerCase();
        String value = request.queryParams("value");
        String id = request.queryParams("tableId");

        LOG.info("Retrieving tables from session");
        Map<String, Table> tables = token.getTables();

        LOG.info("Getting table based on id " + id);
        Table curr = tables.get(id);
        if (curr == null) {
            response.status(400);
            LOG.info("Table id: " + id + " not found\nFailed to add to template");
            return "table id not found";
        }

        if (curr.getDataAt(value) == null) {
            response.status(400);
            LOG.info("Header \'" + value + "\' not found in table\nFailed to add to template");
            return "value not found in table";
        }
        LOG.info("Adding field: " + fieldName + ", " + id + ", " + value);
        currentTemplate.addField(new Field(fieldName, id, value));

        LOG.info("Field \'" + fieldName + "\': successfully added to template");


        return 1;
    }
}
