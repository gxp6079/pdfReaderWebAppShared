package main.webapp.Routes;

import main.webapp.Model.Field;
import main.webapp.Model.Table;
import main.webapp.Model.TableFactory;
import main.webapp.Model.Template;
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
    private static final Logger LOG = Logger.getLogger(WebServer.class.getName());
    private FileHandler fh;


    public postTableInfoRoute() {

        try{
            fh = new FileHandler("PostTableInfoRouteLog.log");
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
        Template currentTemplate = request.session().attribute("template");

        TableFactory factory = request.session().attribute("factory");

        String fieldName = request.queryParams("field");
        String value = request.queryParams("value");
        int id = Integer.parseInt(request.queryParams("id"));

        LOG.info("Retrieving tables from session");
        Map<Integer, Table> tables = request.session().attribute("tables");

        LOG.info("Getting table based on id " + Integer.toString(id));
        Table curr = tables.get(id);
        if (curr == null) {
            response.status(400);
            LOG.info("Table id: " + Integer.toString(id) + " not found\nFailed to add to template");
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

        try {
            fh.flush();
            fh.close();
        } catch (Exception e) {}

        return 1;
    }
}
