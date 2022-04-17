package com.example.projectofmurad.table;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Tables_Fragment#newInstance} factory method to
 * create an instance of requireContext() fragment.
 */
public class Tables_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button btn_calendar;

    public Tables_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use requireContext() factory method to create a new instance of
     * requireContext() fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tables_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static Tables_Fragment newInstance(String param1, String param2) {
        Tables_Fragment fragment = new Tables_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for requireContext() fragment
        return inflater.inflate(R.layout.fragment_tables_, container, false);
    }

    private TableLayout table;
//    private TableView<String[]> tableView;

    private SimpleTableDataAdapter simpleTableDataAdapter;
    private Button btn_add_column;

    private EditText tv_table_name;

    private RecyclerView rv_table;
    private TableAdapter tableAdapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_calendar = view.findViewById(R.id.btn_calendar);
        btn_calendar.setVisibility(View.VISIBLE);
//        btn_calendar.setOnClickListener(v -> startActivity(new Intent(requireContext(), Calendar_Screen.class)));

        tv_table_name = view.findViewById(R.id.tv_table_name);

        table = view.findViewById(R.id.table);
        table.setVisibility(View.GONE);

//        tableView = view.findViewById(R.id.tableView);



//        init(view);

//        addData();

        btn_add_column = view.findViewById(R.id.btn_add_column);
//        btn_add_column.setOnClickListener(this::addColumn);

        excel = view.findViewById(R.id.excel);

        // click on excel to select a file
        excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectFile();
                }
                else {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                }
            }
        });

        rv_table = view.findViewById(R.id.rv_table);



        addDataToRecyclerView();

    }

    private void getDataFromAPI() {

        // creating a string variable for URL.
        String url = "https://spreadsheets.google.com/feeds/list/1AOOaz-5PhVgIvfROammZsdUs92PdYhEUgGoDrYlGGhc/od6/public/values?alt=json";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // creating a variable for our JSON object request and passing our URL to it.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject feedObj = response.getJSONObject("feed");
                    JSONArray entryArray = feedObj.getJSONArray("entry");
                    for(int i=0; i<entryArray.length(); i++){
                        JSONObject entryObj = entryArray.getJSONObject(i);
                        String firstName = entryObj.getJSONObject("gsx$firstname").getString("$t");
                        String lastName = entryObj.getJSONObject("gsx$lastname").getString("$t");
                        String email = entryObj.getJSONObject("gsx$email").getString("$t");
                        String avatar = entryObj.getJSONObject("gsx$avatar").getString("$t");


                        // passing array list to our adapter class.


                        // setting layout manager to our recycler view.


                        // setting adapter to our recycler view.
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handline on error listener method.
                Toast.makeText(requireContext(), "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });
        // calling a request queue method
        // and passing our json object
        queue.add(jsonObjectRequest);
    }

    public void addDataToRecyclerView(){

        FirebaseUtils.getDatabase().getReference("Tables").orderByChild("timestamp").limitToLast(1)
                .addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()){
                            return;
                        }

                        for (DataSnapshot table : snapshot.getChildren()){
                            Log.d(Utils.LOG_TAG, "snapshot.getKey() " + table.getKey());

                            String name = table.getKey();
                            int columnCount = table.child("columnCount").getValue(int.class);
                            int rowCount = table.child("rowCount").getValue(int.class);


                            tv_table_name.setText(name);

                            ArrayList<String> data = (ArrayList<String>) table.child("data").getValue();

                            /*for (DataSnapshot row : table.child("data").getChildren()) {

                                int i = Integer.parseInt(row.getKey());


                            }*/

                            for (int i = 0; i < data.size()-30; i++) {
                                for (int j = i; j < i+30; j++) {
                                    System.out.print(data.get(j) + " | ");
                                }
                                System.out.println();
                            }

                            tableAdapter = new TableAdapter(requireContext(), data);
                            rv_table.setAdapter(tableAdapter);

                            rv_table.setLayoutManager(new GridLayoutManager(requireContext(), columnCount, LinearLayoutManager.HORIZONTAL, false));

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


/*
    public void addData(){

        FirebaseUtils.getDatabase().getReference("Tables").orderByChild("timestamp").limitToLast(1)
                .addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()){
                            return;
                        }

                        for (DataSnapshot table : snapshot.getChildren()){
                            Log.d(Utils.LOG_TAG, "snapshot.getKey() " + table.getKey());

                            String name = table.getKey();
                            int columnCount = table.child("columnCount").getValue(int.class);
                            int rowCount = table.child("rowCount").getValue(int.class);

                            tableView.setColumnCount(columnCount);
                            tv_table_name.setText(name);

                            String[][] data = new String[rowCount][columnCount];

                            for (DataSnapshot row : table.child("data").getChildren()) {

                                int i = Integer.parseInt(row.getKey());

                                String[] rowData = new String[(int) row.getChildrenCount()];

                                for (DataSnapshot cell : row.getChildren()){
                                    int j = Integer.parseInt(cell.getKey());

                                    String cellData = cell.getValue(String.class);

                                    rowData[j] = cellData;

                                    System.out.print(cellData + " | ");
                                }
                                System.out.println();

                                data[i] = rowData;
                            }

                            createTable(data);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
*/

/*
    public void createTable(String[][] data){
        simpleTableDataAdapter = new SimpleTableDataAdapter(requireContext(), data);

        simpleTableDataAdapter.setTextSize(10);




        tableView.setDataAdapter(simpleTableDataAdapter);
    }
*/

    /*public void addColumn(View view){
        tableView.setColumnCount(tableView.getColumnCount()+1);

        tableView.setDataAdapter(simpleTableDataAdapter);
    }*/

    public void init(@NonNull View view) {
        TableRow tbrow0 = new TableRow(requireContext());
        TextView tv0 = new TextView(requireContext());
        tv0.setText(" Sl.No ");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(requireContext());
        tv1.setText(" Product ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(requireContext());
        tv2.setText(" Unit Price ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(requireContext());
        tv3.setText(" Stock Remaining ");
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);
        table.addView(tbrow0);
        for (int i = 0; i < 25; i++) {
            TableRow tbrow = new TableRow(requireContext());
            TextView t1v = new TextView(requireContext());
            t1v.setText("" + i);
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(requireContext());
            t2v.setText("Product " + i);
            t2v.setTextColor(Color.WHITE);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            TextView t3v = new TextView(requireContext());
            t3v.setText("Rs." + i);
            t3v.setTextColor(Color.WHITE);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            TextView t4v = new TextView(requireContext());
            t4v.setText("" + i * 15 / 32 * 10);
            t4v.setTextColor(Color.WHITE);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);
            table.addView(tbrow);
        }

    }

    Button excel;

    // request for storage permission if not given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectFile();
            } else {
                Toast.makeText(requireContext(), "Permission Not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void selectFile() {
        // select the file from the file storage
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select File"), 102);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            String filepath = data.getData().getPath();
            Log.d("murad", "filepath is " + filepath);
            // If excel file then only select the file
            if (/*filepath.endsWith(".xlsx") || filepath.endsWith(".xls")*/true) {
                readFile(data.getData());
            }
            // else show the error
            else {
                Toast.makeText(requireContext(), "Please Select an Excel file to upload", Toast.LENGTH_LONG).show();
            }
        }
    }

    ProgressDialog dialog;

    private void readFile(final Uri file) {
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage("Uploading");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                final HashMap<String, Object> parentMap = new HashMap<>();

                final HashMap<String, Object> myMap = new HashMap<>();

                try {
                    XSSFWorkbook workbook;

                    // check for the input from the excel file
                    try (
                        InputStream inputStream = requireContext().getContentResolver().openInputStream(file)) {
                        workbook = new XSSFWorkbook(inputStream);
                    }
                    final String timestamp = "" + System.currentTimeMillis();
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    sheet.getLeftCol();
                    sheet.getLastRowNum();

                    Log.d(Utils.LOG_TAG, "sheet.getLeftCol() = " + sheet.getLeftCol());
                    Log.d(Utils.LOG_TAG, "sheet.getLastRowNum() = " + sheet.getLastRowNum());

                    String name = sheet.getSheetName();
                    int columnCount = 0;

                    FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                    int rowCount = sheet.getPhysicalNumberOfRows();

                    if (rowCount > 0) {
                        // check row wise data

//                        ArrayList<ArrayList<String>> sheetData = new ArrayList<>();
                        ArrayList<String> sheetData = new ArrayList<>();

                        for (int r = 0; r < rowCount; r++) {
                            Row row = sheet.getRow(r);

                            columnCount = Math.max(columnCount, row.getLastCellNum());
                        }

                        for (int r = 0; r < rowCount; r++) {
                            Row row = sheet.getRow(r);

                            columnCount = Math.max(columnCount, row.getLastCellNum());

                            Log.d(Utils.LOG_TAG, "row.getPhysicalNumberOfCells() = " + row.getPhysicalNumberOfCells());
                            Log.d(Utils.LOG_TAG, "row.getLastCellNum() = " + row.getLastCellNum());

                            /*if (row.getPhysicalNumberOfCells() == cellCount) {

                                // get cell data
                                String A = getCellData(row, 0, formulaEvaluator);
                                String B = getCellData(row, 1, formulaEvaluator);

                                // initialise the hash map and put value of a and b into it
                                HashMap<String, Object> questionMap = new HashMap<>();
                                questionMap.put("A", A);
                                questionMap.put("B", B);
                                String id = UUID.randomUUID().toString();
                                parentMap.put(id, questionMap);
                            }
                            else {
                                dialog.dismiss();
                                Toast.makeText(requireContext(), "row no. " + (r + 1) + " has incorrect data", Toast.LENGTH_LONG).show();
                                return;
                            }*/

                            HashMap<String, Object> questionMap = new HashMap<>();
                            String id = UUID.randomUUID().toString();
                            parentMap.put(id, questionMap);

                            ArrayList<String> rowData = new ArrayList<>();

                            for (int i = 0; i < /*row.getPhysicalNumberOfCells()*/ columnCount; i++) {
                                String cellData = getCellData(row, i, formulaEvaluator);
                                questionMap.put(""+i, cellData);
                                rowData.add(i, cellData);
                            }

//                            sheetData.add(r, rowData);
                            sheetData.addAll(rowData);

                        }

                        // add the data in firebase if everything is correct
                        // add the data according to timestamp
                        FirebaseDatabase.getInstance().getReference().child("Data").
                                child(timestamp).updateChildren(
                                parentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        myMap.put("columnCount", columnCount);
                        myMap.put("rowCount", rowCount);
                        myMap.put("data", sheetData);
                        myMap.put("timestamp", timestamp);

                        FirebaseDatabase.getInstance().getReference().child("Tables").
                                child(name).setValue(myMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    // show the error if file is empty
                    else {
                        dialog.dismiss();
                        Toast.makeText(requireContext(), "File is empty", Toast.LENGTH_LONG).show();
                    }
                    workbook.close();
                }
                // show the error message if failed
                // due to file not found
                // show the error message if there
                // is error in input output
                catch (final IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void readSheet(XSSFSheet sheet){

    }

    private String getCellData(@NonNull Row row, int cellPosition, FormulaEvaluator formulaEvaluator) {

        String value = "";

        // get cell from excel sheet
        Cell cell = row.getCell(cellPosition);
        if (cell == null){
            return "";
        }

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return value + cell.getBooleanCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return value + cell.getNumericCellValue();
            case Cell.CELL_TYPE_STRING:
                return value + cell.getStringCellValue();
            default:
                return value;
        }
    }
}