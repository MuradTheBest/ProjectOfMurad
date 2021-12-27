package com.example.projectofmurad;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.projectofmurad.calendar.Calendar_Screen;
import com.example.projectofmurad.calendar.Calendar_Test_Screen;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Tables_Fragment#newInstance} factory method to
 * create an instance of this fragment.
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
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tables_, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_calendar = view.findViewById(R.id.btn_calendar);
        btn_calendar.setOnClickListener(v -> startActivity(new Intent(getActivity(), Calendar_Test_Screen.class)));
    }

    public class SetDate implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;
            btn_calendar.setText("You selected " + day + "/" + month + "/" + year);
        }
    }
}