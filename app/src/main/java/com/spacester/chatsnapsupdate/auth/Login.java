package com.spacester.chatsnapsupdate.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.MainActivity;
import com.spacester.chatsnapsupdate.R;

import java.util.Objects;

@SuppressWarnings("ALL")
public class Login extends AppCompatActivity {

    ImageView back;
    EditText mail,pass;
    TextView textView2;
    Button login;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        back = findViewById(R.id.back);
        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.pass);
        progressBar = findViewById(R.id.pg);
        textView2 = findViewById(R.id.textView2);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
        login = findViewById(R.id.login);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                login.setText("Loading...");
                String email = mail.getText().toString().trim();
                String password = pass.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    login.setText("Log in");
                    return;
                }else if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    login.setText("Log in");
                    return;
                }else {
                    log(email,password);
                }
            }
        });
    }
    private void log(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            login.setText("Log in");
                        }
                    });
                }
                else {
                    String msg = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(getApplicationContext(), msg,
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    login.setText("Log in");
                }
            }
        });
    }
}