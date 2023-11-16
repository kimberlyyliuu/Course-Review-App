package edu.virginia.sde.reviews;

public class Course {

    private final String mnemonic;

    private final String courseNumber;

    private final String courseName;

    public Course(String mnemonic, String courseNumber, String courseName){
        this.mnemonic = mnemonic;
        this.courseName = courseName;
        this.courseNumber = courseNumber;
    }

    public String getCourseNumber(){
        return courseNumber;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        Course course = (Course) obj;

        if(!getMnemonic().equals(course.getMnemonic())) return false;
        return getCourseNumber().equals(course.getCourseNumber());

    }

    @Override
    public int hashCode() {
        int result = getMnemonic().hashCode();
        result = 31 * result + getCourseNumber().hashCode();
        return result;
    }
}
