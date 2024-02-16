package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class SimpleGoalRepository implements GoalRepository {
    private final InMemoryDataSource dataSource;

    public SimpleGoalRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Subject<Goal> find(int id) {
        return dataSource.getFlashcardSubject(id);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        return dataSource.getAllFlashcardsSubject();
    }

    @Override
    public void save(Goal goal) {
        dataSource.putFlashcard(goal);
    }

    @Override
    public void save(List<Goal> goals) {
        dataSource.putFlashcards(goals);
    }

    @Override
    public void remove(int id) {
        dataSource.removeFlashcard(id);
    }

    @Override
    public void append(Goal goal) {
        dataSource.putFlashcard(
                goal.withSortOrder(dataSource.getMaxSortOrder() + 1)
        );
    }

    @Override
    public void prepend(Goal goal) {
        // Shift all the existing cards up by one.
        dataSource.shiftSortOrders(0, dataSource.getMaxSortOrder(), 1);
        // Then insert the new card before the first one.
        dataSource.putFlashcard(
                goal.withSortOrder(dataSource.getMinSortOrder() -1)
        );
    }
}
