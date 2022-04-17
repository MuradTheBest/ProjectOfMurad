package com.example.projectofmurad.calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
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
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.helpers.ViewAnimationUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapterForFirebase extends FirebaseRecyclerAdapter<UserData, UsersAdapterForFirebase.UserViewHolderForFirebase> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private final String event_private_id;
    private final OnUserClickListener onUserClickListener;
    private final OnUserExpandListener onUserExpandListener;

    private int color;

    private final Context context;

    private int oldPosition = -1;

    public UsersAdapterForFirebase(@NonNull FirebaseRecyclerOptions<UserData> options, Context context, String event_private_id, int color, OnUserClickListener onUserClickListener, OnUserExpandListener onUserExpandListener) {
        super(options);
        this.event_private_id = event_private_id;
        this.onUserClickListener = onUserClickListener;
        this.onUserExpandListener = onUserExpandListener;

        this.color = color;
        this.context = context;
    }

    public class UserViewHolderForFirebase extends RecyclerView.ViewHolder implements
            CompoundButton.OnCheckedChangeListener, View.OnClickListener, View.OnLongClickListener {

        private ConstraintLayout constraintLayout;
        public LinearLayout ll_contact;

        private final CircleImageView iv_profile_picture;
//        private ShimmerFrameLayout shimmer_profile_picture ;

        private final CheckBox checkbox_attendance;

        private final TextView tv_username;
        private final TextView tv_user_phone;

        private final ImageView iv_phone;
        private final ImageView iv_email;
        private final ImageView iv_message;

        public boolean expanded = false;

        public UserViewHolderForFirebase(@NonNull View itemView) {
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
                FirebaseUtils.attendanceDatabase.child(event_private_id).child(FirebaseUtils.getCurrentUID()).child("attend").setValue(isChecked);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView){
                expanded = !expanded;

                if(expanded){
//                    ll_contact.setVisibility(View.VISIBLE);

                    ViewAnimationUtils.expand(ll_contact);

                    if (getBindingAdapterPosition() != oldPosition){
                        Log.d(Utils.LOG_TAG, "oldPosition = " + oldPosition);
                        onUserExpandListener.onUserExpand(getBindingAdapterPosition(), oldPosition);
                    }
                    oldPosition = getBindingAdapterPosition();

//                    animateLayout(constraintLayout, 300, Gravity.BOTTOM);

                }
                else {
//                    ll_contact.setVisibility(View.GONE);
//                    animateLayout(constraintLayout, 300, Gravity.TOP);

                    ViewAnimationUtils.collapse(ll_contact);
                }

                Toast.makeText(context, getItem(getBindingAdapterPosition()).toString(), Toast.LENGTH_SHORT).show();
            }

            switch (v.getId()) {
                case R.id.iv_phone: {
                    String phoneNumber = getItem(getAbsoluteAdapterPosition()).getPhone();

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

                        context.startActivity(Intent.createChooser(intent_sms, "Choose app"));
                    }

                    break;

            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (v == itemView){
                onUserClickListener.onUserClick(getBindingAdapterPosition(), getItem(getBindingAdapterPosition()));
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

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {color, Color.DKGRAY});

        gd.setCornerRadius(10);

        holder.constraintLayout.setBackground(gd);

        Log.d("murad", "RECYCLING STARTED");

        holder.tv_username.setText(model.getUsername());
        Log.d("murad","name: " + model.getUsername());

        holder.tv_user_phone.setText(model.getPhone());
        Log.d("murad","phone: " + model.getPhone());

        if (model.isMadrich()){
            Log.d("murad", model.getUID() + " is madrich" + true);
            holder.iv_profile_picture.getLayoutParams().height = 120;
            holder.iv_profile_picture.getLayoutParams().width = 120;

            holder.iv_profile_picture.setBorderColor(context.getColor(R.color.colorAccent));
            holder.iv_profile_picture.setBorderWidth(4);
        }

        if (event_private_id != null){

            DatabaseReference ref = FirebaseUtils.attendanceDatabase.child(event_private_id).child(model.getUID()).child("attend");

            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.getResult().exists()){
                        boolean attend = task.getResult().getValue(boolean.class);
                        holder.checkbox_attendance.setChecked(attend);
                    }
                }
            });

            holder.checkbox_attendance.setEnabled(FirebaseUtils.isCurrentUID(model.getUID()));

        }
        else {
            holder.checkbox_attendance.setVisibility(View.GONE);
        }

        FirebaseUtils.getProfilePictureFromFB(model.getUID(), context, holder.iv_profile_picture, /*holder.shimmer_profile_picture*/ null);

    }

    @NonNull
    @Override
    public UserViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_info_expanded_with_card_view, parent, false);

        return new UserViewHolderForFirebase(view);
    }

    public interface OnUserClickListener {
        void onUserClick(int position, UserData userData);
    }

    public interface OnUserExpandListener {
        void onUserExpand(int position, int oldPosition);
    }

    @Override
    public int getItemCount() {
        return getSnapshots().size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

