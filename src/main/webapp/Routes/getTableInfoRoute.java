package main.webapp.Routes;

import main.webapp.Model.Table;
import main.webapp.Model.TableFactory;
import main.webapp.Model.Template;
import main.webapp.Model.TemplateReader;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * redirected to from postTemplateRoute when template name given is not found in database
 * prompts the user to give start and end keys for the tables
 *
 * @pre template not in database
 */
public class getTableInfoRoute implements Route {
    private static final Logger LOG = Logger.getLogger(getTableInfoRoute.class.getName());
    private FileHandler fh;

    public getTableInfoRoute() {

        try{
            fh = new FileHandler("GetTableInfoRouteLog.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("TableInfoRoute logger created");
        }
        catch (Exception e){

        }
        LOG.finer("getTableInfoRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Template currentTemplate = request.session().attribute("template");
        LOG.info("Usign template: " + currentTemplate.getType());

        TableFactory factory = request.session().attribute("factory");
        LOG.info("Retrieved table factory from session");

        LOG.info("Reading tables with template reader");
        HashMap<Integer, Table> tables = TemplateReader.getTables(currentTemplate, factory, response.raw().getOutputStream(), LOG);

        LOG.info("Adding tables to session");
        request.session().attribute("tables", tables);

        return 1;
    }
}
