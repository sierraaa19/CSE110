package edu.ucsd.cse110.successorator;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.ui.cardlist.dialog.CreateCardDialogFragment;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;
    private static Date date; // date show in Successorator

    private MainViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        var binding = ActivityMainBinding.inflate(getLayoutInflater());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(binding.getRoot());

        // Show Current Day
        date = new Date();
        displayDate();
        // nextDay Button
        ImageButton next_day_button = (ImageButton)findViewById(R.id.imageButton_next);
        next_day_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextDay();
                displayDate();
                removeAllCompleted();
            }
        });

        viewModel.getGoalsSize().observe(isEmpty -> {
            TextView textViewMessage = findViewById(R.id.text_view_no_goals);
            if (isEmpty) {
                // No goals present, show the message
                textViewMessage.setVisibility(View.VISIBLE);
            } else {
                // Goals are present, hide the message
                textViewMessage.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        var itemId = item.getItemId();

        if (itemId == R.id.action_bar_menu_swap_views) {
            // change to add button
            // TODO: make swap button do somnething
            var dialogFragment = CreateCardDialogFragment.newInstance(date);
            dialogFragment.show(getSupportFragmentManager(), "CreateDialogFragment");
        }

        return super.onOptionsItemSelected(item);
    }

    // next day
    private void nextDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        date = calendar.getTime();
        // you want to refresg the list of goals currently being displayed

    }

    // display Date in textview
    private void displayDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, M/d");
        String currentDate = dateFormat.format(date);
        Log.d("===============", date.toString());
        viewModel.setDate(date);
        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);
    }

    private void removeAllCompleted() {
        viewModel.removeAllCompleted();
    }
}
