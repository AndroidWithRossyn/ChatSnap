package com.spacester.chatsnapsupdate;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ChangeEmail extends AppCompatActivity {

    EditText mail,pass;
    Button login;
    FirebaseAuth mAuth;
    ImageView back;
    ProgressBar pg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        mail = findViewById(R.id.mail);
        mAuth = FirebaseAuth.getInstance();
        pass = findViewById(R.id.pass);
        login = findViewById(R.id.login);
        back = findViewById(R.id.back);
        pg = findViewById(R.id.pg);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pg.setVisibility(View.VISIBLE);
                final String newE = mail.getText().toString().trim();
                final String newP = pass.getText().toString().trim();
                if (TextUtils.isEmpty(newE)){
                    Toast.makeText(getApplicationContext(), "Enter your new Email", Toast.LENGTH_LONG).show();
                    pg.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(newP)){
                    Toast.makeText(getApplicationContext(), "Enter your current password", Toast.LENGTH_LONG).show();
                    pg.setVisibility(View.GONE);
                    return;
                }
                Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(newE);
                emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount()>0){
                            Toast.makeText(getApplicationContext(), "Email already exist", Toast.LENGTH_LONG).show();
                            pg.setVisibility(View.GONE);
                        }
                        updateEmail(newE,newP);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void updateEmail(final String newE, String newP) {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()), newP);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseUser.updateEmail(newE)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Email changed", Toast.LENGTH_LONG).show();
                                        pg.setVisibility(View.GONE);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                pg.setVisibility(View.GONE);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                pg.setVisibility(View.GONE);
            }
        });
    }
}