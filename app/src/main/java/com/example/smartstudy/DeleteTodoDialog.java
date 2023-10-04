package com.example.smartstudy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.smartstudy.models.Todo;

public class DeleteTodoDialog extends DialogFragment {
    Todo todo;

    public DeleteTodoDialog(Todo todo) {
        this.todo = todo;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_todo, null);
        // Inflate and set the layout for the dialog
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());




        builder.setTitle("Delete this to-do?")
                // Pass null as the parent view because its going in the dialog layout
                .setView(view)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {



                        /*getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                new Plan()).commit();

                         */


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });



        // Create the AlertDialog object and return it
        return builder.create();
    }





}


