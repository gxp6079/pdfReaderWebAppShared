package main.webapp;

import main.webapp.Model.DataBaseConnection;
import main.webapp.Routes.WebServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class Application {

    private static final Logger LOG = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) {

        createDatabaseTable();

        WebServer webServer = new WebServer();
        webServer.init();

        LOG.config("Initialization Complete");
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
