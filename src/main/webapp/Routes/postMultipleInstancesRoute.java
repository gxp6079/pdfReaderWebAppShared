package main.webapp.Routes;

import main.webapp.Model.TableAttributes;
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
    private static final Logger LOG = Logger.getLogger(postMultipleInstancesRoute.class.getName());

    public postMultipleInstancesRoute() {
        LOG.finer("postMultipleInstancesRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        int instance = Integer.parseInt(request.queryParams("num"));

        TableAttributes tableAttributes = request.session().attribute("currentAttributes");

        Template currentTemplate = request.session().attribute("template");

        TemplateReader.createTable(currentTemplate, tableAttributes.START, tableAttributes.END, instance);

        request.session().removeAttribute("currentAttributes");
        return 1;
    }
}
