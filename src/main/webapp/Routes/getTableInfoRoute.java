package main.webapp.Routes;

import main.webapp.Application;
import main.webapp.Model.*;
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
    public static FileHandler fh;

    public getTableInfoRoute() {

        try{
            fh = new FileHandler("pdfReaderLogFiles/GetTableInfoRouteLog.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("TableInfoRoute logger created");
        }
        catch (Exception e){
            LOG.info("Failed to initialize logger");
        }
        LOG.info("getTableInfoRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String id = request.queryParams("token");
        Token token = Application.getToken(id, request);


        String encoding = "UTF-8";
        LOG.info("Using encoding: " + encoding);
        response.raw().setContentType("text/html; charset="+encoding);
        response.raw().setCharacterEncoding(encoding);

        Template currentTemplate = token.getTemplate();
        LOG.info("Using template: " + currentTemplate.getType());

        TableFactory factory = token.getTableFactory();
        LOG.info("Retrieved table factory from session");

        LOG.info("Reading tables with template reader");
        HashMap<String, Table> tables = TemplateReader.getTables(currentTemplate, factory, response.raw().getWriter(), LOG);

        LOG.info("Adding tables to session");
        token.setTables(tables);

        return 1;
    }
}
