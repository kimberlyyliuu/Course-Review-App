package edu.virginia.sde.reviews;

import java.util.Objects;

public class Course {

    private final String mnemonic;

    private final int courseNumber;

    private final String courseName;

    // should be to 2 decimal places
    // defaults to 0.00 - won't have any ratings when initially created by user
    private double averageRating = 0.00;

    public Course(String mnemonic, int courseNumber, String courseName) {
        this.mnemonic = mnemonic;
        this.courseNumber = courseNumber;
        this.courseName = courseName;
    }

    public Course(String mnemonic, int courseNumber, String courseName, double averageRating){
        this.mnemonic = mnemonic;
        this.courseName = courseName;
        this.courseNumber = courseNumber;
        this.averageRating = averageRating;
    }


    public String getMnemonic() {
        return mnemonic;
    }

    public int getCourseNumber(){
        return courseNumber;
    }

    public String getCourseName() {
        return courseName;
    }

    public double getAverageRating(){ return averageRating;}



    /**
     *  Overrides toString so it displays nicely in the search screen
     */
    @Override
    public String toString(){
        // Agent: ChatGPT
        // Usage: Formatting flags
        if(averageRating < 1){
            return String.format("%s %04d  |  %s  |  Average Rating: ", mnemonic, courseNumber, courseName);
        }
        return String.format("%s %04d  |  %s  |  Average Rating: %.2f", mnemonic, courseNumber, courseName, averageRating);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        Course course = (Course) obj;

        if(!Objects.equals(getMnemonic(), course.getMnemonic())) return false;
        return Objects.equals(getCourseNumber(), course.getCourseNumber());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getMnemonic(), getCourseNumber());
    }
}
