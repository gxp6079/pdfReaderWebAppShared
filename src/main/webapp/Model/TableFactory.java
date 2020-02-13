package main.webapp.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TableFactory {

    /**
     * left most column of the table
     */
    private int leftCol;

    /**
     * current column that the factory is currently looking at
     */
    private int col;

    /**
     * current row that the factory is currently looking at
     */
    private int row;

    /**
     * list of string arrays, aka csv file text
     */
    private List<String[]> list;

    /**
     * list of indexes we have seen data at
     */
    private ArrayList<Integer> dataIndexes;

    /**
     * list of strings that is constantly updated and eventually sent to the table
     */
    private ArrayList<String> tableRow;

    /**
     * start of word for the table
     */
    private String start;

    /**
     * end word of the table
     */
    private String end;

    /**
     * Should use contains
     */
    private Boolean contains;

    /**
     * whether the table is horizontal or vertical
     * used for accessing data
     */
    private TableAttributes.Orientation orientation;

    private List<Integer[]> locations;

    private static final Logger LOG = Logger.getLogger(TableFactory.class.getName());
    public static FileHandler fh;



    public TableFactory(List<String[]> list) {
        this.tableRow = new ArrayList<>();
        this.dataIndexes = new ArrayList<>();
        this.list = list;
        this.contains = contains = true;
        this.row = 0;
        this.col = 0;

        this.start = start = "";
        this.end = end = "";

        try{
            fh = new FileHandler("pdfReaderLogFiles/TableFactoryLog.log");
            LOG.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOG.info("TableFactoryLogger created");
        }
        catch (Exception e){

        }

    }


    public void initialize(String start, String end, Boolean contains, TableAttributes.Orientation orientation) {
        this.tableRow.clear();
        this.dataIndexes.clear();
        this.row = 0;
        this.col = 0;
        this.contains = contains;
        this.orientation = orientation;
        this.start = start.trim().toLowerCase();
        this.end = end.trim().toLowerCase();
        this.locations = getLocation(this.start, this.end, this.contains);
        LOG.info("Table factory initialized with start, end: " + start + ", " + end);
        LOG.info("Number of locations found: " + locations.size());
    }

    public List<Integer[]> getLocation(String start, String end, Boolean contains){
        LOG.info("getLocation");
        List<Integer[]> locations = new ArrayList<>();
        int leftCol = 0;
        int row = 0;
        while(row < list.size()) {
            //LOG.info("Comparing: " + start + " and " + list.get(row)[leftCol].trim().toLowerCase());
            if ((contains && list.get(row)[leftCol].trim().toLowerCase().contains(start)) ||
                    (!contains && list.get(row)[leftCol].trim().toLowerCase().equals(start))) {
                LOG.info("start found");
                Integer[] loc = new Integer[2];
                loc[0] = row;
                loc[1] = leftCol;
                if (hasEnd(end, row, contains)) locations.add(loc);
            }
            if (leftCol == list.get(row).length - 1) {
                leftCol = 0;
                row++;
            } else {
                leftCol++;
            }
        }
        return locations;
    }

    private boolean hasEnd(String end, int row, Boolean contains) {
        LOG.info(String.format("hasEnd called (%s, %d)", end, row));
        int col = 0;
        try {
            while (row < list.size()) {
                String val = list.get(row)[col].trim().toLowerCase();
                if ((contains && val.contains(end)) || (!contains && val.equals(end))){
                    LOG.info(String.format("End found at row, col: %s, %s", row, col));
                    return true;
                }
                if (col == list.get(row).length - 1) {
                    col = 0;
                    row++;
                    if (row >= list.size()) {
                        // END string not found
                        return false;
                    }
                } else {
                    col++;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }




    public int getNumLocations() {
        return this.locations.size();
    }


    public Table makeTable(int location) {
        LOG.info("Making table with start, end: " + start + ", " + end);

        boolean finishedHead = false;

        if(locations.size() != 1){
            if(locations.size() == 0){
                LOG.info("Failed to make table, no locations found for start, end :"  + start + ", " + end);
                System.out.println("Start not found");
                return new Table(start, end, orientation); //shouldn't we return null here
            }
            else{
                this.row = locations.get(location - 1)[0];
                this.leftCol = locations.get(location - 1)[1];
                LOG.info("Using location row, leftCol: " + this.row + ", " + this.leftCol);
            }
        }
        else{
            this.row = locations.get(0)[0];
            this.leftCol = locations.get(0)[1];
            LOG.info("Only one possible location exists");
            LOG.info("Using location row, leftCol: " + this.row + ", " + this.leftCol);
        }
        //getEndCol(this.end, this.row, this.leftCol);
        Table table = new Table(start, end, orientation);
        LOG.info("Table object created with start, end: " + start + ", " + end);

        initializeHeaders(table);
        LOG.info("Table initialized");

        tableRow.clear();

        this.row++;
        this.col = this.leftCol;
        String val = list.get(row)[col].trim().toLowerCase();
        end = end.trim().toLowerCase();
        while(!((contains && val.contains(end)) || (!contains && val.equals(end)))) {
            val = list.get(row)[col].trim().toLowerCase();
            if (col >= leftCol) {
                if(col == leftCol && !val.equals("")) finishedHead = true;
                checkEntry(table, finishedHead);
                if (!val.equals("")) {
                    tableRow.add(val);
                    LOG.info("Adding '" + val + "' to table row");
                }
                else if (val.equals("") && dataIndexes.contains(col)) {
                    tableRow.add(val);
                    LOG.info("Adding '" + val + "' to table row");
                }
            }

            if(col >= list.get(row).length) {
                if(!list.get(row)[leftCol].equals("") && !tableRow.get(0).contains("...")) {
                    table.addRow(tableRow);
                    LOG.info("Adding row of size: " + tableRow.size());
                }
                col = 0;
                tableRow.clear();
                this.row++;
                if (row >= list.size() && !((contains && val.contains(end)) || (!contains && val.equals(end)))) {
                    System.out.println("End not found");
                    LOG.info("End" + this.end+" was not found, returning empty table");
                    return new Table(start, end, orientation);
                }
            } else {
                col++;
            }
        }
        LOG.info("End of table found at row, col: " + row + ", " + col);
        return table;
    }


    private void checkEntry(Table table, boolean finishedHead) {
        String val = list.get(row)[col].trim().toLowerCase();
        if (!val.equals("")) {
            if (!finishedHead && list.get(row)[leftCol].trim().equals("")) {
                makeSubHeader(table, val);
            } else {
                if (!dataIndexes.contains(this.col)) {
                    checkPreviousColumns(table);
                }
            }
        }


    }


    private void makeSubHeader(Table table, String value) {
        int lastData = 0;
        for (int idx : dataIndexes) {
            if (idx < col) lastData = idx;
            else break;
        }
        Header lastSubHeader = table.getSubHeader(lastData);
        if (table.getHeader(col) != null) {
            Header parent = table.getHeader(col);
            Header child = new Header(row, col, value, parent);
            table.addSubHeader(child);
            table.updateHeader(parent.getCol(), child);
        } else if (lastSubHeader != null && lastSubHeader.getRow() == row) {
            Header sub = table.getSubHeader(lastData);
            Header child = new Header(row, col, value, sub.getParent());
            table.addSubHeader(child);
            table.updateHeader(sub.getParent().getCol(), child);
        } else {
            Header header = new Header(row, col, value);
            table.addHeader(header);
        }
        if (!dataIndexes.contains(col)) dataIndexes.add(col);
        dataIndexes.sort(Integer::compareTo);
    }


    private void checkPreviousColumns(Table table) {
        int lastData = 0;
        for (int idx : dataIndexes) {
            if (idx < col) lastData = idx;
            else break;
        }

        if (list.get(row)[lastData].trim().equals("")) {
            tableRow.remove(tableRow.size() - 1);
        } else {
            for (int idx = 0; idx < dataIndexes.size(); idx++) {
                if (idx == dataIndexes.size() - 1) {
                    String val = list.get(row)[col].trim().toLowerCase();
                    Header parent = table.getHeader(col);

                    Header child;

                    if (parent != null && dataIndexes.get(idx) == col) {
                        child = new Header(row, col, val, parent);
                    } else if (table.getSubHeader(dataIndexes.get(idx)) != null) {
                        Header prevSubHeader = table.getSubHeader(dataIndexes.get(idx));
                        child = new Header(row, col, val, prevSubHeader.getParent());
                    } else {
                        child = new Header(row, col, val);
                    }

                    if (child.hasParent()) table.addSubHeader(child);
                    else table.addHeader(child);

                } else if (col > dataIndexes.get(idx) && col < dataIndexes.get(idx + 1)){
                    Header parent = table.getHeader(dataIndexes.get(idx));
                    Header child = new Header(row, col, list.get(row)[col].trim().toLowerCase(), parent);

                    if (child.hasParent()) {
                        table.addSubHeader(child);
                    } else {
                        table.addHeader(child);
                    }

                    table.updateHeader(parent.getCol(), child);
                    break;
                }
            }
            dataIndexes.add(col);
            dataIndexes.sort(Integer::compareTo);


            // for each index in the dataIndexes, check if we are between 2 columns in the list
            //      if we are:
            //          then make a sub header with a parent at the last data index
            //      if we are at the last index:
            //          then make a sub header, check if there is another header directly above it
            //          if there is no header then check if there is a sub header in the last data index with a parent
        }
    }


    private void initializeHeaders(Table table) {
        LOG.info("initialize header called");
        for (this.col = this.leftCol; this.col < list.get(this.row).length; this.col++) {
            String val = list.get(this.row)[this.col].trim().toLowerCase();
            if (!val.equals("")) {
                Header header = new Header(this.row, this.col, val);
                table.addHeader(header);
                LOG.info("header added " + header.toString());
                if (!val.contains("...")) this.tableRow.add(val);
                dataIndexes.add(col);
            }
        }
    }

    public List<Integer[]> getLocations() {
        return locations;
    }
}
