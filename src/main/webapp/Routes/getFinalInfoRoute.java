package main.webapp.Routes;

import main.webapp.Model.Template;
import main.webapp.Model.TemplateReader;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.logging.Logger;


/**
 * retrieves completed info, very last route called
 */
public class getFinalInfoRoute implements Route {
    private static final Logger LOG = Logger.getLogger(getFinalInfoRoute.class.getName());

    public getFinalInfoRoute() {
        LOG.finer("getFinalInfoRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {

        Template currentTemplate = request.session().attribute("template");

        TemplateReader.addToDB(currentTemplate);

        TemplateReader.readExistingTemplate(request.session().attribute("path").toString(), currentTemplate.getType(), response.raw().getOutputStream());

        return 1;
    }
}
