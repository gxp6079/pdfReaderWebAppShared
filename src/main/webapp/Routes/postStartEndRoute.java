package main.webapp.Routes;

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
        String decodingTry = URLDecoder.decode(request.queryParams("start"), "UTF-8");
        String start = request.queryParams("start");
        String end = request.queryParams("end");

        LOG.info("Start, end: " + start + ", " + end);
        LOG.info("decoding attempt: " + decodingTry);

        Template currentTemplate = request.session().attribute("template");
        LOG.info("Template: " + currentTemplate.getType());

        TableFactory factory = request.session().attribute("factory");
        LOG.info("Initializing the start and end in the factory");
        factory.initialize(start, end);

        if (factory.getNumLocations() == 0) {
            LOG.info("ERROR: start or end was not found in the table");
            //start or end not found
            response.status(400);
            return "Start or end not found";
        }

        if (factory.getNumLocations() > 1) {
            LOG.info("More than one instance of start and end found");
            TableAttributes tableAttributes = new TableAttributes(start, end);
            request.session().attribute("currentAttributes", tableAttributes);

            String message = "These starting locations were found:\n";
            int index = 0;
            while(index < factory.getNumLocations()){
                message += (index + 1)+ ". row: " + factory.getLocations().get(index)[0] + " column: "
                        + factory.getLocations().get(index)[1] + "\n";
                index++;
            }
            return message;
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


        LOG.info("Making table based on the first instance of start end locations");
        Table curr = factory.makeTable(1);

        LOG.info("Adding table to hashmap");
        tables.put(curr.hashCode(), curr);

        LOG.info("TemplateReader.createTable called with templets <" + currentTemplate.getType() + "> and given start end");
        TemplateReader.createTable(currentTemplate, start, end, 1);

        return 1;
    }
}
