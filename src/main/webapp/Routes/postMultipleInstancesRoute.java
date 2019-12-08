package main.webapp.Routes;

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
        TableFactory factory = request.session().attribute("factory");
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
        if (!request.session().attributes().contains("tables")) {
            tables = new HashMap<>();
            request.session().attribute("tables", tables);
            LOG.info("Creating and adding table hashmap to session");
        } else {
            tables = request.session().attribute("tables");
            LOG.info("Loading table hashmap from session");
        }

        TableAttributes tableAttributes = request.session().attribute("currentAttributes");

        Template currentTemplate = request.session().attribute("template");

        LOG.info("Making table based on the" + instance+ " instance of start end locations");
        Table curr = factory.makeTable(instance);

        LOG.info("Adding table to hashmap");
        tables.put(curr.hashCode(), curr);

        TemplateReader.createTable(currentTemplate, tableAttributes.START, tableAttributes.END, instance);

        request.session().removeAttribute("currentAttributes");
        return 1;
    }
}
