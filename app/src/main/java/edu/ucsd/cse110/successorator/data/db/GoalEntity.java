package edu.ucsd.cse110.successorator.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @ColumnInfo(name = "frequency")
    public String frequency;

    @ColumnInfo(name = "date")
    public String date;


//    private Date dateObj;
    GoalEntity(@NonNull String text, boolean isCompleted, int sortOrder,String frequency, String date){
        this.text = text;
        this.isCompleted = isCompleted;
        this.sortOrder = sortOrder;
        this.frequency = frequency;
        this.date = date; //maybe this works
//        this.dateObj = date;
    }

    public static GoalEntity fromGoal(@NonNull Goal goal){
        var goalFE = new GoalEntity(goal.text(), goal.isCompleted(), goal.sortOrder(), goal.getFrequency(), goal.getDate().toString());
        goalFE.id = goal.id();
        return goalFE;
    }

    public @NonNull Goal toGoal(){
        Goal goal = new Goal(id, text, isCompleted, sortOrder,frequency,stringToDate(date));
        return goal;
    }

    public static Date stringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE M/d");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // or a default date as per your requirement
        }
    }

}

