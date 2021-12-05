package com.example.smartstudy;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Language extends Fragment implements View.OnClickListener {

    public ImageButton backbtn;
    public Button addCollection;
    public LinearLayout collections_list;
    DBLanguageHelper dbLanguageHelper = new DBLanguageHelper(getActivity());
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_language, container, false);

        backbtn = view.findViewById(R.id.backbtn);
        addCollection = view.findViewById(R.id.addCollection);
        collections_list = view.findViewById(R.id.collectionsLayout);
        backbtn.setOnClickListener(this);
        addCollection.setOnClickListener(this);

        showCollections(view);


        return view;
    }

    private void showCollections(View view) {
        ArrayList<String> collections = getCollections();
        for (String collection : collections){
            TextView txt = new TextView(view.getContext());
            txt.setText(collection);
            txt.setTextSize(18);
            collections_list.addView(txt);

        }
    }

    private ArrayList<String> getCollections() {
        Cursor cursor = dbLanguageHelper.readAllData();
        ArrayList<String> collections = new ArrayList<String>();
        ArrayList<String> all = new ArrayList<String>();
        if (cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                all.add(cursor.getString(1));
            }

        }
        boolean single;
        for (String c : all){
            single = true;
            for (String s : collections){
                if (c.equals(s)){
                    single = false;
                }
            }
            if(single = true){
                collections.add(c);
            }
        }
        return collections;
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
            Button btn = (Button) view;
            String title =  btn.getText().toString();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new CollectionFragment(title)).commit();
        }
    }
}