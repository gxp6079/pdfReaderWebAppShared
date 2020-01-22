package main.webapp.Routes;

import main.webapp.Application;
import main.webapp.Model.DataBaseConnection;
import main.webapp.Model.Field;
import main.webapp.Model.Token;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;

public class postUpdateFieldRoute implements Route {

    public postUpdateFieldRoute() {

    }


    @Override
    public Object handle(Request request, Response response) throws Exception {

        String key = request.queryParams("key");
        String value = request.queryParams("value");
        String id = request.queryParams("token");
        String fieldName = request.queryParams("fieldName");

        Token token = Application.getToken(id, request);

        Field toUpdate = token.getTemplate().getFields().get(fieldName);

        toUpdate.addTranslation(key, value);

        try {
            if(DataBaseConnection.checkIfObjExists(token.getTemplate().getType(), token.getInstitutionId())) {
                DataBaseConnection.updateTemplateInDB(token.getInstitutionId(), token.getTemplate());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return 1;
    }
}
