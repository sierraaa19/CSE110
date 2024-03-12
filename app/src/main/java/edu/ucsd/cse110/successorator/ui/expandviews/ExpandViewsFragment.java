package edu.ucsd.cse110.successorator.ui.expandviews;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;
import java.time.LocalDate;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentExpandMoreViewsBinding;
import edu.ucsd.cse110.successorator.databinding.FragmentGoalListBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;
import edu.ucsd.cse110.successorator.ui.goallist.GoalListFragment;

public class ExpandViewsFragment extends DialogFragment {
    private FragmentExpandMoreViewsBinding view;
    private MainViewModel activityModel;
    //private Date DisplayDate;

    ExpandViewsFragment(){
        // Required empty public constructor
    }

    public static ExpandViewsFragment newInstance() {
        ExpandViewsFragment fragment = new ExpandViewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize the View
        view = FragmentExpandMoreViewsBinding.inflate(inflater, container, false);

        setupMvp();

        return view.getRoot();
    }

    private void setupMvp() {
        // Observe View -> call Model
        view.todayViewLabel.setOnClickListener(v -> toToday());
        view.tomorrowViewLabel.setOnClickListener(v -> toTomorrow());
        view.pendingViewLabel.setOnClickListener(v -> toPending());
        view.recurringViewLabel.setOnClickListener(v -> toRecurring());
    }

    private void toToday() {
            activityModel.toToday();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, GoalListFragment.newInstance())
                    .commit();
    }

    private void toTomorrow() {
        activityModel.toTomorrow();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, TomorrowFragment.newInstance())
                .commit();
    }

    private void toPending() {
        activityModel.toPending();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, PendingFragment.newInstance())
                .commit();
    }

    private void toRecurring() {
        activityModel.toRecurring();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, RecurringFragment.newInstance())
                .commit();
    }

}



