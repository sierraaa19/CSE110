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
import edu.ucsd.cse110.successorator.databinding.FragmentExpandMoreViewsBinding;
import edu.ucsd.cse110.successorator.databinding.FragmentGoalListBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;

public class ExpandViewsFragment extends Fragment {
    private FragmentExpandMoreViewsBinding view;
    private MainViewModel activityModel;
    //private Date DisplayDate;

    ExpandViewsFragment(){
        // Required empty public constructor
    }


    public static ExpandViewsFragment newInstance(){
        ExpandViewsFragment fragment = new ExpandViewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the ViewModel.
        activityModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentExpandMoreViewsBinding.inflate(inflater, container, false);

        return view.getRoot();
    }

}



