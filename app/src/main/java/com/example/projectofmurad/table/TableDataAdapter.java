package com.example.projectofmurad.table;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TableDataAdapter extends de.codecrafters.tableview.TableDataAdapter<MyTableRow> {

    public TableDataAdapter(Context context, MyTableRow[] data) {
        super(context, data);
    }

    public TableDataAdapter(Context context,
                            List<MyTableRow> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        return null;
    }
}
