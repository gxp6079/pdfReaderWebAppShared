package main.webapp.Routes;

import main.webapp.Model.TableAttributes;
import main.webapp.Model.TableFactory;
import main.webapp.Model.Template;
import main.webapp.Model.TemplateReader;
import spark.Request;
import spark.Response;
import spark.Route;

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

        TableAttributes tableAttributes = request.session().attribute("currentAttributes");

        Template currentTemplate = request.session().attribute("template");

        TemplateReader.createTable(currentTemplate, tableAttributes.START, tableAttributes.END, instance);

        request.session().removeAttribute("currentAttributes");
        return 1;
    }
}
