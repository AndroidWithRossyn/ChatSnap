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
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.UserProfileActivity;
import com.squareup.picasso.Picasso;
import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    final Context context;
    final List<ModelUser> userList;

    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_display, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        final String hisUID = userList.get(position).getId();
        String userImage = userList.get(position).getPhoto();
        final String userName = userList.get(position).getName();
        String userUsernsme = userList.get(position).getUsername();
        holder.mName.setText(userName);
        holder.mUsername.setText(userUsernsme);

        try {
            Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(holder.avatar);
        }catch (Exception e){
            Picasso.get().load(R.drawable.avatar).into(holder.avatar);
        }
        holder.blockedIV.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("hisId", hisUID);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView avatar;
        final ImageView blockedIV;
        final TextView mName;
        final TextView mUsername;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.circleImageView);
            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);
            blockedIV = itemView.findViewById(R.id.blockedIV);
        }

    }
}
