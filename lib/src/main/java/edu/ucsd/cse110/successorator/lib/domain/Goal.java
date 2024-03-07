package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Goal implements Serializable {
    private final @Nullable Integer id;
    private final @NonNull String text;
    private final boolean isCompleted;
    private final int sortOrder;

    private String frequency;


    private Date creationDate;


    public Goal(@Nullable Integer id, @NonNull String text, boolean isCompleted, int sortOrder) {
        this.id = id;
        this.text = text;
        this.isCompleted = isCompleted;
        this.sortOrder = sortOrder;
        this.frequency = "Weekly"; // Default value
        this.creationDate = new Date();
    }

    public @Nullable Integer id() {
        return id;
    }
    public Goal withId(int id) {
        return new Goal(id, this.text, this.isCompleted, this.sortOrder);
    }

    public @NonNull String text() {
        return text;
    }

    public boolean isCompleted(){ return isCompleted;}

    public Goal withCompleted(boolean isCompleted) {
        return new Goal(id, text, isCompleted, sortOrder);
    }

    public int sortOrder() {
        return sortOrder;
    }

    public Goal withSortOrder(int sortOrder) {
        return new Goal(this.id, this.text, this.isCompleted, sortOrder);
    }

    public void setFrequency(@NonNull String frequency) {
        this.frequency = frequency;
    }

    public void setDate (Date current){
        this.creationDate = current;
    }

    public @NonNull String getFrequency() {
        return frequency;
    }
    public @NonNull Date getDate() {
        return this.creationDate;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return sortOrder == goal.sortOrder &&
                Objects.equals(id, goal.id) &&
                Objects.equals(isCompleted, goal.isCompleted) &&
                Objects.equals(text, goal.text) &&
                Objects.equals(frequency,goal.frequency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, isCompleted, sortOrder,frequency);
    }

    @Override
    public String toString() {
        return "\nGoal{" +
                "id=" + id +
                "\n, text='" + text + '\'' +
                "\n, frequency='" + frequency + '\'' +
                "\n, isCompleted=" + isCompleted +
                "\n, sortOrder=" + sortOrder +
                '}';
    }
}
