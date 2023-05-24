package com.example.smartstudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables
    FirebaseAuth auth;
    FirebaseUser user;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView title, headertext;
    private boolean studyneed = false;


    public boolean isStudyneed() {
        return studyneed;
    }

    public void setStudyneed(boolean studyneed) {
        this.studyneed = studyneed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            this.finish();
        }

        SharedPreferences sp = this.getSharedPreferences("SP", 0);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.variabel_text);

        final String[] username = new String[1];
        FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                if (queryDocumentSnapshots.getDocuments().get(i).getString("email").equalsIgnoreCase(user.getEmail())) {
                    username[0] = queryDocumentSnapshots.getDocuments().get(i).getString("username");
                    navigationView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            headertext = navigationView.findViewById(R.id.headertext);
                            if (!sp.getBoolean("studyNeed", false)) {
                                headertext.setText("Hello " + username[0] + ", no need to study anymore today");
                            } else {
                                headertext.setText("Let's get to study, " + username[0]);
                            }

                            navigationView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                }
            }
        });
        //headertext.setText("Hello " + username + ", no need to study today");
        //", good job!"
        //", you have to study more!"

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new MainFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new MainFragment()).commit();
                title.setText("Home");
                navigationView.setCheckedItem(R.id.nav_home);
                break;
            case R.id.nav_group:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new GroupFragment()).commit();
                title.setText("Group");
                navigationView.setCheckedItem(R.id.nav_group);
                break;
            case R.id.nav_timetable:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new TimetableFragment()).commit();
                title.setText("Timetable");
                navigationView.setCheckedItem(R.id.nav_timetable);
                break;
            case R.id.nav_time:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new TimeFragment()).commit();
                title.setText("Time");
                navigationView.setCheckedItem(R.id.nav_time);
                break;
            case R.id.nav_study:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new StudyFragment()).commit();
                title.setText("Study");
                navigationView.setCheckedItem(R.id.nav_study);
                break;
            case R.id.nav_plan:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new Plan()).commit();
                title.setText("Plan");
                navigationView.setCheckedItem(R.id.nav_plan);
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
                title.setText("Settings");
                navigationView.setCheckedItem(R.id.nav_settings);
                break;
            case R.id.nav_logout:
                auth.signOut();
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                this.finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}