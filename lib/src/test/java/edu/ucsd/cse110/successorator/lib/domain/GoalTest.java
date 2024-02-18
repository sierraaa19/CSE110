package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class GoalTest {
    Goal goal1;
    Goal goal2;
    Goal goal3;
    Goal goal4;

    @Before
    public void setUp() {
        goal1 = new Goal(0, "Grocery shopping", false, 0);
        goal2 = new Goal(1, "Go workout", false, 1);
        goal3 = new Goal(2, "Watch CSE110 lecture", true, 2);
        goal4 = new Goal(3, "Finish homework", true, 3);
    }

    @Test
    public void id() {
        // expected, actual
        Integer expected = 0;
        assertEquals(expected, goal1.id());
    }
    @Test
    public void withId() {
        goal1 = goal1.withId(2);
        Integer expected = 2;
        assertEquals(expected, goal1.id());
    }

    @Test
    public void text() {
        String expected = "Grocery shopping";
        assertEquals(expected, goal1.text());
    }

    @Test
    public void isCompleted() {
        boolean expected = false;
        assertEquals(expected, goal1.isCompleted());
    }

    @Test
    public void withCompleted() {
        goal1 = goal1.withCompleted(true);
        boolean expected = true;
        assertEquals(expected, goal1.isCompleted());
    }

    @Test
    public void sortOrder() {
        int expected = 0;
        assertEquals(expected, goal1.sortOrder());
    }

    @Test
    public void withSortOrder() {
        int expected = 0;
        assertEquals(expected, goal1.sortOrder());
    }
}