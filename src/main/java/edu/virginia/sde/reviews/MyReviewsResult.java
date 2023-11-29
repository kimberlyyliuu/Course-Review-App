package edu.virginia.sde.reviews;

public class MyReviewsResult {
    private int rating;
    private String courseMnemonic;
    private int courseNumber;

    public MyReviewsResult(int rating, String courseMnemonic, int courseNumber) {
        this.rating = rating;
        this.courseMnemonic = courseMnemonic;
        this.courseNumber = courseNumber;
    }

    public int getRating() {
        return rating;
    }

    public String getCourseMnemonic() {
        return courseMnemonic;
    }

    public int getCourseNumber() {
        return courseNumber;
    }
}
