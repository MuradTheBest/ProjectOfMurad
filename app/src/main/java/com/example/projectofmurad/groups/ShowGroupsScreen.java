package com.example.projectofmurad.groups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.MyActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.helpers.RecyclerViewSwipeDecorator;
import com.example.projectofmurad.helpers.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.HashMap;

@SuppressLint("MissingPermission")
public class ShowGroupsScreen extends MyActivity implements GroupAdapterForFirebase.OnGroupLongClickListener {

    TextView tv_username;
    RecyclerView rv_groups;
    ProgressBar progressBar;

    FloatingActionButton fab_create_or_join_group;

    GroupAdapterForFirebase groupAdapterForFirebase;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_groups);
        getSupportActionBar().hide();

        tv_username = findViewById(R.id.tv_username);
        rv_groups = findViewById(R.id.rv_groups);
        progressBar = findViewById(R.id.progressBar);

        loadingDialog = new LoadingDialog(this);

        fab_create_or_join_group = findViewById(R.id.fab_create_or_join_group);
        fab_create_or_join_group.setOnClickListener(v -> startActivity(new Intent(this, CreateOrJoinGroupScreen.class)));

        FirebaseUtils.getCurrentUsername().observe(this, username -> tv_username.setText(
                String.format(getString(R.string.these_are_your_groups), username)));

        Query currentUserGroups = FirebaseUtils.groupDatabases
                .orderByChild("Users/" + FirebaseUtils.getCurrentUID() + "/uid").equalTo(FirebaseUtils.getCurrentUID());

        DatabaseReference groups = FirebaseUtils.groups;

        FirebaseRecyclerOptions<Group> options = new FirebaseRecyclerOptions.Builder<Group>()
                .setLifecycleOwner(this)
                .setIndexedQuery(currentUserGroups, groups, Group.class)
                .build();

        groupAdapterForFirebase = new GroupAdapterForFirebase(options, ShowGroupsScreen.this, this);
        rv_groups.setAdapter(groupAdapterForFirebase);
        rv_groups.setLayoutManager(new LinearLayoutManagerWrapper(this).addOnLayoutCompleteListener(
                () -> new Handler().postDelayed(() -> {
                    if (groupAdapterForFirebase.getItemCount() > 0) {
                        progressBar.setVisibility(View.GONE);
                        rv_groups.setVisibility(View.VISIBLE);
                    }
                    else {
                        finish();
                        startActivity(new Intent(ShowGroupsScreen.this, CreateOrJoinGroupScreen.class));
                    }

                }, 500)));

        itemTouchHelper.attachToRecyclerView(rv_groups);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
        }

    }

    private Vibrator vibrator;
    private VibrationEffect vibrationEffect;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END | ItemTouchHelper.START) {
        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 0.5f;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            vibrator.vibrate(vibrationEffect);

            GroupAdapterForFirebase.GroupViewHolderForFirebase groupViewHolder =
                    (GroupAdapterForFirebase.GroupViewHolderForFirebase) viewHolder;

            Group group = groupAdapterForFirebase.getItem(groupViewHolder.getAbsoluteAdapterPosition());

            switch (direction) {
                case ItemTouchHelper.START:

                    if (group.getKey().equals(FirebaseUtils.CURRENT_GROUP_KEY)){
                        Utils.createAlertDialog(ShowGroupsScreen.this, null,
                                "It is current selected group",
                                getString(R.string.ok), (dialog, which) -> dialog.dismiss(),
                                null, null,
                                null).show();
                    }
                    else {
                        Utils.createAlertDialog(ShowGroupsScreen.this, null,
                                "To change current group?",
                                R.string.yes, (dialog, which) -> changeGroup(group),
                                R.string.no, (dialog, which) -> dialog.dismiss(),
                                null).show();
                    }

                    break;
                case ItemTouchHelper.END :

                    String link = getString(R.string.group_join_link) + group.getKey();

                    Intent intent_join = new Intent(Intent.ACTION_SEND);
                    intent_join.setType("text/plain");
                    intent_join.putExtra(Intent.EXTRA_TEXT, "Join to my group in "
                            + getString(R.string.app_name)
                            + ".\n\nFollow the link: "
                            + "\n" + link);

                    startActivity(Intent.createChooser(intent_join, "Choose app"));

                    break;
            }

            itemTouchHelper.startSwipe(viewHolder);

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(Color.GREEN)
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_person_add_24)
                    .addSwipeRightLabel("Invite user")
                    .setSwipeRightLabelColor(Color.WHITE)
                    .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                    .setSwipeRightLabelTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                    .addSwipeLeftBackgroundColor(getColor(R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_double_arrow_24)
                    .addSwipeLeftLabel("Change group")
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                    .setSwipeLeftLabelTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setActionIconTint(Color.WHITE)
                    .create()
                    .decorate();

//            viewHolder.itemView.findViewById(R.id.backgroundStart).setVisibility(dX > 0 ? View.VISIBLE : View.GONE);
//            viewHolder.itemView.findViewById(R.id.backgroundEnd).setVisibility(dX < 0 ? View.VISIBLE : View.GONE);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }

    };

    private void changeGroup(@NonNull Group group) {
        FirebaseUtils.changeGroup(this, group.getKey());
        startActivity(new Intent(this, MainActivity.class));

        FirebaseUtils.getCurrentGroupDatabase().get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                        FirebaseUtils.getDatabase().getReference("dsfdfgdfsdasdf").setValue(map);
                    }
                });
    }

    private void leaveGroup(@NonNull Group group) {
        loadingDialog.setMessage("Leaving this group...");
        loadingDialog.show();

        FirebaseUtils.getCurrentUserDataRef().child("currentGroup").child(group.getKey()).removeValue()
                .addOnSuccessListener(unused ->
                        FirebaseUtils.deleteAll(FirebaseUtils.getGroupDatabase(group.getKey()),
                                FirebaseUtils.getCurrentUID(),
                                new FirebaseUtils.FirebaseCallback() {
                                    @Override
                                    public void onFirebaseCallback() {
                                        FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(group.getKey());
                                        FirebaseUtils.groups.child(group.getKey()).child(Group.KEY_USERS_NUMBER).setValue(group.getUsersNumber()-1);
                                        if (group.getKey().equals(FirebaseUtils.CURRENT_GROUP_KEY)) {
                                            startActivity(new Intent(ShowGroupsScreen.this, ShowGroupsScreen.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            finish();
                                        }
                                    }
                }))
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(ShowGroupsScreen.this,
                            R.string.upps_something_went_wrong,
                            Toast.LENGTH_SHORT).show();
                });
    }

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

    @Override
    public boolean onGroupLongClick(Group group) {
        Utils.createAlertDialog(ShowGroupsScreen.this, "All your data will be deleted",
                "Are you sure about leaving this group?",
                getString(R.string.yes),
                (dialog, which) -> FirebaseUtils.createReAuthenticateDialog(ShowGroupsScreen.this, () -> leaveGroup(group)),
                getString(R.string.no), (dialog, which) -> dialog.dismiss(),
                null).show();

        return false;
    }
}
