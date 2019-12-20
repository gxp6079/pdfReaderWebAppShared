package main.webapp.Model;

import main.webapp.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Token {

    private Template template;
    private String id;
    private String csvPath;
    private TableFactory tableFactory;
    private String pdfPath;
    private Map<Integer, Table> tables;
    private TableAttributes tableAttributes;


    public Token(String id) {
        this.id = Application.getToken();
    }

    public TableAttributes getTableAttributes() {
        return tableAttributes;
    }

    public void setTableAttributes(TableAttributes tableAttributes) {
        this.tableAttributes = tableAttributes;
    }

    public Template getTemplate() {
        return template;
    }

    public Map<Integer, Table> getTables() {
        return tables;
    }

    public void setTables(Map<Integer, Table> tables) {
        this.tables = tables;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCsvPath() {
        return csvPath;
    }

    public void setCsvPath(String csvPath) {
        this.csvPath = csvPath;
    }

    public TableFactory getTableFactory() {
        return tableFactory;
    }

    public void setTableFactory(TableFactory tableFactory) {
        this.tableFactory = tableFactory;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
