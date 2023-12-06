package de.christcoding.smartstudy.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import de.christcoding.smartstudy.R;
import de.christcoding.smartstudy.models.Event;
import de.christcoding.smartstudy.models.Group;
import de.christcoding.smartstudy.utilities.Constants;
import de.christcoding.smartstudy.utilities.PreferenceManager;
import de.christcoding.smartstudy.utilities.Util;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SaveDescription extends DialogFragment {

    private final Event event;
    private final Group group;
    EditText description;
    PreferenceManager preferenceManager;

    public SaveDescription(Group group, Event event) {
        this.event = event;
        this.group = group;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.descriptio_dialog, null);
        // Inflate and set the layout for the dialog
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        description = view.findViewById(R.id.description);
        if (event != null) {
            description.setText(event.getDescription());
        }
        builder.setTitle("Edit description")
                // Pass null as the parent view because its going in the dialog layout
                .setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(event == null) {
                            preferenceManager = new PreferenceManager(getContext());
                            preferenceManager.putString(Constants.KEY_DESCRIPTION, description.getText().toString());
                        } else {
                            List<Event> events = group.events;
                            events.remove(event);
                            event.setDescription(description.getText().toString().trim());
                            events.add(event);
                            FirebaseFirestore.getInstance()
                                    .collection(Constants.KEY_COLLECTION_GROUPS)
                                    .document(group.id)
                                    .update(Constants.KEY_EVENTS, events)
                                    .addOnFailureListener(e -> {
                                        Util.showToast(getContext(),"Failed to update event to db");
                                        e.printStackTrace();
                                    });
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
