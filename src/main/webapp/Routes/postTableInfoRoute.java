package main.webapp.Routes;

import main.webapp.Model.Field;
import main.webapp.Model.Table;
import main.webapp.Model.TableFactory;
import main.webapp.Model.Template;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;
import java.util.logging.Logger;


/**
 * called to add a field data to a template that is being created
 * @redirect getMultipleInstancesRoute if multple instances of start/end were found
 *
 */
public class postTableInfoRoute implements Route {
    private static final Logger LOG = Logger.getLogger(WebServer.class.getName());


    public postTableInfoRoute() {

        LOG.finer("postTableInfoRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Template currentTemplate = request.session().attribute("template");

        TableFactory factory = request.session().attribute("factory");

        String fieldName = request.queryParams("field");
        String value = request.queryParams("value");
        int id = Integer.parseInt(request.queryParams("id"));

        Map<Integer, Table> tables = request.session().attribute("tables");

        Table curr = tables.get(id);
        if (curr == null) {
            response.status(400);
            return "table id not found";
        }

        if (curr.getDataAt(value) == null) {
            response.status(400);
            return "value not found in table";
        }

        currentTemplate.addField(new Field(fieldName, id, value));

        return 1;
    }
}
