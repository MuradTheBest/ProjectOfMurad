package com.example.projectofmurad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.tracking.Training;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainingsAdapter extends RecyclerView.Adapter<TrainingsAdapter.TrainingsViewHolder> {

    private HashMap<String, ArrayList<Training>> trainings;
    private ArrayList<UserAndTraining> userAndTrainingArrayList;

    private OnTrainingClickListener onTrainingClickListener;

    private Context context;

    public TrainingsAdapter(Context context, ArrayList<UserAndTraining> userAndTrainingArrayList, OnTrainingClickListener onTrainingClickListener) {
        super();
        this.context = context;
        this.userAndTrainingArrayList = userAndTrainingArrayList;
        this.onTrainingClickListener = onTrainingClickListener;

    }

    public TrainingsAdapter(HashMap<String, ArrayList<Training>> trainings) {
        super();
        this.trainings = trainings;
    }

    @NonNull
    @Override
    public TrainingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_user_and_training, parent, false);

        return new TrainingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingsViewHolder holder, @SuppressLint("RecyclerView") int position) {
/*        String[] keys = (String[]) trainings.keySet().toArray();

        Log.d("murad", "size = " + trainings.get(keys[position]).size());
        Log.d("murad", trainings.get(keys[position]).toString());*/


        FirebaseUtils.usersDatabase.get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        String profile_picture = task.getResult().child(userAndTrainingArrayList.get(position).getUID()).child("profile_picture").getValue(String.class);

                        Glide.with(context).load(profile_picture).centerCrop().into(holder.iv_profile_picture);
                    }
                });

        ArrayList<Training> trainings = userAndTrainingArrayList.get(position).getTraining();

        TrainingAdapter trainingAdapter = new TrainingAdapter(context, trainings);
        holder.rv_training.setAdapter(trainingAdapter);
        holder.rv_training.setLayoutManager(new LinearLayoutManagerWrapper(context));
        holder.rv_training.addOnItemTouchListener(new RVOnItemTouchListenerForVP2(holder.rv_training,
                MainViewModel.getToSwipeViewModelForTrainings()));


        String UID = userAndTrainingArrayList.get(position).getUID();
        String eventPrivateId = userAndTrainingArrayList.get(position).getEventPrivateId();

        FirebaseUtils.trainingsDatabase.child("Events").child(eventPrivateId).child(UID).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            return;
                        }

                        ArrayList<BarEntry> speedEntries = new ArrayList<>();
                        ArrayList<BarEntry> paceEntries = new ArrayList<>();
                        ArrayList<String> paceText = new ArrayList<>();

//                        String[] dates = new String[(int) snapshot.getChildrenCount()];

                        ArrayList<String> dates = new ArrayList<>();

                        int i = 0;

                        for (DataSnapshot training : snapshot.getChildren()) {
                            Training t = training.getValue(Training.class);
                            double avgSpeed = t.getAvgSpeed();

                            double avgPace = Utils.convertSpeedToMinPerKm(avgSpeed);

                            BarEntry speedEntry = new BarEntry((float) i, (float) avgSpeed, t);
                            BarEntry paceEntry = new BarEntry((float) i, (float) avgPace, t);

                            speedEntries.add(speedEntry);
                            paceEntries.add(paceEntry);

                            paceText.add(Utils.convertSpeedToPace(avgPace));

                            dates.add(t.getDateTime());

                            i++;
                        }

//                        speedEntries.sort((o1, o2) -> Float.compare(o1.getX(), o2.getX()));

                        BarDataSet speedLineDataSet = new BarDataSet(speedEntries, "Average speed");
                        speedLineDataSet.setColor(context.getColor(R.color.colorAccent));

                        Log.d(Utils.LOG_TAG, speedEntries.toString());


                        speedLineDataSet.setDrawValues(true);

//                        paceEntries.sort((o1, o2) -> Float.compare(o1.getX(), o2.getX()));

                        BarDataSet paceLineDataSet = new BarDataSet(paceEntries, "Average pace");
                        paceLineDataSet.setColor(context.getColor(R.color.purple_500));

                        paceLineDataSet.setValueFormatter(new IndexAxisValueFormatter(paceText));

                        holder.bc_average_speed.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates));

                        Log.d(Utils.LOG_TAG, paceEntries.toString());

                        paceLineDataSet.setDrawValues(true);

                        BarData barData = new BarData(speedLineDataSet/*, paceLineDataSet*/);
                        barData.setHighlightEnabled(false);

                        holder.bc_average_speed.setData(barData);
                        holder.bc_average_speed.animateY(2000);
                        holder.bc_average_speed.getDescription().setEnabled(false);


                        holder.bc_average_speed.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                        holder.bc_average_speed.getAxisLeft().setAxisMinimum(0);

                        // below line is to set center axis
                        // labels to our bar chart.
                        holder.bc_average_speed.getXAxis().setCenterAxisLabels(false);


                        // below line is to set granularity
                        // to our x axis labels.
                        holder.bc_average_speed.getXAxis().setGranularity(1);

                        // below line is to enable
                        // granularity to our x axis.
                        holder.bc_average_speed.getXAxis().setGranularityEnabled(true);

                        // below line is to make our
                        // bar chart as draggable.
                        holder.bc_average_speed.setDragEnabled(true);

                        // below line is to make visible
                        // range for our bar chart.
                        holder.bc_average_speed.setVisibleXRangeMaximum(3);

                        holder.bc_average_speed.getAxisRight().setDrawGridLines(false);
                        holder.bc_average_speed.getAxisRight().setEnabled(false);
                        holder.bc_average_speed.getXAxis().setDrawGridLines(false);
                        holder.bc_average_speed.animateXY(2000, 2000);
                        holder.bc_average_speed.getDescription().setEnabled(false);

                        holder.bc_average_speed.setXAxisRenderer(new CustomXAxisRenderer(holder.bc_average_speed.getViewPortHandler(),
                                holder.bc_average_speed.getXAxis(), holder.bc_average_speed.getTransformer(YAxis.AxisDependency.LEFT)));

                        holder.bc_average_speed.setExtraBottomOffset(50f);

//                        holder.bc_average_speed.groupBars(0, 0.5f, 0.1f);

                        holder.bc_average_speed.invalidate();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


/*
        holder.bc_average_speed.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                if (holder.bc_average_speed.isPressed()){
                }

                    Training_Info_DialogFragment training_info_dialogFragment = new Training_Info_DialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Training.KEY_TRAINING, (Training) e.getData());
                    training_info_dialogFragment.setArguments(bundle);

                    FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();

                    training_info_dialogFragment.show(fm, Training_Info_DialogFragment.TAG);

            }

            @Override
            public void onNothingSelected() {

            }
        });
*/

        holder.bc_average_speed.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me,
                                            ChartTouchListener.ChartGesture lastPerformedGesture) {}

            @Override
            public void onChartGestureEnd(MotionEvent me,
                                          ChartTouchListener.ChartGesture lastPerformedGesture) {}

            @Override
            public void onChartLongPressed(MotionEvent me) {

                Log.d(Utils.LOG_TAG, "chart is pressed");

                MPPointD point = holder.bc_average_speed.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(me.getX(), me.getY());

                float xValue = (float) point.x;
                float yValue = (float) point.y;

                Log.d(Utils.LOG_TAG, "me.getX() = " + me.getX());
                Log.d(Utils.LOG_TAG, "me.getY() = " + me.getY());
                Log.d(Utils.LOG_TAG, "xValue = " + xValue);
                Log.d(Utils.LOG_TAG, "yValue = " + yValue);



                Entry e = holder.bc_average_speed.getEntryByTouchPoint(xValue, yValue);

                if (e != null){
                    Log.d(Utils.LOG_TAG, ((Training) e.getData()).toString());

                    Training_Info_DialogFragment training_info_dialogFragment = new Training_Info_DialogFragment();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Training.KEY_TRAINING, (Training) e.getData());
//                training_info_dialogFragment.setArguments(bundle);

                    FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();

                    if (fm.findFragmentByTag(Training_Info_DialogFragment.TAG) == null){
                        training_info_dialogFragment.setArguments(bundle);
                        training_info_dialogFragment.show(fm, Training_Info_DialogFragment.TAG);

                    }

                }


            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {}

            @Override
            public void onChartSingleTapped(MotionEvent me) {}

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX,
                                     float velocityY) {}

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {}

        });

    }

    //class for renedering X Axis labels' data
    public static class CustomXAxisRenderer extends XAxisRenderer {

        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, @NonNull String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            String[] line = formattedLabel.split("\n");

//            com.github.mikephil.charting.utils.Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
//            com.github.mikephil.charting.utils.Utils.drawXAxisValue(c, line[1], x + mAxisLabelPaint.getTextSize(), y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
//            com.github.mikephil.charting.utils.Utils.drawXAxisValue(c, line[1], x, y + mAxisLabelPaint.getTextSize() + 5, mAxisLabelPaint, anchor, angleDegrees);

            for (int i = 0; i < line.length; i++) {
                com.github.mikephil.charting.utils.Utils.drawXAxisValue(c, line[i], x,
                        y + i*(mAxisLabelPaint.getTextSize() + 5), mAxisLabelPaint, anchor, angleDegrees);
            }

        }
    }

    @Override
    public int getItemCount() {
        return userAndTrainingArrayList.size();
    }

    public class TrainingsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView iv_profile_picture;
        RecyclerView rv_training;

        BarChart bc_average_speed;

        public TrainingsViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_profile_picture = itemView.findViewById(R.id.iv_profile_picture);
            rv_training = itemView.findViewById(R.id.rv_training);

            bc_average_speed = itemView.findViewById(R.id.bc_average_speed);
        }


    }

    public interface OnTrainingClickListener{
        void onTrainingClick(int position);
    }
}
