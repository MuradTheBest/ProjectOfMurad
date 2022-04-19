package com.example.projectofmurad.training;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.Utils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder> {

    private final Context context;

    private final ArrayList<Training> trainingArrayList;

    private final int color;

    public TrainingAdapter(Context context, int color, ArrayList<Training> trainingArrayList) {
        super();

        this.color = color;
        this.context = context;
        this.trainingArrayList = trainingArrayList;
    }

    @NonNull
    @Override
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_training, parent, false);

        return new TrainingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingViewHolder holder, int position) {
/*        String[] keys = (String[]) trainings.keySet().toArray();

        Log.d("murad", "size = " + trainings.get(keys[position]).size());
        Log.d("murad", trainings.get(keys[position]).toString());*/

        int textColor = Utils.getContrastColor(color);

        int gradientColor = (textColor == Color.WHITE) ? Color.LTGRAY : Color.DKGRAY;

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {color, color, gradientColor});

        gd.setShape(GradientDrawable.RECTANGLE);

        holder.constraintLayout.setBackground(gd);

        Training training = trainingArrayList.get(position);
        Log.d("murad", "trainingArrayList.size() = " + trainingArrayList.size());
        Log.d("murad", trainingArrayList.toString());


        holder.tv_distance.setText(training.getTotalDistance() + " km");
        holder.tv_time.setText(training.getDuration());
        holder.tv_speed.setText(training.getAvgSpeed() + " km/h");
        holder.tv_date.setText(training.getDate());

        holder.iv_bike.setIconTint(ColorStateList.valueOf(textColor));

        holder.tv_distance.setTextColor(textColor);
        holder.tv_time.setTextColor(textColor);
        holder.tv_speed.setTextColor(textColor);
        holder.tv_date.setTextColor(textColor);

        Log.d("murad", "trainingData.getTotalDistance() = " + training.getTotalDistance());
        Log.d("murad", "trainingData.getTime() = " + training.getTime());
        Log.d("murad", "trainingData.getAvgSpeed() = " + training.getAvgSpeed());
//        Log.d("murad", training.getStartDate());

    }

    @Override
    public int getItemCount() {
        return trainingArrayList.size();
    }

    public class TrainingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ConstraintLayout constraintLayout;

        private final MaterialButton iv_bike;

        private final TextView tv_distance;
        private final TextView tv_time;
        private final TextView tv_speed;
        private final TextView tv_date;

        public TrainingViewHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            iv_bike = itemView.findViewById(R.id.iv_bike);

            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_speed = itemView.findViewById(R.id.tv_speed);
            tv_date = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView){
                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();

                if (fm.findFragmentByTag(Training_Info_DialogFragment.TAG) == null){
                    Training_Info_DialogFragment training_info_dialogFragment = Training_Info_DialogFragment.newInstance(trainingArrayList.get(getAbsoluteAdapterPosition()));
                    training_info_dialogFragment.show(fm, Training_Info_DialogFragment.TAG);
                }
            }
        }
    }

    public interface OnTrainingClickListener{
        void onTrainingClick(int position, Training training);
    }

}
