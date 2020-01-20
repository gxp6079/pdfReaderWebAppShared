package main.webapp.Model;

import java.util.*;

public class Table {
    private List<List<String>> table;

    private String start;
    private String end;

    private TableAttributes.Orientation orientation;

    /**
     * maps header column to header
     */
    private Map<Integer, Header> headerList;


    /**
     * mapping subHeader column to the sub header
     */
    private Map<Integer, Header> subHeaders;

    public Table(String start, String end, TableAttributes.Orientation orientation){
        this.headerList = new HashMap<>();
        this.subHeaders =  new HashMap<>();
        this.start = start;
        this.end = end;
        this.orientation = orientation;
        this.table = new ArrayList<List<String>>();
    }

    public List<List<String>> getTable(){
        return this.table;
    }

    public void addHeader(Header header) {
        this.headerList.put(header.getCol(), header);
    }

    public void addSubHeader(Header sub) {
        this.subHeaders.put(sub.getCol(), sub);
    }

    public Header getHeader(int col) {
        return headerList.get(col);
    }

    public Header getSubHeader(int col) {
        return subHeaders.get(col);
    }


    public void updateHeader(Integer parent, Header subheader){
        headerList.get(parent).addChild(subheader);
    }

    public void addRow(List<String> row){
        table.add(new ArrayList<>(row));
    }


    /***
     * gets data in the column or row of a specified header
     * @param value value to search for in the table
     * @return
     */
    public List<String> getDataAt(String value) {
        boolean found = false;
        value = value.trim().toLowerCase();
        int colNum = 0;
        int rowNum = 0;
        ArrayList<String> data = new ArrayList<>();



        // find value in table

        for (int i : headerList.keySet()) {
            Header h = headerList.get(i);
            if (h.getValue().equals(value)) {
                colNum = h.getCol();
                rowNum = 0;
                found = true; // to skip searching rest of table
                data.add(h.getValue());
                if (subHeaders.containsKey(colNum)) data.add(subHeaders.get(colNum).getValue());
                break;
            }
            if (h.hasChildren()) {
                for (Header child : h.getChildren()) {
                    if (child.getValue().equals(value)) {
                        colNum = child.getCol();
                        rowNum = 0;
                        found = true; // to skip searching rest of table
                        data.add(child.getValue());
                        break;
                    }
                }
            }
        }
        if (!found) {
            for (rowNum = 0; rowNum < this.table.size(); rowNum++) {
                List<String> row = this.table.get(rowNum);
                for (colNum = 0; colNum < row.size(); colNum++) {
                    String currentVal = row.get(colNum).trim().toLowerCase();
                    if (currentVal.equals(value)) break;
                }
            }
        }

        if (orientation.equals(TableAttributes.Orientation.VERTICAL)) {

            for (int i = rowNum; i < table.size(); i++) {
                data.add(table.get(i).get(colNum));
            }
            return data;

        } else {
            List<String> row = this.table.get(rowNum);
            return row.subList(colNum, row.size());
        }
    }

    public String toString(){
        String s = "";
        for (Header header : headerList.values()){
           s += header.toString();
           s += "| ";
        }
        s += "\n";
        String subs = "";
        for (Header parent :headerList.values()){
            if(!parent.hasChildren()){
                subs += "| ";
            }
            else{
                Header child = null;
                for (int i = 0 ; i < parent.getChildren().size() ; i++){
                    child = parent.getChildren().get(i);
                    subs += child.toString();
                    subs += "| ";
                }
            }
        }
        subs += "\n";
        s += subs;
        for (List<String> row : table){
            String rowString = "";
            int idx = 0;
            for(String value : row){
                rowString += value;
                rowString += "| ";
            }
            rowString += "\n";
            s += rowString;
        }
        return s;
    }


    @Override
    public int hashCode() {
        return start.hashCode() + end.hashCode();
    }
}
