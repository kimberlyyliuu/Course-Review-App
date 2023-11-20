package edu.virginia.sde.reviews;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDriver {
    private final String sqliteFilename;
    private Connection connection;

    public DatabaseDriver (String sqlListDatabaseFilename) {
        this.sqliteFilename = sqlListDatabaseFilename;
    }

    /**
     * Connect to a SQLite Database. This turns out Foreign Key enforcement, and disables auto-commits
     * @throws SQLException
     */
    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            throw new IllegalStateException("The connection is already opened");
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + sqliteFilename);
        //the next line enables foreign key enforcement - do not delete/comment out
        connection.createStatement().execute("PRAGMA foreign_keys = ON");
        //the next line disables auto-commit - do not delete/comment out
        connection.setAutoCommit(false);
    }

    /**
     * Commit all changes since the connection was opened OR since the last commit/rollback
     */
    public void commit() throws SQLException {
        connection.commit();
    }

    /**
     * Rollback to the last commit, or when the connection was opened
     */
    public void rollback() throws SQLException {
        connection.rollback();
    }

    /**
     * Ends the connection to the database
     */
    public void disconnect() throws SQLException {
        connection.close();
    }


    /**
     * Creates Users, Courses, and Reviews tables for database
     * @throws SQLException
     */
    public void createTables() throws SQLException {
        // Agent: ChatGPT
        // Usage: Asked how generate certain SQL commands (i.e. based on how to introduce foreign keys)
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS Users (" +
                "UserID INTEGER PRIMARY KEY," +
                "Username TEXT UNIQUE NOT NULL," +
                "Password TEXT NOT NULL," +
                ");");

        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS Courses (" +
                "CourseID INTEGER PRIMARY KEY," +
                "Mnemonic TEXT NOT NULL," +
                "CourseNumber INTEGER NOT NULL," +
                "CourseName TEXT NOT NULL" +
                "AverageRating REAL DEFAULT 0.0," +
                ");");


        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS Reviews (" +
                "ReviewID INTEGER PRIMARY KEY," +
                "UserID INTEGER," +
                "CourseID INTEGER," +
                "Rating REAL NOT NULL," +
                "Comment TEXT," +
                "ReviewTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+ //unsure of how to use this data type
                "FOREIGN KEY(UserID) REFERENCES Users(UserID) ON DELETE CASCADE," +
                "FOREIGN KEY(CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE," +
                ");");

    }

    /**
     * Adds users to the Users table
     * @throws SQLException
     */
    public void addUsers(List<User> users) throws SQLException{
        try{
            var insertUserQuery = "INSERT INTO Users (UserID, Username, Password) VALUES (?, ?, ?)";
            var userStatement = connection.prepareStatement(insertUserQuery);

            for(User user: users){
                userStatement.setInt(1, user.getUserID());
                userStatement.setString(2, user.getUsername());
                userStatement.setString(3, user.getPassword());
                userStatement.executeUpdate();
            }
        } catch (SQLException e){
            rollback();
            throw e;
        }
    }

    /**
     * Adds courses to the Courses table
     * @throws SQLException
     */
    public void addCourses(List<Course> courses) throws SQLException{
        try{
            var insertCourseQuery = "INSERT INTO Courses (Mnemonic, CourseNumber, CourseName, AverageRating) VALUES (?, ?, ?, ?)";
            var courseStatement = connection.prepareStatement(insertCourseQuery);

            for(Course course: courses){
                courseStatement.setString(1, course.getMnemonic());
                courseStatement.setInt(2, course.getCourseNumber());
                courseStatement.setString(3, course.getCourseName());
                courseStatement.setDouble(4, course.getAverageRating());
                courseStatement.executeUpdate();
            }
        } catch (SQLException e){
            rollback();
            throw e;
        }
    }

    /**
     * Adds reviews to the Reviews table
     * @throws SQLException
     */
    public void addReviews(List<Review> reviews) throws SQLException{
        try{
            var insertReviewQuery = "INSERT INTO Reviews (UserID, CourseID, Rating, Comment, ReviewTimestamp) VALUES (?, ?, ?, ?, ?)";
            var reviewStatement = connection.prepareStatement(insertReviewQuery);

            for(Review review: reviews){
                reviewStatement.setInt(1, review.getUserID());
                reviewStatement.setInt(2, review.getCourseID());
                reviewStatement.setDouble(3, review.getRating());
                reviewStatement.setString(4, review.getComment());
                reviewStatement.setTimestamp(5, review.getTimestamp());
                reviewStatement.executeUpdate();
            }
        } catch (SQLException e){
            rollback();
            throw e;
        }
    }

    /**
     * Returns all users in the Users table
     * @return
     * @throws SQLException
     */
    public List<User> getAllUsers() throws SQLException{
        try{
            List<User> userList = new ArrayList<>();
            var statement = connection.prepareStatement("SELECT * FROM Users");

            var resultSet = statement.executeQuery();
            while(resultSet.next()){
                var userID = resultSet.getInt("UserID");
                var username = resultSet.getString("Username");
                var password = resultSet.getString("Password");
                var newUser = new User(userID, username, password);

                userList.add(newUser);
            }
            return userList;
        } catch (SQLException e){
            throw e;
        }
    }

    /**
     * Returns all courses in the Courses table
     * @return
     * @throws SQLException
     */
    public List<Course> getAllCourses() throws SQLException{
        try{
            List<Course> courseList = new ArrayList<>();
            var statement = connection.prepareStatement("SELECT * FROM Courses");
            var resultSet = statement.executeQuery();

            while(resultSet.next()){
                var mnemonic = resultSet.getString("Mnemonic");
                var courseNumber = resultSet.getInt("CourseNumber");
                var courseName = resultSet.getString("CourseName");
                var averageRating = resultSet.getDouble("AverageRating");
                var newCourse = new Course(mnemonic, courseNumber, courseName, averageRating);

                courseList.add(newCourse);
            }
            return courseList;
        } catch (SQLException e){
            throw e;
        }
    }

    /**
     * Returns all reviews in the Reviews table
     * @return
     * @throws SQLException
     */
    public List<Review> getAllReviews() throws SQLException{
        try{
            List<Review> reviewsList = new ArrayList<>();
            var statement = connection.prepareStatement("SELECT * FROM Reviews");
            var resultSet = statement.executeQuery();

            while(resultSet.next()){
               var userID = resultSet.getInt("UserID");
               var courseID = resultSet.getInt("CourseID");
               var rating = resultSet.getDouble("Rating");
               var comment = resultSet.getString("Comment");
               var timestamp = resultSet.getTimestamp("ReviewTimestamp");
               var newReview = new Review(userID, courseID, rating, comment, timestamp);

                reviewsList.add(newReview);
            }
            return reviewsList;
        } catch (SQLException e){
            throw e;
        }
    }
}
