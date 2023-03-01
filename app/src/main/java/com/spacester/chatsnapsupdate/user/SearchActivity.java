package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.adapter.AdapterUsers;
import com.spacester.chatsnapsupdate.MainActivity;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

    EditText search;
    ImageView back;
    RecyclerView userlist;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    String userId;

    RecyclerView reqList;
    AdapterUsers aUsers;
    List<ModelUser> aList;
    List<String> idList;

    RecyclerView friendslist;
    AdapterUsers friendUsers;
    List<ModelUser> fList;
    List<String> flist;

    RelativeLayout empty,mfriends,reqfriends,emotyfriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search = findViewById(R.id.password);
        back = findViewById(R.id.back);
        friendslist = findViewById(R.id.friendslist);
        userlist = findViewById(R.id.userlist);
        reqList = findViewById(R.id.reqlist);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
         userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userlist.setHasFixedSize(true);
        userlist.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        userList = new ArrayList<>();
        userlist.smoothScrollToPosition(0);
        getAllUsers();

        reqList.setHasFixedSize(true);
        reqList.setLayoutManager(new LinearLayoutManager(this));
        aList = new ArrayList<>();
        idList = new ArrayList<>();
        getAllReq();

        friendslist.setHasFixedSize(true);
        friendslist.setLayoutManager(new LinearLayoutManager(this));
        fList = new ArrayList<>();
        flist = new ArrayList<>();
        getAllFriends();

        empty = findViewById(R.id.empty);
        mfriends = findViewById(R.id.mfriends);
        reqfriends = findViewById(R.id.reqfriends);
        emotyfriends = findViewById(R.id.emotyfriends);


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    filterUser(s.toString());
                }else {
                    getAllUsers();
                }

            }
        });
    }


    private void filterUser(final String query) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    assert firebaseUser != null;
                    assert modelUser != null;
                    if (!firebaseUser.getUid().equals(modelUser.getId())){
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getUsername().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                        }
                    }
                    adapterUsers = new AdapterUsers(SearchActivity.this, userList);
                    adapterUsers.notifyDataSetChanged();
                    userlist.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getAllUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    assert firebaseUser != null;
                    assert modelUser != null;
                    if (!firebaseUser.getUid().equals(modelUser.getId())){
                        userList.add(modelUser);
                    }
                    adapterUsers = new AdapterUsers(SearchActivity.this, userList);
                    userlist.setAdapter(adapterUsers);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getAllReq() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Request")
                .child(userId).child("req_received");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                if (!dataSnapshot.exists()){
                    reqfriends.setVisibility(View.GONE);
                    emotyfriends.setVisibility(View.VISIBLE);
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
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

    private void showUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                aList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelUser modelUser = snapshot.getValue(ModelUser.class);
                    for (String id : idList) {
                        assert modelUser != null;
                        if (modelUser.getId().equals(id)){
                            aList.add(modelUser);
                        }
                        aUsers = new AdapterUsers(SearchActivity.this, aList);
                        reqList.setAdapter(aUsers);

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

    private void getAllFriends() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flist.clear();
                if (!dataSnapshot.exists()){
                    mfriends.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    flist.add(snapshot.getKey());
                }
                showFriends();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showFriends(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelUser modelUser = snapshot.getValue(ModelUser.class);
                    for (String id : flist) {
                        assert modelUser != null;
                        if (modelUser.getId().equals(id)){
                            fList.add(modelUser);
                        }
                        friendUsers = new AdapterUsers(SearchActivity.this, fList);
                        friendslist.setAdapter(friendUsers);

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

    @Override
    protected void onRestart() {
        super.onRestart();
        startActivity(getIntent());
    }
}