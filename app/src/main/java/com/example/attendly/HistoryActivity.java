package com.example.attendly;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private HistoryAdapter adapter;

    private Spinner spClass;
    private TextView tvDate, tvSummary, tvEmpty;

    private final Calendar selectedDate = Calendar.getInstance();
    private final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

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
        tvSummary = findViewById(R.id.tvSummary);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter();
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
                loadHistory(); // also runs once when the screen opens
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
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
                    loadHistory();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void loadHistory() {
        String className = spClass.getSelectedItem().toString();
        String date = dbFormat.format(selectedDate.getTime());

        List<AttendanceRecord> records = dbHelper.getAttendanceRecords(className, date);
        adapter.setRecords(records);

        if (records.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvSummary.setVisibility(View.GONE);
        } else {
            int absent = 0;
            for (AttendanceRecord r : records) {
                if (r.isAbsent()) absent++;
            }
            int total = records.size();
            tvSummary.setText("Total: " + total + "   •   Present: " + (total - absent)
                    + "   •   Absent: " + absent);
            tvSummary.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}