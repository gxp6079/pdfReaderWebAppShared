package main.webapp.Model;

import java.io.Serializable;

public class TableAttributes implements Serializable {
    public final String START;
    public final String END;
    private int occurrence = 1;

    public TableAttributes(String start, String end) {
        this.START = start;
        this.END = end;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public int getOccurrence() {
        return occurrence;
    }
}
