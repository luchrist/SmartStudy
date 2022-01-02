package com.example.smartstudy;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class PractiseFragment extends Fragment {



    DBLanguageHelper dbLanguageHelper;
    String title;
    TextView colName, word;
    EditText trans;
    Button send;
    Translation t;


    public PractiseFragment(String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_practise, container, false);
        dbLanguageHelper = new DBLanguageHelper(getActivity());
        colName = view.findViewById(R.id.collectionName);
        colName.setText(title);
        word = view.findViewById(R.id.word);
        trans = view.findViewById(R.id.trans);
        send = view.findViewById(R.id.sendBtn);
        t = showTranslation();
        word.setText(t.getNative_word());
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nat = trans.getText().toString();
                if (nat.equalsIgnoreCase(t.getForeign_word())){
                    Toast.makeText(getActivity(), "Correct", Toast.LENGTH_SHORT).show();
                    t.setCorrect(t.getCorrect()+1);
                }else{
                    Toast.makeText(getActivity(), "Wrong! correct translation is: " + t.getForeign_word(), Toast.LENGTH_SHORT).show();
                    t.setCorrect(t.getCorrect()-1);
                }
                dbLanguageHelper.updateLanguageObject(t);
                t = showTranslation();
                word.setText(t.getNative_word());
                trans.setText("");
            }
        });

        return view;
    }

    private Translation showTranslation() {
        //get all Translations of this Collection
        ArrayList<Translation> colTrans = getColletionTranslations();
        //split them into different groups, depending on how hard they are for the user.
        ArrayList<Translation> neutral = new ArrayList<Translation>();
        ArrayList<Translation> wrongOnes = new ArrayList<Translation>();
        ArrayList<Translation> wrongs = new ArrayList<Translation>();
        ArrayList<Translation> correctOnes = new ArrayList<Translation>();
        ArrayList<Translation> corrects = new ArrayList<Translation>();
        for(Translation t :colTrans){
            if (t.getCorrect() == 0){
                neutral.add(t);
            }else if(t.getCorrect() == -1){
                wrongOnes.add(t);
            }else if(t.getCorrect() < -1){
                wrongs.add(t);
            }else if(t.getCorrect() == 1){
                correctOnes.add(t);
            }else if(t.getCorrect() > 1){
                corrects.add(t);
            }
        }
        //choose of which group the translation will be. Its random but groups that are harder for the user are more likely to get selected.
        Random r = new Random();
        boolean haveTranslation = false;
        Translation translation = null;
        while(!haveTranslation){
            int zufall = r.nextInt(15);
            if(zufall < 5){
                if (wrongs.size() > 0){
                    haveTranslation = true;
                    int index = r.nextInt(wrongs.size());
                    translation = wrongs.get(index);
                }
            }else if (zufall >4 && zufall <9){
                if (wrongOnes.size() > 0){
                    haveTranslation = true;
                    int index = r.nextInt(wrongOnes.size());
                    translation = wrongOnes.get(index);
                }
            }else if (zufall >8 && zufall <12){
                if (neutral.size() > 0){
                    haveTranslation = true;
                    int index = r.nextInt(neutral.size());
                    translation = neutral.get(index);
                }
            }else if (zufall >11 && zufall <14){
                if (correctOnes.size() > 0){
                    haveTranslation = true;
                    int index = r.nextInt(correctOnes.size());
                    translation = correctOnes.get(index);
                }
            }else if (zufall == 14){
                if (corrects.size() > 0){
                    haveTranslation = true;
                    int index = r.nextInt(corrects.size());
                    translation = corrects.get(index);
                }
            }
        }
        return translation;



    }

    private ArrayList<Translation> getColletionTranslations() {
        Cursor cursor = dbLanguageHelper.readAllData();
        ArrayList<Translation> translations = new ArrayList<Translation>();
        ArrayList<Translation> collectionTranslations = new ArrayList<Translation>();
        if (cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
        } else {
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
}