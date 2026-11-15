package com.example.attendly;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AttendanceActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private AttendanceAdapter adapter;

    private Spinner spClass;
    private TextView tvDate, tvNote, tvEmpty;
    private MaterialButton btnSubmit;

    private final Calendar selectedDate = Calendar.getInstance();
    private final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // System bar insets (same as the Students page)
        View root = findViewById(R.id.main);
        View header = findViewById(R.id.headerContainer);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            header.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            view.setPadding(0, 0, 0, systemBars.bottom);
            return windowInsets;
        });

        // Back button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);

        spClass = findViewById(R.id.spClass);
        tvDate = findViewById(R.id.tvDate);
        tvNote = findViewById(R.id.tvNote);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnSubmit = findViewById(R.id.btnSubmit);

        // List
        RecyclerView recyclerView = findViewById(R.id.recyclerViewAttendance);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceAdapter();
        recyclerView.setAdapter(adapter);

        // Date (default = today)
        updateDateText();
        tvDate.setOnClickListener(v -> showDatePicker());

        // Class dropdown
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, DatabaseHelper.CLASS_OPTIONS);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClass.setAdapter(classAdapter);
        spClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadStudents(); // also runs once when the screen opens
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnSubmit.setOnClickListener(v -> confirmAndSubmit());
    }

    private String selectedClass() {
        return spClass.getSelectedItem().toString();
    }

    private void updateDateText() {
        tvDate.setText(displayFormat.format(selectedDate.getTime()));
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    selectedDate.set(year, month, day);
                    updateDateText();
                    loadStudents();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // no future dates
        dialog.show();
    }

    // Loads the students of the chosen class (and saved attendance, if any)
    private void loadStudents() {
        String className = selectedClass();
        String date = dbFormat.format(selectedDate.getTime());

        List<Student> students = dbHelper.getStudentsByClass(className);

        // If attendance was already taken, restore the saved choices
        List<AttendanceRecord> saved = dbHelper.getAttendanceRecords(className, date);
        Set<Integer> absentIds = new HashSet<>();
        for (AttendanceRecord r : saved) {
            if (r.isAbsent()) absentIds.add(r.getStudentId());
        }

        adapter.setData(students, absentIds);

        boolean empty = students.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!empty);

        if (!saved.isEmpty()) {
            tvNote.setText("Attendance already taken for this date. Submitting will update it.");
            tvNote.setVisibility(View.VISIBLE);
        } else {
            tvNote.setVisibility(View.GONE);
        }
    }

    private void confirmAndSubmit() {
        int total = adapter.getStudents().size();
        int absent = adapter.getAbsentIds().size();
        int present = total - absent;

        new AlertDialog.Builder(this)
                .setTitle("Submit Attendance")
                .setMessage(selectedClass() + "\n" + displayFormat.format(selectedDate.getTime())
                        + "\n\nPresent: " + present + "\nAbsent: " + absent)
                .setPositiveButton("Submit", (dialog, which) -> {
                    dbHelper.saveAttendance(
                            selectedClass(),
                            dbFormat.format(selectedDate.getTime()),
                            adapter.getStudents(),
                            adapter.getAbsentIds());
                    Toast.makeText(this, "Attendance saved", Toast.LENGTH_SHORT).show();
                    loadStudents(); // refreshes the "already taken" note
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}