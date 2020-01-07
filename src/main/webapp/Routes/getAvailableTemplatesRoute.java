package main.webapp.Routes;

import main.webapp.Application;
import main.webapp.Model.TemplateReader;
import main.webapp.Model.Token;
import spark.Request;
import spark.Response;
import spark.Route;

public class getAvailableTemplatesRoute implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String id = request.queryParams("token");
        Token token = Application.getToken(id, request);

        return TemplateReader.getTemplatesForInstitutionFromDB(token.getInstitutionId());
    }
}
