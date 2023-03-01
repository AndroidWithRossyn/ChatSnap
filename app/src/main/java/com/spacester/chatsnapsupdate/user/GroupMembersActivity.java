package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.spacester.chatsnapsupdate.adapter.AdapterChatParticipants;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;

import java.util.ArrayList;
import java.util.Objects;

public class GroupMembersActivity extends AppCompatActivity {

    private ArrayList<ModelUser> userArrayList;
    private AdapterChatParticipants adapterParticipants;
    FirebaseAuth mAuth;
    String GroupId, myGroupRole;
    String userId;
    RecyclerView group_rv;
    RelativeLayout empty,mfriends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        mAuth = FirebaseAuth.getInstance();
        group_rv = findViewById(R.id.friendslist);
       ImageView back = findViewById(R.id.back);
       back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBackPressed();
           }
       });
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Intent intent = getIntent();
        GroupId = intent.getStringExtra("groupId");
        mAuth = FirebaseAuth.getInstance();
        empty = findViewById(R.id.empty);
        mfriends = findViewById(R.id.mfriends);

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


        loadMembers();
    }
    private void loadMembers() {
        userArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String id = ""+ds.child("id").getValue();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.orderByChild("id").equalTo(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                mfriends.setVisibility(View.GONE);
                                empty.setVisibility(View.VISIBLE);
                            }
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelUser modelUser = ds.getValue(ModelUser.class);
                                userArrayList.add(modelUser);
                            }
                            adapterParticipants = new AdapterChatParticipants(GroupMembersActivity.this, userArrayList, GroupId, myGroupRole);
                            group_rv.setAdapter(adapterParticipants);
                            adapterParticipants.notifyDataSetChanged();
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