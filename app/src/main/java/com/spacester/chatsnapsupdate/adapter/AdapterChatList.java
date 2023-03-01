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

import com.comix.rounded.RoundedCornerImageView;
import com.google.firebase.auth.FirebaseUser;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.ChatActivity;
import com.spacester.chatsnapsupdate.user.UserProfileActivity;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ALL")
public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {

    final Context context;
    final List<ModelUser> userList;
    private final HashMap<String, String> lastMessageMap;
    FirebaseUser currentUser;

    public AdapterChatList(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
     lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.chatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final String hisUid = userList.get(position).getId();
        String dp = userList.get(position).getPhoto();
        String name = userList.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUid);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("hisId", hisUid);
                context.startActivity(intent);
                return false;
            }
        });


        holder.mName.setText(name);
        if (lastMessage == null || lastMessage.equals("default")){
            holder.message.setText("No message");
        }else {
            holder.message.setVisibility(View.VISIBLE);
            holder.message.setText(lastMessage);
        }
        try{
            Picasso.get().load(dp).placeholder(R.drawable.avatar).into(holder.mDp);
        }catch (Exception e){
            Picasso.get().load(R.drawable.avatar).into(holder.mDp);
        }

        if (userList.get(position).getStatus().equals("online")){
            holder.status.setVisibility(View.VISIBLE);
        }else {
            holder.status.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisId", hisUid);
                context.startActivity(intent);
            }
        });

    }

    public void setLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final RoundedCornerImageView mDp;
        final ImageView status;
        final TextView mName;
        final TextView message;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mDp = itemView.findViewById(R.id.circleImageView);
            status = itemView.findViewById(R.id.status);
            mName = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.username);
        }
    }

}
