package main.webapp.Routes;

import main.webapp.Model.Template;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.logging.Logger;


/**
 * can be used to validate a user's permission to use the API
 */
public class postSignInRoute implements Route {
    private static final Logger LOG = Logger.getLogger(postSignInRoute.class.getName());


    public postSignInRoute() {

        LOG.config("postSignInRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String apiKey = request.queryParams("key");

        request.session().attribute("template", new Template());

        // TODO check if key exists in something (file, map, etc.)


        return 1;
    }
}
