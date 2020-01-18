package main.webapp.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Field implements Serializable {
    public final String NAME;
    public final String TABLE_ID;
    public final String HEADER;
    private HashMap<String, String> wordLUT;

    public Field(String name, String table, String header){
        this.NAME = name;
        this.TABLE_ID = table;
        this.HEADER = header;
        this.wordLUT = new HashMap<>();
    }

    public HashMap<String, String> getWordLUT() {
        return wordLUT;
    }

    public List<String> getValue(Table table){
        return table.getDataAt(HEADER);
    }

    public void addTranslation(String key, String value) {
        this.wordLUT.put(key, value);
    }

    @Override
    public int hashCode() {
        return NAME.hashCode() + TABLE_ID.hashCode();
    }
}
