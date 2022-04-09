package com.example.smartstudy;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Random;


public class TestFragment extends Fragment {

    DBLanguageHelper dbLanguageHelper;
    String title;
    TextView colName, word;
    EditText trans;
    Button send;
    Translation t;

    public TestFragment(String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);
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
                if (nat.equalsIgnoreCase(t.getForeign_word())) {
                    Toast.makeText(getActivity(), "Correct", Toast.LENGTH_SHORT).show();
                    t.setCorrect(t.getCorrect() + 1);
                } else {
                    Toast.makeText(getActivity(), "Wrong! correct translation is: " + t.getForeign_word(), Toast.LENGTH_SHORT).show();
                    t.setCorrect(t.getCorrect() - 1);
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
        //choose of which group the translation will be. Its random but groups that are harder for the user are more likely to get selected.
        Random r = new Random();
        boolean haveTranslation = false;
        Translation translation = null;
        while (!haveTranslation) {
            if (colTrans.size() > 0) {
                int zufall = r.nextInt(colTrans.size());
                haveTranslation = true;
                translation = colTrans.get(zufall);
            } else {
                System.out.println("Keine translation vorhanden");
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
                Translation translation = new Translation(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
                translations.add(translation);
            }

        }

        for (Translation t : translations) {
            if (t.getCollection().equals(title)) {
                collectionTranslations.add(t);
            }
        }
        return collectionTranslations;

    }
}