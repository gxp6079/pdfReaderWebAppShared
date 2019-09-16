package main.webapp.Model;

import java.io.Serializable;
import java.util.List;

public class Field implements Serializable {
    public final String NAME;
    public final int TABLE_ID;
    public final String HEADER;

    public Field(String name, int table, String header){
        this.NAME = name;
        this.TABLE_ID = table;
        this.HEADER = header;
    }

    public List<String> getValue(Table table){
        return table.getDataAt(HEADER);
    }

}
