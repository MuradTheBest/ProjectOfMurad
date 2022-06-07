package com.example.projectofmurad.groups;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.R;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.calendar.UsersAdapter;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.example.projectofmurad.utils.ViewAnimationUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * The type Group info screen.
 */
public class GroupInfoScreen extends AppCompatActivity implements UsersAdapter.OnUserExpandListener,
        UsersAdapter.OnUserLongClickListener {

    /**
     * The Group.
     */
    protected Group group;

    /**
     * The Collapsing toolbar layout.
     */
    protected CollapsingToolbarLayout collapsing_toolbar_layout;
    /**
     * The Iv group picture.
     */
    protected ImageView iv_group_picture;
    /**
     * The Toolbar.
     */
    protected MaterialToolbar toolbar;
    /**
     * The Et group name.
     */
    protected TextInputLayout et_group_name;
    /**
     * The Et group description.
     */
    protected TextInputLayout et_group_description;
    /**
     * The Et group key.
     */
    protected TextInputLayout et_group_key;
    /**
     * The Et trainer code.
     */
    protected TextInputLayout et_trainer_code;
    /**
     * The Et group limit.
     */
    protected TextInputLayout et_group_limit;
    /**
     * The Tv choose color.
     */
    protected TextView tv_choose_color;

    /**
     * The Tv group users number.
     */
    protected TextView tv_group_users_number;
    /**
     * The Rv users.
     */
    protected RecyclerView rv_users;
    /**
     * The Shimmer rv users.
     */
    protected ShimmerFrameLayout shimmer_rv_users;

    /**
     * The Loading dialog.
     */
    protected LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ProjectOfMurad_NoActionBar);
        setContentView(R.layout.activity_group_info_screen);

        collapsing_toolbar_layout = findViewById(R.id.collapsing_toolbar_layout);
        iv_group_picture = findViewById(R.id.iv_group_picture);
        toolbar = findViewById(R.id.toolbar);

        et_group_name = findViewById(R.id.et_group_name);
        et_group_description = findViewById(R.id.et_group_description);
        et_group_key = findViewById(R.id.et_group_key);
        et_trainer_code = findViewById(R.id.et_trainer_code);
        et_group_limit = findViewById(R.id.et_group_limit);

        Utils.addDefaultTextChangedListener(et_group_name, et_group_description, et_group_key, et_trainer_code, et_group_limit);

        tv_choose_color = findViewById(R.id.tv_choose_color);

        tv_group_users_number = findViewById(R.id.tv_group_users_number);
        rv_users = findViewById(R.id.rv_users);
        shimmer_rv_users = findViewById(R.id.shimmer_rv_users);

        group = (Group) getIntent().getSerializableExtra(Group.KEY_GROUP);

        getGroupData();

        Log.d(Utils.LOG_TAG, getLocalClassName());
    }

    /**
     * Gets group data.
     */
    protected void getGroupData() {
        int contrastColor = Utils.getContrastColor(group.getColor());
        collapsing_toolbar_layout.setCollapsedTitleTextColor(contrastColor);
        collapsing_toolbar_layout.setTitle(group.getName());
        collapsing_toolbar_layout.setContentScrimColor(group.getColor());

        Utils.setText(et_group_name, group.getName());
        Utils.setText(et_group_description, group.getDescription());
        Utils.setText(et_group_key, group.getKey());
        Utils.setText(et_trainer_code, String.valueOf(group.getMadrichCode()));
        Utils.setText(et_group_limit, String.valueOf(group.getLimit()));

        tv_choose_color.setTextColor(group.getColor());

        tv_group_users_number.setText(String.format(getString(R.string.current_number_of_users_is), group.getUsersNumber()));

        Glide.with(this)
                .load(group.getPicture())
                .error(R.drawable.sample_group_picture)
                .placeholder(R.drawable.sample_group_picture)
                .centerInside().into(iv_group_picture);

        enableEverything(false);
        setupUsersRecyclerView();
    }

    /**
     * Enable everything.
     *
     * @param enable the enable
     */
    protected void enableEverything(boolean enable) {
        et_group_name.setEnabled(enable);
        et_group_description.setEnabled(enable);
        et_trainer_code.setEnabled(enable);
        et_group_limit.setEnabled(enable);

        et_trainer_code.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    /**
     * Setup users recycler view.
     */
    protected void setupUsersRecyclerView(){
        startRVUsersShimmer();

        Query userKeys = FirebaseUtils.getCurrentGroupUsers();
        DatabaseReference users = FirebaseUtils.usersDatabase;

        FirebaseRecyclerOptions<UserData> userOptions
                = new FirebaseRecyclerOptions.Builder<UserData>()
                .setIndexedQuery(userKeys, users, UserData.class)
                .setLifecycleOwner(this)
                .build();

        UsersAdapter userAdapter = new UsersAdapter(userOptions, this, group.getColor(), this, this);
        rv_users.setAdapter(userAdapter);

        LinearLayoutManagerWrapper linearLayoutManagerWrapper = new LinearLayoutManagerWrapper(this);
        linearLayoutManagerWrapper.setOnLayoutCompleteListener(() -> new Handler().postDelayed(this::stopRVUsersShimmer, 500));

        rv_users.setLayoutManager(linearLayoutManagerWrapper);
    }

    /**
     * Start rv users shimmer.
     */
    protected void startRVUsersShimmer() {
        rv_users.setVisibility(View.INVISIBLE);
        shimmer_rv_users.setVisibility(View.VISIBLE);
        shimmer_rv_users.startShimmer();
    }

    /**
     * Stop rv users shimmer.
     */
    protected void stopRVUsersShimmer() {
        shimmer_rv_users.stopShimmer();
        shimmer_rv_users.setVisibility(View.GONE);
        rv_users.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserExpand(int position, int oldPosition) {
        if (oldPosition > -1) {
            UsersAdapter.UserViewHolder oldCollapsedItem
                    = ((UsersAdapter.UserViewHolder) rv_users.findViewHolderForAdapterPosition(oldPosition));

            if (oldCollapsedItem != null){
                ViewAnimationUtils.collapse(oldCollapsedItem.ll_contact);
                oldCollapsedItem.expanded = false;
            }
        }
    }

    @Override
    public boolean onUserLongClick(int position, UserData userData) {
        return false;
    }
}