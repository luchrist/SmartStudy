package com.example.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class MainFragment extends Fragment implements View.OnClickListener {

    LinearLayout timetableLayout;
    TextView title;
    NavigationView navigationView;
    TextView homeTitle;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.main_fragment, container, false);
        timetableLayout = view.findViewById(R.id.tablelayout);
        title = getActivity().findViewById(R.id.variabel_text);
        homeTitle = view.findViewById(R.id.timetable_title);
        navigationView = getActivity().findViewById(R.id.nav_view);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.weekdays, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        timetableLayout.setOnClickListener(this);
        homeTitle.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(timetableLayout) || v.equals(homeTitle)){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new TimetableFragment()).commit();
            title.setText("Timetable");
            navigationView.setCheckedItem(R.id.nav_timetable);

        }
    }
}
