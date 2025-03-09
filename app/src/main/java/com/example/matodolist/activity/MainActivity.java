package com.example.matodolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.matodolist.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private RecyclerView recyclerView;
    private TodoListAdapter todoListAdapter;
    private ArrayList<String> todoList = new ArrayList<>();
    private EditText editTextTask;
    private Button addTaskButton;
    private TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            launchSignInScreen();
        } else {
            showWelcomeMessage(currentUser);
            initializeUI();
        }
    }

    private void launchSignInScreen() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build()
                        ))
                        .setTheme(R.style.FirebaseLoginTheme)
                        .setLogo(R.drawable.todolist_logo)
                        .build(),
                RC_SIGN_IN
        );
    }

    private void showWelcomeMessage(FirebaseUser user) {
        welcomeMessage = findViewById(R.id.welcomeMessage);
        String userName = user.getDisplayName();
        String welcomeText = "Bienvenue " + (userName != null ? userName : "User") + "!";
        welcomeMessage.setText(welcomeText);
    }

    private void initializeUI() {
        recyclerView = findViewById(R.id.recyclerViewTodoList);
        editTextTask = findViewById(R.id.editTextTask);
        addTaskButton = findViewById(R.id.addTaskButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoListAdapter = new TodoListAdapter(todoList);
        recyclerView.setAdapter(todoListAdapter);

        addTaskButton.setOnClickListener(v -> {
            String task = editTextTask.getText().toString();
            if (!task.isEmpty()) {
                todoList.add(task);
                todoListAdapter.notifyDataSetChanged();
                editTextTask.setText("");
            } else {
                Toast.makeText(this, "Veuillez saisir une tâche", Toast.LENGTH_SHORT).show();
            }
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(task -> {
                        Toast.makeText(this, "Déconnecté avec succès", Toast.LENGTH_SHORT).show();
                        launchSignInScreen();
                        finish();
                    });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    showWelcomeMessage(user);
                    initializeUI();
                }
            } else {
                if (response == null) {
                    Toast.makeText(this, "Connexion annulée", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Erreur de connexion: " + response.getError(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}