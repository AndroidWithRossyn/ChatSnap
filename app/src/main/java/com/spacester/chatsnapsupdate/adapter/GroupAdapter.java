package com.spacester.chatsnapsupdate.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.spacester.chatsnapsupdate.model.ModelGroups;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.GroupChatActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyHolder> {

    final Context context;
    final List<ModelGroups> modelGroups;

    public GroupAdapter(Context context, List<ModelGroups> modelGroups) {
        this.context = context;
        this.modelGroups = modelGroups;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_display, parent, false);
        return new MyHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String GroupId = modelGroups.get(position).getGroupId();
        String GroupName = modelGroups.get(position).getgName();
        String GroupIcon = modelGroups.get(position).getgIcon();

        holder.mName.setText(GroupName);
        try {
            Picasso.get().load(GroupIcon).placeholder(R.drawable.avatar).into(holder.avatar);
        }catch (Exception e){
            Picasso.get().load(R.drawable.avatar).into(holder.avatar);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId", GroupId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.circleImageView);
            mName = itemView.findViewById(R.id.name);

        }
    }
}
