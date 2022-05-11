package com.example.projectofmurad.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectofmurad.R;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventFrequencyDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFrequencyDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String START_DATE_IN_MILLIS = "start_date_in_millis";

    // TODO: Rename and change types of parameters
    private LocalDate startDate;

    public EventFrequencyDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment EventFrequencyDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static EventFrequencyDialogFragment newInstance(long millis) {
        EventFrequencyDialogFragment fragment = new EventFrequencyDialogFragment();
        Bundle args = new Bundle();
        args.putLong(START_DATE_IN_MILLIS, millis);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            long millis = getArguments().getLong(START_DATE_IN_MILLIS);

            startDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
        }

        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_frequency, container, false);
    }

    private EventFrequencyViewModel eventFrequencyViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventFrequencyViewModel = new ViewModelProvider(this).get(EventFrequencyViewModel.class);

    }
}