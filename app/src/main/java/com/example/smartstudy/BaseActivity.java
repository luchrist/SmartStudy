package com.example.smartstudy;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartstudy.utilities.Constants;
import com.example.smartstudy.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userMail = preferenceManager.getString(Constants.KEY_EMAIL);
        if(userMail != null) {
            documentReference = db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_EMAIL));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (documentReference != null) {
            documentReference.update(Constants.KEY_AVAILABILITY, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(documentReference != null) {
            documentReference.update(Constants.KEY_AVAILABILITY, true);
        }
    }
}
