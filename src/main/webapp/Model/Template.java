package main.webapp.Model;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.sql.Connection;
import java.util.logging.Logger;

public class Template implements Serializable {
    private HashMap<String, TableAttributes> tables;
    private HashMap<Integer, Field> fields;
    private String type;

    public Template() {
        this(null);
    }

    public Template(String type) {
        this.tables = new HashMap<>();
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
        this.tables.put(tableAttributes.tableId, tableAttributes);
    }

    public void addField(Field field){
        fields.put(field.hashCode(), field);
    }

    public Map<Integer, Field> getFields() {
        return this.fields;
    }

    public HashMap<String, TableAttributes> getTables() {
        return tables;
    }

    public String getType(){return this.type;}

}
