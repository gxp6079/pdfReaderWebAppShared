package main.webapp.Routes;

import spark.Request;
import spark.Response;
import spark.Route;

import java.nio.file.Files;
import java.nio.file.Path;

public class getUserExitRoute implements Route {
    public getUserExitRoute() {
    }


    @Override
    public Object handle(Request request, Response response) throws Exception {
        Path path = request.session().attribute("path");
        Files.delete(path);

        request.session().invalidate();

        return 1;
    }
}
