package com.spacester.chatsnapsupdate.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.adapter.AdapterChatList;
import com.spacester.chatsnapsupdate.adapter.AdapterChatListGroups;
import com.spacester.chatsnapsupdate.model.ModelChat;
import com.spacester.chatsnapsupdate.model.ModelChatListGroups;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.ChatUserListActivity;
import com.spacester.chatsnapsupdate.user.CreateGroupActivity;
import com.spacester.chatsnapsupdate.user.ProfileActivity;
import com.spacester.chatsnapsupdate.user.SearchActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class ChatFragment extends BaseFragment {

    RecyclerView recyclerView;
    List<ModelChatlist> chatlistList;
    List<ModelUser> userList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatList adapterChatList;
    RecyclerView recyclerView2;
    TextView empty;
    //Groups
    AdapterChatListGroups adapterChatListGroups;
    List<ModelChatListGroups> modelChatListGroupsList;
    public static ChatFragment create(){
        return new ChatFragment();
    }

    @Override
    public int getLayoutRedId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ImageView profile = view.findViewById(R.id.profile);
         recyclerView2 = view.findViewById(R.id.recyclerView2);
        ImageView gChat = view.findViewById(R.id.gChat);
        gChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatUserListActivity.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        ImageView search = view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelChatListGroupsList = new ArrayList<>();
        recyclerView2.smoothScrollToPosition(0);
        getChatGroups();

        empty = view.findViewById(R.id.empty);

        recyclerView = view.findViewById(R.id.recyclerView);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatlistList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatlistList.clear();
                if (!snapshot.exists()){
                    empty.setVisibility(View.VISIBLE);
                }
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChatlist chatlist = ds.getValue(ModelChatlist.class);
                    chatlistList.add(chatlist);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        return view;

    }

    private void getChatGroups() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelChatListGroupsList.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(currentUser.getUid()).exists()){
                        ModelChatListGroups modelChatListGroups = ds.getValue(ModelChatListGroups.class);
                        modelChatListGroupsList.add(modelChatListGroups);
                    }
                    adapterChatListGroups = new AdapterChatListGroups(getActivity(), modelChatListGroupsList);
                    recyclerView2.setAdapter(adapterChatListGroups);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadChats() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUser user = ds.getValue(ModelUser.class);
                    for (ModelChatlist chatlist : chatlistList) {
                        if (Objects.requireNonNull(user).getId() != null && user.getId().equals(chatlist.getId())) {
                            userList.add(user);
                            break;
                        }
                    }
                    adapterChatList = new AdapterChatList(getContext(), userList);
                    recyclerView.setAdapter(adapterChatList);
                    for (int i = 0; i < userList.size(); i++) {
                        lastMessage(userList.get(i).getId());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void lastMessage(final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat == null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if(sender == null || receiver == null){
                        continue;
                    }
                    if (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(currentUser.getUid())){
                        if (chat.getType().equals("image")){
                            theLastMessage = "Sent a photo";
                        }else if (chat.getType().equals("video")){
                            theLastMessage = "Sent a video";
                        } else if (chat.getType().equals("sticker")){
                            theLastMessage = "Sent a sticker";
                        } else if (chat.getType().equals("story")){
                            theLastMessage = "Commented on your story";
                        }
                        else if (chat.getType().equals("story_dis")){
                            theLastMessage = "Commented on your story";
                        }
                        else  {
                            theLastMessage = "Sent a message";
                        }
                    }
                }
                adapterChatList.notifyDataSetChanged();
                adapterChatList.setLastMessageMap(userId, theLastMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
