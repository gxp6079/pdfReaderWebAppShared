package main.webapp.Model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DataBaseConnection {


    public static final String DATABASE_IP = "jdbc:mysql://localhost/PDFreader?serverTimezone=EST";
    private static final String SQL_SERIALIZE_OBJECT = "INSERT INTO TEMPLATES(template_type, template_object) VALUES (?, ?)";
    private static final String SQL_DESERIALIZE_OBJECT = "SELECT template_object FROM TEMPLATES WHERE template_type = ?";
    private static final String SQL_OBJECT_EXISTS = "SELECT EXISTS (SELECT template_object FROM TEMPLATES WHERE template_type = ?) ";

    public static long serializeJavaObjectToDB(Connection connection,
                                               Template objectToSerialize) throws SQLException {

        PreparedStatement pstmt = connection
                .prepareStatement(SQL_SERIALIZE_OBJECT);

        // just setting the class name
        pstmt.setString(1, objectToSerialize.getType());
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


    public static Boolean checkIfObjExists(Connection connection, String type) throws SQLException {

        PreparedStatement pstmt = connection
                .prepareStatement(SQL_OBJECT_EXISTS);
        pstmt.setString(1, type);
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
                                                     String type) throws SQLException, IOException,
            ClassNotFoundException {
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ObjectInputStream objectIn = null;
        Object deSerializedObject = null;

        if (checkIfObjExists(connection, type)) {
            pstmt = connection
                    .prepareStatement(SQL_DESERIALIZE_OBJECT);
            pstmt.setString(1, type);
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
}
