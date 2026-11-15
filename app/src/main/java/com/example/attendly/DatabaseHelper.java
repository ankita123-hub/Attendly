package com.example.attendly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "attendly.db";
    private static final int DB_VERSION = 4; // 4 = attendance table added

    // ---- students table ----
    public static final String TABLE_STUDENTS = "students";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_ROLL = "roll_no";
    public static final String COL_CLASS = "class_name";

    // ---- attendance table ----
    public static final String TABLE_ATTENDANCE = "attendance";
    public static final String COL_STUDENT_ID = "student_id";
    public static final String COL_STUDENT_NAME = "student_name";
    public static final String COL_DATE = "date";          // stored as yyyy-MM-dd
    public static final String COL_ABSENT = "is_absent";   // 1 = absent, 0 = present

    // The fixed list of classes shown in the dropdowns
    public static final String[] CLASS_OPTIONS = {
            "MCA 1st Year Sec A",   // index 0
            "MCA 1st Year Sec B",   // index 1
            "MCA 2nd Year Sec A",   // index 2
            "MCA 2nd Year Sec B"    // index 3
    };

    private static final String C1A = CLASS_OPTIONS[0];
    private static final String C1B = CLASS_OPTIONS[1];
    private static final String C2A = CLASS_OPTIONS[2];
    private static final String C2B = CLASS_OPTIONS[3];

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_STUDENTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_ROLL + " TEXT NOT NULL, "
                + COL_CLASS + " TEXT NOT NULL, "
                + "UNIQUE (" + COL_ROLL + ", " + COL_CLASS + "))");

        createAttendanceTable(db);
        seedStudents(db); // predefined students, runs once on first install
    }

    private void createAttendanceTable(SQLiteDatabase db) {
        // student name/roll/class are copied here so history stays correct
        // even if a student is removed from the list later
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ATTENDANCE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_STUDENT_ID + " INTEGER NOT NULL, "
                + COL_STUDENT_NAME + " TEXT NOT NULL, "
                + COL_ROLL + " TEXT NOT NULL, "
                + COL_CLASS + " TEXT NOT NULL, "
                + COL_DATE + " TEXT NOT NULL, "
                + COL_ABSENT + " INTEGER NOT NULL, "
                + "UNIQUE (" + COL_STUDENT_ID + ", " + COL_DATE + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // very old structure: full reset
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
            onCreate(db);
        } else if (oldVersion < 4) {
            // keep existing students, just add the attendance table
            createAttendanceTable(db);
        }
    }

    private void seedStudents(SQLiteDatabase db) {
        String[][] defaults = {
                // {name, roll number, class}

                // ----- Top: MCA 2nd Year Sec A -----
                {"Subhankar Maity", "016", C2A},
                {"Tanmay Pal", "017", C2A},
                {"Ankita Paul", "008", C2A},
                {"Sanjukta Sarkar", "005", C2A},

                // ----- Earlier predefined students -----
                {"Aarav Sharma", "101", C1A},
                {"Priya Das", "102", C1A},
                {"Rohan Mukherjee", "103", C1B},
                {"Sneha Roy", "104", C2A},
                {"Imran Khan", "105", C2B},

                // ----- MCA 1st Year Sec A (10) -----
                {"Arjun Banerjee", "001", C1A},
                {"Debojyoti Ghosh", "002", C1A},
                {"Riya Chatterjee", "003", C1A},
                {"Sourav Dutta", "004", C1A},
                {"Pooja Singh", "005", C1A},
                {"Abhishek Mondal", "006", C1A},
                {"Nandini Saha", "007", C1A},
                {"Rahul Biswas", "008", C1A},
                {"Mitali Pramanik", "009", C1A},
                {"Koushik Halder", "010", C1A},

                // ----- MCA 1st Year Sec B (10) -----
                {"Ishita Bose", "001", C1B},
                {"Sayan Mitra", "002", C1B},
                {"Payel Dey", "003", C1B},
                {"Anirban Sen", "004", C1B},
                {"Moumita Paul", "005", C1B},
                {"Soumya Chakraborty", "006", C1B},
                {"Rajat Kundu", "007", C1B},
                {"Tuli Majumdar", "008", C1B},
                {"Dipanjan Roy", "009", C1B},
                {"Sreya Nandi", "010", C1B},

                // ----- MCA 2nd Year Sec A (10) -----
                {"Bikash Das", "001", C2A},
                {"Rima Ghosh", "002", C2A},
                {"Suman Patra", "003", C2A},
                {"Anindita Sinha", "004", C2A},
                {"Joydeep Laha", "006", C2A},
                {"Barnali Dey", "007", C2A},
                {"Prasenjit Barman", "009", C2A},
                {"Shreya Kar", "010", C2A},
                {"Debasish Mandal", "011", C2A},
                {"Madhuri Pal", "012", C2A},

                // ----- MCA 2nd Year Sec B (10) -----
                {"Aritra Sarkar", "001", C2B},
                {"Sutapa Roy", "002", C2B},
                {"Kunal Jana", "003", C2B},
                {"Trisha Bhattacharya", "004", C2B},
                {"Ayan Dolui", "005", C2B},
                {"Puja Maity", "006", C2B},
                {"Saikat Giri", "007", C2B},
                {"Rupsa Das", "008", C2B},
                {"Sumit Pradhan", "009", C2B},
                {"Namita Khatun", "010", C2B}
        };

        for (String[] s : defaults) {
            ContentValues cv = new ContentValues();
            cv.put(COL_NAME, s[0]);
            cv.put(COL_ROLL, s[1]);
            cv.put(COL_CLASS, s[2]);
            db.insert(TABLE_STUDENTS, null, cv);
        }
    }

    // =====================================================================
    //  STUDENTS
    // =====================================================================

    /** Returns true if inserted, false if failed (e.g. duplicate roll number in the same class). */
    public boolean addStudent(String name, String rollNo, String className) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_ROLL, rollNo);
        cv.put(COL_CLASS, className);
        return db.insert(TABLE_STUDENTS, null, cv) != -1;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_STUDENTS, null, null, null,
                null, null, COL_ID + " ASC")) {
            while (c.moveToNext()) {
                list.add(readStudent(c));
            }
        }
        return list;
    }

    /** Students of one class, ordered by roll number. */
    public List<Student> getStudentsByClass(String className) {
        List<Student> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_STUDENTS, null,
                COL_CLASS + " = ?", new String[]{className},
                null, null,
                "CAST(" + COL_ROLL + " AS INTEGER) ASC, " + COL_ROLL + " ASC")) {
            while (c.moveToNext()) {
                list.add(readStudent(c));
            }
        }
        return list;
    }

    private Student readStudent(Cursor c) {
        return new Student(
                c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_ROLL)),
                c.getString(c.getColumnIndexOrThrow(COL_CLASS)));
    }

    /** Deletes the student with this id. Returns true if a row was removed. */
    public boolean deleteStudent(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_STUDENTS, COL_ID + " = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    // =====================================================================
    //  ATTENDANCE
    // =====================================================================

    /**
     * Saves attendance for one class on one date (date format: yyyy-MM-dd).
     * If attendance was already taken for that class and date, it is replaced.
     */
    public void saveAttendance(String className, String date,
                               List<Student> students, Set<Integer> absentIds) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_ATTENDANCE,
                    COL_CLASS + " = ? AND " + COL_DATE + " = ?",
                    new String[]{className, date});

            for (Student s : students) {
                ContentValues cv = new ContentValues();
                cv.put(COL_STUDENT_ID, s.getId());
                cv.put(COL_STUDENT_NAME, s.getName());
                cv.put(COL_ROLL, s.getRollNo());
                cv.put(COL_CLASS, className);
                cv.put(COL_DATE, date);
                cv.put(COL_ABSENT, absentIds.contains(s.getId()) ? 1 : 0);
                db.insert(TABLE_ATTENDANCE, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /** Saved attendance for a class on a date. Empty list = attendance not taken. */
    public List<AttendanceRecord> getAttendanceRecords(String className, String date) {
        List<AttendanceRecord> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_ATTENDANCE, null,
                COL_CLASS + " = ? AND " + COL_DATE + " = ?",
                new String[]{className, date},
                null, null,
                "CAST(" + COL_ROLL + " AS INTEGER) ASC, " + COL_ROLL + " ASC")) {
            while (c.moveToNext()) {
                list.add(new AttendanceRecord(
                        c.getInt(c.getColumnIndexOrThrow(COL_STUDENT_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_STUDENT_NAME)),
                        c.getString(c.getColumnIndexOrThrow(COL_ROLL)),
                        c.getInt(c.getColumnIndexOrThrow(COL_ABSENT)) == 1));
            }
        }
        return list;
    }
}