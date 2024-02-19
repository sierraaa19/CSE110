package edu.ucsd.cse110.successorator.data.db;

import static androidx.lifecycle.Transformations.map;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.util.LiveDataSubjectAdapter;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalList;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class RoomGoalRepository implements GoalRepository {
    private final GoalDao goalDao;
    private GoalList goals;

    public RoomGoalRepository(GoalDao goalDao){
        this.goalDao = goalDao;
        this.goals = new GoalList();
    }


    @Override
    public void syncLists() {
        LiveData<List<GoalEntity>> sf = goalDao.findAsLiveData();
        var flashcardLiveData = map(sf,entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });

        List<Goal> goalsData = new LiveDataSubjectAdapter<>(flashcardLiveData).getValue();

        if (goalsData == null) {
            goalsData = new ArrayList<>();
        }

        GoalList goalsLogic = new GoalList();
        List<Goal> newGoalsData;

        newGoalsData = goalsLogic.fillGoals(goalsData);

        // remove all goals from data
        goalsData.forEach(goal -> {
            goalDao.delete(goal.id());
        });

        // reinsert them
        for (int i = 0; i < newGoalsData.size(); i++) {
            GoalEntity fe = GoalEntity.fromGoal(newGoalsData.get(i));
            goalDao.insert(fe);
        }

        this.goals = goalsLogic;

    }

    @Override
    public Subject<Goal> find (int id) {
        var entityLiveData = goalDao.findAsLiveData(id);

        var flashcardLiveData = map(entityLiveData,GoalEntity::toGoal);
        return new LiveDataSubjectAdapter<>(flashcardLiveData);
    }

    @Override
    public Subject<List<Goal>> findAll(){
        var entityLiveData= goalDao.findAsLiveData();
        var flashcardLiveData = map(entityLiveData,entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(flashcardLiveData);
    }

    @Override
    public void save(Goal goal) {
        goalDao.insert(GoalEntity.fromGoal(goal));
    }

    @Override
    public void save(List<Goal> goals){
        var entities = goals.stream()
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
        goalDao.insert(entities);
    }

    @Override
    public void append(Goal goal){
        goalDao.append(GoalEntity.fromGoal(goal));
    }

    @Override
    public void prepend(Goal goal){
        syncLists();
        int sortOrder = goals.getGoalSortOrder(goal, false);

        // get index of where insertion/start of moving goals
        goal = goal.withSortOrder(sortOrder);

        // Shift all the existing cards up by one.
        goalDao.shiftSortOrders(goal.sortOrder(), goalDao.getMaxSortOrder(), 1);
        // Then insert the new card before the first one.

        //dataSource.putFlashcard(goal);
        goalDao.prepend(GoalEntity.fromGoal(goal));

        syncLists();


    }

    @Override
    public void removeCompleted() {

    }

    @Override
    public void remove(int id){
        goalDao.delete(id);
    }
}
