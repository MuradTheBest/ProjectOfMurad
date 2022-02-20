package com.example.projectofmurad.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.UserData;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapterForFirebase extends
        FirebaseRecyclerAdapter<UserData, UsersAdapterForFirebase.UserViewHolderForFirebase> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private String event_private_id;
    private ObservableSnapshotArray<UserData> userDataArrayList;
    private final OnUserListener onUserListener;
    private FirebaseRecyclerOptions<UserData> options;
    private Context context;

    public UsersAdapterForFirebase(@NonNull FirebaseRecyclerOptions<UserData> options, String event_private_id, Context context, OnUserListener onUserListener) {
        super(options);
        this.userDataArrayList = options.getSnapshots();
        this.event_private_id = event_private_id;
        this.onUserListener = onUserListener;
        this.context = context;

    }

    public class UserViewHolderForFirebase extends RecyclerView.ViewHolder implements
            CompoundButton.OnCheckedChangeListener, View.OnClickListener {

        private ConstraintLayout constraintLayout;

        private CircleImageView iv_profile_picture;
        private ShimmerFrameLayout shimmer_profile_picture ;

        private CheckBox checkbox_attendance;

        private TextView tv_username;
        private TextView tv_user_phone;

        public UserViewHolderForFirebase(@NonNull View itemView, OnUserListener onEventListener) {
            super(itemView);

            itemView.setOnClickListener(this);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            iv_profile_picture = itemView.findViewById(R.id.iv_profile_picture);
            shimmer_profile_picture = itemView.findViewById(R.id.shimmer_profile_picture);

            tv_username = itemView.findViewById(R.id.tv_username);
            tv_user_phone = itemView.findViewById(R.id.tv_user_phone);

            checkbox_attendance = itemView.findViewById(R.id.checkbox_attendance);
            checkbox_attendance.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == checkbox_attendance){
                FirebaseUtils.attendanceDatabase.child(event_private_id)
                        .child(FirebaseUtils.getCurrentUID()).setValue(isChecked);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView){
                onUserListener.onUserClick(getBindingAdapterPosition(), getItem(getBindingAdapterPosition()));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolderForFirebase holder, int position, @NonNull UserData model) {
        Log.d("murad", "RECYCLING STARTED");

        holder.tv_username.setText(model.getUsername());
        Log.d("murad","name: " + model.getUsername());

        holder.tv_user_phone.setText(model.getPhone());
        Log.d("murad","phone: " + model.getPhone());

        String UID = model.getUID();

        if (model.isMadrich()){
            holder.iv_profile_picture.getLayoutParams().height = 120;
            holder.iv_profile_picture.getLayoutParams().width = 120;

            holder.iv_profile_picture.setBorderColor(context.getResources().getColor(R.color.colorAccent));
            holder.iv_profile_picture.setBorderWidth(4);
        }

        if(getItem(position).getUID().equals(FirebaseUtils.getCurrentUID())){
            holder.checkbox_attendance.setEnabled(true);
        }

        Log.d("murad", "position = " + position);

        DatabaseReference ref = FirebaseUtils.attendanceDatabase.child(event_private_id).child(UID);

        final boolean[] attend = {false};
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()){
                    attend[0] = task.getResult().getValue(boolean.class);
                    holder.checkbox_attendance.setChecked(attend[0]);
                }
            }
        });

        holder.shimmer_profile_picture.startShimmer();

        FirebaseUtils.getProfilePictureFromFB(UID, context, holder.iv_profile_picture, holder.shimmer_profile_picture);

    }

    @NonNull
    @Override
    public UserViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_info_expanded_with_card_view, parent, false);

        return new UserViewHolderForFirebase(view, onUserListener);
    }

    public interface OnUserListener {
        void onUserClick(int position, UserData userData);
    }

    public interface OnAttendListener {
        void onAttendCheck(int position, boolean isChecked);
    }

    @Override
    public int getItemCount() {
        return getSnapshots().size();
    }
}

