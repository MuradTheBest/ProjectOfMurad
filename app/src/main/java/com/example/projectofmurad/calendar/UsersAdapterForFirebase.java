package com.example.projectofmurad.calendar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.UserData;
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
    private final OnUserExpandListener onUserExpandListener;

    private FirebaseRecyclerOptions<UserData> options;
    private Context context;

    private int oldPosition = -1;

    public UsersAdapterForFirebase(@NonNull FirebaseRecyclerOptions<UserData> options, String event_private_id, Context context, OnUserListener onUserListener, OnUserExpandListener onUserExpandListener) {

        super(options);
        this.userDataArrayList = options.getSnapshots();
        this.event_private_id = event_private_id;
        this.onUserListener = onUserListener;
        this.onUserExpandListener = onUserExpandListener;

        this.context = context;

    }

    public class UserViewHolderForFirebase extends RecyclerView.ViewHolder implements
            CompoundButton.OnCheckedChangeListener, View.OnClickListener, View.OnLongClickListener {

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

        public UserViewHolderForFirebase(@NonNull View itemView, OnUserListener onEventListener) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            ll_contact = itemView.findViewById(R.id.ll_contact);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            iv_profile_picture = itemView.findViewById(R.id.iv_profile_picture);
//            shimmer_profile_picture = itemView.findViewById(R.id.shimmer_profile_picture);

            tv_username = itemView.findViewById(R.id.tv_username);
            tv_user_phone = itemView.findViewById(R.id.tv_user_phone);

            checkbox_attendance = itemView.findViewById(R.id.checkbox_attendance);
            checkbox_attendance.setOnCheckedChangeListener(this);

            iv_phone = itemView.findViewById(R.id.iv_phone);
            iv_phone.setOnClickListener(this);
            iv_email = itemView.findViewById(R.id.iv_email);
            iv_email.setOnClickListener(this);
            iv_message = itemView.findViewById(R.id.iv_message);
            iv_message.setOnClickListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == checkbox_attendance){
                FirebaseUtils.attendanceDatabase.child(event_private_id)
                        .child(FirebaseUtils.getCurrentUID()).child("attend").setValue(isChecked);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView){
                expanded = !expanded;

                if(expanded){
                    ll_contact.setVisibility(View.VISIBLE);
                    if (getAbsoluteAdapterPosition() != oldPosition){
                        onUserExpandListener.onUserExpand(getAbsoluteAdapterPosition(), oldPosition);
                    }
                    oldPosition = getAbsoluteAdapterPosition();

                    animateLayout(constraintLayout, 300, Gravity.BOTTOM);
                }
                else {
                    ll_contact.setVisibility(View.GONE);

                   /* ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.tv_event_place, ConstraintSet.TOP, R.id.tv_event_description, ConstraintSet.BOTTOM);
                    //constraintSet.applyTo(constraintLayout);*/

                    animateLayout(constraintLayout, 300, Gravity.TOP);
                }
            }

            switch (v.getId()) {
                case R.id.iv_phone: {
                    String phoneNumber = getItem(getAbsoluteAdapterPosition()).getPhone();

//                onCallListener.OnCall(getBindingAdapterPosition(), phoneNumber);

                    if (phoneNumber == null || phoneNumber.isEmpty()) {
                        Toast.makeText(context, "This user has no registered phone number",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + phoneNumber));//change the number
                        context.startActivity(callIntent);
                    }
                    break;
                }
                case R.id.iv_email:
                    String email = getItem(getBindingAdapterPosition()).getEmail();
                    if (email == null || email.isEmpty() || /*!getItem(getAbsoluteAdapterPosition()).isEmailVerified()*/
                            !FirebaseUtils.getCurrentFirebaseUser().isEmailVerified()){
                        Toast.makeText(context, "This user has no verified email address",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String[] emails = {email};
//                onEmailListener.OnEmail(getBindingAdapterPosition(), emails[0]);
                        Intent intent_email = new Intent(Intent.ACTION_SEND);
                        intent_email.setType("text/plain");
                        intent_email.putExtra(Intent.EXTRA_EMAIL, emails);
                        intent_email.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        intent_email.putExtra(Intent.EXTRA_TEXT, "this is the email body");
//                context.startActivity(Intent.createChooser(intent_email, "send email"));
                        context.startActivity(intent_email);
                    }

                    break;
                case R.id.iv_message:

                    String phoneNumber = getItem(getAbsoluteAdapterPosition()).getPhone();

                    if (phoneNumber == null || phoneNumber.isEmpty()) {
                        Toast.makeText(context, "This user has no registered phone number",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent_sms = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("sms:" + phoneNumber));
//                        context.startActivity(intent_sms);
                        context.startActivity(Intent.createChooser(intent_sms, "Choose app"));
                    }

                    break;

            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (v == itemView){
                onUserListener.onUserClick(getBindingAdapterPosition(), getItem(getBindingAdapterPosition()));
            }
            return false;
        }

        public void animateLayout(ViewGroup layout, long duration, int direction) {
            AutoTransition trans = new AutoTransition();
            trans.setDuration(duration);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());
            //trans.setInterpolator(new DecelerateInterpolator());
            //trans.setInterpolator(new FastOutSlowInInterpolator());
            TransitionManager.beginDelayedTransition(layout, trans);

            /*Slide slide = new Slide(direction);
            slide.setInterpolator(new AccelerateDecelerateInterpolator());
            slide.setDuration(500);
            TransitionManager.beginDelayedTransition(layout, slide);*/

            /*ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(duration);
            changeBounds.setInterpolator(new AccelerateDecelerateInterpolator());

            TransitionManager.beginDelayedTransition(layout, changeBounds);*/
        }
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolderForFirebase holder, int position, @NonNull UserData model) {

        final int posRevers = getItemCount() - (position + 1);

        Log.d("murad", "RECYCLING STARTED");

        holder.tv_username.setText(model.getUsername());
        Log.d("murad","name: " + model.getUsername());

        holder.tv_user_phone.setText(model.getPhone());
        Log.d("murad","phone: " + model.getPhone());

        String UID = model.getUID();

        if (model.isMadrich()){
            Log.d("murad", model.getUID() + " is madrich" + true);
            holder.iv_profile_picture.getLayoutParams().height = 110;
            holder.iv_profile_picture.getLayoutParams().width = 110;

            holder.iv_profile_picture.setBorderColor(context.getResources().getColor(R.color.colorAccent));
            holder.iv_profile_picture.setBorderWidth(4);
        }

        if (event_private_id != null){
            if(getItem(position).getUID().equals(FirebaseUtils.getCurrentUID())){
                holder.checkbox_attendance.setEnabled(true);
            }

            Log.d("murad", "position = " + position);

            DatabaseReference ref = FirebaseUtils.attendanceDatabase.child(event_private_id).child(UID).child("attend");

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
        }
        else {
            holder.checkbox_attendance.setVisibility(View.GONE);
        }

        FirebaseUtils.getProfilePictureFromFB(UID, context, holder.iv_profile_picture, /*holder.shimmer_profile_picture*/ null);

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

    public interface OnCallListener {
        void OnCall(int position, String phone);
    }

    public interface OnEmailListener {
        void OnEmail(int position, String email);
    }

    public interface OnMessageListener {
        void OnMessage(int position, String phone);
    }

    public interface OnUserExpandListener {
        void onUserExpand(int position, int oldPosition);
    }

    public interface OnAttendListener {
        void onAttendCheck(int position, boolean isChecked);
    }

    @Override
    public int getItemCount() {
        return getSnapshots().size();
    }
}

