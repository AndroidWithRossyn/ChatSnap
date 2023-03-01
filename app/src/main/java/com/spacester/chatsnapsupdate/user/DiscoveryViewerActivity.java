package com.spacester.chatsnapsupdate.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.adapter.AdapterUsers;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiscoveryViewerActivity extends AppCompatActivity {

    String id;
    List<String> idList;
    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_viewer);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        recyclerView = findViewById(R.id.friendslist);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        idList = new ArrayList<>();

        getViews();
    }
    private void getViews(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Discovery")
                .child(id).child(Objects.requireNonNull(getIntent().getStringExtra("storyid"))).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    idList.add(snapshot1.getKey());
                    adapterUsers = new AdapterUsers(DiscoveryViewerActivity.this, userList);
                    recyclerView.setAdapter(adapterUsers);

                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelUser modelUser = snapshot.getValue(ModelUser.class);
                    for (String id : idList) {
                        assert modelUser != null;
                        if (modelUser.getId().equals(id)){
                            userList.add(modelUser);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}