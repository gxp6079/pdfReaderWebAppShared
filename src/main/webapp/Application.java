package main.webapp;

import main.webapp.Model.DataBaseConnection;
import main.webapp.Routes.*;
import spark.servlet.SparkApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import static spark.Spark.get;
import static spark.Spark.post;

public class Application implements SparkApplication {

    public static final String TEMPLATE_URL = "/PDFreader";

    public static final String TABLE_INFO_URL = "/tableInfo";

    public static final String START_END_URL = "/startEnd";

    public static final String MULTIPLE_INSTANCE_URL = "/multi";

    public static final String FINAL_INFO = "/finalInfo";

    public static final String SIGN_IN = "/signIn";

    public static final String EXIT = "/exit";

    private static final Logger LOG = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) {

        createDatabaseTable();

        LOG.config("Initialization Complete");
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init() {
        get(FINAL_INFO, new getFinalInfoRoute());

        get(MULTIPLE_INSTANCE_URL, new getMultipleInstancesRoute());

        get(TABLE_INFO_URL, new getTableInfoRoute());

        get(EXIT, new getUserExitRoute());

        post(MULTIPLE_INSTANCE_URL, new postMultipleInstancesRoute());

        post(START_END_URL, new postStartEndRoute());

        post(TABLE_INFO_URL, new postTableInfoRoute());

        post(TEMPLATE_URL, "multipart/form-data", new postTemplateRoute());

        post(SIGN_IN, new postSignInRoute());

        LOG.finer("WebServer Initialized");
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
}
