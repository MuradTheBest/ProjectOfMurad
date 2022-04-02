package com.example.projectofmurad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.tracking.Training;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

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



    }

    @Override
    public int getItemCount() {
        return userAndTrainingArrayList.size();
    }

    public class TrainingsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView iv_profile_picture;
        RecyclerView rv_training;

        public TrainingsViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_profile_picture = itemView.findViewById(R.id.iv_profile_picture);
            rv_training = itemView.findViewById(R.id.rv_training);
        }


    }

    public interface OnTrainingClickListener{
        void onTrainingClick(int position);
    }
}
