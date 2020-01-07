package main.webapp.Routes;

import main.webapp.Application;
import main.webapp.Model.Token;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class getUserExitRoute implements Route {
    private static final Logger LOG = Logger.getLogger(getUserExitRoute.class.getName());
    public static FileHandler fh;



    public getUserExitRoute() {
        try {
            fh = new FileHandler("pdfReaderLogFiles/ExitRouteLog.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("Created");
        } catch (IOException e) {}
        LOG.info("Exit route initialized");
    }


    @Override
    public Object handle(Request request, Response response) throws Exception {

        String tokenId = request.queryParams("token");
        Token token = Application.getToken(tokenId, request);

        try {
            LOG.info("Getting path for CSV file");
            String pathString = token.getCsvPath();
            LOG.info("Got path string: " + pathString);
            Path path = Paths.get(pathString);
            LOG.info("Got path");
            Files.deleteIfExists(path);
            LOG.info("CSV deleted");

            LOG.info("Getting path for PDF file");
            String pdfPathString = token.getPdfPath();
            LOG.info("Got path string: " + pdfPathString);
            Path pdfPath = Paths.get(pdfPathString);
            LOG.info("Got path");
            Files.deleteIfExists(pdfPath);
            LOG.info("PDF deleted");
        }
        catch (Exception e){
            LOG.info(e.getMessage());
        }


        request.session().invalidate();

        LOG.info("Session invalidated");

        return 1;
    }
}
