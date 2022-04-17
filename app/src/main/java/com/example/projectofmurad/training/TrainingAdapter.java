package com.example.projectofmurad.training;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;

import java.util.ArrayList;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder> {

    private final Context context;

    private final ArrayList<Training> trainingArrayList;

    private OnTrainingClickListener onTrainingClickListener;

    public TrainingAdapter(Context context, ArrayList<Training> trainingArrayList) {
        super();

        this.context = context;
        this.trainingArrayList = trainingArrayList;
    }

    @NonNull
    @Override
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_training, parent, false);

        return new TrainingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingViewHolder holder, int position) {
/*        String[] keys = (String[]) trainings.keySet().toArray();

        Log.d("murad", "size = " + trainings.get(keys[position]).size());
        Log.d("murad", trainings.get(keys[position]).toString());*/

        Training training = trainingArrayList.get(position);
        Log.d("murad", "trainingArrayList.size() = " + trainingArrayList.size());
        Log.d("murad", trainingArrayList.toString());

        holder.tv_distance.setText(""+training.getTotalDistance());
        holder.tv_time.setText(training.getDuration());
        holder.tv_speed.setText(""+training.getAvgSpeed());
        holder.tv_date.setText(training.getDate());

        Log.d("murad", "trainingData.getTotalDistance() = " + training.getTotalDistance());
        Log.d("murad", "trainingData.getTime() = " + training.getTime());
        Log.d("murad", "trainingData.getAvgSpeed() = " + training.getAvgSpeed());
//        Log.d("murad", training.getStartDate());



    }

    @Override
    public int getItemCount() {
        return trainingArrayList.size();
    }

    public class TrainingViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        TextView tv_distance;
        TextView tv_time;
        TextView tv_speed;
        TextView tv_date;

        public TrainingViewHolder(@NonNull View itemView) {
            super(itemView);

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

                Training_Info_DialogFragment training_info_dialogFragment = Training_Info_DialogFragment.newInstance(trainingArrayList.get(getAbsoluteAdapterPosition()));
                training_info_dialogFragment.show(fm, Training_Info_DialogFragment.TAG);
            }
        }
    }

    public interface OnTrainingClickListener{
        void onTrainingClick(int position, Training training);
    }

}
