
# User Stories

### 1. Current date (1hr , High)

  As a user with a lot of goals  
  I want to know the current day of the week and the date   
  So that I can set my daily goals based on the current date  
  
  Scenario 1: When the current day passed  
  Given the current date is Mon 1/29  
  And Goal1 is crossed  
  And Goal2 is still there  
  When the current date has passed  
  Then the current date should become to Tue 1/30  
  And Goal1 has been removed  
  And Goal2 will be remained   


### 2. Add goal (4.5hr , High)

  As a user with a lot of goals   
  I want to add new goals to the list of goals  
  So that I can see my new daily goals on the list  
  
  Scenario 1: When there is no goal in the list  
  Given the current date is Mon 1/29  
  And there is no goal in the list  
  When I add a new goal   
  Then the new goal will be show after the date which is the top of the goal list  
  
  Scenario 2: When there are some uncrossed goals in the list  
  Given there are some uncrossed goals in the list  
  When I add a new goal   
  Then the new goal will be show after all uncrossed goals in the list  
  
  Scenario 3: When there are some uncrossed goals in the list and some crossed goals in the list  
  Given there are some uncrossed goals in the list  
  And there are some crossed goals in the list  
  When I add a new goal   
  Then the new goal will be show after all uncrossed goals in the list and before all crossed goals in the list  
  
  Scenario 4: When there are some crossed goals in the list  
  Given there are some crossed goals in the list  
  When I add a new goal   
  Then the new goal will be show before all crossed goals in the list  



### 3. Cross goal (2hr , High)

  As a user with a lot of goals   
  I want to cross a goal when I reach it  
  So that I can see the goal I reached has been crossed and moved to the bottom of the list  
  
  Scenario 1: When there are some goals  
  Given there are Goal1, Goal2, Goal3 in the goals list  
  When I tap "Goal1"  
  Then there Goal1 will be crossed  
  And Goal1 will be moved to the bottom of the list  
  And Goal2 will be moved to the top of the list   
  And Goal3 will be moved to the second place of the list  



# Tasks for First Iteration

  - US1-Current_date-task-1: getCurrentDate
  - US1-Current_date-task-2: displayCurrentDate
  - US2-Add_goal-task-1: addGoals
  - US2-Add_goal-task-2: displayGoals
  - US3-Cross_goal-task-1: deletGoal
  - US3-Cross_goal-task-2: displayCrossedGoal

# Iterations/Milestone
- First Iteration
  - US1-Current_date
  - US2-Add_goal-task
  - US3-Cross_goal
- Secode Ietration
  - US4-Mic     
 













