package com.spacester.chatsnapsupdate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spacester.chatsnapsupdate.auth.Welcome;

@SuppressWarnings("ALL")
public class Check extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(Check.this, Welcome.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(Check.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}