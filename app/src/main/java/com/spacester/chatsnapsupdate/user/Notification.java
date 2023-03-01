package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.adapter.AdapterNotify;
import com.spacester.chatsnapsupdate.model.ModelNotification;
import com.spacester.chatsnapsupdate.R;

import java.util.ArrayList;
import java.util.Objects;

public class Notification extends AppCompatActivity {

    String userId;
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelNotification> notifications;
    private AdapterNotify adapterNotify;
    RelativeLayout empty,mfriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
       ImageView back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.friendslist);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        firebaseAuth = FirebaseAuth.getInstance();
        empty = findViewById(R.id.empty);
        mfriends = findViewById(R.id.mfriends);
        getAllNotifications();
    }
    private void getAllNotifications() {
        notifications = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notifications.clear();
                        if (!snapshot.exists()){
                            mfriends.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                        }
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelNotification modelNotification = ds.getValue(ModelNotification.class);
                            notifications.add(modelNotification);
                        }
                        adapterNotify = new AdapterNotify(Notification.this, notifications);
                        recyclerView.setAdapter(adapterNotify);
                                  }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}