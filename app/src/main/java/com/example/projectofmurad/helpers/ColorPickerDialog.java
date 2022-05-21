package com.example.projectofmurad.helpers;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.example.projectofmurad.R;

import petrov.kristiyan.colorpicker.ColorPicker;

public class ColorPickerDialog extends ColorPicker {

    public ColorPickerDialog(Activity context) {
        super(context);

        setDefaultColorButton(R.color.colorAccent);
        setRoundColorButton(true);
        setColorButtonSize(30, 30);
        setColorButtonTickColor(Color.BLACK);
        getPositiveButton().setVisibility(View.GONE);
        getNegativeButton().setVisibility(View.GONE);
    }

    @Override
    public ColorPicker setOnFastChooseColorListener(OnFastChooseColorListener listener) {
        super.setOnFastChooseColorListener(listener);
        getDialogViewLayout().findViewById(R.id.buttons_layout).setVisibility(View.VISIBLE);
        return this;
    }
}
