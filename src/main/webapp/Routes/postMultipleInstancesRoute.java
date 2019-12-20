package main.webapp.Routes;

import main.webapp.Application;
import main.webapp.Model.*;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * called to specify which instance of start/end the user wants to use
 */
public class postMultipleInstancesRoute implements Route {
    private static Logger LOG;

    public postMultipleInstancesRoute(Logger LOG) {
        this.LOG = LOG;

        LOG.finer("postMultipleInstancesRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String id = request.queryParams("token");
        Token token = Application.getToken(id, request);


        TableFactory factory = token.getTableFactory();
        int totalLocations = factory.getNumLocations();
        if (totalLocations < 2) {
            response.status(300);
            return "Only 1 location found";
        }

        int instance = Integer.parseInt(request.queryParams("num"));

        if (instance > totalLocations) {
            response.status(301);
            return "Too large instance input, no valid location";
        }

        Map<Integer, Table> tables;
        if (token.getTables() == null) {
            tables = new HashMap<>();
            token.setTables(tables);
            LOG.info("Creating and adding table hashmap to session");
        } else {
            tables = token.getTables();
            LOG.info("Loading table hashmap from session");
        }

        TableAttributes tableAttributes = token.getTableAttributes();

        Template currentTemplate = token.getTemplate();

        LOG.info("Making table based on the" + instance+ " instance of start end locations");
        Table curr = factory.makeTable(instance);

        LOG.info("Adding table to hashmap");
        tables.put(curr.hashCode(), curr);

        TemplateReader.createTable(currentTemplate, tableAttributes.START, tableAttributes.END, instance);

        token.setTableAttributes(null);
        return 1;
    }
}
