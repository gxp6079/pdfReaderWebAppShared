package main.webapp.Model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Header {
    private String value;
    private int row;
    private int col;
    private Header parent;
    private List<Header> children;
    private int length;

    /**
     * constructor where there is no parent
     * @param row
     * @param col
     * @param value
     */
    public Header(int row, int col, String value) {
        this.value = value;
        this.row = row;
        this.col = col;
        this.parent = null;
        this.children = new ArrayList<Header>();
        this.length = value.length();
    }

    /**
     * constructor for sub header with parent
     * @param row
     * @param col
     * @param value
     * @param parent
     */
    public Header(int row, int col, String value, Header parent) {
        this.value = value;
        this.row = row;
        this.col = col;
        this.parent = parent;
        this.children = new ArrayList<Header>();
        this.length = value.length();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Header getParent() {
        return parent;
    }

    public String getValue() {
        return value;
    }


    public boolean hasParent() {
        return this.parent != null;
    }

    public boolean hasChildren() { return children.size() != 0; }

    public void addChild(Header child){
        children.add(child);
    }

    public List<Header> getChildren() { return this.children; }

    public int getSizeChildren(){
        return this.children.size();
    }

    public void updateLength(int update){ this.length = update;}

    public int getLength(){return this.length;}

    @Override
    public String toString() {
        String s = "";
        s += this.value;
        if(parent == null) {
            int toAdd = -this.value.length();
            for (Header child : getChildren()) {
                toAdd += child.getValue().length();
            }
            if (toAdd > 0) {
                s += StringUtils.repeat(' ', toAdd);
            }
        }
        updateLength(s.length());
        return s;
    }
}
