package edu.ucsd.cse110.successorator.lib.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public List<Goal> getGoals() {
        return this.goals;
    }

    public void addGoal(Goal goal) {
        if (goal.isCompleted()) {
            completedGoals.add(goal);
        } else {
            uncompletedGoals.add(goal);
        }

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

        Collections.sort(completedGoals, new Comparator<Goal>() {
            @Override
            public int compare(Goal g1, Goal g2) {
                return Integer.compare(g1.sortOrder(), g2.sortOrder());
            }
        });

        Collections.sort(uncompletedGoals, new Comparator<Goal>() {
            @Override
            public int compare(Goal g1, Goal g2) {
                return Integer.compare(g1.sortOrder(), g2.sortOrder());
            }
        });

        /*
        // Test sort orders
        System.out.println("");
        // See what's happening
        System.out.println("Goals uncompleted:");
        uncompletedGoals.forEach(goal -> {
            System.out.print(goal.toString() + ", ");
        });

        System.out.println("");

        System.out.println("Goals completed:");
        completedGoals.forEach(goal -> {
            System.out.print(goal.toString() + ", ");
        });

        System.out.println("");
         */

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

    // gets the correct sortOrder
    // for the goal being added
    // to a different list
    // depending on the goals.isCompleted() flag
    public int getGoalSortOrder(Goal goal) {
        boolean done = false;
        int sortOrder = -1;

        for (int i = 0; i < goals.size(); i++) {
            if (goal.isCompleted() && goals.get(i).isCompleted() && !done) {
                sortOrder = goals.get(i).sortOrder();
                done = true;
            } else if (!goal.isCompleted() && !goals.get(i).isCompleted() && !done) {
                sortOrder = goals.get(i).sortOrder();
                done = true;
            } else {
                sortOrder = 0;
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
