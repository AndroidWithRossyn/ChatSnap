package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.comix.rounded.RoundedCornerImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.spacester.chatsnapsupdate.Adpref;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.notification.Data;
import com.spacester.chatsnapsupdate.notification.Sender;
import com.spacester.chatsnapsupdate.notification.Token;
import com.spacester.chatsnapsupdate.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"ALL", "RedundantThrows"})
public class UserProfileActivity extends AppCompatActivity {
    RoundedCornerImageView roundedImageView;
    TextView mName,mUsername,since,text_block;
    ImageView back;
    ConstraintLayout add,cancelreq,accept,unfriend,decline,block,myfriend;
    private String mCurrent_state;
    private DatabaseReference mFriendsReqDatabase;
    private DatabaseReference mFriendsDatabase;
    String userId;
    boolean isBlocked = false;
    String hisId;
    private RequestQueue requestQueue;
    private boolean notify = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        back = findViewById(R.id.back);
        roundedImageView = findViewById(R.id.roundedImageView);
        mName = findViewById(R.id.name);
        mUsername = findViewById(R.id.username);
        since = findViewById(R.id.since);
        add= findViewById(R.id.edit);
        decline = findViewById(R.id.decline);
        text_block = findViewById(R.id.text74);
        block = findViewById(R.id.block);
        myfriend = findViewById(R.id.myfriend);
        cancelreq = findViewById(R.id.cancelreq);
        accept = findViewById(R.id.accept);
        unfriend = findViewById(R.id.unfriend);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        hisId = getIntent().getStringExtra("hisId");
        mCurrent_state = "not_friends";


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

        myfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserFriendListActivity.class);
                intent.putExtra("hisId", hisId);
                startActivity(intent);
            }
        });

        mFriendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
          userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        assert hisId != null;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(hisId);
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

        mFriendsReqDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(hisId)) {
                    String req_type= Objects.requireNonNull(snapshot.child(hisId).child("request_type").getValue()).toString();
                    if (req_type.equals("received")){
                        add.setVisibility(View.GONE);
                        mCurrent_state = "req_received";
                        decline.setVisibility(View.VISIBLE);
                        accept.setVisibility(View.VISIBLE);
                    }else if (req_type.equals("sent")){
                        mCurrent_state = "req_sent";
                        cancelreq.setVisibility(View.VISIBLE);
                    }
                }else {
                    mFriendsDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(hisId)){
                                add.setVisibility(View.GONE);
                                mCurrent_state = "friends";
                                cancelreq.setVisibility(View.GONE);
                                unfriend.setVisibility(View.VISIBLE);
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

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendsReqDatabase.child(userId).child(hisId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendsReqDatabase.child(hisId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                add.setVisibility(View.VISIBLE);
                                mCurrent_state = "not_friends";
                                cancelreq.setVisibility(View.GONE);
                                accept.setVisibility(View.GONE);
                                unfriend.setVisibility(View.GONE);
                                addToHisNotification(""+hisId,"","Declined your friend request");
                                //--------Request Cancel------
                                FirebaseDatabase.getInstance().getReference().child("Request").child(userId)
                                        .child("req_received").child(hisId).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("Request").child(hisId)
                                        .child("req_sent").child(userId).removeValue();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                if (mCurrent_state.equals("not_friends")){
                    mFriendsReqDatabase.child(userId).child(hisId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mFriendsReqDatabase.child(hisId).child(userId).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Request sent",
                                                Toast.LENGTH_LONG).show();
                                        add.setVisibility(View.GONE);
                                        mCurrent_state = "req_sent";
                                        cancelreq.setVisibility(View.VISIBLE);
                                        addToHisNotification(""+hisId,"","Sent you a friend request");
                                        //--------Request List------
                                        FirebaseDatabase.getInstance().getReference().child("Request").child(userId)
                                                .child("req_sent").child(hisId).setValue(true);
                                        FirebaseDatabase.getInstance().getReference().child("Request").child(hisId)
                                                .child("req_received").child(userId).setValue(true);
                                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                                        dataRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                                                if (notify){
                                                    sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a request");

                                                }
                                                notify = false;
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });
                            }else {
                                Toast.makeText(getApplicationContext(), "Failed sending request",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        cancelreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrent_state.equals("req_sent")){
                    mFriendsReqDatabase.child(userId).child(hisId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsReqDatabase.child(hisId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    add.setVisibility(View.VISIBLE);
                                    mCurrent_state = "not_friends";
                                    cancelreq.setVisibility(View.GONE);

                                    //--------Request Cancel------
                                    FirebaseDatabase.getInstance().getReference().child("Request").child(userId)
                                            .child("req_sent").child(hisId).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("Request").child(hisId)
                                            .child("req_received").child(userId).removeValue();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                if (mCurrent_state.equals("req_received")){
                            mFriendsDatabase.child(userId).child(hisId).setValue(true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendsDatabase.child(hisId).child(userId).setValue(true)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mFriendsReqDatabase.child(userId).child(hisId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mFriendsReqDatabase.child(hisId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            add.setVisibility(View.GONE);
                                                                            mCurrent_state = "friends";
                                                                            cancelreq.setVisibility(View.GONE);
                                                                            decline.setVisibility(View.GONE);
                                                                            accept.setVisibility(View.GONE);
                                                                            unfriend.setVisibility(View.VISIBLE);
                                                                            addToHisNotification(""+hisId,"","Accepted your friend request");
                                                                            //--------Request Cancel------
                                                                            FirebaseDatabase.getInstance().getReference().child("Request").child(userId)
                                                                                    .child("req_received").child(hisId).removeValue();
                                                                            FirebaseDatabase.getInstance().getReference().child("Request").child(hisId)
                                                                                    .child("req_sent").child(userId).removeValue();
                                                                            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                                                                            dataRef.addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                                                                                    if (notify){
                                                                                        sendNotification(hisId, Objects.requireNonNull(user).getName(), "Accepted your request");

                                                                                    }
                                                                                    notify = false;
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(getApplicationContext(), e.getMessage(),
                                                                                    Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                }

            }
        });

        unfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrent_state.equals("friends")) {
                    mFriendsDatabase.child(userId).child(hisId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabase.child(hisId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    add.setVisibility(View.VISIBLE);
                                    mCurrent_state = "not_friends";
                                    addToHisNotification(""+hisId,"","Removed you from friends");
                                    unfriend.setVisibility(View.GONE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        checkBlocked();
        imBLockedOrNot();

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBlocked){
                    unBlockUser();
                }else {
                    BlockUser();

                }
            }
        });


        final TextView friendsNo = findViewById(R.id.friendsNo);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(hisId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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

    }
    private void addToHisNotification(String hisUid, String pId, String notification){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", notification);
        hashMap.put("sUid", userId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void imBLockedOrNot (){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisId).child("BlockedUsers").orderByChild("id").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                add.setVisibility(View.GONE);
                                cancelreq.setVisibility(View.GONE);
                                accept.setVisibility(View.GONE);
                                decline.setVisibility(View.GONE);
                                unfriend.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "You are blocked by this user",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkBlocked() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userId).child("BlockedUsers").orderByChild("id").equalTo(hisId).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                text_block.setText("Unblock");
                                isBlocked = true;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void BlockUser() {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("id", hisId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userId).child("BlockedUsers").child(hisId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        text_block.setText("Unblock");
                        Toast.makeText(getApplicationContext(), "Blocked",
                                Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hisId.equals(userId)){
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        }
    }

    private void unBlockUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userId).child("BlockedUsers").orderByChild("id").equalTo(hisId).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                text_block.setText("Block");
                                                Toast.makeText(getApplicationContext(), "Unblocked",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendNotification(final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(userId, name + " : " + message, "New Message", hisId, R.drawable.logo);
                    Sender sender = new Sender(data, Objects.requireNonNull(token).getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON_RESPONSE", "onResponse" + response.toString());

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse" + error.toString());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAANDu7kPI:APA91bFtJB-Gf0jGN4IzrJ8WPk24tbH4GyQEJn1dxCHFA8WpddAYFxeA5bt-ZYDIj-Yv60_6J2FhTKEPVcArD0DFJMxoCgBLaI0PimA7fm2fkGN12yZdyw1RDP0ih4IcfqHBrls6gZd5");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
