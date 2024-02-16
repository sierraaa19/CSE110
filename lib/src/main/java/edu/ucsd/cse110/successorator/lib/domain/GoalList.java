package edu.ucsd.cse110.successorator.lib.domain;


import java.util.ArrayList;
import java.util.List;

public class GoalList {
    private List<Goal> uncompleted_GoalList;
    private List<Goal> completed_GoalList;

    public GoalList() {
        uncompleted_GoalList = new ArrayList<>();
        completed_GoalList = new ArrayList<>();
    }

    public void addGoal(Goal goal) {
        if (goal.isCompleted()) {
            completed_GoalList.add(goal);
        } else {
            uncompleted_GoalList.add(goal);
        }

        System.out.printf(this.toString());
    }

    @Override
    public String toString() {
        var unComplte = "";
        var complete = "";
        for (Goal g : uncompleted_GoalList){

            unComplte += g + " ";
        }
        for (Goal g : completed_GoalList){

            complete += g + " ";
        }

        return unComplte + "\n" + complete;
    }
}
