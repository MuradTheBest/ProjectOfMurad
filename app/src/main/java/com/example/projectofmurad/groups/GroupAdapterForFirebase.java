package com.example.projectofmurad.groups;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textview.MaterialTextView;


/** FirebaseRecyclerAdapter is a class provided by
   FirebaseUI. it provides functions to bind, adapt and show
   database contents in a Recycler View */
public class GroupAdapterForFirebase extends FirebaseRecyclerAdapter<Group, GroupAdapterForFirebase.GroupViewHolderForFirebase> {

    private final Context context;
    private final OnGroupLongClickListener onGroupLongClickListener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     * @param context
     * @param onGroupLongClickListener
     */
    public GroupAdapterForFirebase(@NonNull FirebaseRecyclerOptions<Group> options, @NonNull Context context,
                                   OnGroupLongClickListener onGroupLongClickListener) {

        super(options);
        this.context = context;
        this.onGroupLongClickListener = onGroupLongClickListener;
    }

    public class GroupViewHolderForFirebase extends RecyclerView.ViewHolder implements View.OnClickListener {

        ConstraintLayout constraintLayout;

        MaterialTextView tv_group_name;
        MaterialTextView tv_group_description;
        MaterialTextView tv_users;

        private boolean isMadrich;

        public GroupViewHolderForFirebase(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            tv_group_name = itemView.findViewById(R.id.tv_group_name);
            tv_group_description = itemView.findViewById(R.id.tv_group_description);
            tv_users = itemView.findViewById(R.id.tv_users);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(v -> onGroupLongClickListener.onGroupLongClick(getItem(getBindingAdapterPosition())));
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, isMadrich() ? GroupInfoScreenMadrich.class : GroupInfoScreen.class);
            intent.putExtra(Group.KEY_GROUP, getItem(getBindingAdapterPosition()));
            context.startActivity(intent);
        }

        public boolean isMadrich() {
            return isMadrich;
        }

        public void setMadrich(boolean madrich) {
            isMadrich = madrich;
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupViewHolderForFirebase holder, int position, @NonNull Group model) {
        int textColor = Utils.getContrastColor(model.getColor());

        GradientDrawable gd = Utils.getGradientBackground(model.getColor());

        if (FirebaseUtils.isCurrentGroup(model.getKey())){
            gd.setStroke(Utils.dpToPx(5, context), context.getColor(R.color.colorAccent));
        }

        gd.setCornerRadius(Utils.dpToPx(10, context));

        holder.constraintLayout.setBackground(gd);

        holder.tv_group_name.setText(model.getName());

        holder.tv_group_description.setText(model.getDescription());


        FirebaseUtils.getGroupDatabase(model.getKey()).child("Users").child(FirebaseUtils.getCurrentUID())
                .child(UserGroupData.KEY_MADRICH).get()
                .addOnSuccessListener(snapshot -> {
                        holder.setMadrich(snapshot.exists() && snapshot.getValue(boolean.class));
                        if (holder.isMadrich()){
                            holder.tv_group_name.setCompoundDrawablesRelative(
                                    AppCompatResources.getDrawable(context, R.drawable.ic_baseline_person_24),
                                    null, null, null);
                        }
                });

        holder.tv_group_name.setTextColor(textColor);
        TextViewCompat.setCompoundDrawableTintList(holder.tv_group_name, ColorStateList.valueOf(textColor));
        holder.tv_group_description.setTextColor(textColor);
        holder.tv_users.setTextColor(textColor);
        TextViewCompat.setCompoundDrawableTintList(holder.tv_users, ColorStateList.valueOf(textColor));

        if (model.getLimit() == 0){
            holder.tv_users.setText(model.getUsersNumber() + "/" + context.getString(R.string.no_user_limit));
        }
        else {
            holder.tv_users.setText(model.getUsersNumber() + "/" + model.getLimit());
        }
    }

    @NonNull
    @Override
    public GroupViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_group, parent, false);
        return new GroupViewHolderForFirebase(view);
    }

    public interface OnGroupLongClickListener{
        boolean onGroupLongClick(Group group);
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
