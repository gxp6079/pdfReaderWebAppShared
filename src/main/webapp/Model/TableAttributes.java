package main.webapp.Model;

import java.io.Serializable;

public class TableAttributes implements Serializable {
    public final String START;
    public final String END;
    public final Boolean contains;
    public final String tableId;
    private int occurrence = 1;

    public TableAttributes(String start, String end, Boolean contains, String tableId) {
        this.START = start;
        this.END = end;
        this.contains = contains;
        this.tableId = tableId;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public int getOccurrence() {
        return occurrence;
    }
}
