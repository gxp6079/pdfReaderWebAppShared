package main.webapp.Routes;

import com.google.gson.Gson;
import main.webapp.Application;
import main.webapp.Model.Token;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * can be used to validate a user's permission to use the API
 */
public class getSignInRoute implements Route {
    private static final Logger LOG = Logger.getLogger(getSignInRoute.class.getName());
    public static FileHandler fh;

    public getSignInRoute() {
        try{
            fh = new FileHandler("pdfReaderLogFiles/PostSignInRoute.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("SignIn logger created");
        }
        catch (Exception e){
            LOG.info("Failed to initialize logger");
        }
        LOG.info("getSignInRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {

        HashMap<String, Token> tokens;

        String id = Application.getToken();
        LOG.info("Token generated from application: " + id);
        if (request.session().attribute("tokens") == null) {
            tokens = new HashMap<>();
            request.session().attribute("tokens", tokens);
            LOG.info("tokens created: " + tokens);
        } else {
            tokens = request.session().attribute("tokens");
        }

        while(tokens.containsKey(id)) {
            id = Application.getToken();
        }

        Token newGeneratedToken = new Token(id);

        String institutionId = request.queryParams("institutionId");
        newGeneratedToken.setInstitutionId(institutionId);

        tokens.put(id, newGeneratedToken);
        LOG.info("added token "+ newGeneratedToken + " with institutionId " + institutionId);

        Gson gson = new Gson();
        return gson.toJson(newGeneratedToken);
    }
}
