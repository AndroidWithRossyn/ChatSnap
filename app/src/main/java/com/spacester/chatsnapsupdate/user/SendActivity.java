package com.spacester.chatsnapsupdate.user;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.spacester.chatsnapsupdate.adapter.AdapterSendUsers;
import com.spacester.chatsnapsupdate.adapter.GroupSendAdapter;
import com.spacester.chatsnapsupdate.model.ModelGroups;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class SendActivity extends AppCompatActivity {

    RelativeLayout addStories,ourStories;
    String myUid;
    String storyId;
    long timeend;
    DatabaseReference reference,Oeference;
    private static String media_uri,type;
    public static String getMedia_uri() {
        return media_uri;
    }

    public static String getType() {
        return type;
    }


    public SendActivity(){

    }

    RecyclerView friendslist,grouplist;
    List<ModelUser> userList;
    List<ModelGroups> modelGroupsList;
    List<String> idList;
    String userId;
    AdapterSendUsers adapterUsers;
    GroupSendAdapter groupSendAdapter;
    RelativeLayout empty2,empty,groups,mfriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        addStories = findViewById(R.id.addStories);
        ourStories = findViewById(R.id.ourStories);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        friendslist = findViewById(R.id.grouplist);
        grouplist = findViewById(R.id.friendslist);

        empty2 = findViewById(R.id.empty2);
        empty = findViewById(R.id.empty);
        groups = findViewById(R.id.groups);
        mfriends = findViewById(R.id.mfriends);

        friendslist.setHasFixedSize(true);
        friendslist.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        idList = new ArrayList<>();

        grouplist.setHasFixedSize(true);
        grouplist.setLayoutManager(new LinearLayoutManager(this));
        modelGroupsList = new ArrayList<>();

        getMyGroups();
        getAllUsers();

         media_uri = getIntent().getStringExtra("uri");
        type = getIntent().getStringExtra("type");

        if (Objects.requireNonNull(type).equals("img")){
            final Uri uri = Uri.parse(media_uri);
            myUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            reference = FirebaseDatabase.getInstance().getReference("Story").child(myUid);
            Oeference = FirebaseDatabase.getInstance().getReference("Discovery").child(myUid);
            storyId = reference.push().getKey();
            timeend = System.currentTimeMillis()+86400000;

            addStories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Please wait, Uploading..",
                            Toast.LENGTH_LONG).show();
                    String image = uri.toString();
                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    String filePathAndName = "Story/" + "Story_" + timeStamp;
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                    ref.putFile(Uri.parse(image)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                            if (uriTask.isSuccessful()){

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("imageUri", downloadUri);
                                hashMap.put("timestart", ServerValue.TIMESTAMP);
                                hashMap.put("timeend", timeend);
                                hashMap.put("storyid", storyId);
                                hashMap.put("userid", myUid);

                                reference.child(storyId).setValue(hashMap);

                                Toast.makeText(getApplicationContext(), "Added to your story",
                                        Toast.LENGTH_LONG).show();
                            }
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

            ourStories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Please wait, Uploading..",
                            Toast.LENGTH_LONG).show();
                    String image = uri.toString();
                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    String filePathAndName = "Discovery/" + "Discovery_" + timeStamp;
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                    ref.putFile(Uri.parse(image)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                            if (uriTask.isSuccessful()){

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("imageUri", downloadUri);
                                hashMap.put("timestart", ServerValue.TIMESTAMP);
                                hashMap.put("timeend", timeend);
                                hashMap.put("storyid", storyId);
                                hashMap.put("userid", myUid);

                                Oeference.child(storyId).setValue(hashMap);

                                Toast.makeText(getApplicationContext(), "Added to our story",
                                        Toast.LENGTH_LONG).show();
                            }
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
        if (type.equals("video")){
            addStories.setVisibility(View.GONE);
            ourStories.setVisibility(View.GONE);
        }


    }

    private void getAllUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                if (!dataSnapshot.exists()){
                    groups.setVisibility(View.GONE);
                    empty2.setVisibility(View.VISIBLE);
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
                        adapterUsers = new AdapterSendUsers(SendActivity.this, userList);

                        friendslist.setAdapter(adapterUsers);
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
                    groupSendAdapter = new GroupSendAdapter(getApplicationContext(), modelGroupsList);
                    grouplist.setAdapter(groupSendAdapter);
                    groupSendAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}