package edu.ucsd.cse110.successorator.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
@Entity(tableName = "goals")
public class GoalEntity {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="id")
    public Integer id = null;
    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "is_completed")
    public boolean isCompleted;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;




    GoalEntity(@NonNull String text, boolean isCompleted, int sortOrder){
        this.text = text;
        this.isCompleted = isCompleted;
        this.sortOrder = sortOrder;
    }

    public static GoalEntity fromGoal(@NonNull Goal goal){
        var goalFE = new GoalEntity(goal.text(), goal.isCompleted(), goal.sortOrder());
        goalFE.id = goal.id();
        return goalFE;
    }

    public @NonNull Goal toGoal(){
        return new Goal(id, text, isCompleted, sortOrder);
    }
}

