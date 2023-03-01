package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.comix.rounded.RoundedCornerImageView;
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
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.Settings;
import com.spacester.chatsnapsupdate.auth.Username;
import com.squareup.picasso.Picasso;

import java.util.Objects;

@SuppressWarnings("UnnecessaryCallToStringValueOf")
public class ProfileActivity extends AppCompatActivity {

    ImageView back,settings;
    RoundedCornerImageView roundedImageView;
    TextView mName,mUsername,since;
    ConstraintLayout notifications,edit,addstories,request,addfriend,myfriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        back = findViewById(R.id.back);
        settings = findViewById(R.id.settings);
        roundedImageView = findViewById(R.id.roundedImageView);
        mName = findViewById(R.id.name);
        mUsername = findViewById(R.id.username);
        since = findViewById(R.id.since);
        notifications = findViewById(R.id.notifications);
        addstories = findViewById(R.id.addstories);
        request = findViewById(R.id.request);
        edit = findViewById(R.id.edit);
        addfriend = findViewById(R.id.addfriend);

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


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, Settings.class);
                startActivity(intent);
            }
        });
        addstories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, AddStoryActivity.class);
                startActivity(intent);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        myfriend = findViewById(R.id.myfriend);
        myfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FriendListActivity.class);
                startActivity(intent);
            }
        });
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, Notification.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, RequestActivity.class);
                startActivity(intent);
            }
        });
        //Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                if (username.isEmpty()){
                    Intent intent = new Intent(ProfileActivity.this, Username.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });



        final TextView friendsNo = findViewById(R.id.friendsNo);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChildren()){

                    friendsNo.setVisibility(View.VISIBLE);
                    int pLikes = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                    friendsNo.setText("Friends "+Integer.toString(pLikes));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                String photo = Objects.requireNonNull(snapshot.child("photo").getValue()).toString();

                mName.setText(name);
                mUsername.setText(username);
                try {
                    Picasso.get().load(photo).placeholder(R.drawable.avatar).into(roundedImageView);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Picasso.get().load(R.drawable.avatar).into(roundedImageView);
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