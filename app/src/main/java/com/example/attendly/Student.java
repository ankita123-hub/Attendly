package com.example.attendly;

public class Student {
    private final int id;
    private final String name;
    private final String rollNo;
    private final String className;

    public Student(int id, String name, String rollNo, String className) {
        this.id = id;
        this.name = name;
        this.rollNo = rollNo;
        this.className = className;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRollNo() { return rollNo; }
    public String getClassName() { return className; }
}