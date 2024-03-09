package edu.ucsd.cse110.successorator;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.format.DateTimeFormatter;
// import java.util.Date; NOTE: Use java.time API instead
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

public class MainViewModel extends ViewModel {
    // Domain state (true "Model" state)
    private final GoalRepository goalRepositoryDB;

    // UI state
    private final MutableSubject<List<Goal>> goals;
    private final MutableSubject<Goal> goal;
    private final MutableSubject<Boolean> isCompleted;

    private final MutableSubject<Boolean> isEmpty;
    private MutableSubject<List<Goal>> weeklyGoals;
    MutableSubject<List<Goal>> todayGoals = new SimpleSubject<>();
    private Date currentDate;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getGoalRepositoryDB());
                    });

    public MainViewModel(GoalRepository goalRepositoryDB) {
        this.goalRepositoryDB = goalRepositoryDB;

        // Create the observable subjects.
        this.goals = new SimpleSubject<>();
        this.goal = new SimpleSubject<>();
        this.currentDate = new Date();
        this.isCompleted = new SimpleSubject<>();
        this.isEmpty = new SimpleSubject<>();
        isEmpty.setValue(true);
        this.weeklyGoals = new SimpleSubject<>();

       goalRepositoryDB.findAll().observe(goalList -> {
            if (goalList == null) return; // not ready yet, ignore
            isEmpty.setValue(goalList.isEmpty());
            goals.setValue(goalList);
       });
        goalRepositoryDB.findAllWeeklyGoals().observe(goalList -> {
            weeklyGoals.setValue(goalList);
        });



        weeklyGoals.observe(weeklyGoalsList -> {
            LocalDate currentLocalDate = currentDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
//        Log.d("======================", currentLocalDate.toString());

            DayOfWeek currentDayOfWeek = currentLocalDate.getDayOfWeek();

            if (weeklyGoalsList == null) {
                todayGoals.setValue(new ArrayList<>()); // No goals available
                return;
            }

            List<Goal> filteredGoals = new ArrayList<>();
            for (Goal goal : weeklyGoalsList) {
                LocalDate goalCreationLocalDate = goal.getDate();
                DayOfWeek goalDayOfWeek = goalCreationLocalDate.getDayOfWeek();

                if (goalDayOfWeek == currentDayOfWeek) {
                    filteredGoals.add(goal);
                }
            }

            todayGoals.setValue(filteredGoals);
//            updateDisplayedGoals();
        });


    }

    public Subject<List<Goal>> getGoals() {
        return goals;
    }

    public MutableSubject<Boolean> getGoalsSize() {
        //List<Goal> goalsList = goals.getValue();
        return isEmpty;
    }

    public void save(Goal goal) { goalRepositoryDB.save(goal); }

    // Mainly gets called when a new goal is added.
    public void append(Goal goal) {
        goalRepositoryDB.append(goal);
        updateIsEmpty();
    }

    // Mainly gets called from CardListFragment when goal is tapped.
    public void prepend(Goal goal) {
        goalRepositoryDB.prepend(goal);
        updateIsEmpty();
    }

    public void remove (int id){
        goalRepositoryDB.remove(id);
    }

    public void removeAllCompleted() {

    }


    private void updateIsEmpty() {
        List<Goal> currentGoals = goals.getValue();
        if (currentGoals != null) {
            isEmpty.setValue(currentGoals.isEmpty());
        }
    }

    public Subject<List<Goal>> getWeeklyGoals() {
        return weeklyGoals;
    }




    public Subject<List<Goal>> getGoalsForToday() {
        return todayGoals;
    }


    public void setCurrentDate(Date date) {
        this.currentDate = date;
        updateDisplayedGoals();
    }

    public void updateDisplayedGoals() {
        LocalDate displayLocalDate = Instant.ofEpochMilli(currentDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        DayOfWeek displayDayOfWeek = displayLocalDate.getDayOfWeek();
        List<Goal> currentGoals = goals.getValue();
        List<Goal> updatedWeeklyGoals = new ArrayList<>();
        for (Goal goal : currentGoals) {
            LocalDate goalCreationDate = goal.getDate();
            DayOfWeek goalDayOfWeek = goalCreationDate.getDayOfWeek();
            if (goalDayOfWeek == displayDayOfWeek) {
                updatedWeeklyGoals.add(goal);
            }
        }
        this.todayGoals.setValue(updatedWeeklyGoals);

    }
}
