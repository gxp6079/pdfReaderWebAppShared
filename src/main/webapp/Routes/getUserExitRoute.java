package main.webapp.Routes;

import main.webapp.Application;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class getUserExitRoute implements Route {
    private static final Logger LOG = Logger.getLogger(getUserExitRoute.class.getName());
    private FileHandler fh;



    public getUserExitRoute() {
        try {
            fh = new FileHandler("ExitRouteLog.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("Created");
        } catch (IOException e) {}
        LOG.info("Exit route initialized");
    }


    @Override
    public Object handle(Request request, Response response) throws Exception {

        LOG.info("Getting path for Csv file");
        Path path = request.session().attribute("path");
        Files.deleteIfExists(path);

        LOG.info("CSV deleted");

        request.session().invalidate();

        LOG.info("Session invalidated");


        Application.multiple_inst_fh.close();
        LOG.info("Closed multiple instance route file handler");
        Application.start_end_fh.close();
        LOG.info("Closed start end route file handler");

        fh.close();

        return 1;
    }
}
