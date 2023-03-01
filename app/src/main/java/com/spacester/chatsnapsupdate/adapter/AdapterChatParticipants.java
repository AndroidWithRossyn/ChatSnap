package com.spacester.chatsnapsupdate.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comix.rounded.RoundedCornerImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

@SuppressWarnings("ALL")
public class AdapterChatParticipants extends RecyclerView.Adapter<AdapterChatParticipants.HolderParticipantsAdd>{
    private final Context context;
    private final List<ModelUser> userList;
    private final String groupId;

    @SuppressWarnings("unused")
    public AdapterChatParticipants(Context context, List<ModelUser> userList, String groupId, @SuppressWarnings("unused") String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public HolderParticipantsAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_display, parent, false);

        return new HolderParticipantsAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantsAdd holder, int position) {
        final ModelUser modelUser = userList.get(position);
        String mName = modelUser.getName();
        String mUsername = modelUser.getUsername();
        String dp = modelUser.getPhoto();
        final String uid = modelUser.getId();

        holder.name.setText(mName);
        try {
            Picasso.get().load(dp).placeholder(R.drawable.avatar).into(holder.circleImageView);

        }catch (Exception e){
            Picasso.get().load(R.drawable.avatar).into(holder.circleImageView);
        }
        checkifAlreadyExists(modelUser, holder,mUsername);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisId", uid);
                context.startActivity(intent);
            }
        });
    }


    private void checkifAlreadyExists(ModelUser modelUser, final HolderParticipantsAdd holder, final String mUsername) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String hisRole = ""+snapshot.child("role").getValue();
                            holder.username.setText(mUsername + " - " +hisRole);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class HolderParticipantsAdd extends RecyclerView.ViewHolder{

        private final RoundedCornerImageView circleImageView;
        private final TextView name;
        private final TextView username;

        public HolderParticipantsAdd(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);


        }
    }
}
