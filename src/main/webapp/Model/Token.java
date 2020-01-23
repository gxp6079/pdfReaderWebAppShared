package main.webapp.Model;

import main.webapp.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Token {

    private Template template;
    private String id;
    private String institutionId;
    private String csvPath;
    private TableFactory tableFactory;
    private String pdfPath;
    private Map<String, Table> tables;
    private HashMap<String, TableAttributes> tableAttributes;


    public Token(String id) {
        this.id = id;
        this.tableAttributes = new HashMap<>();
    }

    public TableAttributes getTableAttributes(String tableId) {
        return tableAttributes.get(tableId);
    }

    public void setTableAttributes(TableAttributes tableAttributes) {
        this.tableAttributes.put(tableAttributes.tableId, tableAttributes);
    }

    public Template getTemplate() {
        return template;
    }

    public Map<String, Table> getTables() {
        return tables;
    }

    public void setTables(Map<String, Table> tables) {
        this.tables = tables;
    }

    public void setTemplate(Template newTemplate) {
        this.template = newTemplate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitutionId() { return institutionId; }

    public void setInstitutionId(String institutionId) { this.institutionId = institutionId; }

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
