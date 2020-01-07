package main.webapp.Routes;

import main.webapp.Application;
import main.webapp.Model.TableAttributes;
import main.webapp.Model.TableFactory;
import main.webapp.Model.Token;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.logging.Logger;


/**
 * redirected to from postTableInfoRoute when multiple instances of start/end key were found
 *
 * next action should be to call postMultipleInstanceRoute
 */
public class getMultipleInstancesRoute implements Route {
    private static final Logger LOG = Logger.getLogger(getMultipleInstancesRoute.class.getName());


    public getMultipleInstancesRoute() {

    }

    @Override
    public Object handle(Request request, Response response) throws Exception {

        Token token = Application.getToken(request.queryParams("token"), request);
        TableFactory tableFactory = token.getTableFactory();

        TableAttributes tableAttributes = request.session().attribute("currentAttributes");
        return "Found " + tableFactory.getNumLocations() + " instances of start: " + tableAttributes.START;
    }
}
