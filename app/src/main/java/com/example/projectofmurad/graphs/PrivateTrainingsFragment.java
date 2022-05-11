package com.example.projectofmurad.graphs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.training.Training;
import com.example.projectofmurad.training.TrainingAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrivateTrainingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivateTrainingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PrivateTrainingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment PrivateTrainingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static PrivateTrainingsFragment newInstance(String param1, String param2) {
        PrivateTrainingsFragment fragment = new PrivateTrainingsFragment();
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
        return inflater.inflate(R.layout.fragment_private_trainings, container, false);
    }

    RecyclerView rv_private_training;
    ProgressViewModel progressViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_private_training = view.findViewById(R.id.rv_private_training);

        progressViewModel = new ViewModelProvider(requireActivity()).get(ProgressViewModel.class);
        progressViewModel.getPrivateTrainings().observe(getViewLifecycleOwner(), this::setUpRV);
    }

    public void setUpRV(List<Training> trainings){
        TrainingAdapter trainingAdapter = new TrainingAdapter(requireContext(), requireContext().getColor(R.color.colorAccent),
                (ArrayList<Training>) trainings);

        rv_private_training.setAdapter(trainingAdapter);
        rv_private_training.setLayoutManager(new LinearLayoutManagerWrapper(requireContext()));
    }
}