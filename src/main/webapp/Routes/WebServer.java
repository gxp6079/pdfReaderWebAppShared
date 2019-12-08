package main.webapp.Routes;


import spark.servlet.SparkApplication;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static spark.Spark.get;
import static spark.Spark.post;

public class WebServer implements SparkApplication {

    private static final Logger LOG = Logger.getLogger(WebServer.class.getName());

    public static final Logger START_END_LOGGER = Logger.getLogger(postStartEndRoute.class.getName());
    public static FileHandler start_end_fh;

    public static final Logger MULTIPLE_LOG = Logger.getLogger(postMultipleInstancesRoute.class.getName());
    public static FileHandler multiple_inst_fh;


    public static final String TEMPLATE_URL = "/PDFreader";

    public static final String TABLE_INFO_URL = "/tableInfo";

    public static final String START_END_URL = "/startEnd";

    public static final String MULTIPLE_INSTANCE_URL = "/multi";

    public static final String FINAL_INFO = "/finalInfo";

    public static final String SIGN_IN = "/signIn";

    public static final String EXIT = "/exit";

    public WebServer() {
    }


    @Override
    public void init() {

        initLogs();

        get(FINAL_INFO, new getFinalInfoRoute());

        get(MULTIPLE_INSTANCE_URL, new getMultipleInstancesRoute());

        get(TABLE_INFO_URL, new getTableInfoRoute());

        get(EXIT, new getUserExitRoute());

        post(MULTIPLE_INSTANCE_URL, new postMultipleInstancesRoute(MULTIPLE_LOG));

        post(START_END_URL, new postStartEndRoute(START_END_LOGGER));

        post(TABLE_INFO_URL, new postTableInfoRoute());

        post(TEMPLATE_URL, new postTemplateRoute());

        post(SIGN_IN, new postSignInRoute());

        LOG.finer("WebServer Initialized");
    }

    @Override
    public void destroy() {
        start_end_fh.close();
    }

    private void initLogs() {
        try{
            start_end_fh = new FileHandler("PostStartEndRouteLog.log");
            START_END_LOGGER.addHandler(start_end_fh);
            SimpleFormatter formatter = new SimpleFormatter();
            start_end_fh.setFormatter(formatter);
            START_END_LOGGER.info("Created");
        } catch (Exception e) {}


        try{
            multiple_inst_fh = new FileHandler("PostMultipleInstance.log");
            MULTIPLE_LOG.addHandler(multiple_inst_fh);
            SimpleFormatter formatter = new SimpleFormatter();
            multiple_inst_fh.setFormatter(formatter);
            MULTIPLE_LOG.info("Created");
        } catch (Exception e) {}


    }
}
