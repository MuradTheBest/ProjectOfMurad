package com.example.projectofmurad.training;

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

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.Utils;
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

    public final static String TAG = "Training_Info_DialogFragment";

    private Training training;

    public Training_Info_DialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param training Training which details are shown.
     *
     * @return A new instance of fragment Training_Info_DialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static Training_Info_DialogFragment newInstance(Training training) {
        Training_Info_DialogFragment fragment = new Training_Info_DialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(Training.KEY_TRAINING, training);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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


    private TextView tv_name;
    private TextView tv_start_date;
    private TextView tv_time;
    private TextView tv_duration;
    private TextView tv_distance;
    private TextView tv_pace;


    private TextView tv_training_duration;
    private TextView tv_training_total_duration;
    private TextView tv_training_distance;
    private TextView tv_training_average_speed;
    private TextView tv_training_max_speed;
    private TextView tv_training_average_pace;
    private TextView tv_training_max_pace;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart_speed = view.findViewById(R.id.lineChart_speed);
        speeds = training.getSpeeds();


        tv_name = view.findViewById(R.id.tv_name);
        tv_start_date = view.findViewById(R.id.tv_start_date);
        tv_time = view.findViewById(R.id.tv_time);
        tv_duration = view.findViewById(R.id.tv_duration);
        tv_distance = view.findViewById(R.id.tv_distance);
        tv_pace = view.findViewById(R.id.tv_pace);


        tv_name.setText(training.getName());
        tv_start_date.setText(training.getStartDate());
        tv_time.setText(training.getStartTime() + " - " + training.getEndTime());
        tv_duration.setText("" + training.getDuration());
        tv_distance.setText("" + training.getTotalDistance() + " km");
        tv_pace.setText(training.getAvgPace());


        tv_training_duration = view.findViewById(R.id.tv_training_duration);
        tv_training_total_duration = view.findViewById(R.id.tv_training_total_duration);
        tv_training_distance = view.findViewById(R.id.tv_training_distance);
        tv_training_average_speed = view.findViewById(R.id.tv_training_average_speed);
        tv_training_max_speed = view.findViewById(R.id.tv_training_max_speed);
        tv_training_average_pace = view.findViewById(R.id.tv_training_average_pace);
        tv_training_max_pace = view.findViewById(R.id.tv_training_max_pace);


        tv_training_duration.setText("" + training.getDuration());
        tv_training_total_duration.setText("" + training.getTotalDuration());
        tv_training_distance.setText("" + training.getTotalDistance() + " km");
        tv_training_average_speed.setText("" + training.getAvgSpeed() + " km/h");
        tv_training_max_speed.setText("" + training.getMaxSpeed() + " km/h");
        tv_training_average_pace.setText("" + training.getAvgPace());
        tv_training_max_pace.setText("" + training.getMaxPace());

//        Log.d("murad", speedsData.toString());

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
                float time = Float.parseFloat(key.replace(" sec", ""));

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

        LineDataSet speedLineDataSet = new LineDataSet(speedEntries, "Speed");
        speedLineDataSet.setLabel("");

        Log.d(Utils.LOG_TAG, speedEntries.toString());

        speedLineDataSet.setCircleColor(requireContext().getColor(R.color.colorAccent));
        speedLineDataSet.setFillColor(requireContext().getColor(R.color.colorAccent));
        speedLineDataSet.setDrawFilled(true);

        speedLineDataSet.setMode(LineDataSet.Mode.LINEAR);
        speedLineDataSet.setDrawValues(false);
        speedLineDataSet.setDrawCircles(false);



        paceEntries.sort((o1, o2) -> Float.compare(o1.getX(), o2.getX()));

        LineDataSet paceLineDataSet = new LineDataSet(paceEntries, "Pace");
        paceLineDataSet.setLabel("");

        Log.d(Utils.LOG_TAG, paceEntries.toString());

        paceLineDataSet.setCircleColor(requireContext().getColor(R.color.purple_500));
        paceLineDataSet.setFillColor(requireContext().getColor(R.color.purple_500));
        paceLineDataSet.setDrawFilled(true);

        paceLineDataSet.setMode(LineDataSet.Mode.LINEAR);
        paceLineDataSet.setDrawValues(false);
        paceLineDataSet.setDrawCircles(false);

        LineData lineData = new LineData(speedLineDataSet, paceLineDataSet);


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