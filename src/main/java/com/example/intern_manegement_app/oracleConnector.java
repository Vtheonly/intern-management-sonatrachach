package com.example.intern_manegement_app;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.DriverManager.getConnection;
public class oracleConnector {


  private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
  private static final String USERNAME = "system";
  private static final String PASSWORD = "rootroot";

  private static Connection connection;

  //  establish the connectION
  static {
    try {
      connection = getConnection(URL, USERNAME, PASSWORD);
      System.out.println("Connected!");
    } catch (SQLException e) {
      System.out.println(
              "Failed to establish a connection to the Oracle database.");
      e.printStackTrace();
    }
  }

  public static void closeConnection() {
    try {
      if (connection != null) {
        connection.close();
        System.out.println("Connection closed.");
      }
    } catch (SQLException e) {
      System.out.println("Failed to close the connection.");
      e.printStackTrace();
    }
  }

  public static boolean isAdmin(String username, String password) {
    String query = "SELECT \"role_id\" FROM \"worker_user\" WHERE \"username\" = ? AND \"password_hash\" = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, password);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          int roleId = resultSet.getInt("role_id");
          return roleId == 4;
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to execute login query.");
      e.printStackTrace();
    }
    return false;
  }

  public static boolean isChief(String username, String password) {
    String query = "SELECT \"role_id\" FROM \"worker_user\" WHERE \"username\" = ? AND \"password_hash\" = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, password);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          int roleId = resultSet.getInt("role_id");
          return roleId == 5;
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to execute login query.");
      e.printStackTrace();
    }
    return false;
  }

  public static boolean login(String username, String password) {
    String query = "SELECT COUNT(*) FROM \"worker_user\" WHERE \"username\" = ? AND \"password_hash\" = ?";
    try (PreparedStatement preparedStatement =connection.prepareStatement(query)) {
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, password);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          int count = resultSet.getInt(1);
          return count == 1; // If count is 1, login is successful
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to execute login query.");
      e.printStackTrace();
    }
    return false;}

  private static boolean isColumnExist(String tableName, String columnName, Connection connection) {
    try (ResultSet columns = connection.getMetaData().getColumns(null, null, tableName, columnName)) {
      return columns.next();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }




  public static void insertIntern(HashMap<String, Object> internData) {
    PreparedStatement pstmt = null;

    try {
      // Construct the SQL INSERT statement
      StringBuilder columns = new StringBuilder();
      StringBuilder values = new StringBuilder();
      for (String key : internData.keySet()) {
        if (columns.length() > 0) {
          columns.append(", ");
          values.append(", ");
        }
        columns.append("\"").append(key).append("\"");
        values.append("?");
      }

      String query = "INSERT INTO \"intern\" (" + columns.toString() + ") VALUES (" + values.toString() + ")";

      // Prepare the statement
      pstmt = connection.prepareStatement(query);

      // Set the values
      int index = 1;
      for (Object value : internData.values()) {
        pstmt.setObject(index++, value);
      }

      // Execute the statement
      pstmt.executeUpdate();
      JOptionPane.showMessageDialog(null, "Record inserted successfully. ", "Information", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Failed to execute query. ", "Information", JOptionPane.INFORMATION_MESSAGE);

    } finally {
      // Close resources
      try { if (pstmt != null) pstmt.close(); } catch (Exception e) { e.printStackTrace(); }
    }
  }

  public static void insertTheme(HashMap<String, Object> themeData) {
    PreparedStatement pstmt = null;

    try {
      // Construct the SQL INSERT statement
      StringBuilder columns = new StringBuilder();
      StringBuilder values = new StringBuilder();
      for (String key : themeData.keySet()) {
        if (columns.length() > 0) {
          columns.append(", ");
          values.append(", ");
        }
        columns.append("\"").append(key).append("\"");
        values.append("?");
      }

      String query = "INSERT INTO \"theme\" (" + columns.toString() + ") VALUES (" + values.toString() + ")";

      // Prepare the statement
      pstmt = connection.prepareStatement(query);

      // Set the values
      int index = 1;
      for (Object value : themeData.values()) {
        pstmt.setObject(index++, value);
      }

      // Execute the statement
      pstmt.executeUpdate();
      JOptionPane.showMessageDialog(null, "Record inserted successfully.", "Information", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Failed to execute query.", "Information", JOptionPane.INFORMATION_MESSAGE);
    } finally {
      // Close resources
      try { if (pstmt != null) pstmt.close(); } catch (Exception e) { e.printStackTrace(); }
    }
  }

  public static void insertWorkerUser(Map<String, String> workerUserData) throws SQLException {
    // Construct the SQL INSERT statement
    StringBuilder columns = new StringBuilder();
    StringBuilder values = new StringBuilder();
    for (String key : workerUserData.keySet()) {
      if (columns.length() > 0) {
        columns.append(", ");
        values.append(", ");
      }
      columns.append("\"").append(key).append("\"");
      values.append("?");
    }

    String query = "INSERT INTO \"worker_user\" (" + columns.toString() + ") VALUES (" + values.toString() + ")";

    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      // Set parameter values based on the workerUserData HashMap
      int index = 1;
      for (String value : workerUserData.values()) {
        pstmt.setString(index++, value);
      }

      // Execute the INSERT statement
      pstmt.executeUpdate();
      System.out.println("Data inserted successfully into worker_user table.");

      JOptionPane.showMessageDialog(null, "Data inserted successfully. ", "Information", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException e) {
      System.err.println("Error inserting data into worker_user table: " + e.getMessage());
      JOptionPane.showMessageDialog(null, "Error inserting data. ", "Information", JOptionPane.INFORMATION_MESSAGE);

      throw e; // Propagate the exception to the caller
    }
  }

  public static void insertDepartment(HashMap<String, Object> departmentData) {
    PreparedStatement pstmt = null;

    try {
      // Construct the SQL INSERT statement
      StringBuilder columns = new StringBuilder();
      StringBuilder values = new StringBuilder();
      for (String key : departmentData.keySet()) {
        if (columns.length() > 0) {
          columns.append(", ");
          values.append(", ");
        }
        columns.append("\"").append(key).append("\"");
        values.append("?");
      }

      String query = "INSERT INTO \"department\" (" + columns.toString() + ") VALUES (" + values.toString() + ")";

      // Prepare the statement
      pstmt = connection.prepareStatement(query);

      // Set the values
      int index = 1;
      for (Object value : departmentData.values()) {
        pstmt.setObject(index++, value);
      }

      // Execute the statement
      pstmt.executeUpdate();
      JOptionPane.showMessageDialog(null, "Record inserted successfully.", "Information", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Failed to execute query.", "Information", JOptionPane.INFORMATION_MESSAGE);
    } finally {
      // Close resources
      try { if (pstmt != null) pstmt.close(); } catch (Exception e) { e.printStackTrace(); }
    }
  }







  public static List<Map<String, Object>> searchTheme(HashMap<String, Object> filters) {
    PreparedStatement pstmt = null;
    List<Map<String, Object>> results = new ArrayList<>();

    try {
      // Construct the SQL SELECT statement
      StringBuilder query = new StringBuilder("SELECT * FROM \"theme\"");
      if (!filters.isEmpty()) {
        query.append(" WHERE ");
        for (String key : filters.keySet()) {
          query.append("\"").append(key).append("\" = ? AND ");
        }
        // Remove the last "AND "
        query.setLength(query.length() - 5);
      }

      // Prepare the statement
      pstmt = connection.prepareStatement(query.toString());

      // Set the values
      int index = 1;
      for (Object value : filters.values()) {
        pstmt.setObject(index++, value);
      }

      // Execute the statement and process the result set
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        Map<String, Object> row = new HashMap<>();
        row.put("theme_id", rs.getInt("theme_id"));
        row.put("description", rs.getString("description"));
        row.put("theme_name", rs.getString("theme_name"));
        row.put("theme_responsible",


                rs.getString("responsible")

        );
        row.put("department_id", rs.getInt("department_id"));
        results.add(row);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Failed to execute query.", "Information", JOptionPane.INFORMATION_MESSAGE);
    } finally {
      // Close resources
      try { if (pstmt != null) pstmt.close(); } catch (Exception e) { e.printStackTrace(); }
    }

    return results;
  }

  public static List<Map<String, Object>> searchDepartment(Map<String, String> filters) {
    List<Map<String, Object>> departmentList = new ArrayList<>();
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      StringBuilder query = new StringBuilder("SELECT * FROM \"department\" WHERE 1=1");

      for (String key : filters.keySet()) {
        query.append(" AND \"").append(key).append("\" = ?");
      }

      pstmt = connection.prepareStatement(query.toString());

      int index = 1;
      for (String value : filters.values()) {
        pstmt.setString(index++, value);
      }

      rs = pstmt.executeQuery();

      while (rs.next()) {
        Map<String, Object> department = new HashMap<>();
        department.put("department_id", rs.getInt("department_id"));
        department.put("location", rs.getString("location"));
        department.put("department_name", rs.getString("department_name"));
        department.put("department_description", rs.getString("department_description"));
        department.put("fax", rs.getString("fax"));
        departmentList.add(department);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
      try { if (pstmt != null) pstmt.close(); } catch (Exception e) { e.printStackTrace(); }
    }

    return departmentList;
  }

  public static List<Map<String, Object>> searchIntern(Map<String, String> filters) {
    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM \"intern\"");
    List<String> params = new ArrayList<>();



    // Check if there are any filters to apply
    if (!filters.isEmpty()) {
      queryBuilder.append(" WHERE ");
      filters.forEach((key, value) -> {
        queryBuilder.append("\"").append(key).append("\" = ? AND ");
        if ("start_date".equals(key)) {
          // Convert date strings to java.sql.Date
          LocalDate localDate = LocalDate.parse(value);
          params.add(String.valueOf(Date.valueOf(localDate)));
        } else {
          params.add(value);
        }
      });
      // Remove the last "AND "
      queryBuilder.setLength(queryBuilder.length() - 5);
    }




    List<Map<String, Object>> result = new ArrayList<>();
    String query = queryBuilder.toString();
    System.out.println("Executing query: " + query);
    System.out.println("With parameters: " + params);

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      int index = 1;
      for (String param : params) {
        preparedStatement.setString(index++, param);
      }


      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          Map<String, Object> row = new HashMap<>();
          row.put("name", resultSet.getString("name"));
          row.put("age", resultSet.getString("age"));
          row.put("email", resultSet.getString("email"));
          row.put("theme_id", resultSet.getString("theme_id"));
          row.put("university", resultSet.getString("university"));
          row.put("phone_number", resultSet.getString("phone_number"));
          row.put("IS_ACCEPTED", resultSet.getString("IS_ACCEPTED"));
          row.put("intern_id", resultSet.getString("intern_id"));
          row.put("start_date", resultSet.getString("start_date"));
          result.add(row);
        }
      }
    } catch (SQLException e) {

      System.err.println("Failed to execute query: " + e.getMessage());
      e.printStackTrace();

      JOptionPane.showMessageDialog(null, "Failed to execute query ", "Information", JOptionPane.INFORMATION_MESSAGE);

    }
    return result;
  }

  public static List<Map<String, Object>> searchWorkerUser(Map<String, String> filters) {
    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM \"worker_user\"");
    List<String> params = new ArrayList<>();

    // Check if there are any filters to apply
    if (!filters.isEmpty()) {
      queryBuilder.append(" WHERE ");
      filters.forEach((key, value) -> {
        queryBuilder.append("\"").append(key).append("\" = ? AND ");
        params.add(value);
      });
      // Remove the last "AND "
      queryBuilder.setLength(queryBuilder.length() - 5);
    }

    List<Map<String, Object>> result = new ArrayList<>();
    String query = queryBuilder.toString();
    System.out.println("Executing query: " + query);
    System.out.println("With parameters: " + params);

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      int index = 1;
      for (String param : params) {
        preparedStatement.setString(index++, param);
      }

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          Map<String, Object> row = new HashMap<>();
          row.put("user_id", resultSet.getString("user_id"));
          row.put("full_name", resultSet.getString("full_name"));
          row.put("department_id", resultSet.getInt("department_id"));
          row.put("supervisor_id", resultSet.getInt("supervisor_id"));
          row.put("username", resultSet.getString("username"));
          row.put("email_address", resultSet.getString("email_address"));
          row.put("phone_number", resultSet.getString("phone_number"));
          row.put("fax_number", resultSet.getString("fax_number"));
          row.put("password_hash", resultSet.getString("password_hash"));
          row.put("salt", resultSet.getString("salt"));
          row.put("password_creation_date", resultSet.getDate("password_creation_date"));
          row.put("password_expiry_date", resultSet.getDate("password_expiry_date"));
          row.put("role_id", resultSet.getInt("role_id"));

          result.add(row);
        }
      }
    } catch (SQLException e) {
      System.err.println("Failed to execute query: " + e.getMessage());

      JOptionPane.showMessageDialog(null, "Failed to execute query. ", "Information", JOptionPane.INFORMATION_MESSAGE);

      e.printStackTrace();
    }
    return result;
  }




  public static Integer getIdByName(String tableName, String name) {
    Integer id = null;
    String columnName = "";

    switch(tableName) {
      case "theme":
        columnName = "theme_id";
        break;
      case "responsible":
        columnName = "responsible_id";
        break;
      case "role":
        columnName = "role_id";
        break;
      case "worker_user_user":
        columnName = "user_id";
        tableName="worker_user";
        break;
      case "worker_user_super":
        columnName = "supervisor_id";
        tableName="worker_user";
        break;
      case "department":
        columnName = "department_id";
        break;
      default:
        throw new IllegalArgumentException("Invalid table name: " + tableName);
    }

    String query = "";
    if (tableName.equals("theme")) {
      query = "SELECT \"" + columnName + "\" FROM \"SYSTEM\".\"" + tableName + "\" WHERE \"theme_name\" = ?";
    } else if (tableName.equals("responsible")) {
      query = "SELECT \"" + columnName + "\" FROM \"SYSTEM\".\"" + tableName + "\" WHERE \"name\" = ?";
    }
    else if (tableName.equals("role")) {
      query = "SELECT \"" + columnName + "\" FROM \"SYSTEM\".\"" + tableName + "\" WHERE \"role_name\" = ?";
    }
    else if (tableName.equals("worker_user")) {
      query = "SELECT \"" + columnName + "\" FROM \"SYSTEM\".\"" + tableName + "\" WHERE \"full_name\" = ?";
    }
    else if (tableName.equals("department")) {
      query = "SELECT \"" + columnName + "\" FROM \"SYSTEM\".\"" + tableName + "\" WHERE \"department_name\" = ?";
    }



    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, name);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          id = resultSet.getInt(1);
        }
      }
    } catch (SQLException e) {
      System.err.println("Failed to execute query: " + e.getMessage());
      JOptionPane.showMessageDialog(null, "Failed to execute query. ", "Information", JOptionPane.INFORMATION_MESSAGE);

    }

    return id;
  }

  public static String getNameById(int id, String table, String ID_column ,String Name_column) {
    String query = "SELECT \"" + Name_column + "\" FROM \"" + table + "\" WHERE \""+ID_column+"\" = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, id);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getString(Name_column);
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to execute query.");
      JOptionPane.showMessageDialog(null, "Failed to execute query. ", "Information", JOptionPane.INFORMATION_MESSAGE);

      e.printStackTrace();
    }
    return null;
  }

  public static Integer getUserAccessLevel(String username, String password) {
    int roleId = -1;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    try {
      // Prepare the SQL query with double quotes for Oracle identifiers
      String query = "SELECT \"role_id\" FROM \"worker_user\" WHERE \"username\" = ? AND \"password_hash\" = ?";
      preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, password);

      // Execute the query
      resultSet = preparedStatement.executeQuery();

      // Check if a row was returned
      if (resultSet.next()) {
        roleId = resultSet.getInt("role_id");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      // Close the ResultSet and PreparedStatement (connection is statically managed)
      if (resultSet != null) {
        try {
          resultSet.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (preparedStatement != null) {
        try {
          preparedStatement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    return roleId;
  }

  public static ArrayList<String> getSelectableOptions(String table, String column) {
    ArrayList<String> result = new ArrayList<>();
    if (!isColumnExist(table, column, connection)) {
      throw new IllegalArgumentException("Table or column does not exist in the database.");
    }
    String query = "SELECT \"" + column + "\" FROM \"" + table + "\"";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          String rowValue = resultSet.getString(1);
          result.add(rowValue);
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  public static Integer getMaxId(String tableName, String columnName) {
    Statement stmt = null;
    ResultSet rs = null;
    Integer maxId = null;

    try {
      // Create a statement
      stmt = connection.createStatement();

      // Execute the query to get the maximum value of the specified column
      String query = "SELECT MAX(\"" + columnName + "\") FROM \"" + tableName + "\"";
      rs = stmt.executeQuery(query);

      // Retrieve the result using column index
      if (rs.next()) {
        maxId = rs.getInt(1); // Get the value from the first column of the result set
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // Close resources
      try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
      try { if (stmt != null) stmt.close(); } catch (Exception e) { e.printStackTrace(); }
    }

    return maxId;
  }






  public static void updateInter(Map<String, String> whereParams, Map<String, String> newUpdateParams) {
    StringBuilder queryBuilder = new StringBuilder("UPDATE \"intern\" SET ");

    // Append new update parameters to the query
    newUpdateParams.forEach((key, value) -> {
      queryBuilder.append("\"").append(key).append("\" = ?, ");
    });

    // Remove the last comma and space
    queryBuilder.setLength(queryBuilder.length() - 2);

    // Append where conditions to the query
    queryBuilder.append(" WHERE ");
    whereParams.forEach((key, value) -> {
      queryBuilder.append("\"").append(key).append("\" = ? AND ");
    });

    // Remove the last "AND "
    queryBuilder.setLength(queryBuilder.length() - 5);

    String query = queryBuilder.toString();

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      int index = 1;

      // Set the new update parameters in the prepared statement
      for (String value : newUpdateParams.values()) {
        preparedStatement.setString(index++, value);
      }

      // Set the where parameters in the prepared statement
      for (String value : whereParams.values()) {
        preparedStatement.setString(index++, value);
      }

      // Print out the query being executed (for debugging)
      System.out.println("Executing query: " + preparedStatement);

      // Execute the update query
      int rowsAffected = preparedStatement.executeUpdate();

      // Print out the number of rows affected (for debugging)
      System.out.println("Rows affected: " + rowsAffected);

      // Commit the transaction if auto-commit is not enabled
      // connection.commit(); // Uncomment if auto-commit is disabled

      System.out.println("Update successful."); // Print success message
      JOptionPane.showMessageDialog(null, "Update successful. ", "Information", JOptionPane.INFORMATION_MESSAGE);


    } catch (SQLException e) {
      System.err.println("Failed to execute update query: " + e.getMessage());
      JOptionPane.showMessageDialog(null, "Failed to execute update. ", "Information", JOptionPane.INFORMATION_MESSAGE);

      e.printStackTrace();
    }
  }

  public static void updateWorkerUser(Map<String, String> whereParams, Map<String, String> newUpdateParams) {
    StringBuilder queryBuilder = new StringBuilder("UPDATE \"worker_user\" SET ");

    // Append new update parameters to the query
    newUpdateParams.forEach((key, value) -> {
      queryBuilder.append("\"").append(key).append("\" = ?, ");
    });

    // Remove the last comma and space
    queryBuilder.setLength(queryBuilder.length() - 2);

    // Append where conditions to the query
    queryBuilder.append(" WHERE ");
    whereParams.forEach((key, value) -> {
      queryBuilder.append("\"").append(key).append("\" = ? AND ");
    });

    // Remove the last "AND "
    queryBuilder.setLength(queryBuilder.length() - 5);

    String query = queryBuilder.toString();

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      int index = 1;

      // Set the new update parameters in the prepared statement
      for (String value : newUpdateParams.values()) {
        preparedStatement.setString(index++, value);
      }

      // Set the where parameters in the prepared statement
      for (String value : whereParams.values()) {
        preparedStatement.setString(index++, value);
      }

      // Print out the query being executed (for debugging)
      System.out.println("Executing query: " + preparedStatement);

      // Execute the update query
      int rowsAffected = preparedStatement.executeUpdate();

      // Print out the number of rows affected (for debugging)
      System.out.println("Rows affected: " + rowsAffected);
      JOptionPane.showMessageDialog(null, "Update is accepted. ", "Information", JOptionPane.INFORMATION_MESSAGE);

      // Commit the transaction if auto-commit is not enabled
      // connection.commit(); // Uncomment if auto-commit is disabled

      System.out.println("Update successful."); // Print success message
    } catch (SQLException e) {
      System.err.println("Failed to execute update query: " + e.getMessage());
      JOptionPane.showMessageDialog(null, "Failed to execute update. ", "Information", JOptionPane.INFORMATION_MESSAGE);

      e.printStackTrace();
    }
  }

  public static void updateChiefDecision(boolean isAccepted, String name, int id) {
    // Define the query to update the "IS_ACCEPTED" column
    String query = "UPDATE \"intern\" SET \"IS_ACCEPTED\" = ? WHERE \"name\" = ? AND \"intern_id\" = ?";

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      // Set the "IS_ACCEPTED" value based on the boolean parameter
      if (isAccepted) {
        preparedStatement.setString(1, "Accepted");
      } else {
        preparedStatement.setString(1, "Rejected");
      }

      // Set the name and ID parameters in the query
      preparedStatement.setString(2, name);
      preparedStatement.setInt(3, id);

      // Execute the update
      int rowsAffected = preparedStatement.executeUpdate();

      if (rowsAffected > 0) {
        System.out.println("Intern decision updated successfully.");
      } else {
        System.out.println("No intern found with the provided name and ID.");
      }
    } catch (SQLException e) {
      System.err.println("Failed to execute update: " + e.getMessage());

      JOptionPane.showMessageDialog(null, "Failed to execute update. ", "Information", JOptionPane.INFORMATION_MESSAGE);

      e.printStackTrace();
    }
  }





  public static void deleteDepartment(int departmentId, String departmentName) {
    PreparedStatement pstmt = null;

    try {
      String query = "DELETE FROM \"department\" WHERE \"department_id\" = ? AND \"department_name\" = ?";
      pstmt = connection.prepareStatement(query);
      pstmt.setInt(1, departmentId);
      pstmt.setString(2, departmentName);

      int deletedRows = pstmt.executeUpdate();
      if (deletedRows > 0) {
        JOptionPane.showMessageDialog(null, "Department deleted successfully.", "Information", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(null, "No department found with provided ID and name.", "Information", JOptionPane.INFORMATION_MESSAGE);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Failed to delete department.", "Information", JOptionPane.INFORMATION_MESSAGE);
    } finally {
      try { if (pstmt != null) pstmt.close(); } catch (Exception e) { e.printStackTrace(); }
    }
  }

  public static void deleteTheme(int themeId, String themeName) {
    PreparedStatement pstmt = null;

    try {
      String query = "DELETE FROM \"theme\" WHERE \"theme_id\" = ? AND \"theme_name\" = ?";
      pstmt = connection.prepareStatement(query);
      pstmt.setInt(1, themeId);
      pstmt.setString(2, themeName);

      int deletedRows = pstmt.executeUpdate();
      if (deletedRows > 0) {
        JOptionPane.showMessageDialog(null, "Theme deleted successfully.", "Information", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(null, "No theme found with provided ID and name.", "Information", JOptionPane.INFORMATION_MESSAGE);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Failed to delete theme.", "Information", JOptionPane.INFORMATION_MESSAGE);
    } finally {
      try { if (pstmt != null) pstmt.close(); } catch (Exception e) { e.printStackTrace(); }
    }
  }

  public static void deleteWorkerUser(int userId, String fullName) {
    PreparedStatement pstmt = null;

    try {
      String query = "DELETE FROM \"worker_user\" WHERE \"user_id\" = ? AND \"full_name\" = ?";
      pstmt = connection.prepareStatement(query);
      pstmt.setInt(1, userId);
      pstmt.setString(2, fullName);

      int deletedRows = pstmt.executeUpdate();
      if (deletedRows > 0) {
        JOptionPane.showMessageDialog(null, "Worker user deleted successfully.", "Information", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(null, "No worker user found with provided ID and full name.", "Information", JOptionPane.INFORMATION_MESSAGE);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Failed to delete worker user.", "Information", JOptionPane.INFORMATION_MESSAGE);
    } finally {
      try { if (pstmt != null) pstmt.close(); } catch (Exception e) { e.printStackTrace(); }
    }
  }

  public static void deleteIntern(int internId, String name) {
    PreparedStatement pstmt = null;

    try {
      String query = "DELETE FROM \"intern\" WHERE \"intern_id\" = ? AND \"name\" = ?";
      pstmt = connection.prepareStatement(query);
      pstmt.setInt(1, internId);
      pstmt.setString(2, name);

      int deletedRows = pstmt.executeUpdate();
      if (deletedRows > 0) {
        JOptionPane.showMessageDialog(null, "Intern deleted successfully.", "Information", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(null, "No intern found with provided ID and name.", "Information", JOptionPane.INFORMATION_MESSAGE);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Failed to delete intern.", "Information", JOptionPane.INFORMATION_MESSAGE);
    } finally {
      try { if (pstmt != null) pstmt.close(); } catch (Exception e) { e.printStackTrace(); }
    }
  }



  public static void setAllInternsAccepted() {
    String query = "UPDATE \"intern\" SET \"IS_ACCEPTED\" = 'Accepted'";

    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      int rowsAffected = pstmt.executeUpdate();
      JOptionPane.showMessageDialog(null, "All interns set to ACCEPTED. Rows affected: " + rowsAffected, "Information", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Failed to execute update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public static void setAllInternsRejected() {
    String query = "UPDATE \"intern\" SET \"IS_ACCEPTED\" = 'Rejected'";

    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      int rowsAffected = pstmt.executeUpdate();
      JOptionPane.showMessageDialog(null, "All interns set to REJECTED. Rows affected: " + rowsAffected, "Information", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Failed to execute update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

}