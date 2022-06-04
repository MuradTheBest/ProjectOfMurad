package com.example.projectofmurad.graphs;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.EventInfoDialogFragment;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupTrainingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupTrainingsFragment extends Fragment implements EventAndTrainingsAdapterForFirebase.OnEventClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public GroupTrainingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment GroupTrainingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static GroupTrainingsFragment newInstance(String param1, String param2) {
        GroupTrainingsFragment fragment = new GroupTrainingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_trainings, container, false);
    }

    RecyclerView rv_group_training;
    ProgressBar progressBar;
    TextView tv_there_are_no_group_trainings;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_group_training = view.findViewById(R.id.rv_group_training);
        progressBar = view.findViewById(R.id.progressBar);
        tv_there_are_no_group_trainings = view.findViewById(R.id.tv_there_are_no_group_trainings);

        Query eventKeys = FirebaseUtils.getGroupTrainingsDatabase().orderByValue();
        DatabaseReference events = FirebaseUtils.getAllEventsDatabase();

        FirebaseRecyclerOptions<CalendarEvent> options = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setLifecycleOwner(this)
                .setIndexedQuery(eventKeys, events, CalendarEvent.class)
                .build();

        EventAndTrainingsAdapterForFirebase adapter
                = new EventAndTrainingsAdapterForFirebase(options, requireContext(), this);
        rv_group_training.setAdapter(adapter);

        LinearLayoutManagerWrapper layoutManager = new LinearLayoutManagerWrapper(requireContext());
        layoutManager.setOnLayoutCompleteListener(
                () -> new Handler().postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                    rv_group_training.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.INVISIBLE);
                    tv_there_are_no_group_trainings.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                }, 500));

        rv_group_training.setLayoutManager(layoutManager);
    }

    @Override
    public void onEventClick(int position, CalendarEvent calendarEvent) {
        FragmentManager fm = getParentFragmentManager();

        if (fm.findFragmentByTag(EventInfoDialogFragment.TAG) == null){
            EventInfoDialogFragment event_info_dialogFragment = EventInfoDialogFragment.newInstance(calendarEvent, true);
            event_info_dialogFragment.show(fm, EventInfoDialogFragment.TAG);
        }
    }
}