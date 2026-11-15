package com.example.attendly;

public class AttendanceRecord {
    private final int studentId;
    private final String studentName;
    private final String rollNo;
    private final boolean absent;

    public AttendanceRecord(int studentId, String studentName, String rollNo, boolean absent) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.absent = absent;
    }

    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getRollNo() { return rollNo; }
    public boolean isAbsent() { return absent; }
}