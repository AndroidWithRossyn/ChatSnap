package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.adapter.AdapterNewChat;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatUserListActivity extends AppCompatActivity {

    EditText search;
    ImageView back;
    RecyclerView userlist;
    AdapterNewChat adapterUsers;
    List<ModelUser> userList;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_list);
        back = findViewById(R.id.back);
        userlist = findViewById(R.id.friendslist);
        search = findViewById(R.id.password);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userlist.setHasFixedSize(true);
        userlist.setLayoutManager(new LinearLayoutManager(ChatUserListActivity.this));
        userList = new ArrayList<>();
        userlist.smoothScrollToPosition(0);
        getAllUsers();

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
                    adapterUsers = new AdapterNewChat(ChatUserListActivity.this, userList);
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
                    adapterUsers = new AdapterNewChat(ChatUserListActivity.this, userList);
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
}