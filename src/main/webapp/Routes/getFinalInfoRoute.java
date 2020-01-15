package main.webapp.Routes;

import main.webapp.Application;
import main.webapp.Model.Template;
import main.webapp.Model.TemplateReader;
import main.webapp.Model.Token;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * retrieves completed info, very last route called
 */
public class getFinalInfoRoute implements Route {
    private static final Logger LOG = Logger.getLogger(getFinalInfoRoute.class.getName());
    public static FileHandler fh;


    public getFinalInfoRoute() {

        try{
            fh = new FileHandler("pdfReaderLogFiles/GetFinalInfoRouteLog.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("Created");
        } catch (Exception e) {

        }

        LOG.info("getFinalInfoRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {


        Token token = Application.getToken(request.queryParams("token"), request);

        Template currentTemplate = token.getTemplate();
        String institutionId = token.getInstitutionId();

        LOG.info("Checking if exists template type: " + currentTemplate.getType());
        try {
            if (!TemplateReader.checkIfExists(currentTemplate.getType(), institutionId)) {
                TemplateReader.addToDB(currentTemplate, institutionId);
                LOG.info("Adding template \'" + currentTemplate.getType() + "\' to database");
            } else if(TemplateReader.checkIfExists(currentTemplate.getType(), institutionId)) {
                LOG.info("Template: " + currentTemplate.getType() + " already exists in database");
            }
            else {
                return "Not all required fields were set";
            }
        } catch (Exception e) {
            LOG.info("Exception thrown when adding template to database");
        }

        try {
            String encoding = "UTF-8";
            LOG.info("Using encoding: " + encoding);
            response.raw().setContentType("text/html; charset="+encoding);
            response.raw().setCharacterEncoding(encoding);

            LOG.info("Reading data from template: " + currentTemplate.getType());
            TemplateReader.readExistingTemplate(token.getCsvPath(),
                    currentTemplate.getType(),
                    institutionId,
                    response.raw().getWriter());
        } catch (Exception e) {
            LOG.info("Exception thrown when reading template" + e.getMessage());
        }

        LOG.info("GetFinalInfo completed successfully");
        fh.flush();

        return 1;
    }
}
