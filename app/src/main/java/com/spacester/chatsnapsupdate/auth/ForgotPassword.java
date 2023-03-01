package com.spacester.chatsnapsupdate.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.spacester.chatsnapsupdate.R;

import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {

    EditText username;
    Button signup;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        signup = findViewById(R.id.signup);
        username = findViewById(R.id.username);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar = findViewById(R.id.pg);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = username.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);

                }else {

                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            { Toast.makeText(getApplicationContext(), "Reset link sent",
                                    Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            }else {
                                String msg = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(getApplicationContext(), msg,
                                        Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        }
                    });
                }
            }
        });
    }
}