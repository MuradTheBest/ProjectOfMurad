package com.example.projectofmurad.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projectofmurad.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThereIsNoEventFragment#newInstance(String)} factory method to
 * create an instance of this fragment.
 */
public class ThereIsNoEventFragment extends Fragment {

    private static final String ARG_TEXT = "text";

    private String text;

    /**
     * Instantiates a new There is no event fragment.
     */
    public ThereIsNoEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param text Text to show.
     *
     * @return A new instance of fragment ThereIsNoEventFragment.
     */
    @NonNull
    public static ThereIsNoEventFragment newInstance(String text) {
        ThereIsNoEventFragment fragment = new ThereIsNoEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString(ARG_TEXT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_there_is_no_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tv_there_is_no_event = view.findViewById(R.id.tv_there_is_no_event);
        tv_there_is_no_event.setText(text);
    }
}