package com.example.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class StudyFragment extends Fragment {
    Button languages,math, informatic, aiGenerateExam;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.study_fragment, container, false);
        languages= view.findViewById(R.id.languages);
        math = view.findViewById(R.id.math);
        informatic = view.findViewById(R.id.informatic);
        aiGenerateExam = view.findViewById(R.id.aiGenerateExam);

        aiGenerateExam.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AiGenerateExam.class));
        });


        languages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new Language()).commit();

            }
        });
        math.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new MathFragment()).commit();

            }
        });

        informatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new InformaticFragment()).commit();

            }
        });
        return view;
    }
}
