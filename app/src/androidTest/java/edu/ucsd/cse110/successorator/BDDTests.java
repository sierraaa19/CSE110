package edu.ucsd.cse110.successorator;

import junit.framework.TestCase;

import org.junit.Before;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;

public class BDDTests extends TestCase {


    private SimpleGoalRepository mockRepository;
    private InMemoryDataSource dataSource;
    private MainViewModel viewModel;

    @Before
    public void setUp() {
        dataSource = new InMemoryDataSource();
        dataSource.putGoal(new Goal(0, "Go home", false, 1, "Weekly", SuccessDate.getCurrentDateAsString(), "Home"));
        dataSource.putGoal(new Goal(1, "Do homework", true, 2, "Weekly", SuccessDate.getCurrentDateAsString(), "Home"));
        dataSource.putGoal(new Goal(2, "Go to gym", false, 3, "Weekly", SuccessDate.getCurrentDateAsString(), "Home"));
        dataSource.putGoal(new Goal(3, "Go shopping", true, 4, "Weekly", SuccessDate.getCurrentDateAsString(), "Home"));
        mockRepository = new SimpleGoalRepository(dataSource);
        viewModel = new MainViewModel(mockRepository);
    }

    public void testSetupDateObserver() {

    }

}

