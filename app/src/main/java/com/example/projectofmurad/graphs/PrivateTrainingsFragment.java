package com.example.projectofmurad.graphs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.training.Training;
import com.example.projectofmurad.training.TrainingAdapterForFirebase;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

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
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_private_trainings, container, false);
    }

    ProgressBar progressBar;
    TextView tv_there_are_no_private_trainings;
    RecyclerView rv_private_training;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar);
        tv_there_are_no_private_trainings = view.findViewById(R.id.tv_there_are_no_private_trainings);
        rv_private_training = view.findViewById(R.id.rv_private_training);

        Query trainings = FirebaseUtils.getCurrentUserPrivateTrainingsRef().orderByChild(Training.KEY_TRAINING_START);

        FirebaseRecyclerOptions<Training> options = new FirebaseRecyclerOptions.Builder<Training>()
                .setLifecycleOwner(this)
                .setQuery(trainings, Training.class)
                .build();

        TrainingAdapterForFirebase trainingAdapter
                = new TrainingAdapterForFirebase(options, requireContext(), FirebaseUtils.CURRENT_GROUP_COLOR);

        rv_private_training.setAdapter(trainingAdapter);
        LinearLayoutManagerWrapper layoutManager = new LinearLayoutManagerWrapper(requireContext());
        layoutManager.setOnLayoutCompleteListener(
                () -> {
                    boolean isShowingItems = trainingAdapter.getItemCount() > 0;
                    progressBar.setVisibility(View.GONE);
                    tv_there_are_no_private_trainings.setVisibility(isShowingItems ? View.GONE : View.VISIBLE);
                    rv_private_training.setVisibility(isShowingItems ? View.VISIBLE : View.INVISIBLE);
                });
        rv_private_training.setLayoutManager(layoutManager);
    }
}