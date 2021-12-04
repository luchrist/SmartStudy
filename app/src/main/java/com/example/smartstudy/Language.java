package com.example.smartstudy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class Language extends Fragment implements View.OnClickListener {

    public ImageButton backbtn;
    public Button addCollection;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_language, container, false);

        backbtn = view.findViewById(R.id.backbtn);
        addCollection = view.findViewById(R.id.addCollection);
        backbtn.setOnClickListener(this);
        addCollection.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == backbtn){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new StudyFragment()).commit();
        }else if(view == addCollection){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new AddCollection()).commit();
        }else{

        }
    }
}