package com.example.projectofmurad.training;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.SuperUserTraining;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainingAdapterForFirebase extends
        FirebaseRecyclerAdapter<SuperUserTraining, TrainingAdapterForFirebase.TrainingViewHolder> {
    /**
     * Construct a new FirestorePagingAdapter from the given {@link DatabasePagingOptions}.
     *
     * @param options
     */
    public TrainingAdapterForFirebase(@NonNull FirebaseRecyclerOptions<SuperUserTraining> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TrainingAdapterForFirebase.TrainingViewHolder viewHolder,
                                    int position, @NonNull SuperUserTraining model) {

        viewHolder.tv_username.setText(model.getTrainings().toString());
//        viewHolder.tv_user_phone.setText(model.getTraining().toString());
    }

    @NonNull
    @Override
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_user_and_training, parent, false);

        return new TrainingViewHolder(view);
    }

    public static class TrainingViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout constraintLayout;
        public LinearLayout ll_contact;

        private CircleImageView iv_profile_picture;
//        private ShimmerFrameLayout shimmer_profile_picture ;

        private CheckBox checkbox_attendance;

        private TextView tv_username;
        private TextView tv_user_phone;

        private ImageView iv_phone;
        private ImageView iv_email;
        private ImageView iv_message;

        public boolean expanded = false;

        public TrainingViewHolder(@NonNull View itemView) {
            super(itemView);

            ll_contact = itemView.findViewById(R.id.ll_contact);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            iv_profile_picture = itemView.findViewById(R.id.iv_profile_picture);
//            shimmer_profile_picture = itemView.findViewById(R.id.shimmer_profile_picture);

            tv_username = itemView.findViewById(R.id.tv_username);
            tv_user_phone = itemView.findViewById(R.id.tv_user_phone);

            checkbox_attendance = itemView.findViewById(R.id.checkbox_attendance);

            iv_phone = itemView.findViewById(R.id.iv_phone);

            iv_email = itemView.findViewById(R.id.iv_email);

            iv_message = itemView.findViewById(R.id.iv_message);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }
}
