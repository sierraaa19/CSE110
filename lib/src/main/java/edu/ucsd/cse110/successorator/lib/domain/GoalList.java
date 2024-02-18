package edu.ucsd.cse110.successorator.lib.domain;


import java.util.ArrayList;
import java.util.List;

public class GoalList {
    private List<Goal> uncompletedGoals;
    private List<Goal> completedGoals;
    private List<Goal> goals;

    public GoalList() {
        uncompletedGoals = new ArrayList<>();
        completedGoals = new ArrayList<>();
        goals = new ArrayList<>();
    }

    public void addGoal(Goal goal) {
        if (goal.isCompleted()) {
            completedGoals.add(goal);
        } else {
            uncompletedGoals.add(goal);
        }

        System.out.printf(this.toString());
    }

    public List<Goal> fillGoals(List<Goal> goalsData) {
        this.goals = goalsData;

        // NOTE: the data coming in may not be in correct order.
        // rn we are simply ...
        for (int i = 0; i < goalsData.size(); i++) {
            // separate into two lists of completed and uncompleted.
            if (goalsData.get(i).isCompleted()) {
                completedGoals.add(goalsData.get(i));
            } else {
                uncompletedGoals.add(goalsData.get(i));
            }
        }

        // Resets sortOrder to be correct
        // and create two new lists in the process
        int order = 0;
        Goal newGoal;
        List<Goal> newUncompletedGoals = new ArrayList<Goal>();
        for (int i = 0; i < uncompletedGoals.size(); i++) {
            newGoal = uncompletedGoals.get(i).withSortOrder(order);
            newUncompletedGoals.add(newGoal);
            order++;
        }


        List<Goal> newCompletedGoals = new ArrayList<Goal>();
        // reset sortOrders to be correct for completedGOals
        for (int i = 0; i < completedGoals.size(); i++) {
            newGoal = completedGoals.get(i).withSortOrder(order);
            newCompletedGoals.add(newGoal);
            order++;
        }

        // simply append to a singular list
        // this list can be sent back to update the Data
        this.uncompletedGoals = newUncompletedGoals;
        this.completedGoals = newCompletedGoals;

        // simply append the two lists to create the correct goals
        this.goals = new ArrayList<>(uncompletedGoals);
        goals.addAll(completedGoals);

        return this.goals;
    }

    public int getGoalSortOrder(Goal goal) {
        Goal newGoal = null;
        boolean done = false;
        int sortOrder = -1;

        for (int i = 0; i < this.goals.size(); i++) {
            if (goal.isCompleted() && goals.get(i).isCompleted() && !done) {
                sortOrder = goals.get(i).sortOrder();
                done = true;
            } else if (!goal.isCompleted() && !goals.get(i).isCompleted() && !done) {
                sortOrder = goals.get(i).sortOrder();
                done = true;
            }
        }

        return sortOrder;
    }

    @Override
    public String toString() {
        var unComplte = "";
        var complete = "";
        for (Goal g : uncompletedGoals){

            unComplte += g + " ";
        }
        for (Goal g : completedGoals){

            complete += g + " ";
        }

        return unComplte + "\n" + complete;
    }
}
