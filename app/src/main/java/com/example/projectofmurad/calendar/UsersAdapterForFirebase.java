package com.example.projectofmurad.calendar;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.R;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.groups.UserGroupData;
import com.example.projectofmurad.helpers.utils.CalendarUtils;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.helpers.utils.Utils;
import com.example.projectofmurad.helpers.utils.ViewAnimationUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapterForFirebase extends FirebaseRecyclerAdapter<UserData,
        UsersAdapterForFirebase.UserViewHolderForFirebase> {

    private final String event_private_id;
    private final long end;
    private final OnUserLongClickListener onUserLongClickListener;
    private final OnUserExpandListener onUserExpandListener;
    private final int color;
    private final Context context;
    private int oldPosition = -1;

    public UsersAdapterForFirebase(@NonNull FirebaseRecyclerOptions<UserData> options, Context context, int color,
                                   OnUserLongClickListener onUserLongClickListener,
                                   OnUserExpandListener onUserExpandListener) {

        this(options, context, null, 0, color, onUserLongClickListener, onUserExpandListener);
    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     * @param context
     * @param event_private_id
     * @param end
     * @param color
     * @param onUserLongClickListener
     * @param onUserExpandListener
     */
    public UsersAdapterForFirebase(@NonNull FirebaseRecyclerOptions<UserData> options, Context context,
                                   String event_private_id, long end, int color,
                                   OnUserLongClickListener onUserLongClickListener,
                                   OnUserExpandListener onUserExpandListener) {
        super(options);
        this.event_private_id = event_private_id;
        this.end = end;
        this.onUserLongClickListener = onUserLongClickListener;
        this.onUserExpandListener = onUserExpandListener;
        this.color = color;
        this.context = context;
    }

    public class UserViewHolderForFirebase extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ConstraintLayout constraintLayout;
        public final LinearLayout ll_contact;

        private final CircleImageView iv_profile_picture;

        private final MaterialCheckBox cb_attendance;

        private final TextView tv_username;
        private final TextView tv_email;
        private final TextView tv_phone;

        private final AppCompatImageView iv_phone;
        private final AppCompatImageView iv_email;
        private final AppCompatImageView iv_message;

        public boolean expanded = false;

        public UserViewHolderForFirebase(@NonNull View itemView) {
            super(itemView);

            ll_contact = itemView.findViewById(R.id.ll_contact);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            iv_profile_picture = itemView.findViewById(R.id.iv_profile_picture);

            tv_username = itemView.findViewById(R.id.tv_username);
            tv_email = itemView.findViewById(R.id.tv_email);
            tv_phone = itemView.findViewById(R.id.tv_user_phone);

            cb_attendance = itemView.findViewById(R.id.checkbox_attendance);
            cb_attendance.setOnClickListener(v ->
                    FirebaseUtils.getCurrentUserTrackingRef(event_private_id).child("attend").setValue(cb_attendance.isChecked()));

            iv_phone = itemView.findViewById(R.id.iv_phone);
            iv_phone.setOnClickListener(this);

            iv_email = itemView.findViewById(R.id.iv_email);
            iv_email.setOnClickListener(this);

            iv_message = itemView.findViewById(R.id.iv_message);
            iv_message.setOnClickListener(this);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(v ->
                    onUserLongClickListener.onUserLongClick(getBindingAdapterPosition(), getItem(getBindingAdapterPosition())));
        }

        @Override
        public void onClick(View v) {
            if (v == itemView){
                expanded = !expanded;

                if(expanded){
                    ViewAnimationUtils.expand(ll_contact);

                    if (getBindingAdapterPosition() != oldPosition){
                        onUserExpandListener.onUserExpand(getBindingAdapterPosition(), oldPosition);
                    }
                    oldPosition = getBindingAdapterPosition();
                }
                else {
                    ViewAnimationUtils.collapse(ll_contact);
                }
            }
            else if (v == iv_phone) {
                String phoneNumber = getItem(getBindingAdapterPosition()).getPhone();

                if (phoneNumber == null || phoneNumber.isEmpty()) {
                    Toast.makeText(context, "This user has no registered phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    context.startActivity(callIntent);
                }
            }
            else if (v == iv_email) {
                String email = getItem(getBindingAdapterPosition()).getEmail();
                if (email == null || email.isEmpty()) {
                    Toast.makeText(context, "This user has no registered email address", Toast.LENGTH_SHORT).show();
                }
                else {
                    String[] emails = {email};
                    Intent intent_email = new Intent(Intent.ACTION_SEND);
                    intent_email.setType("text/plain");
                    intent_email.putExtra(Intent.EXTRA_EMAIL, emails);
                    intent_email.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    intent_email.putExtra(Intent.EXTRA_TEXT, "This is the email body");
                    context.startActivity(intent_email);
                }
            }
            else if (v == iv_message) {
                String phoneNumber = getItem(getBindingAdapterPosition()).getPhone();

                if (phoneNumber == null || phoneNumber.isEmpty()) {
                    Toast.makeText(context, "This user has no registered phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent_sms = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                    context.startActivity(Intent.createChooser(intent_sms, "Choose app"));
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolderForFirebase holder, int position, @NonNull UserData model) {

        int textColor = Utils.getContrastColor(color);
        GradientDrawable gd = Utils.getGradientBackground(color);

        if (FirebaseUtils.isCurrentUID(model.getUID())){
            gd.setStroke(Utils.dpToPx(4, context), FirebaseUtils.CURRENT_GROUP_COLOR);
        }

        gd.setCornerRadius(Utils.dpToPx(10, context));

        holder.constraintLayout.setBackground(gd);

        holder.tv_username.setTextColor(textColor);
        holder.tv_email.setTextColor(textColor);
        holder.tv_phone.setTextColor(textColor);

        holder.cb_attendance.setButtonTintList(ColorStateList.valueOf(textColor));

        holder.iv_phone.setImageTintList(ColorStateList.valueOf(textColor));
        holder.iv_email.setImageTintList(ColorStateList.valueOf(textColor));
        holder.iv_message.setImageTintList(ColorStateList.valueOf(textColor));

        holder.tv_username.setText(model.getUsername());
        holder.tv_email.setText(model.getEmail());
        holder.tv_phone.setText(model.getPhone());

        FirebaseUtils.getCurrentGroupUsers().child(model.getUID()).child(UserGroupData.KEY_MADRICH).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue(boolean.class)) {
                            holder.iv_profile_picture.getLayoutParams().height = Utils.dpToPx(55, context);
                            holder.iv_profile_picture.getLayoutParams().width = Utils.dpToPx(55, context);

                            holder.iv_profile_picture.setBorderColor(FirebaseUtils.CURRENT_GROUP_COLOR);
                            holder.iv_profile_picture.setBorderWidth(Utils.dpToPx(2, context));

                            CalendarUtils.animate(holder.constraintLayout);
                        }
                    }
                });

        if (event_private_id != null) {
            FirebaseUtils.getUserTrackingRef(event_private_id, model.getUID()).child("attend").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            holder.cb_attendance.setChecked(snapshot.exists() &&  snapshot.getValue(boolean.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

            holder.cb_attendance.setEnabled(FirebaseUtils.isCurrentUID(model.getUID()) && end > System.currentTimeMillis());
            holder.cb_attendance.setAlpha(holder.cb_attendance.isEnabled() ? 1f : 0.7f);
        }
        else {
            holder.cb_attendance.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(model.getPicture())
                .error(R.drawable.sample_profile_picture)
                .placeholder(R.drawable.sample_profile_picture)
                .centerCrop().into(holder.iv_profile_picture);
    }

    @NonNull
    @Override
    public UserViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);

        return new UserViewHolderForFirebase(view);
    }

    public interface OnUserLongClickListener {
        boolean onUserLongClick(int position, UserData userData);
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

