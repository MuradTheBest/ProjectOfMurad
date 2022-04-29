package com.example.projectofmurad.groups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.helpers.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


/** FirebaseRecyclerAdapter is a class provided by
   FirebaseUI. it provides functions to bind, adapt and show
   database contents in a Recycler View */
public class GroupAdapterForFirebase extends FirebaseRecyclerAdapter<Group, GroupAdapterForFirebase.GroupViewHolderForFirebase> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private final Context context;

    public GroupAdapterForFirebase(@NonNull FirebaseRecyclerOptions<Group> options, @NonNull Context context) {

        super(options);
        this.context = context;
    }

    public static class GroupViewHolderForFirebase extends RecyclerView.ViewHolder implements View.OnClickListener{

        ConstraintLayout constraintLayout;

        TextView tv_group_name;
        TextView tv_group_description;
        ImageView iv_edit;
        TextView tv_users;

        public GroupViewHolderForFirebase(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            tv_group_name = itemView.findViewById(R.id.tv_group_name);
            tv_group_description = itemView.findViewById(R.id.tv_group_description);
            iv_edit = itemView.findViewById(R.id.iv_edit);
            tv_users = itemView.findViewById(R.id.tv_users);

            iv_edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    protected void onBindViewHolder(@NonNull GroupViewHolderForFirebase holder, int position, @NonNull Group model) {
        Log.d("murad", "RECYCLING STARTED");

        int textColor = Utils.getContrastColor(model.getColor());

        int gradientColor = Utils.getContrastBackgroundColor(textColor);

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {model.getColor(), model.getColor(), gradientColor});

        gd.setShape(GradientDrawable.RECTANGLE);

        if (model.getKey().equals(FirebaseUtils.CURRENT_GROUP_KEY)){
            gd.setStroke(Utils.dpToPx(4, context), context.getColor(R.color.colorAccent));
        }

        gd.setCornerRadius(Utils.dpToPx(10, context));

        holder.constraintLayout.setBackground(gd);

        holder.tv_group_name.setText(model.getName());

        holder.tv_group_description.setText(model.getDescription());

        holder.tv_users.setText(model.getUsersNumber() + "/" + model.getLimit());

        FirebaseUtils.getCurrentUserDataRef().child("groups").child(model.getKey()).child("madrich").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue(boolean.class)){
                            holder.tv_group_name.setCompoundDrawablesRelative(
                                    AppCompatResources.getDrawable(context, R.drawable.ic_baseline_person_24), null, null, null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.tv_group_name.setTextColor(textColor);
        holder.tv_group_name.setCompoundDrawableTintList(ColorStateList.valueOf(textColor));
        holder.tv_group_description.setTextColor(textColor);
        holder.iv_edit.setImageTintList(ColorStateList.valueOf(textColor));
        holder.tv_users.setTextColor(textColor);
        holder.tv_users.setCompoundDrawableTintList(ColorStateList.valueOf(textColor));
    }

    @NonNull
    @Override
    public GroupViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_group, parent, false);

        return new GroupViewHolderForFirebase(view);
    }

    public interface OnEventClickListener {
        void onEventClick(int position, CalendarEvent calendarEvent);
    }

    public interface OnEventChooseListener {
        void onEventChoose(int oldPosition, int newPosition, String eventPrivateId);
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
