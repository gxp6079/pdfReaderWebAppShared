package main.webapp.Model;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.sql.Connection;
import java.util.logging.Logger;

public class Template implements Serializable {
    private List<TableAttributes> tables;
    private HashMap<String, Field> fields;
    private String type;

    public Template() {
        this(null);
    }

    public Template(String type) {
        this.tables = new ArrayList<>();
        this.fields = new HashMap<>();
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEmpty() {
        return this.type == null;
    }

    public void addTable(TableAttributes tableAttributes) {
        this.tables.add(tableAttributes);
    }

    public void addField(Field field){
        fields.put(field.NAME, field);
    }

    public Map<String, Field> getFields() {
        return this.fields;
    }

    public List<TableAttributes> getTables() {
        return tables;
    }

    public String getType(){return this.type;}

    public boolean shouldSave(Logger LOG){

        LOG.info(fields.toString());

        for (Field field : fields.values()){
            if(field == null){
                return false;
            }
        }
        return  true;
    }

    public void save() {
        String filename = this.type + ".ser";
        try {
            FileOutputStream fos = new FileOutputStream(new File(filename));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);

            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void saveDB() throws SQLException, IOException {

        Connection connection = DataBaseConnection.makeConnection();

        // serializing java object to mysql database
        DataBaseConnection.serializeJavaObjectToDB(connection, this);

        connection.close();
    }

}
