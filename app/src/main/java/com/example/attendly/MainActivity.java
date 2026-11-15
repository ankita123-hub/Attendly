package com.example.attendly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Insets: purple bar extends under the status bar, background fills the screen
        View appBar = findViewById(R.id.appBar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            appBar.setPadding(0, bars.top, 0, 0);
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return insets;
        });

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");

        // 1. Apply the Material 3 text style FIRST
        toolbar.setTitleTextAppearance(this, com.google.android.material.R.style.TextAppearance_Material3_HeadlineMedium);

        // 2. Apply the white text color AFTER the style
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);

        // User icon -> popup with "Log out"
        View imgUser = findViewById(R.id.imgUser);
        imgUser.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.getMenuInflater().inflate(R.menu.menu_user, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    logout();
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // Dashboard cards
        MaterialCardView cardClasses = findViewById(R.id.cardClasses);
        cardClasses.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ClassActivity.class)));

        MaterialCardView cardStudents = findViewById(R.id.cardStudents);
        cardStudents.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, StudentActivity.class)));

        MaterialCardView cardAttendance = findViewById(R.id.cardAttendance);
        cardAttendance.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AttendanceActivity.class)));

        MaterialCardView cardHistory = findViewById(R.id.cardHistory);
        cardHistory.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
    }

    private void logout() {
        // TODO: clear your saved session here, e.g.
        // getSharedPreferences("session", MODE_PRIVATE).edit().clear().apply();
        // or FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, LoginActivity.class); // change to your login screen
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}