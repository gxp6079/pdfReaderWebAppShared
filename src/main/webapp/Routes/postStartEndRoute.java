package main.webapp.Routes;

import main.webapp.Application;
import main.webapp.Model.*;
import spark.Request;
import spark.Response;
import spark.Route;

import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * used to give start/end keys for a table
 *
 * each call contains one start and one end key
 */
public class postStartEndRoute implements Route {
    private Logger LOG;

    public postStartEndRoute(Logger LOG) {
        this.LOG = LOG;

        LOG.info("postStartEndRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String id = request.queryParams("token");
        Token token = Application.getToken(id, request);
        String start = request.queryParams("start");
        String end = request.queryParams("end");
        String tableId = request.queryParams("tableId");

        String tableOrientation = request.queryParams("orientation");
        TableAttributes.Orientation orientation = tableOrientation.equals("1") ? TableAttributes.Orientation.VERTICAL : TableAttributes.Orientation.HORIZONTAL;


        Boolean contains = true;
        try {
            contains = Boolean.valueOf(request.queryParams("use_contains"));
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }

        LOG.info("Start, end: " + start + ", " + end);

        Template currentTemplate = token.getTemplate();
        LOG.info("Template: " + currentTemplate.getType());

        TableFactory factory = token.getTableFactory();
        LOG.info("Initializing the start and end in the factory");
        factory.initialize(start, end, contains, orientation);

        if (factory.getNumLocations() == 0) {
            LOG.info("ERROR: start or end was not found in the table.");
            //start or end not found
            response.status(400);
            return "Start or end not found";
        }

        if (factory.getNumLocations() > 1) {
            LOG.info("More than one instance of start and end found");
            TableAttributes tableAttributes = new TableAttributes(start, end, contains, tableId, orientation);
            token.setTableAttributes(tableAttributes);

            String message = "These starting locations were found:\n";
            int index = 0;
            while(index < factory.getNumLocations()){
                LOG.info("found location " + factory.getLocations().get(index));
                message += (index + 1)+ ". row: " + factory.getLocations().get(index)[0] + " column: "
                        + factory.getLocations().get(index)[1] + "\n";
                LOG.info("message is now = " + message);
                index++;
            }
            return message;
        }


        Map<String, Table> tables;
        if (token.getTables() == null) {
            tables = new HashMap<>();
            token.setTables(tables);
            LOG.info("Creating and adding table hashmap to session");
        } else {
            tables = token.getTables();
            LOG.info("Loading table hashmap from session");
        }


        LOG.info("Making table based on the first instance of start end locations");
        Table curr = factory.makeTable(1);

        LOG.info("Adding table to hashmap");
        tables.put(tableId, curr);

        LOG.info("TemplateReader.createTable called with templets <" + currentTemplate.getType() + "> and given start end");
        TemplateReader.createTable(currentTemplate, start, end, contains, tableId,1, orientation);

        return 1;
    }
}
