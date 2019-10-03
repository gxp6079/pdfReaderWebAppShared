package main.webapp.Model;

import java.util.*;

public class Table {
    private List<List<String>> table;

    private String start;
    private String end;


    /**
     * maps header column to header
     */
    private Map<Integer, Header> headerList;


    /**
     * mapping subHeader column to the sub header
     */
    private Map<Integer, Header> subHeaders;

    public Table(String start, String end){
        this.headerList = new TreeMap<Integer, Header>();
        this.subHeaders =  new TreeMap<Integer, Header>();
        this.start = start;
        this.end = end;
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
     * @param header
     * @return
     */
    public List<String> getDataAt(String header) {
        boolean found = false;
        header = header.trim().toLowerCase();
        int col = 0;
        for (int i : headerList.keySet()) {
            String val = headerList.get(i).getValue();
            if (val.contains(header)){
                found = true;
                break;
            }
            else if (headerList.get(i).hasChildren()) {
                for (Header h : headerList.get(i).getChildren()) {
                    if (h.getValue().contains(header)){
                        found = true;
                        break;
                    }
                    col++;
                }
            } else {
                if (headerList.get(i).getValue().contains(header)){
                    found = true;
                    break;
                }
                col++;
            }
        }

        if (!found){
            for (List<String> row : table){
                if(row.get(0).contains(header)){
                    return row.subList(0, row.size());
                }
            }
            return null;
        }

        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            data.add(table.get(i).get(col));
        }
        return data;
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
