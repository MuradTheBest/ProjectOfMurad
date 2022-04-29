package com.example.projectofmurad.calendar;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.projectofmurad.R;

import java.time.LocalDate;

public class ChooseEventFrequency_Screen extends DialogFragment {

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    private int year;
    private int month;
    private int day;

    private ChooseEventFrequencyDialogCustomWithExposedDropdown d;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    @NonNull
    static ChooseEventFrequency_Screen newInstance(int year, int month, int day) {
        ChooseEventFrequency_Screen fragment = new ChooseEventFrequency_Screen();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        args.putInt(MONTH, month);
        args.putInt(DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    public ChooseEventFrequency_Screen() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme);

        if(getArguments() != null) {
            year = getArguments().getInt(YEAR);
            month = getArguments().getInt(MONTH);
            day = getArguments().getInt(DAY);
        }
        LocalDate startDate = LocalDate.of(year, month, day);

//        d = new ChooseEventFrequencyDialogCustomWithExposedDropdown(requireContext(), startDate);

//        ((ChooseEventFrequencyDialogCustomWithExposedDropdown) getDialog()).btn_ok.;
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
//        super.show(manager, tag);
        getDialog().show();
   }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
//        getParentFragmentManager().beginTransaction().hide(ChooseEventFrequency_Screen.this).commit();
//        getDialog().hide();
        Toast.makeText(getContext(), "Hiding DialogFragment", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        LocalDate startDate = LocalDate.of(year, month, day);

        ChooseEventFrequencyDialogCustomWithExposedDropdown d = new ChooseEventFrequencyDialogCustomWithExposedDropdown(requireContext(), startDate);

        return d;
    }

    public static String TAG = "ChooseEventFrequencyDialogCustomWithExposedDropdown";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

/*        View view = inflater.inflate(R.layout.choose_event_frequency_dialogcustom_with_exposed_dropdown_scroll_view, container, false);
        return view;*/
        return super.onCreateView(inflater, container, savedInstanceState);

    }




}
