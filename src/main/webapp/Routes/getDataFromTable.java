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

public class getDataFromTable implements Route {

    private static final Logger LOG = Logger.getLogger(postTemplateRoute.class.getName());
    public static FileHandler fh;

    public getDataFromTable() {
        try{
            fh = new FileHandler("pdfReaderLogFiles/PostTamplateRouteLog.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("Created");
        }
        catch (Exception e){

        }
        LOG.info("postTemplateRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String tableId = request.queryParams("tableId");
        String tokenString = request.queryParams("token");

        Token token = Application.getToken(tokenString, request);
        Template template = token.getTemplate();

        HashMap<String, Table> tables = TemplateReader.getTables(template, token.getTableFactory(),LOG);

        for(Field field : token.getTemplate().getFields().values()){
            if(field.TABLE_ID == tableId){
                response.raw().getWriter().println(tables.get(tableId).getDataAt(field.HEADER));
            }
        }

        return null;
    }
}
