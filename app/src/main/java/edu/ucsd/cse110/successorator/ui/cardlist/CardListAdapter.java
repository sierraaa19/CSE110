package edu.ucsd.cse110.successorator.ui.cardlist;


import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.ListItemCardBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class CardListAdapter extends ArrayAdapter<Goal> {
    private final Consumer<Goal> onGoalClicked;
    private final Consumer<Goal> onDeleteClicked;
    public CardListAdapter(
            Context context,
            List<Goal> goals,
            Consumer<Goal> onGoalClicked,
            Consumer<Goal> onDeleteClicked
    ) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(goals));
        this.onGoalClicked = onGoalClicked;
        this.onDeleteClicked = onDeleteClicked;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the goal for this position.
        var goal = getItem(position);

        assert goal != null;

        // Check if a view is being reused...
        ListItemCardBinding binding;
        if (convertView != null) {
            // if so, bind to it
            binding = ListItemCardBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemCardBinding.inflate(layoutInflater, parent, false);
        }

        // Populate the view with the goal's data.
        // M -> V
        binding.cardFrontText.setText(goal.text());
        if (goal.isCompleted()) {
            // TODO: replace with strikethrough
            binding.cardFrontText.setPaintFlags(binding.cardFrontText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            binding.cardFrontText.setPaintFlags(binding.cardFrontText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // V -> M
        binding.cardFrontText.setOnClickListener(v->{
            onGoalClicked.accept(goal);
        });

        //Bind the delete button to the callback.
       // binding.cardDeleteButton.setOnClickListener(v -> {
           // onDeleteClicked.accept(goal);
      //  });

        return binding.getRoot();
   }

    // The below methods aren't strictly necessary, usually.
    // But get in the habit of defining them because they never hurt
    // (as long as you have IDs for each item) and sometimes you need them.

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var flashcard = getItem(position);
        assert flashcard != null;

        var id = flashcard.id();
        assert id != null;

        return id;
    }
}
