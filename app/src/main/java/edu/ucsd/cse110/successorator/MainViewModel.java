package edu.ucsd.cse110.successorator;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private final GoalRepository goalRepository;
    private final GoalRepository goalRepositoryDB;

    // UI state
    private final MutableSubject<List<Goal>> goals;
    private final MutableSubject<Goal> goal;
    private final MutableSubject<Boolean> isCompleted;

    private final MutableSubject<Boolean> isEmpty;


    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getGoalRepository(), app.getGoalRepositoryDB());
                    });

    public MainViewModel(GoalRepository goalRepository, GoalRepository goalRepositoryDB) {
        this.goalRepository = goalRepository;
        this.goalRepositoryDB = goalRepositoryDB;

        // Create the observable subjects.
        this.goals = new SimpleSubject<>();
        this.goal = new SimpleSubject<>();
        this.isCompleted = new SimpleSubject<>();
        this.isEmpty = new SimpleSubject<>();

        isEmpty.setValue(true);

        // When the list of cards changes (or is first loaded), reset the ordering.
        goalRepositoryDB.findAll().observe(goalList -> {
                if (goalList == null) return; // not ready yet, ignore

                var goalListSorted = goalList.stream()
                        .sorted(Comparator.comparingInt(Goal::sortOrder))
                        .collect(Collectors.toList());

                goalRepository.save(goalListSorted);
        });

       goalRepository.findAll().observe(goalList -> {
            if (goalList == null) return; // not ready yet, ignore

            var goalListSorted = goalList.stream()
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .collect(Collectors.toList());
            goals.setValue(goalListSorted);

            goalRepositoryDB.save(goals.getValue());
       });

       goalRepository.findAll().observe(goals -> {
           if (goals.isEmpty()) {
               isEmpty.setValue(true);
           }
           else {
               isEmpty.setValue(false);
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

    public void save(Goal goal) { goalRepository.save(goal); }

    public void append(Goal goal) {
        List<Goal> saveGoals = goalRepository.append(goal);
        goalRepositoryDB.save(saveGoals);
    }

    public void prepend(Goal goal) {
        List<Goal> saveGoals = goalRepository.prepend(goal);
        goalRepositoryDB.save(saveGoals);
    }

    public void syncLists() {
        List<Goal> saveGoals = goalRepository.syncLists();
        goalRepositoryDB.save(saveGoals);
    }

    public void remove (int id){
        List<Goal> saveGoals = goalRepository.remove(id);
        goalRepositoryDB.save(saveGoals);
    }

    public void removeCompleted() {
        List<Goal> deleteGoals = goalRepository.removeCompleted();
        deleteGoals.forEach(goal -> {
            goalRepositoryDB.remove(goal.id());
        });
    }
}
