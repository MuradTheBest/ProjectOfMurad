package com.example.projectofmurad.table;

import java.util.ArrayList;

public class MyTableRow{

    private String uid;

    private ArrayList<String> columns;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<String> columns) {
        this.columns = columns;
    }

    public void addColumn(String column) {
        this.columns.add(column);
    }

    public void deleteColumn(String column) {
        this.columns.remove(column);
    }
}
