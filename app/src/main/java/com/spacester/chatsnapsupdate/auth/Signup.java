package com.spacester.chatsnapsupdate.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.R;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("ALL")
public class Signup extends AppCompatActivity {

    ImageView back;
    EditText mail,pass,mName;
    Button signup;
    private FirebaseAuth mAuth;
    DatabaseReference reference;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        back = findViewById(R.id.back);
        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.pass);
        signup = findViewById(R.id.signup);
        mName = findViewById(R.id.name);
        progressBar = findViewById(R.id.pg);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                signup.setText("Loading...");
                final String email = mail.getText().toString().trim();
                final String name = mName.getText().toString().trim();
                final String password = pass.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter name",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    signup.setText("Continue");
                    return;
                }else if ( TextUtils.isEmpty(email) ){
                    Toast.makeText(getApplicationContext(), "Enter email",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    signup.setText("Continue");
                    return;
                }else if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    signup.setText("Continue");
                    return;
                }
                else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password should have minimum 6 characters",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    signup.setText("Continue");
                    return;
                } else {
                    Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(email);
                emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            Toast.makeText(getApplicationContext(), "Email already exist",
                                    Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            signup.setText("Continue");
                            return;
                        } else {
                            register_btn(email, name, password);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                                Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        signup.setText("Continue");
                    }
                });
            }

            }

        });
    }

    private void register_btn(final String email,final String name, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String userid = Objects.requireNonNull(firebaseUser).getUid();

                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("name", name);
                    hashMap.put("email", email);
                    hashMap.put("username", "");
                    hashMap.put("status","online");
                    hashMap.put("typingTo","noOne");
                    hashMap.put("photo", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/avatar.png?alt=media&token=53a1d14a-875a-4ab0-ba34-f4c611a02e65");
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(Signup.this, Username.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            signup.setText("Continue");
                        }
                    });

                }else {
                    String msg = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(getApplicationContext(), msg,
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    signup.setText("Continue");
                }
            }
        });


    }
}