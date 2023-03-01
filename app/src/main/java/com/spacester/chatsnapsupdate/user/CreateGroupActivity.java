package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.adapter.GroupAdapter;
import com.spacester.chatsnapsupdate.model.ModelGroups;
import com.spacester.chatsnapsupdate.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CreateGroupActivity extends AppCompatActivity {

    Button create;
    FirebaseUser currentUser;
    EditText mName;
    RecyclerView friendslist;
    //Groups
    GroupAdapter adapterGroups;
    List<ModelGroups> modelGroupsList;
    private String userId;
    RelativeLayout empty,mfriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        create = findViewById(R.id.video_choose);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mName = findViewById(R.id.name);
        friendslist = findViewById(R.id.friendslist);
        ImageView back = findViewById(R.id.back);
        empty = findViewById(R.id.empty);
        mfriends = findViewById(R.id.mfriends);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Groups
        friendslist.setHasFixedSize(true);
        friendslist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        modelGroupsList = new ArrayList<>();
        friendslist.smoothScrollToPosition(0);
        getMyGroups();
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(CreateGroupActivity.this, "Enter name",
                            Toast.LENGTH_LONG).show();
                }else {
                    final String timeStamp = ""+System.currentTimeMillis();
                    final HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("groupId", ""+timeStamp);
                    hashMap.put("gName", ""+name);
                    hashMap.put("gIcon", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/group.jpg?alt=media&token=07e29b7f-99b6-4a8d-b0a0-70c5bffc5648");
                    hashMap.put("timestamp", ""+timeStamp);
                    hashMap.put("createdBy", ""+currentUser.getUid());

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                    ref.child(timeStamp).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    HashMap<String, String> hashMap1 = new HashMap<>();
                                    hashMap1.put("id", mAuth.getUid());
                                    hashMap1.put("role","creator");
                                    hashMap.put("timestamp", timeStamp);
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                                    reference.child(timeStamp).child("Participants").child(Objects.requireNonNull(mAuth.getUid()))
                                            .setValue(hashMap1)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mName.setText("");
                                                    Toast.makeText(CreateGroupActivity.this, "Group created",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            });

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateGroupActivity.this, e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });

    }
    private void getMyGroups() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroupsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(userId).exists()){
                        ModelGroups modelGroups = ds.getValue(ModelGroups.class);
                        modelGroupsList.add(modelGroups);
                    }
                    adapterGroups = new GroupAdapter(getApplicationContext(), modelGroupsList);
                    friendslist.setAdapter(adapterGroups);
                 adapterGroups.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}