package com.sdproject.app.database;

import com.sdproject.app.model.*;

import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class DummyDatabase implements Database {
  private ArrayList<User> allUsers;
  private ArrayList<Task> allTasks;
  private ArrayList<Team> allTeams;

  public DummyDatabase() {
    this.allUsers = new ArrayList<User>();
    this.allTasks = new ArrayList<Task>();
    this.allTeams = new ArrayList<Team>();  
  }

  public int insert(Query q) {
    if (q.getTable().equals("User")) {
      return insertUser(q);
    } else if (q.getTable().equals("Task")) {
      return insertTask(q);
    } else if (q.getTable().equals("Team")) {
      return insertTeam(q);
    } else {
      return -1;
    }
  }

  public int delete(Query q) {
    if (q.getTable().equals("User")) {
      return deleteUser(q);
    } else if (q.getTable().equals("Task")) {
      return deleteTask(q);
    } else if (q.getTable().equals("Team")) {
      return deleteTeam(q);
    } else {
      return -1;
    }
  }

  public int modify(Query q) {
    if (q.getTable().equals("User")) {
      return modifyUser(q);
    } else if (q.getTable().equals("Task")) {
      return modifyTask(q);
    } else if (q.getTable().equals("Team")) {
      return modifyTeam(q);
    } else {
      return -1;
    }
  }

  public <T> ArrayList<T> get(Query q) {
    if (q.getTable().equals("User")) {
      return (ArrayList<T>) getUsers(q);
    } else if (q.getTable().equals("Task")) {
      return (ArrayList<T>) getTasks(q);
    } else if (q.getTable().equals("Team")) {
      return (ArrayList<T>) getTeams(q);
    }
    return null;
  }

  public <T> T getOne(Query q) {
    return (T) get(q).get(0);
  }

  //USER METHODS

  public int insertUser(Query q) {
    User newUser = new User(q.getUserName(), q.getUserPass(), UserType.valueOf(q.getUserType()));
    allUsers.add(newUser);
    return newUser.getUserId();
  }

  public int deleteUser(Query q) {
    User deletedUser = getOne(q);
    int retval = deletedUser.getUserId();
    allUsers.remove(deletedUser);
    return retval;
  }

  public int modifyUser(Query q) {
    User modifiedUser = getOne(q.getToModify());

    if (q.getUserName() != null)
	    modifiedUser.setUserName(q.getUserName());
    if (q.getUserPass() != null)
            modifiedUser.setUserPass(q.getUserPass());
    if (q.getUserType() != null)
            modifiedUser.setUserType(UserType.valueOf(q.getUserType()));
    return modifiedUser.getUserId();
  }

  public boolean checkNoDuplicateUser(Query q) {
    Query dupeCheck = new Query().tableIs(q.getTable()).userNameIs(q.getUserName());
    ArrayList<User> searchedUser = getUsers(dupeCheck);
    return (searchedUser.size() == 0);
  }

  public ArrayList<User> getUsers(Query q) {
    ArrayList<User> res = new ArrayList<User>();
    for (User user : allUsers) {
      if (verifyUserMatchesQuery(user, q))
        res.add(user);
    }
    return res;
  }

  private boolean verifyUserMatchesQuery(User user, Query q) {
    boolean testName = (q.getUserName() == null) || ((q.getUserName() != null) && user.getUserName().equals(q.getUserName()));
    boolean testPass = (q.getUserPass() == null) || ((q.getUserPass() != null) && user.getUserPass().equals(q.getUserPass()));
    boolean testType = (q.getUserType() == null) || ((q.getUserType() != null) && user.getUserType().name().equals(q.getUserType()));
    boolean testID = (q.getUserId() == 0) || ((q.getUserId() != 0) && (user.getUserId() == q.getUserId()));
    return (testName && testPass && testType && testID);
  }

  //TASK METHODS

  public int insertTask(Query q) {
    Task newTask = new Task(q.getTaskName(), q.getTaskDesc(), q.getCreatedById());
    optionalTaskFields(q, newTask);
    allTasks.add(newTask);
    return newTask.getTaskId();
  }

  private void optionalTaskFields(Query q, Task t) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    if (q.getTaskStatus() != null)
	    t.setTaskStatus(TaskStatus.valueOf(q.getTaskStatus()));
    if (q.getAssignedToId() != 0)
	    t.setAssignedToId(q.getAssignedToId());
    if (q.getColorHex() != null)
	    t.setColorHex(q.getColorHex());
    if (q.getDueDate() != null)
      t.setDueDate(LocalDateTime.parse(q.getDueDate(), formatter));
    if (q.getRecurringDays() != 0)
      t.setRecurringDays(q.getRecurringDays());
    if (q.getSubtasks().size() > 0)
      t.setSubtaskIDs(q.getSubtasks());
  }

  public int deleteTask(Query q) {
    Task deletedTask = getOne(q);
    int retval = deletedTask.getTaskId();
    allTasks.remove(deletedTask);
    return retval;
  }

  public int modifyTask(Query q) {
    Task modifiedTask = getOne(q.getToModify());

    if (q.getTaskName() != null)
      modifiedTask.setTaskName(q.getTaskName());
    if (q.getTaskDesc() != null)
      modifiedTask.setTaskDesc(q.getTaskDesc());
    optionalTaskFields(q, modifiedTask);

    return modifiedTask.getTaskId();
  }

  public ArrayList<Task> getTasks(Query q) {
    ArrayList<Task> res = new ArrayList<Task>();
    for (Task task : allTasks) {
      if (verifyTaskMatchesQuery(task, q))
        res.add(task);
    }
    return res;
  }

  public boolean verifyTaskMatchesQuery(Task t, Query q) {
    boolean testID = (q.getTaskId() == 0) || ((q.getTaskId() != 0) && (t.getTaskId() == q.getTaskId()));
    boolean testName = (q.getTaskName() == null) || ((q.getTaskName() != null) && (t.getTaskName().equals(q.getTaskName())));
    boolean testCreatedBy = (q.getCreatedById() == 0) || ((q.getCreatedById() != 0) && (t.getCreatedById() == q.getCreatedById()));
    boolean testStatus = (q.getTaskStatus() == null) || ((q.getTaskStatus() != null) && (t.getTaskStatus().name().equals(q.getTaskStatus())));
    boolean testAssignedTo = (q.getAssignedToId() == 0) || ((q.getAssignedToId() != 0) && (t.getAssignedToId() == q.getAssignedToId()));
    boolean testColor = (q.getColorHex() == null) || ((q.getColorHex() != null) && (t.getColorHex().equals(q.getColorHex())));
    return (testID && testName && testCreatedBy && testStatus && testAssignedTo && testColor);
  }

  // TEAM METHODS

  public int insertTeam(Query q) {
    Team newTeam = new Team(q.getTeamName());
    
    if (q.getTeamMembers().size() != 0) {
      newTeam.setMembers(q.getTeamMembers());
    }

    allTeams.add(newTeam);
    return newTeam.getTeamId();
  }

  public ArrayList<Team> getTeams(Query q) {
    ArrayList<Team> res = new ArrayList<Team>();
    for (Team team : allTeams) {
      if (verifyTeamMatchesQuery(team, q))
        res.add(team);
    }
    return res;
  }

  public int deleteTeam(Query q) {
    Team deletedTeam = getOne(q);
    int retval = deletedTeam.getTeamId();
    allTeams.remove(deletedTeam);
    return retval;
  }

  public int modifyTeam(Query q) {
    Team modifiedTeam = getOne(q.getToModify());
    
    if (q.getTeamName() != null)
      modifiedTeam.setTeamName(q.getTeamName());
    if (q.getTeamMembers().size() > 0) {
      modifiedTeam.setMembers(q.getTeamMembers());
    }

    allTeams.add(modifiedTeam);
    return modifiedTeam.getTeamId();
  }

  private boolean verifyTeamMatchesQuery(Team team, Query q) {
    if(team.getTeamName() != q.getTeamName())
      return false;
    for(int i = 0; i < team.getTeamSize(); i++) {
      if( team.getTeamMembers().get(i).getUserId() != q.getTeamMembers().get(i).getUserId()) {
        return false;
      }
    }
    return true;
  }

  //AUXILIARY METHODS

  public ArrayList<User> getAllUsers() {
    return this.allUsers;
  }

  public ArrayList<Task> getAllTasks() {
    return this.allTasks;
  }

  public ArrayList<Team> getAllTeams() {
    return this.allTeams;
  }

}
