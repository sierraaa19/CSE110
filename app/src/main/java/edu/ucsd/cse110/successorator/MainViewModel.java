package edu.ucsd.cse110.successorator;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import androidx.lifecycle.LiveData;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.GoalList;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;
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
    private final Subject<List<Goal>> weeklyGoals;
    private final Subject<List<Goal>> completedWeeklyGoals;
    private Date d;

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
        this.isCompleted = new SimpleSubject<>();
        this.isEmpty = new SimpleSubject<>();


        isEmpty.setValue(true);
        this.weeklyGoals = goalRepositoryDB.findAllWeeklyGoals();
        this.completedWeeklyGoals = goalRepositoryDB.findAllWeeklyGoals();
        // When the list of cards changes (or is first loaded), reset the ordering.
        //goalRepositoryDB.findAll().observe(goalList -> {
        //        if (goalList == null) return; // not ready yet, ignore
        //        var goalListSorted = goalList.stream()
        //                .sorted(Comparator.comparingInt(Goal::sortOrder))
        //                .collect(Collectors.toList());
        //        // goalRepository.save(goalListSorted);
        //});

       goalRepositoryDB.findAll().observe(goalList -> {
            if (goalList == null) return; // not ready yet, ignore

           Log.d("findAll", "Start");
           goalList.forEach(g -> {
               Log.d("findAll", g.toString());
           });
           isEmpty.setValue(goalList.isEmpty());
            goals.setValue(goalList);
       });


    }

    //public Subject<List<Goal>> getGoals() {
    //    return goals;
    //}

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
        // List<Goal> saveGoals = goalRepository.append(goal);
        goal.setDate(getDate());

        goalRepositoryDB.append(goal);
        updateIsEmpty();
    }

    // Mainly gets called from CardListFragment when goal is tapped.
    public void prepend(Goal goal) {

        goalRepositoryDB.prepend(goal);
        updateIsEmpty();
    }

    public void setDate(Date d){
        this.d = d;
        System.out.println("Date = " + this.d);
    }

    public Date getDate(){
        return this.d;
    }

    //public void syncLists() {
    //    // List<Goal> saveGoals = goalRepository.syncLists();
    //    // goalRepositoryDB.save(saveGoals);
    //}

    public void remove (int id){
        // List<Goal> saveGoals = goalRepository.remove(id);
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

    public Subject<List<Goal>> getCompletedWeeklyGoals() {
        return completedWeeklyGoals;
    }
}
