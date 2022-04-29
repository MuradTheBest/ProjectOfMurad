package com.example.projectofmurad.graphs;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupTrainingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupTrainingsFragment extends Fragment implements EventsAndTrainingsAdapterForFirebase.OnEventClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_trainings, container, false);
    }

    RecyclerView rv_group_training;
    ProgressViewModel progressViewModel;
    ProgressBar progressBar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_group_training = view.findViewById(R.id.rv_group_training);
        progressBar = view.findViewById(R.id.progressBar);

        progressViewModel = new ViewModelProvider(requireActivity()).get(ProgressViewModel.class);

        DatabaseReference eventKeys = FirebaseUtils.getTrainingsDatabase().child("Events");
        DatabaseReference events = FirebaseUtils.getAllEventsDatabase();

        FirebaseRecyclerOptions<CalendarEvent> options = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setLifecycleOwner(this)
                .setIndexedQuery(eventKeys, events, CalendarEvent.class)
                .build();

        EventsAndTrainingsAdapterForFirebase eventsAdapterForFirebase = new EventsAndTrainingsAdapterForFirebase(options, FirebaseUtils.getCurrentUID(), requireContext(), this);

        rv_group_training.setAdapter(eventsAdapterForFirebase);
        rv_group_training.setLayoutManager(new LinearLayoutManagerWrapper(requireContext()).addOnLayoutCompleteListener(
                () -> {
                    new Handler().postDelayed(() -> {
                        progressBar.setVisibility(View.GONE);
                        rv_group_training.setVisibility(View.VISIBLE);
                    }, 500);
                }));
    }

    @Override
    public void onEventClick(int position, CalendarEvent calendarEvent) {

    }
}