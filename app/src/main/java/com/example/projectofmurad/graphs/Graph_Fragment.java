package com.example.projectofmurad.graphs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.ZoomOutPageTransformer;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Graph_Fragment#newInstance(String, String)} factory method to
 * create an instance of this fragment.
 */
public class Graph_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Graph_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Graph_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static Graph_Fragment newInstance(String param1, String param2) {
        Graph_Fragment fragment = new Graph_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph_, container, false);
    }

    TabLayout tabLayout_trainings;
    ViewPager2 vp_trainings;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout_trainings = view.findViewById(R.id.tabLayout_trainings);
        vp_trainings = view.findViewById(R.id.vp_trainings);

        ProgressSlidePageAdapter pagerAdapter = new ProgressSlidePageAdapter(this);

        vp_trainings.setAdapter(pagerAdapter);
        vp_trainings.setPageTransformer(new ZoomOutPageTransformer());
        vp_trainings.setNestedScrollingEnabled(true);
        vp_trainings.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout_trainings, vp_trainings,
                (tab, position) -> tab.setText(position == 0 ? "Group trainings" : "Private trainings")).attach();

        tabLayout_trainings.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}