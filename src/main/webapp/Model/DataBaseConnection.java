package main.webapp.Model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class DataBaseConnection {


    public static final String DATABASE_IP = "jdbc:mysql://localhost/PDFreader?serverTimezone=EST";
    private static final String SQL_SERIALIZE_OBJECT = "INSERT INTO TEMPLATES(template_type, template_object, institution_id) VALUES (?, ?, ?)";
    private static final String SQL_DESERIALIZE_OBJECT = "SELECT template_object FROM TEMPLATES WHERE (template_type = ? AND institution_id = ?)";
    private static final String SQL_TEMPLATES_FOR_INST = "SELECT template_type FROM TEMPLATES WHERE institution_id = ?";
    private static final String SQL_OBJECT_EXISTS = "SELECT EXISTS (SELECT template_object FROM TEMPLATES WHERE (template_type = ? AND institution_id = ?)) ";
    private static final String SQL_UPDATE_TEMPLATE = "UPDATE TEMPLATES SET template_object = ? WHERE (template_type = ? AND institution_id = ?)";

    public static long serializeJavaObjectToDB(Connection connection,
                                               Template objectToSerialize, String institutionId) throws SQLException {

        PreparedStatement pstmt = connection
                .prepareStatement(SQL_SERIALIZE_OBJECT);

        // just setting the class name
        pstmt.setString(1, objectToSerialize.getType());
        pstmt.setString(3, institutionId);
        pstmt.setObject(2, objectToSerialize);
        pstmt.executeUpdate();
        //ResultSet rs = pstmt.getGeneratedKeys();
        //int serialized_id = -1;
        //if (rs.next()) {
        //    serialized_id = rs.getInt(1);
        //}
        //rs.close();
        pstmt.close();
        System.out.println("Java object serialized to database. Object: "
                + objectToSerialize);
        return 1;
    }


    public static Boolean checkIfObjExists(Connection connection, String type, String institutionId) throws SQLException {

        PreparedStatement pstmt = connection
                .prepareStatement(SQL_OBJECT_EXISTS);
        pstmt.setString(1, type);
        pstmt.setString(2, institutionId);
        ResultSet rs = pstmt.executeQuery();
        rs.next();

        byte[] buf = rs.getBytes(1);

        rs.close();
        pstmt.close();

        if (buf[0] == 49) {
            return true;
        }

        return false;
    }

    /**
     * To de-serialize a java object from database
     *
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deSerializeJavaObjectFromDB(Connection connection,
                                                     String type,
                                                     String institutionId) throws SQLException, IOException,
            ClassNotFoundException {
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ObjectInputStream objectIn = null;
        Object deSerializedObject = null;

        if (checkIfObjExists(connection, type, institutionId)) {
            pstmt = connection
                    .prepareStatement(SQL_DESERIALIZE_OBJECT);
            pstmt.setString(1, type);
            pstmt.setString(2, institutionId);
            rs = pstmt.executeQuery();

            rs.next();

            // Object object = rs.getObject(1);

            byte[] buf = rs.getBytes(1);
            objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
            deSerializedObject = objectIn.readObject();
        }

        rs.close();
        pstmt.close();

        return deSerializedObject;
    }

    public static Connection makeConnection() throws SQLException {
        Connection connection = null;

        String driver = "com.mysql.cj.jdbc.Driver";
        String url = DATABASE_IP;

        String username = "brit"; //"brit";
        String password = "x0EspnYA8JaqCPT9"; //"x0EspnYA8JaqCPT9";

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection(url, username, password);

        return connection;
    }

    public static ArrayList<String> getTemplatesForInstitution(Connection connection, String institutionId, Logger LOG) throws SQLException, IOException,
            ClassNotFoundException {
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ObjectInputStream objectIn = null;
        ArrayList<String> listOfTemplates = new ArrayList<>();

        pstmt = connection
                .prepareStatement(SQL_TEMPLATES_FOR_INST);
        pstmt.setString(1, institutionId);
        rs = pstmt.executeQuery();

        LOG.info("executed query and got response:" + rs);
        while(rs.next()) {
            LOG.info("attempt to get the first item");
            try {
                String type = rs.getString("template_type");
                listOfTemplates.add(type);
            }
            catch (Exception e){
                LOG.info(e.getMessage());
            }
        }

        rs.close();
        pstmt.close();

        return listOfTemplates;
    }

    public static void updateTemplateInDB(Connection connection, String institutionId, Template updateTo)throws SQLException, IOException,
            ClassNotFoundException {
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        pstmt = connection
                .prepareStatement(SQL_UPDATE_TEMPLATE);
        pstmt.setObject(1, updateTo);
        pstmt.setString(2, updateTo.getType());
        pstmt.setString(3, institutionId);
        pstmt.executeUpdate();
        pstmt.close();
    }
}
