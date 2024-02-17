package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class GoalTest {
    @Test
    public void withCompleted() {
        Goal goal = new Goal(1, "Example Goal", false, 2);

        Goal completedGoal = goal.withCompleted(true);

        assertTrue(completedGoal.isCompleted());
        assertEquals(1, (int) completedGoal.id());
        assertEquals("Example Goal", completedGoal.text());
        assertEquals(2, completedGoal.sortOrder());
    }

    @Test
    public void id() {
        Goal goal = new Goal(1, "Example Goal", false, 2);

        assertEquals(1, (int) goal.id());
    }

    @Test
    public void withId() {
        Goal goal = new Goal(1, "Original Goal", false, 2);

        Goal goalWithId = goal.withId(2);

        assertEquals(2, (int) goalWithId.id());
        assertEquals("Original Goal", goalWithId.text());
        assertFalse(goalWithId.isCompleted());
        assertEquals(2, goalWithId.sortOrder());
    }

    @Test
    public void text() {
        Goal goal = new Goal(1, "Example Goal", false, 2);

        assertEquals("Example Goal", goal.text());
    }

    @Test
    public void isCompleted() {
        Goal goal = new Goal(1, "Example Goal", false, 2);

        assertFalse(goal.isCompleted());
    }

    @Test
    public void testWithCompleted() {
        Goal goal = new Goal(1, "Example Goal", false, 2);

        Goal updatedGoal = goal.withCompleted(true);

        assertTrue(updatedGoal.isCompleted());
        assertEquals(1, (int) updatedGoal.id());
        assertEquals("Example Goal", updatedGoal.text());
        assertEquals(2, updatedGoal.sortOrder());
    }

    @Test
    public void sortOrder() {
        Goal goal = new Goal(1, "Example Goal", false, 2);

        assertEquals(2, goal.sortOrder());
    }

    @Test
    public void withSortOrder() {
        Goal goal = new Goal(1, "Example Goal", false, 2);

        Goal goalWithNewSortOrder = goal.withSortOrder(3);

        assertEquals(1, (int) goalWithNewSortOrder.id());
        assertEquals("Example Goal", goalWithNewSortOrder.text());
        assertFalse(goalWithNewSortOrder.isCompleted());
        assertEquals(3, goalWithNewSortOrder.sortOrder());
    }

    @Test
    public void testEquals() {
        Goal goal1 = new Goal(1, "Example Goal", false, 2);
        Goal goal2 = new Goal(1, "Example Goal", false, 2);

        assertEquals(goal1, goal2);
    }

    @Test
    public void testHashCode() {
        Goal goal1 = new Goal(1, "Example Goal", false, 2);
        Goal goal2 = new Goal(1, "Example Goal", false, 2);

        assertEquals(goal1.hashCode(), goal2.hashCode());
    }
}