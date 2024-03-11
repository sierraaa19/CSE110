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
    private MutableSubject<List<Goal>> dailyGoals;
    private MutableSubject<List<Goal>> oneTimeGoals;
    private MutableSubject<List<Goal>> monthlyGoals;
    private MutableSubject<List<Goal>> yearlyGoals;
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
        this.oneTimeGoals = new SimpleSubject<>();
        this.dailyGoals =  new SimpleSubject<>();
        this.monthlyGoals = new SimpleSubject<>();
        this.yearlyGoals = new SimpleSubject<>();

       goalRepositoryDB.findAll().observe(goalList -> {
            if (goalList == null) return; // not ready yet, ignore
            isEmpty.setValue(goalList.isEmpty());
            goals.setValue(goalList);
           updateGoalsForToday();
       });
        goalRepositoryDB.findAllWeeklyGoals().observe(goalList -> {
            weeklyGoals.setValue(goalList);
        });



        goals.observe(GoalList -> {
            if (GoalList == null) {
                todayGoals.setValue(new ArrayList<>());
            }
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

    public Subject<List<Goal>> getOneTimeGoals() {
        return oneTimeGoals;
    }
    public Subject<List<Goal>> getDailyGoals() {
        return dailyGoals;
    }
    public Subject<List<Goal>> getMonthlyGoals() {
        return monthlyGoals;
    }
    public Subject<List<Goal>> getYearlyGoals() {
        return yearlyGoals;
    }


    public Subject<List<Goal>> getGoalsForToday() {
        return todayGoals;
    }


    public void setCurrentDate(Date date) {
        this.currentDate = date;
        resetRecursiveGoalstoIncomplete();
        updateGoalsForToday();
    }
    /*
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
     */
    private void updateGoalsForToday() {
        LocalDate displayLocalDate = currentDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        List<Goal> currentGoals = goals.getValue();
        if (currentGoals != null) {
            List<Goal> filteredGoalsForToday = new ArrayList<>();

            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Daily"));

            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Weekly", displayLocalDate));

            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Monthly", displayLocalDate));

            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Yearly", displayLocalDate));

            filteredGoalsForToday.addAll(currentGoals.stream()
                    .filter(goal -> "One Time".equals(goal.getFrequency()) &&
                            goal.getDate().isEqual(displayLocalDate))
                    .collect(Collectors.toList()));

            todayGoals.setValue(filteredGoalsForToday);
        }
    }


    private List<Goal> filterGoalsByFrequency(List<Goal> goals, String frequency) {
        return goals.stream()
                .filter(goal -> goal.getFrequency().equals(frequency))
                .collect(Collectors.toList());
    }

    private List<Goal> filterGoalsByFrequency(List<Goal> goals, String frequency, LocalDate referenceDate) {
        switch (frequency) {
            case "Weekly":
                DayOfWeek referenceDayOfWeek = referenceDate.getDayOfWeek();
                return goals.stream()
                        .filter(goal -> goal.getFrequency().equals(frequency) && goal.getDate().getDayOfWeek() == referenceDayOfWeek)
                        .collect(Collectors.toList());
            case "Monthly":
                int referenceDayOfMonth = referenceDate.getDayOfMonth();
                return goals.stream()
                        .filter(goal -> goal.getFrequency().equals(frequency) && goal.getDate().getDayOfMonth() == referenceDayOfMonth)
                        .collect(Collectors.toList());
                /*
                if (day % 7 == 0) {
                    x = day / 7 - 1;
                } else {
                    x = (int) day / 7
                }
                 */
            case "Yearly":
                referenceDayOfMonth = referenceDate.getDayOfMonth();
                return goals.stream()
                        .filter(goal -> goal.getFrequency().equals(frequency) &&
                                goal.getDate().getMonth() == referenceDate.getMonth() &&
                                goal.getDate().getDayOfMonth() == referenceDayOfMonth)
                        .collect(Collectors.toList());
            default:
                return filterGoalsByFrequency(goals, frequency);
        }
    }
    public void resetRecursiveGoalstoIncomplete () {
        List<Goal> currentGoals = goals.getValue();
        if (currentGoals != null) {
            List<Goal> updatedGoals = new ArrayList<>();

            for (Goal goal : currentGoals) {
                if (!goal.getFrequency().equals("One Time") && goal.isCompleted()) {
                    updatedGoals.add(goal.withCompleted(false));
                } else {
                    updatedGoals.add(goal);
                }
            }
            goalRepositoryDB.save(updatedGoals);

            goals.setValue(updatedGoals);

            updateGoalsForToday();
        }
    }

}
