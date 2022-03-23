package com.example.projectofmurad;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.projectofmurad.tracking.Training;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Training_Info_DialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Training_Info_DialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Training training;

    public Training_Info_DialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment Training_Info_DialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static Training_Info_DialogFragment newInstance(String param1, String param2) {
        Training_Info_DialogFragment fragment = new Training_Info_DialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            training = (Training) getArguments().getSerializable(Training.KEY_TRAINING);
        }

        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light);
        setCancelable(true);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training__info__dialog, container, false);
    }

    private LineChart lineChart_speed;
    private HashMap<String, Double> speeds;


    private TextView tv_training_name;
    private TextView tv_training_start_date;
    private TextView tv_training_time;
    private TextView tv_training_duration;
    private TextView tv_training_distance;
    private TextView tv_training_pace;


    private TextView tv_duration;
    private TextView tv_total_duration;
    private TextView tv_distance;
    private TextView tv_average_speed;
    private TextView tv_max_speed;
    private TextView tv_average_pace;
    private TextView tv_max_pace;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart_speed = view.findViewById(R.id.lineChart_speed);
        speeds = training.getSpeeds();



        tv_training_name = view.findViewById(R.id.tv_training_name);
        tv_training_start_date = view.findViewById(R.id.tv_training_start_date);
        tv_training_time = view.findViewById(R.id.tv_training_time);
        tv_training_duration = view.findViewById(R.id.tv_training_duration);
        tv_training_distance = view.findViewById(R.id.tv_training_distance);
        tv_training_pace = view.findViewById(R.id.tv_training_pace);


        tv_training_name.setText("" + training.getName());
        tv_training_start_date.setText("" + training.getStartDate());
        tv_training_time.setText("" + training.getStartTime() + " - " + training.getEndTime());
        tv_training_duration.setText("" + training.getDuration());
        tv_training_distance.setText("" + training.getTotalDistance());
        tv_training_pace.setText("" + training.getAvgPace());


        tv_duration = view.findViewById(R.id.tv_duration);
        tv_total_duration = view.findViewById(R.id.tv_total_duration);
        tv_distance = view.findViewById(R.id.tv_distance);
        tv_average_speed = view.findViewById(R.id.tv_average_speed);
        tv_max_speed = view.findViewById(R.id.tv_max_speed);
        tv_average_pace = view.findViewById(R.id.tv_average_pace);
        tv_max_pace = view.findViewById(R.id.tv_max_pace);


        tv_duration.setText("" + training.getDuration());
        tv_total_duration.setText("" + training.getTotalDuration());
        tv_distance.setText("" + training.getTotalDistance() + " km");
        tv_average_speed.setText("" + training.getAvgSpeed() + " km/h");
        tv_max_speed.setText("" + training.getMaxSpeed() + " km/h");
        tv_average_pace.setText("" + training.getAvgPace());
        tv_max_pace.setText("" + training.getMaxPace());

//        Log.d("murad", speeds.toString());

        setUpGraph(speeds);
    }

    public void setUpGraph(HashMap<String, Double> speeds){
        ArrayList<Entry> speedEntries = new ArrayList<>();
        ArrayList<Entry> paceEntries = new ArrayList<>();

        float maxX = 0;
        float maxY = 0;

        Entry entry_zero = new Entry(0, 0);
        speedEntries.add(entry_zero);
        paceEntries.add(entry_zero);

        if (speeds != null){
            for (String key : speeds.keySet()){
                double speed = speeds.get(key);
                double pace = Utils.convertSpeedToMinPerKm(speed);
                float time = Float.parseFloat(key);

                maxX = Math.max(maxX, time);
                maxY = (float) Math.max(maxY, speed);

                Entry speedEntry = new Entry(time, (float) speed);
                Entry paceEntry = new Entry(time, (float) pace);

                speedEntries.add(speedEntry);
                paceEntries.add(paceEntry);
            }

        }

        /*speedEntries.sort(new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                return (o1.getX() < o2.getX() ? -1 :
                            (o1.getX() == o2.getX() ? 0 : 1));
            }
        });*/

        speedEntries.sort((o1, o2) -> Float.compare(o1.getX(), o2.getX()));

        LineDataSet speed_lineDataSet = new LineDataSet(speedEntries, "");
        speed_lineDataSet.setLabel("");

        Log.d(Utils.LOG_TAG, speedEntries.toString());

        speed_lineDataSet.setCircleColor(requireContext().getColor(R.color.colorAccent));
        speed_lineDataSet.setFillColor(requireContext().getColor(R.color.colorAccent));
        speed_lineDataSet.setDrawFilled(true);

        speed_lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        speed_lineDataSet.setDrawValues(false);
        speed_lineDataSet.setDrawCircles(false);



        paceEntries.sort((o1, o2) -> Float.compare(o1.getX(), o2.getX()));

        LineDataSet pace_lineDataSet = new LineDataSet(paceEntries, "");
        pace_lineDataSet.setLabel("");

        Log.d(Utils.LOG_TAG, paceEntries.toString());

        pace_lineDataSet.setCircleColor(requireContext().getColor(R.color.purple_500));
        pace_lineDataSet.setFillColor(requireContext().getColor(R.color.purple_500));
        pace_lineDataSet.setDrawFilled(true);

        pace_lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        pace_lineDataSet.setDrawValues(false);
        pace_lineDataSet.setDrawCircles(false);

        LineData lineData = new LineData(speed_lineDataSet, pace_lineDataSet);


        lineChart_speed.setData(lineData);
        lineChart_speed.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChart_speed.getAxisLeft().setAxisMinimum(0);

        lineChart_speed.getAxisRight().setDrawGridLines(false);
//        lineChart_speed.getAxisLeft().setDrawGridLines(false);
        lineChart_speed.getXAxis().setDrawGridLines(false);
        lineChart_speed.animateXY(2000, 2000);
        lineChart_speed.getDescription().setEnabled(false);

    }

}