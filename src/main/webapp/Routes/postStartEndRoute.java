package main.webapp.Routes;

import main.webapp.Model.*;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * used to give start/end keys for a table
 *
 * each call contains one start and one end key
 */
public class postStartEndRoute implements Route {
    private static final Logger LOG = Logger.getLogger(postStartEndRoute.class.getName());


    public postStartEndRoute() {

        LOG.finer("postStartEndRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String start = request.queryParams("start");
        String end = request.queryParams("end");

        Template currentTemplate = request.session().attribute("template");

        TableFactory factory = request.session().attribute("factory");
        factory.initialize(start, end);

        if (factory.getNumLocations() == 0) {
            //start or end not found
            response.status(400);
            return "Start or end not found";
        }

        if (factory.getNumLocations() > 1) {
            TableAttributes tableAttributes = new TableAttributes(start, end);
            request.session().attribute("currentAttributes", tableAttributes);

            response.redirect(WebServer.MULTIPLE_INSTANCE_URL + "?num=" + factory.getNumLocations());
            return null;
        }

        Map<Integer, Table> tables;
        if (!request.session().attributes().contains("tables")) {
            tables = new HashMap<>();
        } else {
            tables = request.session().attribute("tables");
        }

        Table curr = factory.makeTable(1);

        tables.put(curr.hashCode(), curr);

        TemplateReader.createTable(currentTemplate, start, end, 1);

        return 1;
    }
}
