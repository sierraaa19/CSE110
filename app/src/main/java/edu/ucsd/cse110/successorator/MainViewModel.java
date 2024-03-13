package edu.ucsd.cse110.successorator;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
// import java.util.Date; NOTE: Use java.time API instead
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    MutableSubject<List<Goal>> tomorrowGoals = new SimpleSubject<>();

    MutableSubject<List<Goal>> pendingGoals = new SimpleSubject<>();

    MutableSubject<List<Goal>> recurringGoals = new SimpleSubject<>();
    private Date currentDate;

    private MutableSubject<String> label ;

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
        this.label = new SimpleSubject<>();
        isEmpty.setValue(true);
        label.setValue("Today");
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
           updateGoalsForTomorrow();
           updateGoalsForRecurring();
       });
        goalRepositoryDB.findAllWeeklyGoals().observe(goalList -> {
            weeklyGoals.setValue(goalList);
        });



        goals.observe(GoalList -> {
            if (GoalList == null) {
                todayGoals.setValue(new ArrayList<>());
                tomorrowGoals.setValue(new ArrayList<>());
                pendingGoals.setValue(new ArrayList<>());
                recurringGoals.setValue(new ArrayList<>());
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
        updateGoalsForTomorrow();
        updateGoalsForRecurring();
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

            // Collect goals based on frequency
            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Daily"));
            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Weekly", LocalDate.from(displayLocalDate)));
            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Monthly", LocalDate.from(displayLocalDate)));
            filteredGoalsForToday.addAll(filterGoalsByFrequency(currentGoals, "Yearly", LocalDate.from(displayLocalDate)));
            filteredGoalsForToday.addAll(currentGoals.stream()
                    .filter(goal -> "One Time".equals(goal.getFrequency()) && goal.getDate().isEqual(displayLocalDate))
                    .collect(Collectors.toList()));

            // Separate into incomplete and complete goals
            List<Goal> incompleteGoals = filteredGoalsForToday.stream()
                    .filter(goal -> !goal.isCompleted())
                    .collect(Collectors.toList());
            List<Goal> completeGoals = filteredGoalsForToday.stream()
                    .filter(Goal::isCompleted)
                    .collect(Collectors.toList());

            // Define context order
            List<String> contextOrder = List.of("Home", "Work", "School", "Errands");
            Map<String, Integer> contextPriority = new HashMap<>();
            for (int i = 0; i < contextOrder.size(); i++) {
                contextPriority.put(contextOrder.get(i), i);
            }
            incompleteGoals.sort((goal1, goal2) -> {
                // First, compare by context priority
                int contextCompare = Integer.compare(
                        contextPriority.getOrDefault(goal1.getContext(), Integer.MAX_VALUE),
                        contextPriority.getOrDefault(goal2.getContext(), Integer.MAX_VALUE));
                if (contextCompare != 0) {
                    return contextCompare;
                }

                // If contexts are the same, directly compare by sortOrder
                return Integer.compare(goal2.sortOrder(), goal1.sortOrder());
            });


            // Combine the sorted incomplete goals with complete goals
            List<Goal> sortedGoals = new ArrayList<>(incompleteGoals);
            sortedGoals.addAll(completeGoals);

            todayGoals.setValue(sortedGoals);
        }
    }


    private void updateGoalsForTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        LocalDate displayLocalDate = calendar.getTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();


        List<Goal> currentGoals = goals.getValue();
        if (currentGoals != null) {
            List<Goal> filteredGoalsForTomorrow = new ArrayList<>();

            filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Daily"));

            filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Weekly", displayLocalDate));

            filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Monthly", displayLocalDate));

            filteredGoalsForTomorrow.addAll(filterGoalsByFrequency(currentGoals, "Yearly", displayLocalDate));


            filteredGoalsForTomorrow.sort(Comparator.comparing(Goal::getContext));
            tomorrowGoals.setValue(filteredGoalsForTomorrow);

        }
    }

    private void updateGoalsForRecurring() {


        List<Goal> currentGoals = goals.getValue();
        if (currentGoals != null) {
            List<Goal> filteredGoalsForRecurring = new ArrayList<>();

            filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Daily"));

            filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Weekly"));

            filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Monthly"));

            filteredGoalsForRecurring.addAll(filterGoalsByFrequency(currentGoals, "Yearly"));


            filteredGoalsForRecurring.sort(Comparator.comparing(Goal::getContext));

            recurringGoals.setValue(filteredGoalsForRecurring);

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
                Month referenceMonth = referenceDate.getMonth();
                int referenceWeekInMonth = (referenceDate.getDayOfMonth() + 6) / 7;
                DayOfWeek referenceWeekDay = referenceDate.getDayOfWeek();

                return goals.stream()
                        .filter(goal -> {
                            if (!goal.getFrequency().equals(frequency)) {
                                return false;
                            }

                            LocalDate goalDate = goal.getDate();
                            Month goalMonth = goalDate.getMonth();
                            int goalWeekInMonth = (goalDate.getDayOfMonth() + 6) / 7;
                            DayOfWeek goalWeekDay = goalDate.getDayOfWeek();
                            LocalDate firstDayOfNextMonth = referenceDate.plusMonths(1).withDayOfMonth(1);
                            LocalDate firstSameDayOfWeekNextMonth = firstDayOfNextMonth.with(TemporalAdjusters.firstInMonth(goalWeekDay));
                            LocalDate lastSameDayOfWeekLasttMonth = referenceDate.minusMonths(1).with(TemporalAdjusters.lastInMonth(referenceWeekDay));
                            int lastWeekInMonth = (lastSameDayOfWeekLasttMonth.getDayOfMonth()+6)/7;
                            if (goalWeekInMonth==5 && !referenceDate.equals(goalDate)&&referenceWeekInMonth!=5 &&lastWeekInMonth<5 ){
                                return goalWeekDay == referenceWeekDay && referenceWeekInMonth==1 && referenceMonth.getValue()>goalMonth.plus(1).getValue();
                            }

                            return goalWeekInMonth == referenceWeekInMonth && goalWeekDay == referenceWeekDay;
                        })
                        .collect(Collectors.toList());

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
            updateGoalsForTomorrow();
            updateGoalsForRecurring();
        }
    }

    public void toToday(){
        label.setValue("Today");
    }

    public Date getDate(){
        return currentDate;
    }

    public void toTomorrow(){
        label.setValue("Tomorrow");
    }
    public void toPending(){
        label.setValue("Pending");
    }
    public void toRecurring(){
        label.setValue("Recurring");
    }

    public Subject<String> getLabel(){
        return label;
    }

    public Subject<List<Goal>>  getGoalsForTomorrow() {
        return tomorrowGoals;
    }

    public static LocalDate getNextMonthSameDayOfWeek() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        // Calculate the ordinal (nth occurrence) of today's dayOfWeek in the current month
        int ordinal = (today.getDayOfMonth() - 1) / 7 + 1;

        // Get the first day of the next month
        LocalDate firstDayOfNextMonth = today.plusMonths(1).withDayOfMonth(1);

        // Find the nth occurrence of today's dayOfWeek in the next month
        LocalDate nextMonthSameDayOfWeek = firstDayOfNextMonth.with(TemporalAdjusters.dayOfWeekInMonth(ordinal, dayOfWeek));

        return nextMonthSameDayOfWeek;
    }


    public Subject<List<Goal>> getGoalsForPending() {
        return pendingGoals;
    }

    public Subject<List<Goal>> getGoalsForRecurring() {
        return recurringGoals;
    }
}
