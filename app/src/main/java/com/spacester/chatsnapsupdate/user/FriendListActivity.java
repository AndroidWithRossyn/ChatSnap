package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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

public class FriendListActivity extends AppCompatActivity {

    RecyclerView reqList;
    AdapterUsers adapterUsers;
    String userId;
    List<String> idList;
    List<ModelUser> userList;
    RelativeLayout empty,mfriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        reqList = findViewById(R.id.friendslist);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        reqList.setHasFixedSize(true);
        reqList.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        idList = new ArrayList<>();
        empty = findViewById(R.id.empty);
        mfriends = findViewById(R.id.mfriends);

        getAllUsers();


    }

    private void getAllUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                if (!dataSnapshot.exists()){
                    mfriends.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelUser modelUser = snapshot.getValue(ModelUser.class);
                    for (String id : idList) {
                        assert modelUser != null;
                        if (modelUser.getId().equals(id)) {
                            userList.add(modelUser);
                        }
                        adapterUsers = new AdapterUsers(FriendListActivity.this, userList);
                        reqList.setAdapter(adapterUsers);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}