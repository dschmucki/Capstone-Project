package com.example.catcha;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.example.catcha.locations.dataadapter.StationAutoCompleteAdapter;
import com.example.catcha.sync.model.Location;

import butterknife.ButterKnife;

public class LocationDialogFragment extends DialogFragment {

    private Location startLocation;
    private Location destinationLocation;

    public interface LocationDialogListener {
        void onDialogPositiveClick(Location startBp, Location endBp);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_location_create, null);

        final StationAutoCompleteAdapter startAutoCompleteAdapter = new StationAutoCompleteAdapter(getActivity());
        final StationAutoCompleteAdapter destinationAutoCompleteAdapter = new StationAutoCompleteAdapter(getActivity());

        final AutoCompleteTextView startTextView = ButterKnife.findById(dialogView, R.id.autocomplete_start);
        startTextView.setAdapter(startAutoCompleteAdapter);
        startTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startLocation = startAutoCompleteAdapter.getItem(position);
            }
        });
        startTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startLocation = null;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final AutoCompleteTextView destinationTextView = ButterKnife.findById(dialogView, R.id.autocomplete_destination);
        destinationTextView.setAdapter(destinationAutoCompleteAdapter);
        destinationTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                destinationLocation = destinationAutoCompleteAdapter.getItem(position);
            }
        });
        destinationTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                destinationLocation = null;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final AlertDialog dialog = builder.setMessage("Create Location")
                .setView(dialogView)
                .setPositiveButton("Create",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (startLocation != null && destinationLocation != null) {
                            ((LocationDialogListener) getTargetFragment()).onDialogPositiveClick(startLocation, destinationLocation);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }
}
