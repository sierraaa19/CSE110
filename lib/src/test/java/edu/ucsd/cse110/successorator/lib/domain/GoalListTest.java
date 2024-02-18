package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GoalListTest {
    GoalList goals;

    @Before
    public void setUp() {
        goals = new GoalList();
        goals.setGoals(List.of(
                new Goal(0, "Do Homework", false, 0),
                new Goal(1, "Go to Gym", false, 1),
                new Goal(2, "Eat Dinner", false, 2),
                new Goal(3, "Buy Groceries", false, 3),
                new Goal(4, "Meeting with CSE110", false, 4),
                new Goal(5, "Club Activities", true, 5),
                new Goal(6, "Watch Lecture", true, 6),
                new Goal(7, "Visit family", true, 7),
                new Goal(8, "Study for CSE110", true, 8)
        ));
    }

    @Test
    public void getGoals() {
        List<Goal> expected = new ArrayList<Goal>(List.of(new Goal(0, "Do Homework", false, 0),
                        new Goal(1, "Go to Gym", false, 1),
                        new Goal(2, "Eat Dinner", false, 2),
                        new Goal(3, "Buy Groceries", false, 3),
                        new Goal(4, "Meeting with CSE110", false, 4),
                        new Goal(5, "Club Activities", true, 5),
                        new Goal(6, "Watch Lecture", true, 6),
                        new Goal(7, "Visit family", true, 7),
                        new Goal(8, "Study for CSE110", true, 8)
                ));

        assertEquals(expected, goals.getGoals());
    }

    @Test
    public void fillGoals() {
        List<Goal> actual = goals.fillGoals(List.of(
                new Goal(0, "Do Homework", false, 0),
                new Goal(1, "Go to Gym", true, 1),
                new Goal(2, "Eat Dinner", false, 2),
                new Goal(3, "Buy Groceries", true, 3),
                new Goal(4, "Meeting with CSE110", false, 4),
                new Goal(5, "Club Activities", true, 5),
                new Goal(6, "Watch Lecture", false, 6),
                new Goal(7, "Visit family", true, 7),
                new Goal(8, "Study for CSE110", false, 8)
        ));

        List<Goal> expected = new ArrayList<Goal>(List.of(
                new Goal(0, "Do Homework", false, 0),
                new Goal(2, "Eat Dinner", false, 1),
                new Goal(4, "Meeting with CSE110", false, 2),
                new Goal(6, "Watch Lecture", false, 3),
                new Goal(8, "Study for CSE110", false, 4),
                new Goal(1, "Go to Gym", true, 5),
                new Goal(3, "Buy Groceries", true, 6),
                new Goal(5, "Club Activities", true, 7),
                new Goal(7, "Visit family", true, 8)
        ));

        assertEquals(expected, actual);
    }

    @Test
    public void getGoalSortOrder() {
        Goal goal = new Goal(9, "Do laundry", true, 9);
        List<Goal> throwAway = goals.fillGoals(goals.getGoals());

        // NOTE: Since the goal is completed, it is being added
        // as the first goal to the completed list.
        // therefore sortOrder should be 0 for the
        // completed list, this sortOrder
        // is then used to sort the singular list
        // in the database.
        int actual = goals.getGoalSortOrder(goal);
        int expected = 0;

        assertEquals(expected, actual);
    }

}
