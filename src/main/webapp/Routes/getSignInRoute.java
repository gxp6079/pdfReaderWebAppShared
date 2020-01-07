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
            fh = new FileHandler("pdfReaderLogFiles/GetSignInRoute.log");
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

        LOG.info("starting handle method");

        String id = Application.getToken();
        LOG.info("Token retrieved from application: " + id);
        if (request.session().attribute("tokens") == null) {
            LOG.info("there are no tokens in session, creating hashmap");
            tokens = new HashMap<>();
            request.session().attribute("tokens", tokens);
        } else {
            LOG.info("there are tokens in session, getting existing hashmap");
            tokens = request.session().attribute("tokens");
        }

        while(tokens.containsKey(id)) {
            id = Application.getToken();
        }

        LOG.info("creating new token with id: " + id);
        Token newGeneratedToken = new Token(id);
        tokens.put(id, newGeneratedToken);

        Gson gson = new Gson();

        response.raw().getWriter().println(gson.toJson(newGeneratedToken));

        LOG.info("token generated: " + gson.toJson(newGeneratedToken));

        return id;
    }
}
