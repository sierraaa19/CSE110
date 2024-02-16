package edu.ucsd.cse110.successorator.lib.domain;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GoalList {
    private List<Goal> uncompleted_GoalList;
    private List<Goal> completed_GoalList;

    public GoalList() {
        uncompleted_GoalList = new ArrayList<>();
        completed_GoalList = new ArrayList<>();
    }

    public void addGoal(Goal goal) {
        if (goal.goalStatus()) {
            completed_GoalList.add(goal);
        } else {
            uncompleted_GoalList.add(goal);
        }
    }

    public List<Goal> getCompletedGoals() {
        return completed_GoalList;
    }

    public List<Goal> getUncompletedGoals() {
        return uncompleted_GoalList;
    }


}
