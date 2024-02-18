package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class SimpleGoalRepository implements GoalRepository {
    private final InMemoryDataSource dataSource;
    private GoalList goals;

    public SimpleGoalRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
        this.goals = new GoalList();
    }

    public void syncLists() {
        List<Goal> goalsData = this.dataSource.getFlashcards();
        GoalList goalsLogic = new GoalList();
        List<Goal> newGoalData;
        newGoalData = goalsLogic.fillGoals(goalsData);

        // remove all goals from data
        goalsData.forEach(goal -> {
            this.dataSource.removeFlashcard(goal.id());
        });

        // reinsert them
        this.dataSource.putFlashcards(newGoalData);
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
        // process in GoalList

        dataSource.putFlashcard(goal);
    }

    @Override
    public void save(List<Goal> goals) {

        // process in GoalList, adjust sortorder


        dataSource.putFlashcards(goals);
    }

    @Override
    public void remove(int id) {
        // remove from GoalList
        dataSource.removeFlashcard(id);

        // prepend in GoalList, update sort order in process
        //dataSource.putFlashcard

    }

    @Override
    public void append(Goal goal) {

        // process in GoalList

        dataSource.putFlashcard(
                goal.withSortOrder(dataSource.getMaxSortOrder() + 1)
        );
    }

    @Override
    public void prepend(Goal goal) {

        // process in GoalList, simply call syncList
        syncLists();
        int sortOrder = goals.getGoalSortOrder(goal);
        goal = goal.withSortOrder(sortOrder);

        // Shift all the existing cards up by one.
        dataSource.shiftSortOrders(goal.sortOrder(), dataSource.getMaxSortOrder(), 1);
        // Then insert the new card before the first one.
        dataSource.putFlashcard(goal);
        syncLists();

    }
}
