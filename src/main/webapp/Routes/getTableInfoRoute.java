package main.webapp.Routes;

import main.webapp.Model.Table;
import main.webapp.Model.TableFactory;
import main.webapp.Model.Template;
import main.webapp.Model.TemplateReader;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.logging.Logger;


/**
 * redirected to from postTemplateRoute when template name given is not found in database
 * prompts the user to give start and end keys for the tables
 *
 * @pre template not in database
 */
public class getTableInfoRoute implements Route {
    private static final Logger LOG = Logger.getLogger(getTableInfoRoute.class.getName());


    public getTableInfoRoute() {
        LOG.finer("getTableInfoRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Template currentTemplate = request.session().attribute("template");

        TableFactory factory = request.session().attribute("factory");

        HashMap<Integer, Table> tables = TemplateReader.getTables(currentTemplate, factory, response.raw().getOutputStream());

        request.session().attribute("tables", tables);

        return 1;
    }
}
