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

import java.util.Objects;

public class ChangePassword extends AppCompatActivity {

    EditText mail,pass;
    Button login;
    FirebaseAuth mAuth;
    ImageView back;
    ProgressBar pg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mail = findViewById(R.id.mail);
        mAuth = FirebaseAuth.getInstance();
        pass = findViewById(R.id.pass);
        pg = findViewById(R.id.pg);
        login = findViewById(R.id.login);
        back = findViewById(R.id.back);
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
                String oldP = mail.getText().toString().trim();
                String newP = pass.getText().toString().trim();
                if (TextUtils.isEmpty(oldP)){
                    Toast.makeText(getApplicationContext(), "Enter your current password", Toast.LENGTH_LONG).show();
                    pg.setVisibility(View.GONE);
                    return;
                }else if (TextUtils.isEmpty(newP)){
                    Toast.makeText(getApplicationContext(), "Enter your new password", Toast.LENGTH_LONG).show();
                    pg.setVisibility(View.GONE);
                    return;
                }else if (newP.length()<6){
                    Toast.makeText(getApplicationContext(), "Password should have minimum 6 characters", Toast.LENGTH_LONG).show();
                    pg.setVisibility(View.GONE);
                    return;
                }
                updatePassword(oldP,newP);
            }
        });
    }

    private void updatePassword(String oldP, final String newP) {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()), oldP);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseUser.updatePassword(newP)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Password updated", Toast.LENGTH_LONG).show();
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