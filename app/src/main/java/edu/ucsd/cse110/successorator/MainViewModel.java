package edu.ucsd.cse110.successorator;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.domain.FilterGoals;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class MainViewModel extends ViewModel {
    private final GoalRepository goalRepositoryDB;
    private final MutableSubject<Goal> goal;
    private final MutableSubject<Boolean> isCompleted;
    private final MutableSubject<Boolean> isEmpty;

    // allGoals, today, tomorrow, pending, recurring
    // Changes on database update only
    MutableSubject<List<Goal>> allGoals;
    MutableSubject<List<Goal>> todayGoals;
    MutableSubject<List<Goal>> tomorrowGoals;
    MutableSubject<List<Goal>> pendingGoals;
    MutableSubject<List<Goal>> recurringGoals;
    MutableSubject<List<Goal>> forDateGoals;


    // allGoals, today, tomorrow, pending, recurring
    // Changes on state update and database update
    MutableSubject<List<Goal>> showingGoals;
    MutableSubject<List<Goal>> showingTodayGoals;
    MutableSubject<List<Goal>> showingTomorrowGoals;
    MutableSubject<List<Goal>> showingPendingGoals;
    MutableSubject<List<Goal>> showingRecurringGoals;
    MutableSubject<List<Goal>> showingForDateGoals;

    //private final ArrayList<String> dropdown = new ArrayList<>(Arrays.asList("Today", "Tomorrow", "Pending", "Recurring"));
    private MutableSubject<String> currentDateSubject;
    private String currentDate = SuccessDate.getCurrentDateAsString();

    // focus can be: Home, Work, School, Errands, All
    private MutableSubject<String> focus;
    // label can be: Today, Tomorrow, Pending, Recurring
    private MutableSubject<String> label;

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
        this.goal = new SimpleSubject<>();

        this.isCompleted = new SimpleSubject<>();
        this.isEmpty = new SimpleSubject<>();
        isEmpty.setValue(true);

        // state
        this.label = new SimpleSubject<>();
        this.focus = new SimpleSubject<>();
        this.label.setValue("Today");
        this.focus.setValue("All");

        // Semi-Asynchronous, changes depending on focus, label state and database update
        this.showingGoals = new SimpleSubject<>();
        this.showingTodayGoals = new SimpleSubject<>();
        this.showingTomorrowGoals = new SimpleSubject<>();
        this.showingPendingGoals = new SimpleSubject<>();
        this.showingRecurringGoals = new SimpleSubject<>();

        this.showingGoals.setValue(new ArrayList<>());
        this.showingTodayGoals.setValue(new ArrayList<>());
        this.showingTomorrowGoals.setValue(new ArrayList<>());
        this.showingPendingGoals.setValue(new ArrayList<>());
        this.showingRecurringGoals.setValue(new ArrayList<>());

        // for advanced date
        this.showingForDateGoals = new SimpleSubject<>();
        this.showingForDateGoals.setValue(new ArrayList<>());

        // Asynchronous, changes only on database update
        this.allGoals = new SimpleSubject<>();
        this.todayGoals = new SimpleSubject<>();
        this.tomorrowGoals = new SimpleSubject<>();
        this.pendingGoals = new SimpleSubject<>();
        this.recurringGoals = new SimpleSubject<>();

        this.forDateGoals = new SimpleSubject<>();
        this.currentDateSubject = new SimpleSubject<>();

        setupDateObserver();
        setupDatabaseObservers();
        setupAllGoalsObserver();
        setupFocusStateObserver();
        setupLabelStateObserver();
    }

    public void setupDateObserver() {
        currentDateSubject.observe(date -> {
            if (date == null) return;
            if (date.equals(SuccessDate.getCurrentDateAsString())) {
                this.label.setValue("Today");
            } else if (date.equals(SuccessDate.getTmwsDateAsString())) {
                this.label.setValue("Tomorrow");
            } else if (!date.equals(SuccessDate.getCurrentDateAsString()) && !date.equals(SuccessDate.getTmwsDateAsString())) {
                this.label.setValue(date);
            }

            if (!date.equals(SuccessDate.getCurrentDateAsString())) {
                removeAllCompleted();
            }
        });
    }

    public void setupDatabaseObservers() {
        // When database updates, reflect changes on UI
        // e.g. if we add a new goal in TomorrowFragment,
        // and this goal has a date for tomorrow,
        // we want this goal to show up there right away.
        todayGoals.observe(goalList -> {
            if (goalList == null || focus.getValue() == null) return;

            goalList = FilterGoals.filterByCompletedAndContext(goalList);
            showingTodayGoals.setValue(FilterGoals.focusFilter(goalList, focus.getValue()));
        });
        tomorrowGoals.observe(goalList -> {
            if (goalList == null || focus.getValue() == null) return;

            goalList = FilterGoals.filterByCompletedAndContext(goalList);
            showingTomorrowGoals.setValue(FilterGoals.focusFilter(goalList, focus.getValue()));
        });
        pendingGoals.observe(goalList -> {
            if (goalList == null || focus.getValue() == null) return;

            goalList = FilterGoals.filterByCompletedAndContext(goalList);
            showingPendingGoals.setValue(FilterGoals.focusFilter(goalList, focus.getValue()));
        });
        recurringGoals.observe(goalList -> {
            if (goalList == null || focus.getValue() == null) return;

            goalList = FilterGoals.filterByCompletedAndContext(goalList);
            showingRecurringGoals.setValue(FilterGoals.focusFilter(goalList, focus.getValue()));
        });
        forDateGoals.observe(goalList -> {
            if (goalList == null || focus.getValue() == null) return;

            goalList = FilterGoals.filterByCompletedAndContext(goalList);
            //showingForDateGoals.setValue(FilterGoals.recurringFilter(FilterGoals.focusFilter(goalList, focus.getValue()), currentDateSubject.getValue(), false));
            showingForDateGoals.setValue(FilterGoals.focusFilter(goalList, focus.getValue()));
        });
    }

    public void setupAllGoalsObserver() {
        // get all goals from database, update on database change only
        // NOTE: showingGoals == todayGoals
        goalRepositoryDB.findAll().observe(goalList -> {
            if (goalList == null || label.getValue() == null || focus.getValue() == null)
                return; // not ready yet, ignore
            isEmpty.setValue(goalList.isEmpty());

            goalList = FilterGoals.filterByCompletedAndContext(goalList);

            allGoals.setValue(goalList);
            showingGoals.setValue(FilterGoals.labelFilter(allGoals.getValue(), label.getValue()));

            // filter for todays goals, also filter for label and focus SOON
            todayGoals.setValue(FilterGoals.recurringFilter(goalList, SuccessDate.getCurrentDateAsString(), false));
            tomorrowGoals.setValue(FilterGoals.recurringFilter(goalList, SuccessDate.getTmwsDateAsString(), false));
            pendingGoals.setValue(FilterGoals.pendingFilter(goalList));
            recurringGoals.setValue(FilterGoals.recurringFilter(goalList, null, true));

            forDateGoals.setValue(FilterGoals.recurringFilter(goalList, currentDateSubject.getValue(), false));
        });
    }

    public void setupFocusStateObserver() {
        focus.observe(focusString -> {
            stateObserverBody(focusString, label.getValue());
        });
    }

    public void setupLabelStateObserver() {
        label.observe(labelString -> {
            stateObserverBody(focus.getValue(), labelString);
        });
    }

    public void stateObserverBody(String f, String l) {
        if (l == null || f == null) return;
        List<Goal> temp1;
        List<Goal> temp2;
        List<Goal> temp3;
        List<Goal> temp4;
        List<Goal> temp5;
        if (todayGoals.getValue() != null) {
            temp1 = FilterGoals.labelFilter(todayGoals.getValue(), l);
            temp1 = FilterGoals.focusFilter(temp1, f);
            temp1 = FilterGoals.recurringFilter(temp1, SuccessDate.getCurrentDateAsString(), false);
            showingTodayGoals.setValue(temp1);
        }
        if (tomorrowGoals.getValue() != null) {
            temp2 = FilterGoals.labelFilter(tomorrowGoals.getValue(), l);
            temp2 = FilterGoals.focusFilter(temp2, f);
            temp2 = FilterGoals.recurringFilter(temp2, SuccessDate.getTmwsDateAsString(), false);
            showingTomorrowGoals.setValue(temp2);
        }
        if (pendingGoals.getValue() != null) {
            temp3 = FilterGoals.labelFilter(pendingGoals.getValue(), l);
            temp3 = FilterGoals.focusFilter(temp3, f);
            temp3 = FilterGoals.pendingFilter(temp3);
            showingPendingGoals.setValue(temp3);
        }
        if (recurringGoals.getValue() != null) {
            temp4 = FilterGoals.labelFilter(recurringGoals.getValue(), l);
            temp4 = FilterGoals.focusFilter(temp4, f);
            temp4 = FilterGoals.recurringFilter(temp4, null, true);
            showingRecurringGoals.setValue(temp4);
        }
        if (allGoals.getValue() != null) {
            temp5 = FilterGoals.labelFilter(allGoals.getValue(), l);
            temp5 = FilterGoals.focusFilter(temp5, f);
            temp5 = FilterGoals.recurringFilter(temp5, SuccessDate.getCurrentDateAsString(), false);
            showingGoals.setValue(temp5);
        }
        if (forDateGoals.getValue() != null) {
            temp1 = FilterGoals.labelFilter(allGoals.getValue(), l);
            temp1 = FilterGoals.focusFilter(temp1, f);
            temp1 = FilterGoals.recurringFilter(temp1, currentDateSubject.getValue(), false);
            showingForDateGoals.setValue(temp1);
        }
    }

    public Subject<String> getFocus() {
        return focus;
    }

    public String getDate() {
        return currentDate;
    }

    public String getDateOtherFormat() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EE M/d");
        String formatDate = SuccessDate.stringToDate(currentDate).format(dateFormat);
        return formatDate;
    }

    public Subject<String> getLabel() {
        return label;
    }

    public Subject<List<Goal>> getGoals() {
        return showingGoals;
    }

    /*public Subject<List<Goal>> getGoalsForToday() {
        return showingTodayGoals;
    }*/

    public Subject<List<Goal>> getGoalsForTomorrow() {
        return showingTomorrowGoals;
    }

    public Subject<List<Goal>> getGoalsForPending() {
        return showingPendingGoals;
    }

    public Subject<List<Goal>> getGoalsForRecurring() {
        return showingRecurringGoals;
    }

    public Subject<List<Goal>> getGoalsForDate() {
        return showingForDateGoals;
    }

    public void toToday() {
        label.setValue("Today");
    }

    public void toTomorrow() {
        label.setValue("Tomorrow");
    }

    public void toPending() {
        label.setValue("Pending");
    }

    public void toRecurring() {
        label.setValue("Recurring");
    }

    public void focusHome() {
        focus.setValue("Home");
    }

    public void focusWork() {
        focus.setValue("Work");
    }

    public void focusSchool() {
        focus.setValue("School");
    }

    public void focusErrands() {
        focus.setValue("Errands");
    }

    public void focusAll() {
        focus.setValue("All");
    }

    /* public void save(Goal goal) { goalRepositoryDB.save(goal); }*/

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

    public void remove(int id) {
        goalRepositoryDB.remove(id);
    }

    public void removeAllCompleted() {
        goalRepositoryDB.removeAllCompleted();
    }

    public MutableSubject<Boolean> getGoalsSize() {
        //List<Goal> goalsList = goals.getValue();
        return isEmpty;
    }

    private void updateIsEmpty() {
        List<Goal> currentGoals = showingGoals.getValue();
        if (currentGoals != null) {
            isEmpty.setValue(currentGoals.isEmpty());
        }
    }

    public void setCurrentDate(String date) {
        this.currentDate = date;

        // the date passed in is in the format EEEE M/d
        // we want M-D-YYYY
        DateTimeFormatter formatting = DateTimeFormatter.ofPattern(SuccessDate.getFormatString());
        String currentDate = SuccessDate.stringToDate(this.currentDate).format(formatting);
        this.currentDateSubject.setValue(currentDate);
    }
}