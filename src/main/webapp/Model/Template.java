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

}
