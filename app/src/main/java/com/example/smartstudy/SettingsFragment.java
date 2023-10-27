package com.example.smartstudy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartstudy.utilities.PreferenceManager;


public class SettingsFragment extends Fragment {
    Button save;
    EditText learnTime, pauseTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        save = view.findViewById(R.id.saveSettings);
        learnTime = view.findViewById(R.id.editTime);
        pauseTime = view.findViewById(R.id.breakEdit);
        setListeners();

        return view;
    }

    private void setListeners() {
        save.setOnClickListener(v -> {
            int learnTimeInt = Integer.parseInt(learnTime.getText().toString());
            int pauseTimeInt = Integer.parseInt(pauseTime.getText().toString());
            PreferenceManager preferenceManager = new PreferenceManager(getContext());
            preferenceManager.putInt("timer", learnTimeInt);
            preferenceManager.putInt("break", pauseTimeInt);
        });
    }
}