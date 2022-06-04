package com.example.projectofmurad.training;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
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
import com.example.projectofmurad.helpers.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.google.android.material.button.MaterialButton;

public class TrainingAdapterForFirebase extends
        FirebaseRecyclerAdapter<Training, TrainingAdapterForFirebase.TrainingViewHolder> {
    /**
     * Construct a new FirestorePagingAdapter from the given {@link DatabasePagingOptions}.
     *
     * @param options
     */

    private final Context context;
    private final int color;

    public TrainingAdapterForFirebase(@NonNull FirebaseRecyclerOptions<Training> options, Context context, int color) {
        super(options);

        this.color = color;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull TrainingAdapterForFirebase.TrainingViewHolder holder,
                                    int position, @NonNull Training model) {

        int textColor = Utils.getContrastColor(color);

        GradientDrawable gd = Utils.getGradientBackground(color);

        holder.constraintLayout.setBackground(gd);

        holder.tv_distance.setText(model.getTotalDistance() + " km");
        holder.tv_time.setText(model.getDuration());
        holder.tv_speed.setText(model.getAvgSpeed() + " km/h");
        holder.tv_date.setText(model.getDate());

        holder.iv_bike.setIconTint(ColorStateList.valueOf(textColor));

        holder.tv_distance.setTextColor(textColor);
        holder.tv_time.setTextColor(textColor);
        holder.tv_speed.setTextColor(textColor);
        holder.tv_date.setTextColor(textColor);
    }

    @NonNull
    @Override
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_training, parent, false);

        return new TrainingViewHolder(view);
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

                if (fm.findFragmentByTag(TrainingInfoDialogFragment.TAG) == null){
                    TrainingInfoDialogFragment training_info_dialogFragment = TrainingInfoDialogFragment.newInstance(getItem(getAbsoluteAdapterPosition()));
                    training_info_dialogFragment.show(fm, TrainingInfoDialogFragment.TAG);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return getSnapshots().size();
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
