package com.example.smartstudy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCollection#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCollection extends Fragment {

    public Button addTrans;
    public ImageButton back;
    public TextInputEditText title, languageOne , foreign;
    public LinearLayout transList;
    DBLanguageHelper dbLanguageHelper;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddCollection() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCollection.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCollection newInstance(String param1, String param2) {
        AddCollection fragment = new AddCollection();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_add_collection, container, false);

       dbLanguageHelper = new DBLanguageHelper(getActivity());

       addTrans = view.findViewById(R.id.addTrans);
       title = view.findViewById(R.id.titleInput);
       foreign = view.findViewById(R.id.foreignInput);
       languageOne = view.findViewById(R.id.nativeInput);
       transList = view.findViewById(R.id.translationList);
       back = view.findViewById(R.id.back);

       addTrans.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String natWord, foreignWord, show_text;
               natWord = languageOne.getText().toString();
               foreignWord = foreign.getText().toString();
               String tit = title.getText().toString();

               System.out.println(tit + "    " + natWord + "    "+ foreignWord);
               Translation translation = new Translation( "", tit, natWord, foreignWord, 0, 0);

               dbLanguageHelper.addLanguageObject(translation);
              /* show_text = natWord +" <-> " + foreignWord;
               TextView txt = new TextView(view.getContext());
               txt.setText(show_text);
               txt.setTextSize(18);
               transList.addView(txt);


               */
               //DB Speicherung fehlt

           }
       });

       back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                       new Language()).commit();
           }
       });

       return view;
    }
}