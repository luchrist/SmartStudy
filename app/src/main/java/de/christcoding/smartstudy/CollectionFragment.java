package de.christcoding.smartstudy;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class CollectionFragment extends Fragment implements View.OnClickListener {

    String title;
    TextView titleCollection;
    LinearLayout list;
    DBLanguageHelper dbLanguageHelper;
    ImageButton back;
    Button practise, test;
    CheckBox delete;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CollectionFragment(String title) {
        // Required empty public constructor
        this.title = title;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        dbLanguageHelper = new DBLanguageHelper(getActivity());
        titleCollection = view.findViewById(R.id.titleCollection);
        back = view.findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new Language()).commit();
            }
        });
        test = view.findViewById(R.id.test);
        test.setOnClickListener(this);
        practise = view.findViewById(R.id.practise);
        practise.setOnClickListener(this);
        delete = view.findViewById(R.id.deleteMode);
        titleCollection.setText(title);
        list = view.findViewById(R.id.list);
        showTranslations(view);

        return view;
    }

    private void showTranslations(View view) {
        ArrayList<Translation> colTrans = getColletionTranslations();
        for(Translation t :colTrans){
            String show_text = t.getNative_word() +" <-> " + t.getForeign_word();
            TextView txt = new TextView(view.getContext());
            txt.setText(show_text);
            txt.setTextSize(18);
            list.addView(txt);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(delete.isChecked()){
                        dbLanguageHelper.deleteLanguageObject(t.getId());
                        list.removeView(txt);
                    }

                }
            });
        }

    }

    private ArrayList<Translation> getColletionTranslations() {
        Cursor cursor = dbLanguageHelper.readAllData();
        ArrayList<Translation> translations = new ArrayList<Translation>();
        ArrayList<Translation> collectionTranslations = new ArrayList<Translation>();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Translation translation = new Translation(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getInt(4));
                translations.add(translation);
            }
        }

        for (Translation t : translations){
            if(t.getCollection().equals(title)){
                collectionTranslations.add(t);
            }
        }
        return collectionTranslations;

    }

    @Override
    public void onClick(View view) {
        if(view == test){
            //Todo: delete class

        }else if(view == practise){

//Todo: delete class

        }
    }
}