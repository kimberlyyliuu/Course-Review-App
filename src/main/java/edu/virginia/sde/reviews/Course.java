package edu.virginia.sde.reviews;

import java.util.Objects;

public class Course {

    private final String mnemonic;

    private final int courseID;

    private final int courseNumber;

    private final String courseName;

    private double averageRating; //should be to 2 decimal places

    public Course(int courseID, String mnemonic, int courseNumber, String courseName, double averageRating){
        this.courseID = courseID;
        this.mnemonic = mnemonic;
        this.courseName = courseName;
        this.courseNumber = courseNumber;
        this.averageRating = averageRating;
    }


    public int getCourseID(){return courseID;}
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
