package main.webapp;

import main.webapp.Model.DataBaseConnection;
import main.webapp.Model.RandomString;
import main.webapp.Model.TableFactory;
import main.webapp.Model.Token;
import main.webapp.Routes.*;
import spark.Request;
import spark.servlet.SparkApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static spark.Spark.get;
import static spark.Spark.post;

public class Application implements SparkApplication {

    public static final RandomString tokenGenerator = new RandomString(15, new Random(42));

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

    public static final String AVAILABLE_TEMP = "/availableTemplates";

    public static final String UPDATE_FIELD = "/updateField";

    public static final String GET_DATA_FROM_TABLE = "/getDataFromTable";

    public static void main(String[] args) {

        createDatabaseTable();
    }

    @Override
    public void destroy() {

        multiple_inst_fh.close();
        start_end_fh.close();
        getFinalInfoRoute.fh.close();
        getTableInfoRoute.fh.close();
        getUserExitRoute.fh.close();
        postTableInfoRoute.fh.close();
        postTemplateRoute.fh.close();
        TableFactory.fh.close();
        getSignInRoute.fh.close();
        getAvailableTemplatesRoute.fh.close();
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

        post(TEMPLATE_URL, "multipart/form-data", new postTemplateRoute());

        get(SIGN_IN, new getSignInRoute());

        get(AVAILABLE_TEMP, new getAvailableTemplatesRoute());

        post(UPDATE_FIELD, new postUpdateFieldRoute());

        get(GET_DATA_FROM_TABLE, new getDataFromTable());
    }

    public static void createDatabaseTable() {
        String databaseUrl = DataBaseConnection.DATABASE_IP;

        try {
            Connection connectionSource = DriverManager.getConnection(databaseUrl, "brit", "x0EspnYA8JaqCPT9");
            Statement s = connectionSource.createStatement();
            //int Result=s.executeUpdate("CREATE DATABASE PDFreader");
            String table = "CREATE TABLE IF NOT EXISTS `TEMPLATES` (\n" +
                    "`template_type` varchar(50) NOT NULL,\n" +
                    "`template_object` blob,\n" +
                    "PRIMARY KEY (`template_type`)\n" +
                    ")";
            int Result=s.executeUpdate(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initLogs() {


        try{
            start_end_fh = new FileHandler("pdfReaderLogFiles/PostStartEndRouteLog.log");
            START_END_LOGGER.addHandler(start_end_fh);
            SimpleFormatter formatter = new SimpleFormatter();
            start_end_fh.setFormatter(formatter);
            START_END_LOGGER.info("Created");
        } catch (Exception e) {}


        try{
            multiple_inst_fh = new FileHandler("pdfReaderLogFiles/PostMultipleInstance.log");
            MULTIPLE_LOG.addHandler(multiple_inst_fh);
            SimpleFormatter formatter = new SimpleFormatter();
            multiple_inst_fh.setFormatter(formatter);
            MULTIPLE_LOG.info("Created");
        } catch (Exception e) {}


    }

    /**
     * gets a new token id
     * @return unique token id
     */
    public static String getToken() {
        return tokenGenerator.nextString();
    }


    /**
     * get a specific token from the map based on an id
     * @param id unique token id
     * @param request request to get session from
     * @return token object with id provided
     */
    public static Token getToken(String id, Request request) {
        HashMap<String, Token> map = request.session().attribute("tokens");
        return map.get(id);
    }

    /**
     * remove a token object from the session map
     * @param id unique token id
     * @param request request to get the session from
     */
    public static void clearToken(String id, Request request) {
        HashMap<String, Token> map = request.session().attribute("tokens");
        map.remove(id);
    }


}
