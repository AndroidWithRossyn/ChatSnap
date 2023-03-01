package com.spacester.chatsnapsupdate.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.MainActivity;
import com.spacester.chatsnapsupdate.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ALL")
public class Username extends AppCompatActivity {

    EditText username;
    Button signup;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        mAuth = FirebaseAuth.getInstance();
        signup = findViewById(R.id.signup);
        username = findViewById(R.id.username);
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        progressBar = findViewById(R.id.pg);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                signup.setText("Loading...");
                final String mUsername = username.getText().toString();
                if (TextUtils.isEmpty(mUsername)) {
                    Toast.makeText(getApplicationContext(), "Enter username",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }else {
                    Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(mUsername);
                    usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount()>0){
                                Toast.makeText(getApplicationContext(), "Username already exist",
                                        Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }else {
                                    addUsername(mUsername);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private void addUsername(String mUsername) {
        Map hashMap = new HashMap();
        hashMap.put("username", mUsername);
        mDatabase.updateChildren(hashMap);
        Intent intent = new Intent(Username.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
