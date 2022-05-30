package com.example.projectofmurad.training;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Show;
import com.example.projectofmurad.groups.UserGroupData;
import com.example.projectofmurad.helpers.CalendarUtils;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.MyAlertDialogBuilder;
import com.example.projectofmurad.helpers.Utils;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainingsAdapterForFirebase extends RecyclerView.Adapter<TrainingsAdapterForFirebase.TrainingsViewHolder> {

    private final ArrayList<String> UIDs;

    private final Context context;

    private final int color;
    private final String event_private_id;

    private final OnShowToOthersListener onShowToOthersListener;

    public TrainingsAdapterForFirebase(Context context, ArrayList<String> UIDs, String event_private_id, int color, OnShowToOthersListener onShowToOthersListener) {
        super();
        this.context = context;
        this.color = color;
        this.UIDs = UIDs;
        this.onShowToOthersListener = onShowToOthersListener;
        this.event_private_id = event_private_id;
    }

    @NonNull
    @Override
    public TrainingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_and_training, parent, false);

        return new TrainingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String UID = UIDs.get(position);

        int textColor = Utils.getContrastColor(color);

        GradientDrawable gd = Utils.getGradientBackground(color);

        if (FirebaseUtils.isCurrentUID(UID)){
            gd.setStroke(Utils.dpToPx(4, context), FirebaseUtils.CURRENT_GROUP_COLOR);
        }

        gd.setCornerRadius(Utils.dpToPx(10, context));
        holder.constraintLayout.setBackground(gd);

        holder.switch_choose_visibility.setTextColor(textColor);

        FirebaseUtils.getProfilePictureFromFB(UID, context, holder.iv_profile_picture);

        holder.switch_choose_visibility.setVisibility(FirebaseUtils.isCurrentUID(UID) ? View.VISIBLE : View.GONE);

        if (FirebaseUtils.isCurrentUID(UID)){
            FirebaseUtils.getCurrentUserTrainingsRefForEvent(event_private_id).getParent().child("show").get()
                    .addOnSuccessListener(ds -> holder.switch_choose_visibility.setTag(ds.getValue(int.class)));
        }

        FirebaseUtils.getUserTrainingsForEvent(UID, event_private_id).observe(
                (LifecycleOwner) context,
                new Observer<ArrayList<Training>>() {
                    @Override
                    public void onChanged(ArrayList<Training> trainings) {
                        TrainingAdapter trainingAdapter = new TrainingAdapter(context, color, trainings);
                        holder.rv_training.setAdapter(trainingAdapter);
                        holder.rv_training.setLayoutManager(new LinearLayoutManagerWrapper(context));
                        setupGraph(trainings, holder.bc_average_speed, textColor);
                    }
                });

        FirebaseUtils.getCurrentGroupUsers().child(UID).child(UserGroupData.KEY_MADRICH).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue(boolean.class)) {
                            holder.iv_profile_picture.getLayoutParams().height = Utils.dpToPx(55, context);
                            holder.iv_profile_picture.getLayoutParams().width = Utils.dpToPx(55, context);

                            holder.iv_profile_picture.setBorderColor(context.getColor(R.color.colorAccent));
                            holder.iv_profile_picture.setBorderWidth(Utils.dpToPx(2, context));

                            CalendarUtils.animate(holder.constraintLayout);
                        }
                    }
                });

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
                    Log.d(Utils.LOG_TAG, e.getData().toString());

                    FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();

                    if (fm.findFragmentByTag(TrainingInfoDialogFragment.TAG) == null){
                        TrainingInfoDialogFragment training_info_dialogFragment = TrainingInfoDialogFragment.newInstance((Training) e.getData());
                        training_info_dialogFragment.show(fm, TrainingInfoDialogFragment.TAG);
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

    public void setupGraph(@NonNull ArrayList<Training> trainings, BarChart bc_average_speed, int textColor){
        ArrayList<BarEntry> speedEntries = new ArrayList<>();
        ArrayList<BarEntry> paceEntries = new ArrayList<>();
        ArrayList<String> paceText = new ArrayList<>();

        ArrayList<String> dates = new ArrayList<>();

        int i = 0;

        for (Training t : trainings) {
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
        speedLineDataSet.setValueTextColor(textColor);
//                        speedLineDataSet.setGradientColor(gradientColor, context.getColor(R.color.colorAccent));

        Log.d(Utils.LOG_TAG, speedEntries.toString());


        speedLineDataSet.setDrawValues(true);

//                        paceEntries.sort((o1, o2) -> Float.compare(o1.getX(), o2.getX()));

        BarDataSet paceLineDataSet = new BarDataSet(paceEntries, "Average pace");
        paceLineDataSet.setColor(context.getColor(R.color.purple_500));

        paceLineDataSet.setValueFormatter(new IndexAxisValueFormatter(paceText));

        bc_average_speed.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates));

        Log.d(Utils.LOG_TAG, paceEntries.toString());

        paceLineDataSet.setDrawValues(true);

        BarData barData = new BarData(speedLineDataSet/*, paceLineDataSet*/);
        barData.setHighlightEnabled(false);
        barData.setValueTextColor(textColor);

        bc_average_speed.setData(barData);
        bc_average_speed.animateY(2000);
        bc_average_speed.getDescription().setEnabled(false);

        bc_average_speed.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        bc_average_speed.getAxisLeft().setAxisMinimum(0);

        // below line is to set center axis
        // labels to our bar chart.
        bc_average_speed.getXAxis().setCenterAxisLabels(false);


        // below line is to set granularity
        // to our x axis labels.
        bc_average_speed.getXAxis().setGranularity(1);

        // below line is to enable
        // granularity to our x axis.
        bc_average_speed.getXAxis().setGranularityEnabled(true);

        // below line is to make our
        // bar chart as draggable.
        bc_average_speed.setDragEnabled(true);

        // below line is to make visible
        // range for our bar chart.
        bc_average_speed.setVisibleXRangeMaximum(3);

        bc_average_speed.getAxisRight().setDrawGridLines(false);
        bc_average_speed.getAxisRight().setEnabled(false);
        bc_average_speed.getXAxis().setDrawGridLines(false);
        bc_average_speed.animateXY(2000, 2000);
        bc_average_speed.getDescription().setEnabled(false);

        bc_average_speed.setXAxisRenderer(new CustomXAxisRenderer(bc_average_speed.getViewPortHandler(),
                bc_average_speed.getXAxis(), bc_average_speed.getTransformer(YAxis.AxisDependency.LEFT)));

        bc_average_speed.setExtraBottomOffset(50f);

//                        bc_average_speed.groupBars(0, 0.5f, 0.1f);

        bc_average_speed.invalidate();
    }

    //class for rendering X Axis labels' data
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

    public class TrainingsViewHolder extends RecyclerView.ViewHolder{

        private final ConstraintLayout constraintLayout;

        private final CircleImageView iv_profile_picture;
        private final RecyclerView rv_training;

        private final BarChart bc_average_speed;

        private final SwitchMaterial switch_choose_visibility;

        public TrainingsViewHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            iv_profile_picture = itemView.findViewById(R.id.iv_profile_picture);
            rv_training = itemView.findViewById(R.id.rv_training);

            bc_average_speed = itemView.findViewById(R.id.bc_average_speed);

            switch_choose_visibility = itemView.findViewById(R.id.switch_choose_visibility);
            switch_choose_visibility.setOnClickListener(v -> createChooseVisibilityDialog());
        }

        public void createChooseVisibilityDialog() {
            MyAlertDialogBuilder builder = new MyAlertDialogBuilder(context);

            builder.setTitle("Make your results visible to");

            builder.setSingleChoiceItems(new CharSequence[]{Show.NO_ONE.toString(), "Madrichs only", "Everyone"},
                    (int) switch_choose_visibility.getTag(),
                    (dialog, which) -> {
                        switch_choose_visibility.setTag(which);
                        Log.d("snapshot", "switch_choose_visibility.getTag() = " + switch_choose_visibility.getTag());
                        switch_choose_visibility.setChecked((int) switch_choose_visibility.getTag() > Show.NO_ONE.getValue());
                        switch_choose_visibility.setText("Visible to " + Show.values()[which].toString());
                        onShowToOthersListener.onShowToOthers(which);
                        dialog.dismiss();
                    });

            builder.setOnDismissListener(dialog -> {
                Log.d("snapshot", "switch_choose_visibility.getTag() = " + switch_choose_visibility.getTag());
                switch_choose_visibility.setChecked((int) switch_choose_visibility.getTag() > Show.NO_ONE.getValue());
            });

            builder.show();
        }
    }

    public interface OnShowToOthersListener {
        void onShowToOthers(int toWho);
    }

    @Override
    public int getItemCount() {
        return UIDs.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
