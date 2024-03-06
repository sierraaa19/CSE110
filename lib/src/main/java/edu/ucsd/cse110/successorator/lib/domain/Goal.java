package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Goal implements Serializable {
    private final @Nullable Integer id;
    private final @NonNull String text;
    private final boolean isCompleted;
    private final int sortOrder;


    public Goal(@Nullable Integer id, @NonNull String text, boolean isCompleted, int sortOrder) {
        this.id = id;
        this.text = text;
        this.isCompleted = isCompleted;
        this.sortOrder = sortOrder;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return sortOrder == goal.sortOrder &&
                Objects.equals(id, goal.id) &&
                Objects.equals(isCompleted, goal.isCompleted) &&
                Objects.equals(text, goal.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, isCompleted, sortOrder);
    }

    @Override
    public String toString() {
        return "\nGoal{" +
                "id=" + id +
                "\n, text='" + text + '\'' +
                "\n, isCompleted=" + isCompleted +
                "\n, sortOrder=" + sortOrder +
                '}';
    }
}
