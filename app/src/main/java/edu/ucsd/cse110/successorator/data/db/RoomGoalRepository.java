package edu.ucsd.cse110.successorator.data.db;

import static androidx.lifecycle.Transformations.map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.util.LiveDataSubjectAdapter;

public class RoomGoalRepository implements GoalRepository {
    private final GoalDao goalDao;

    public RoomGoalRepository(GoalDao goalDao) {
        this.goalDao = goalDao;
    }

    List<Goal> getCompletedOrUncompleted(boolean isComplete) {
        List<GoalEntity> goals = goalDao.findAll();
        List<Goal> newGoals = new ArrayList<Goal>();
        goals.forEach(goalE -> {
            if (goalE.isCompleted == isComplete) {
                newGoals.add(goalE.toGoal());
            }
        });
        newGoals.forEach(goal -> {
            goalDao.delete(goal.id());
        });
        return newGoals;
    }

    @Override
    public Subject<Goal> find(int id) {
        var entityLiveData = goalDao.findAsLiveData(id);

        var flashcardLiveData = map(entityLiveData, GoalEntity::toGoal);
        return new LiveDataSubjectAdapter<>(flashcardLiveData);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        var entityLiveData = goalDao.findAsLiveData();
        var flashcardLiveData = map(entityLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(flashcardLiveData);
    }

/*    @Override
    public void save(Goal goal) {
        goalDao.insert(GoalEntity.fromGoal(goal));
    }*/

    @Override
    public void save(List<Goal> goals) {
        var entities = goals.stream()
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
        goalDao.insert(entities);
    }

    @Override
    public void append(Goal goal) {
        List<Goal> goalsC = getCompletedOrUncompleted(true);
        List<Goal> goalsU = getCompletedOrUncompleted(false);

        if (goal.isCompleted()) {
            goalsC.add(goal);
        } else {
            goalsU.add(goal);
        }
        List<Goal> newAllGoals = new ArrayList<Goal>();
        List<Goal> allGoals = new ArrayList<Goal>(goalsU);
        allGoals.addAll(goalsC);

        // withId withSOrt order causes us to lose Goal creationDate and frequency by calling the constructor.
        // all the information should basically be the same except for maybe the sort order and the id
        for (int i = 0; i < allGoals.size(); i++) {
            Goal g = allGoals.get(i).withId(i).withSortOrder(i + 1);
            g.setFrequency(allGoals.get(i).getFrequency());
            g.setDate(allGoals.get(i).getDate());
            newAllGoals.add(g);
        }

        save(newAllGoals);
    }

    @Override
    public void prepend(Goal goal) {
        List<Goal> goalsC = getCompletedOrUncompleted(true);
        List<Goal> goalsU = getCompletedOrUncompleted(false);

        if (goal.isCompleted()) {
            goalsC.add(0, goal);
        } else {
            goalsU.add(0, goal);
        }
        List<Goal> newAllGoals = new ArrayList<Goal>();
        List<Goal> allGoals = new ArrayList<Goal>(goalsU);
        allGoals.addAll(goalsC);

        // reset id and sortOrder?
        for (int i = 0; i < allGoals.size(); i++) {
            Goal g = allGoals.get(i).withId(i).withSortOrder(i + 1);
            g.setFrequency(allGoals.get(i).getFrequency());
            g.setDate(allGoals.get(i).getDate());

            newAllGoals.add(g);
        }

        save(newAllGoals);
    }

    @Override
    public void removeAllCompleted() {
        List<Goal> goalsList = getCompletedOrUncompleted(true);
        List<Goal> filteredList = new ArrayList<>();
        for (Goal goal : goalsList) {
            if (goal.getDate().equals(SuccessDate.dateToString(SuccessDate.getCurrentDate().minusDays(1)))) {
                filteredList.add(goal);
            }
        }

        filteredList.forEach(goal -> {
            if (goal.id() != null) {
                goalDao.delete(goal.id());
            }
        });
    }

    @Override
    public void remove(int id){
        goalDao.delete(id);
    }

    // Implementing repository methods for "Weekly" goals
    //public Subject<List<Goal>> findAllWeeklyGoals() {
    //    LiveData<List<GoalEntity>> liveData = goalDao.findAllWeeklyGoals();
    //    LiveData<List<Goal>> transformedLiveData = Transformations.map(liveData, entities ->
    //            entities.stream().map(GoalEntity::toGoal).collect(Collectors.toList()));
    //    return new LiveDataSubjectAdapter<>(transformedLiveData);
    //}

    //public Subject<List<Goal>> findAllFrequencyGoals(String frequency) {
    //    List<GoalEntity> freqGoals = goalDao.findAllFrequencyGoals(frequency);

    //    return new LiveDataSubjectAdapter<>(transformedLiveData);
    //}

    //@Override
    //public Subject<List<Goal>> findAllContextsGoals(String context) {
    //    List<GoalEntity> contextGoals = goalDao.findAllContextGoals(context);
    //    List<Goal> mappedGoals = contextGoals.stream().map(GoalEntity::toGoal).collect(Collectors.toList()));
    //    MutableSubject<>

    //    return new MutableSubject<>(mappedGoals);
    //}

/*    @Override
    public Subject<List<Goal>> findAllDropdownGoalsLiveData(String choice) {
        // get all of Todays goals
        String currDate = SuccessDate.dateToString(SuccessDate.getCurrentDate());
        Log.d("RoomGoalRepo", currDate);
        LiveData<List<GoalEntity>> liveData1 = goalDao.findAllGoalsAtDate(currDate);
        LiveData<List<Goal>> transformedLiveData1 =
                Transformations.map(liveData1, entities ->
                                entities.
                                stream().
                                map(GoalEntity::toGoal).
                                collect(Collectors.toList()));
        Subject<List<Goal>> todaysGoals = new LiveDataSubjectAdapter<>(transformedLiveData1);

        // get all of Tomorrows goals
        String tmwsDate = SuccessDate.dateToString(SuccessDate.getCurrentDate().plusDays(1));
        LiveData<List<GoalEntity>> liveData2 = goalDao.findAllGoalsAtDate(tmwsDate);
        LiveData<List<Goal>> transformedLiveData2 =
                Transformations.map(liveData2, entities ->
                        entities.
                                stream().
                                map(GoalEntity::toGoal).
                                collect(Collectors.toList()));
        Subject<List<Goal>> tmwsGoals = new LiveDataSubjectAdapter<>(transformedLiveData2);

        // get all Recurring Goals, sorted by date from oldest down to newest
        LiveData<List<GoalEntity>> liveData3 = goalDao.findAsLiveData();
        LiveData<List<Goal>> transformedLiveData3 =
                Transformations.map(liveData3, entities ->
                                entities.
                                stream().
                                map(GoalEntity::toGoal).
                                collect(Collectors.toList()));
        // this will need filtering in ModelView or Fragment
        Subject<List<Goal>> recurringGoals = new LiveDataSubjectAdapter<>(transformedLiveData3);

        // get all Pending Goals
        String pendingDate = "Pending";
        LiveData<List<GoalEntity>> liveData4 = goalDao.findAllGoalsAtDate(pendingDate);
        LiveData<List<Goal>> transformedLiveData4 =
                Transformations.map(liveData4, entities ->
                        entities.
                                stream().
                                map(GoalEntity::toGoal).
                                collect(Collectors.toList()));
        Subject<List<Goal>> pendingGoals = new LiveDataSubjectAdapter<>(transformedLiveData4);

        Subject<List<Goal>> finalData = recurringGoals; // recurringGoals is all goals in database by default
        if (choice.equals("Today")) {
            finalData = todaysGoals;
        } else if (choice.equals("Tomorrow")) {
            finalData = tmwsGoals;
        } else if (choice.equals("Pending")) {
            finalData = pendingGoals;
        } else if (choice.equals("Recurring")) {
            finalData = recurringGoals; // change this soon
        }

        return finalData;
    }

    public String getDisplayDate (Goal goal){
        return goal.getDate();
    }

    @Override
    public Subject<List<Goal>> findAllFrequencyGoals(String freq) {
        return null;
    }

    @Override
    public Subject<List<Goal>> findAllContextsGoals(String context) {
        return null;
    }*/
}
