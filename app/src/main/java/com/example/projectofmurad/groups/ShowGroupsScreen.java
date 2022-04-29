package com.example.projectofmurad.groups;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.MyActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.Splash_Screen;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.RecyclerViewSwipeDecorator;
import com.example.projectofmurad.helpers.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class ShowGroupsScreen extends MyActivity {

    TextView tv_username;
    RecyclerView rv_groups;
    ProgressBar progressBar;

    GroupAdapterForFirebase groupAdapterForFirebase;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_groups);
        getSupportActionBar().hide();

        tv_username = findViewById(R.id.tv_username);
        rv_groups = findViewById(R.id.rv_groups);
        progressBar = findViewById(R.id.progressBar);

        progressDialog = new ProgressDialog(this);
        Utils.createCustomDialog(progressDialog);

        FirebaseUtils.getCurrentUsername().observe(this, username -> tv_username.setText(
                String.format(getString(R.string.these_are_your_groups), username)));

        DatabaseReference currentUserGroups = FirebaseUtils.getCurrentUserDataRef().child("groups");
        DatabaseReference allGroups = FirebaseUtils.groupsDatabase;

        FirebaseRecyclerOptions<Group> options = new FirebaseRecyclerOptions.Builder<Group>()
                .setLifecycleOwner(this)
                .setIndexedQuery(currentUserGroups, allGroups, Group.class)
                .build();

        groupAdapterForFirebase = new GroupAdapterForFirebase(options, this);
        rv_groups.setAdapter(groupAdapterForFirebase);
        rv_groups.setLayoutManager(new LinearLayoutManagerWrapper(this).addOnLayoutCompleteListener(
                new LinearLayoutManagerWrapper.OnLayoutCompleteCallback() {
                    @Override
                    public void onLayoutComplete() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(groupAdapterForFirebase.getItemCount() > 0){
                                    progressBar.setVisibility(View.GONE);
                                    rv_groups.setVisibility(View.VISIBLE);
                                }
                                else {
                                    finish();
                                    startActivity(new Intent(ShowGroupsScreen.this, CreateOrJoinGroupScreen.class));
                                }

                            }
                        }, 500);
                    }
                }));

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
//            return super.getSwipeThreshold(viewHolder);
            return 0.5f;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @SuppressLint("MissingPermission")

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            vibrator.vibrate(vibrationEffect);

            GroupAdapterForFirebase.GroupViewHolderForFirebase groupViewHolder = (GroupAdapterForFirebase.GroupViewHolderForFirebase) viewHolder;
            Group group = groupAdapterForFirebase.getItem(groupViewHolder.getAbsoluteAdapterPosition());

            switch (direction) {
                case ItemTouchHelper.START:
                    Utils.createDialog(ShowGroupsScreen.this,
                            "Are you sure about leaving this group?",
                            getString(R.string.yes), (dialog, which) -> leaveGroup(group),
                            getString(R.string.no), (dialog, which) -> dialog.dismiss(),
                            null).show();

                    break;
                case ItemTouchHelper.END :

                    Intent intent_invite = new Intent(Intent.ACTION_SEND);
                    intent_invite.setType("text/plain");
                    intent_invite.putExtra(Intent.EXTRA_TEXT, "Join to my group in "
                            + getString(R.string.app_name)
                            + ".\nGroup key: " + group.getKey());

                    startActivity(Intent.createChooser(intent_invite, "Choose app"));

                    break;
            }
//            rv_events.getAdapter().notifyItemChanged(position);
            itemTouchHelper.startSwipe(viewHolder);

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(ShowGroupsScreen.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(Color.GREEN)
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_person_add_24)
                    .addSwipeRightLabel("Invite user")
                    .setSwipeRightLabelColor(Color.WHITE)
                    .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                    .setSwipeRightLabelTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                    .addSwipeLeftBackgroundColor(Color.RED)
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_logout_24)
                    .addSwipeLeftLabel("Leave group")
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                    .setSwipeLeftLabelTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setActionIconTint(Color.WHITE)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }
    };

    private void leaveGroup(@NonNull Group group) {
        progressDialog.setMessage("Leaving this group...");
        HashMap<String, Object> map = new HashMap<>();

        if (group.getKey().equals(FirebaseUtils.CURRENT_GROUP_KEY)){
            map.put("currentGroup", null);
        }

        map.put("groups/" + group.getKey(), null);
        FirebaseUtils.getCurrentUserDataRef().updateChildren(map)
                .addOnSuccessListener(unused -> delete(group.getKey(), FirebaseUtils.getCurrentUID(),
                        new FirebaseUtils.FirebaseCallback(){
                            @Override
                            public void onFirebaseCallback() {
                                progressDialog.dismiss();
                                FirebaseUtils.groupsDatabase.child(group.getKey()).child("usersNumber").setValue(group.getUsersNumber()-1);
                                if (group.getKey().equals(FirebaseUtils.CURRENT_GROUP_KEY)){
                                    startActivity(new Intent(ShowGroupsScreen.this, Splash_Screen.class));
                                }
                            }
                }))
                .addOnFailureListener(e -> Toast.makeText(ShowGroupsScreen.this, "Uppss... Something went wrong.\nPlease try again", Toast.LENGTH_SHORT).show());
    }

    public void delete(String rootKey, String key, FirebaseUtils.FirebaseCallback firebaseCallback){
        FirebaseUtils.getDatabase().getReference(rootKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        deleteData(snapshot, key);
                        firebaseCallback.onFirebaseCallback();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

    }

    public void deleteData(@NonNull DataSnapshot snapshot, String key){
        if (!snapshot.exists() && !snapshot.hasChildren()){
            return;
        }

        Log.d("snapshot", snapshot.getRef().toString());
        Log.d("snapshot", snapshot.getRef().getParent().toString());

        for (DataSnapshot data : snapshot.getChildren()){
            if (Objects.equals(data.getKey(), key)){
                data.getRef().removeValue();
            }
            deleteData(data, key);
        }
    }

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
}
