package de.christcoding.smartstudy;

import static com.google.firebase.firestore.Filter.equalTo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import de.christcoding.smartstudy.Builder.EventBuilder;

import de.christcoding.smartstudy.models.Event;
import de.christcoding.smartstudy.models.Group;
import de.christcoding.smartstudy.models.Member;
import de.christcoding.smartstudy.models.User;
import de.christcoding.smartstudy.utilities.Constants;
import de.christcoding.smartstudy.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());
    //Variables
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    PreferenceManager preferenceManager;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView title, headertext, pointsCount;
    LinearLayout points;
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
        navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        headertext = header.findViewById(R.id.headertext);

        preferenceManager = new PreferenceManager(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            this.finish();
        } else {
            preferenceManager.putString(Constants.KEY_EMAIL, user.getEmail());
            drawerLayout = findViewById(R.id.drawer);
            toolbar = findViewById(R.id.toolbar);
            title = findViewById(R.id.variabel_text);
            pointsCount = findViewById(R.id.points);
            points = findViewById(R.id.pointsContainer);
            //super.onCreate(savedInstanceState);
            if(getIntent() != null && getIntent().getExtras() != null) {
                String notificationType = getIntent().getExtras().getString("notificationType");
                String fragment = getIntent().getExtras().getString("fragment");
                if (notificationType != null) {
                    switch (notificationType) {
                        case "flashcards":
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, new StudyFragment()).commit();
                            title.setText("Study");
                            navigationView.setCheckedItem(R.id.nav_study);
                            break;
                        case "testExam":
                            startActivity(new Intent(MainActivity.this, AiGenerateExam.class));
                            break;
                        case "chatMessage":
                            Intent intent = new Intent(MainActivity.this, GroupChatActivity.class);
                            String groupId = getIntent().getExtras().getString(Constants.KEY_GROUP_ID);
                            preferenceManager.putString(Constants.KEY_GROUP_NAME, getIntent().getExtras().getString(Constants.KEY_GROUP_NAME));
                            preferenceManager.putString(Constants.KEY_GROUP_ID, groupId);
                            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_GROUPS).document(groupId).get()
                                    .addOnSuccessListener(document -> {
                                        Group group = document.toObject(Group.class);
                                        for (Member member : group.members) {
                                            if (member.email.equals(user.getEmail())) {
                                                intent.putExtra(Constants.KEY_SENDER, member);
                                                break;
                                            }
                                        }
                                        startActivity(intent);
                                    });
                            break;
                        case "newExamAdded":
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, new PlanFragment()).commit();
                            title.setText("Plan");
                            navigationView.setCheckedItem(R.id.nav_plan);
                            break;
                        default:
                            break;
                    }
                }
            }

            points.setOnClickListener(view -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new PointsFragment()).commit();
                title.setText("Points");
                navigationView.setCheckedItem(R.id.nav_points);
            });

            final String[] username = {""};
            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS).document(user.getEmail()).get()
                    .addOnSuccessListener(document -> {
                        username[0] = document.getString(Constants.KEY_USER_NAME);
                        setHeaderText(username[0]);

                        navigationView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                setHeaderText(username[0]);
                            }
                        });
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
                        new MainFragment(), "home").commit();
                navigationView.setCheckedItem(R.id.nav_home);
            }
            navigationView.setNavigationItemSelectedListener(this);

            getFCMToken();
            FirebaseMessaging.getInstance().subscribeToTopic("News")
                    .addOnCompleteListener(task -> {
                        String message = "Successful";
                        if (!task.isSuccessful()) {
                            message = "Failed";
                        }
                        logger.log(Level.INFO, message);
                    });
            displayCurrentUserPoints();
            updateEventsFromGroups();
            if(preferenceManager.getBoolean(Constants.KEY_TUTORIAL)) {
                tuturial();
            }
        }
    }

    private void tuturial() {

    }

    private void displayCurrentUserPoints() {
        db.collection(Constants.KEY_COLLECTION_USERS).document(user.getEmail()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    pointsCount.setText(String.valueOf(user.points));
                });
    }

    private void updateEventsFromGroups() {
        db.collection(Constants.KEY_COLLECTION_USERS).document(user.getEmail())
                .collection(Constants.KEY_COLLECTION_GROUPS)
                .where(equalTo(Constants.KEY_ADD_EXAMS_TO_PLAN, true)).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Stream.Builder<String> groupIdsBuilder = Stream.builder();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        groupIdsBuilder.add(document.getId());
                    }
                    List<String> groupIds = groupIdsBuilder.build().collect(Collectors.toList());
                    try (DBEventHelper dbEventHelper = new DBEventHelper(getApplicationContext())) {
                        for (String id : groupIds) {
                            db.collection(Constants.KEY_COLLECTION_GROUPS).document(id)
                                    .get().addOnSuccessListener(doc -> {
                                        Group group = doc.toObject(Group.class);
                                        if(group.events != null) {
                                            for (Event event : group.events) {
                                                List<String> notWanted = event.getNotWanted();
                                                if (notWanted == null || !notWanted.contains(user.getEmail())) {
                                                    if(event.getDbId() != 0) {
                                                        dbEventHelper.deleteEventObject(new EventBuilder().setId(String.valueOf(event.getDbId())).build());
                                                    } else {
                                                        addPoints();
                                                    }
                                                    List<Event> events = group.events;
                                                    events.remove(event);
                                                    event.setNecessaryMissingAttributes(id);
                                                    long eventID = dbEventHelper.addEventObject(event);
                                                    event.setDbId(eventID);
                                                    events.add(event);
                                                    db.collection(Constants.KEY_COLLECTION_GROUPS).document(id)
                                                            .update(Constants.KEY_EVENTS, events);
                                                }
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        showToast("Failed to update events");
                                        logger.log(Level.WARNING, "Failed to update events", e);
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to update events");
                    logger.log(Level.WARNING, "Failed to update events", e);
                });
    }

    private void addPoints() {
        int points = Integer.parseInt(pointsCount.getText().toString().trim());
        points += 50;
        pointsCount.setText(String.valueOf(points));
        YoYo.with(Techniques.Bounce)
                .duration(500)
                .repeat(1)
                .playOn(pointsCount);

        db.collection(Constants.KEY_COLLECTION_USERS).document(user.getEmail())
                .update(Constants.KEY_POINTS, FieldValue.increment(50));
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            logger.log(Level.WARNING, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        updateToken(token);

                        // Log and toast
                        logger.log(Level.INFO, token);
                    }
                });
    }

    private void updateToken(String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_USERS)
                .document(user.getEmail());
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    private void setHeaderText(String username) {
        if(headertext!=null) {
            headertext.setText("Hello " + username);
           /* if (!sp.getBoolean("studyNeed", false)) {
                headertext.setText("Hello " + username + ", no need to study anymore today");
            } else {
                headertext.setText("Let's get to study, " + username);
            }*/
        }
    }

    @Override
    public void onBackPressed() {
        Fragment home = getSupportFragmentManager().findFragmentByTag("home");
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if(home == null || !home.isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new MainFragment(), "home").commit();
            title.setText("Home");
            navigationView.setCheckedItem(R.id.nav_home);
        }else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new MainFragment(), "home").commit();
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
                        new TimetableFragment(null)).commit();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new PlanFragment()).commit();
                title.setText("Plan");
                navigationView.setCheckedItem(R.id.nav_plan);
                break;
            case R.id.nav_points:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new PointsFragment()).commit();
                title.setText("Points");
                navigationView.setCheckedItem(R.id.nav_points);
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
                title.setText("Settings");
                navigationView.setCheckedItem(R.id.nav_settings);
                break;
            case R.id.nav_logout:
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseFirestore.getInstance()
                                .collection(Constants.KEY_COLLECTION_USERS)
                                .document(user.getEmail())
                                .update(Constants.KEY_FCM_TOKEN, FieldValue.delete())
                                .addOnSuccessListener(unused -> {
                                    preferenceManager.clearPreferences();
                                    auth.signOut();
                                    Intent intent = new Intent(this, Login.class);
                                    startActivity(intent);
                                    this.finish();
                                })
                                .addOnFailureListener(e -> {
                                    showToast("Unable to sign out");
                                });
                    }
                });
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}