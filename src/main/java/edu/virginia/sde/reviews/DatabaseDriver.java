package edu.virginia.sde.reviews;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
                "Subject TEXT NOT NULL," +
                "Number INTEGER NOT NULL," +
                "Title TEXT NOT NULL" +
                ");");

        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS Reviews (" +
                "ReviewID INTEGER PRIMARY KEY," +
                "Rating INTEGER NOT NULL," +
                "Comment TEXT," +
                "Timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+ //unsure of how to use this data type
                "UserID INTEGER," +
                "CourseID INTEGER," +
                "FOREIGN KEY(UserID) REFERENCES Users(UserID) ON DELETE CASCADE," +
                "FOREIGN KEY(CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE," +
                ");");

    }
}
