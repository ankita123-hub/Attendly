package com.example.attendly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StudentActivity extends AppCompatActivity {

    private ImageView backButton;
    private FloatingActionButton fabAddStudent;

    private DatabaseHelper dbHelper;
    private StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge display
        EdgeToEdge.enable(this);

        // Set Layout
        setContentView(R.layout.activity_student);

        // Hide the default action bar if the theme has one (we use our own header)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Handle system bar insets:
        // blue header extends behind the status bar, content avoids the nav bar
        View root = findViewById(R.id.main);
        View header = findViewById(R.id.headerContainer);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            header.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            view.setPadding(0, 0, 0, systemBars.bottom);

            return windowInsets;
        });

        // Back button
        backButton = findViewById(R.id.backButton3);
        backButton.setOnClickListener(v -> finish());

        // Database + list setup
        dbHelper = new DatabaseHelper(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter();
        recyclerView.setAdapter(adapter);

        // When a row's delete icon is tapped, ask for confirmation
        adapter.setOnDeleteClickListener(this::showDeleteConfirmDialog);

        loadStudents();

        // Initialize Add Student FAB
        fabAddStudent = findViewById(R.id.fabAddStudent);
        fabAddStudent.setOnClickListener(v -> showAddStudentDialog());
    }

    // Reads all students from SQLite and shows them in the list
    private void loadStudents() {
        adapter.setStudents(dbHelper.getAllStudents());
    }

    // Confirmation popup before removing a student
    private void showDeleteConfirmDialog(Student student) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Student")
                .setMessage("Remove " + student.getName() + " (Roll No: "
                        + student.getRollNo() + ") from the list?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    if (dbHelper.deleteStudent(student.getId())) {
                        loadStudents();
                        Toast.makeText(this, "Student removed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Could not remove student", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Popup form to add a new student
    private void showAddStudentDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_student, null);

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etRollNo = dialogView.findViewById(R.id.etRollNo);
        Spinner spClass = dialogView.findViewById(R.id.spClass);

        // Fill the dropdown with the fixed class list
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                DatabaseHelper.CLASS_OPTIONS);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClass.setAdapter(classAdapter);

        new AlertDialog.Builder(this)
                .setTitle("Add Student")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String roll = etRollNo.getText().toString().trim();
                    String cls = spClass.getSelectedItem().toString();

                    if (name.isEmpty() || roll.isEmpty()) {
                        Toast.makeText(this, "Please fill name and roll number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (dbHelper.addStudent(name, roll, cls)) {
                        loadStudents();
                        Toast.makeText(this, "Student added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Roll number already exists", Toast.LENGTH_SHORT).show();
                    }
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