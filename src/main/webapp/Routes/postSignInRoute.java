package main.webapp.Routes;

import main.webapp.Application;
import main.webapp.Model.Template;
import main.webapp.Model.Token;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * can be used to validate a user's permission to use the API
 */
public class postSignInRoute implements Route {
    private static final Logger LOG = Logger.getLogger(postSignInRoute.class.getName());


    public postSignInRoute() {
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {

        HashMap<String, Token> tokens;

        String id = Application.getToken();
        if (request.session().attribute("tokens") == null) {
            tokens = new HashMap<>();
            request.session().attribute("tokens", tokens);
        } else {
            tokens = request.session().attribute("tokens");
        }

        while(tokens.containsKey(id)) {
            id = Application.getToken();
        }

        tokens.put(id, new Token(id));


        return 1;
    }
}
