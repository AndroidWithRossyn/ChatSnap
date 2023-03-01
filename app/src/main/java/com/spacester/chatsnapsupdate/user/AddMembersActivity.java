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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.Adpref;
import com.spacester.chatsnapsupdate.adapter.AdapterParticipants;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddMembersActivity extends AppCompatActivity {

    RecyclerView group_rv;
    private FirebaseAuth firebaseAuth;
    private String groupId,myGroupRole;

    AdapterParticipants adapterParticipants;
    List<ModelUser> userList;
    List<String> idList;
    String userId;
    RelativeLayout empty,mfriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);
        group_rv = findViewById(R.id.friendslist);
       ImageView back = findViewById(R.id.back);
       back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBackPressed();
           }
       });
        firebaseAuth = FirebaseAuth.getInstance();
        groupId = getIntent().getStringExtra("groupId");
        loadGroupInfo();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Adpref adpref;
        adpref = new Adpref(Objects.requireNonNull(getApplicationContext()));
        if (adpref.loadAdsModeState()){
            mAdView.setVisibility(View.VISIBLE);

        }else {
            mAdView.setVisibility(View.GONE);
        }


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        empty = findViewById(R.id.empty);
        mfriends = findViewById(R.id.mfriends);
        //User
        group_rv.setHasFixedSize(true);
        group_rv.setLayoutManager(new LinearLayoutManager(AddMembersActivity.this));
        userList = new ArrayList<>();
        idList = new ArrayList<>();
        group_rv.smoothScrollToPosition(0);

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
                        adapterParticipants = new AdapterParticipants(AddMembersActivity.this, userList,""+groupId,""+myGroupRole);
                        adapterParticipants.notifyDataSetChanged();
                        group_rv.setAdapter(adapterParticipants);

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



    private void loadGroupInfo() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    String groupId = ""+ds.child("groupId").getValue();

                    reference.child(groupId).child("Participants").child(Objects.requireNonNull(firebaseAuth.getUid()))
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        myGroupRole = ""+snapshot.child("role").getValue();
                                        getAllUsers();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), error.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}