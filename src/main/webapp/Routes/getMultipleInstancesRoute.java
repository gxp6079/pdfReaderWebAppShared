package main.webapp.Routes;

import main.webapp.Model.TableAttributes;
import main.webapp.Model.TableFactory;
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

        LOG.finer("getMultipleInstancesRoute initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        TableFactory tableFactory = request.session().attribute("factory");
        TableAttributes tableAttributes = request.session().attribute("currentAttributes");
        return "Found " + tableFactory.getNumLocations() + " instances of start: " + tableAttributes.START;
    }
}
