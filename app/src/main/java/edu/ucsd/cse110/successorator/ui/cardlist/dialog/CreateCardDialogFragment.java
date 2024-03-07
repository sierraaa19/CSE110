package edu.ucsd.cse110.successorator.ui.cardlist.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.MainActivity;
import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogCreateTaskBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class CreateCardDialogFragment extends DialogFragment {
    private FragmentDialogCreateTaskBinding view;
    private MainViewModel activityModel;

    CreateCardDialogFragment(){
        // Required empty public constructor
    }

    public static CreateCardDialogFragment newInstance(){
        var fragment = new CreateCardDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

//        var modelOwner = requireActivity();
//        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
//        var modelProvider = new ViewModelProvider(modelOwner,modelFactory);
//        this.activityModel = modelProvider.get(MainViewModel.class);
        activityModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
//        this.view = FragmentDialogCreateTaskBinding.inflate(getLayoutInflater());
//
//        return new AlertDialog.Builder(getActivity())
//                .setTitle("New Task...")
//                .setMessage("Please provide the new task title.")
//                .setView(view.getRoot())
//                .setPositiveButton("Save",this::onPositiveButtonClick)
//                .setNegativeButton("Cancel",this::onNegativeButtonClick)
//                .create();
        view = FragmentDialogCreateTaskBinding.inflate(getLayoutInflater());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Task...")
                .setMessage("Please provide the new task title.")
                .setView(view.getRoot())
                .setPositiveButton("Save", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick);
        return builder.create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which){
        var text = view.cardFrontEditText.getText().toString();
        var frequency = getSelectedFrequency(view.frequencyGroup);

        // sort order is an invalid value here, because append/prepend will replace it
        var goal = new Goal(null, text, false, -1);
        goal.setFrequency(frequency);
        activityModel.append(goal);
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which){
        dialog.cancel();
    }
    private String getSelectedFrequency(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = view.getRoot().findViewById(selectedId);

        return selectedRadioButton.getText().toString();
    }




}
