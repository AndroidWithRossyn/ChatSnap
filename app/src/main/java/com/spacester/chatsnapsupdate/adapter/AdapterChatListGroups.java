package com.spacester.chatsnapsupdate.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.model.ModelChatListGroups;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.GroupChatActivity;
import com.spacester.chatsnapsupdate.user.GroupProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

@SuppressWarnings("ALL")
public class AdapterChatListGroups extends RecyclerView.Adapter<AdapterChatListGroups.MyHolder> {

    final Context context;
    final List<ModelChatListGroups> modelGroups;
    String myId;

    public AdapterChatListGroups(Context context, List<ModelChatListGroups> modelGroups) {
        this.context = context;
        this.modelGroups = modelGroups;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.groupchatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
         final String GroupId = modelGroups.get(position).getGroupId();
        String GroupName = modelGroups.get(position).getgName();
        String GroupIcon = modelGroups.get(position).getgIcon();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        myId = currentUser.getUid();

        ModelChatListGroups modelChatListGroups = modelGroups.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, GroupProfile.class);
                intent.putExtra("groupId", GroupId);
                context.startActivity(intent);
                return false;
            }
        });


        loadLastMsg(holder, modelChatListGroups );

        holder.mName.setText(GroupName);
        try {
            Picasso.get().load(GroupIcon).placeholder(R.drawable.group).into(holder.avatar);
        }catch (Exception e){
            Picasso.get().load(R.drawable.group).into(holder.avatar);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId", GroupId);
                context.startActivity(intent);
            }
        });

    }



    private void loadLastMsg(final MyHolder holder, ModelChatListGroups modelChatListGroups) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(modelChatListGroups.getGroupId()).child("Message").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            final String message = ""+ds.child("msg").getValue();
                            String sender = ""+ds.child("sender").getValue();
                            final String type = ""+ds.child("type").getValue();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.orderByChild("id").equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds: snapshot.getChildren()){
                                                String name = ""+ds.child("name").getValue();
                                                if (type.equals("text")){
                                                    holder.mUsername.setText(name+": "+message);
                                                }
                                                else if (type.equals("image")){
                                                    holder.mUsername.setText(name+": "+"Sent a photo");

                                                }else if (type.equals("video")){
                                                    holder.mUsername.setText(name+": "+"Sent a Video");
                                                }
                                                else if (type.equals("post")){
                                                    holder.mUsername.setText(name+": "+"Sent a post");
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return modelGroups.size();
    }



    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView avatar;
        final TextView mName;
        final TextView mUsername;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.circleImageView);
            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);

        }
    }
}
