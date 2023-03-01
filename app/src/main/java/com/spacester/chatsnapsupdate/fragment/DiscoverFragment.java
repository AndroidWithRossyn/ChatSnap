package com.spacester.chatsnapsupdate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.adapter.AdapterDiscovery;
import com.spacester.chatsnapsupdate.adapter.AdapterStory;
import com.spacester.chatsnapsupdate.model.ModelStory;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.Notification;
import com.spacester.chatsnapsupdate.user.ProfileActivity;
import com.spacester.chatsnapsupdate.user.RequestActivity;
import com.spacester.chatsnapsupdate.user.SearchActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class DiscoverFragment extends BaseFragment {
    RecyclerView topstories,mystories;

    private AdapterStory story;
    private List<ModelStory> storyList;

    private AdapterDiscovery adapterStory;
    private List<ModelStory> modelStories;

    List<String> friendsList;
    List<String> userList;

    String userId;

    public static DiscoverFragment create(){
        return new DiscoverFragment();
    }
    @Override
    public int getLayoutRedId() {
        return R.layout.fragment_discover;
    }

    @Override
    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        ImageView search = view.findViewById(R.id.search);
        ImageView profile = view.findViewById(R.id.profile);
        topstories = view.findViewById(R.id.topstories);
        mystories = view.findViewById(R.id.mystories);
        ImageView request = view.findViewById(R.id.request);
        ImageView notifications = view.findViewById(R.id.gChat);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RequestActivity.class);
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Notification.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        mystories.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        story = new AdapterStory(getContext(), storyList);
        mystories.setAdapter(story);
        checkSFollowing();

        LinearLayoutManager linear = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        topstories.setLayoutManager(linear);
        modelStories = new ArrayList<>();
        adapterStory = new AdapterDiscovery(getContext(), modelStories);
        topstories.setAdapter(adapterStory);
        user();
        return view;


    }
    private void user(){
        userList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //noinspection unused
                int i = 0;
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (!snapshot.hasChild(userId)){
                        userList.add(snapshot.getKey());
                    }

                }
                desStory();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void desStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Discovery");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                modelStories.clear();
                modelStories.add(new ModelStory("",0,0,"", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
                for (String id : userList){
                    int countStory = 0;
                    ModelStory modelStory = null;
                    for (DataSnapshot snapshot1 : snapshot.child(id).getChildren()){
                        modelStory = snapshot1.getValue(ModelStory.class);
                        if (timecurrent > Objects.requireNonNull(modelStory).getTimestart() && timecurrent < modelStory.getTimeend()){
                            countStory++;
                        }
                    }
                    if (countStory > 0){
                        modelStories.add(modelStory);
                    }
                }
                adapterStory.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkSFollowing(){
        friendsList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    friendsList.add(snapshot.getKey());
                }
                readStory();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new ModelStory("",0,0,"", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
                for (String id : friendsList){
                    int countStory = 0;
                    ModelStory modelStory = null;
                    for (DataSnapshot snapshot1 : snapshot.child(id).getChildren()){
                        modelStory = snapshot1.getValue(ModelStory.class);
                        if (timecurrent > Objects.requireNonNull(modelStory).getTimestart() && timecurrent < modelStory.getTimeend()){
                            countStory++;
                        }
                    }
                    if (countStory > 0){
                        storyList.add(modelStory);
                    }
                }
                story.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
