package main.webapp.Routes;

import com.google.gson.Gson;
import main.webapp.Application;
import main.webapp.Model.TemplateReader;
import main.webapp.Model.Token;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class getAvailableTemplatesRoute implements Route {

    private static final Logger LOG = Logger.getLogger(postTableInfoRoute.class.getName());
    public static FileHandler fh;

    public getAvailableTemplatesRoute(){
        try{
            fh = new FileHandler("pdfReaderLogFiles/GetAvailableTemplatesRouteLog.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("Created");
        } catch (Exception e) {

        }
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String id = request.queryParams("token");
        Token token = Application.getToken(id, request);

        fh.flush();
        Gson gson = new Gson();
        ArrayList<String> availableTemplateTypes = TemplateReader.getTemplatesForInstitutionFromDB(token.getInstitutionId(), LOG);
        return gson.toJson(availableTemplateTypes);
    }
}
