package edu.ucsd.cse110.successorator.ui.goallist.dialog;
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

import java.util.Date;
import java.time.LocalDate;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogCreateTaskBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;

public class CreateGoalDialogFragment extends DialogFragment {
    private FragmentDialogCreateTaskBinding view;
    private MainViewModel activityModel;
    private Date DisplayDate;
    private String context;

    CreateGoalDialogFragment(){
        // Required empty public constructor
    }

//    public static CreateCardDialogFragment newInstance(){
//        var fragment = new CreateCardDialogFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static CreateGoalDialogFragment newInstance(Date displayedDate) {
        CreateGoalDialogFragment fragment = new CreateGoalDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("displayedDate", displayedDate); // Assuming Date is Serializable
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
//        activityModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        if (getArguments() != null) {
            DisplayDate = (Date) getArguments().getSerializable("displayedDate");
        }
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
        setupContextButtons();
        return builder.create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which){
        var text = view.cardFrontEditText.getText().toString();
        var frequency = getSelectedFrequency(view.frequencyGroup);

        var goalDateString = view.goalDate.getText().toString();
        LocalDate goalCreationDate;
        if (DisplayDate != null) {
            goalCreationDate = SuccessDate.dateToLocalDate(DisplayDate); // Assuming SuccessDate has this method
        } else {
            // Fallback to the current date if DisplayDate is not available
            goalCreationDate = SuccessDate.getCurrentDate();
        }
        var goal = new Goal(null, text, false, -1, frequency, goalCreationDate,context);
        activityModel.append(goal);
//        activityModel.updateDisplayedGoals();
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
    private void setupContextButtons() {
        // Assuming you have access to your buttons in view binding
        view.homeButton.setOnClickListener(v -> context = "Home");
        view.workButton.setOnClickListener(v -> context = "Work");
        view.schoolButton.setOnClickListener(v -> context = "School");
        view.errandsButton.setOnClickListener(v -> context = "Errands");
    }



}
