package main.webapp.Routes;

import spark.Request;
import spark.Response;
import spark.Route;

public class postUpdateFieldRoute implements Route {

    public postUpdateFieldRoute() {

    }


    @Override
    public Object handle(Request request, Response response) throws Exception {

        String key = request.queryParams("key");
        String value = request.queryParams("value");
        String id = request.queryParams("id");


        return null;
    }
}
