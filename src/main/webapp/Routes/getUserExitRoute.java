package main.webapp.Routes;

import spark.Request;
import spark.Response;
import spark.Route;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class getUserExitRoute implements Route {
    private static final Logger LOG = Logger.getLogger(postMultipleInstancesRoute.class.getName());
    public getUserExitRoute() {
        LOG.finer("getUserExitRoute initialized");
    }


    @Override
    public Object handle(Request request, Response response) throws Exception {
        Path path = request.session().attribute("path");
        Files.delete(path);

        request.session().invalidate();

        return 1;
    }
}
