package com.spacester.chatsnapsupdate.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.MediaView;
import com.spacester.chatsnapsupdate.model.ModelChat;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.ChatActivity;
import com.spacester.chatsnapsupdate.user.ViewDiscoveryActivity;
import com.spacester.chatsnapsupdate.user.ViewStoryActivity;

import java.util.List;

@SuppressWarnings("ALL")
public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private final Context context;
    private final List<ModelChat> modelChats;
    FirebaseUser firebaseUser;
    DatabaseReference  databaseReference;

    public AdapterChat(Context context, List<ModelChat> modelChats) {
        this.context = context;
        this.modelChats = modelChats;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if (viewType == MSG_TYPE_RIGHT){
           View view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false);

           return new MyHolder(view);
       }
        View view = LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        ModelChat mChat = modelChats.get(position);
        final String msg = modelChats.get(position).getMsg();
        final String type = modelChats.get(position).getType();
        final String hisId = modelChats.get(position).getReceiver();

        final PopupMenu popupMenu = new PopupMenu(context, holder.itemView);

        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Copy");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id==0){
                    deleteMessage(position);
                }
                if (id==2){
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip =  ClipData.newPlainText("text", msg);
                    assert clipboard != null;
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Message Copied",Toast.LENGTH_SHORT).show();

                }

                return false;
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popupMenu.show();
                return false;
            }
        });

        holder.media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MediaView.class);
                intent.putExtra("uri", msg);
                intent.putExtra("type", type);
                context.startActivity(intent);

            }
        });

        String hName = ChatActivity.getName();


        if (type.equals("text")){
            holder.name.setText(hName);
            holder.myname.setText("Me");
            holder.sticker.setVisibility(View.GONE);
            holder.message.setText(msg);
            holder.media.setVisibility(View.GONE);
            holder.play.setVisibility(View.GONE);
        }
        else if (type.equals("image")){
            holder.name.setText(hName);
            holder.message.setVisibility(View.GONE);
            holder.myname.setText("Me");
            holder.sticker.setVisibility(View.GONE);
            holder.play.setVisibility(View.GONE);
            holder.media.setVisibility(View.VISIBLE);
            Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.media);
        }else if (type.equals("video")){
            holder.name.setText(hName);
            holder.myname.setText("Me");
            holder.sticker.setVisibility(View.GONE);
            holder.message.setVisibility(View.GONE);
            Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.media);
            holder.play.setVisibility(View.VISIBLE);
            holder.media.setVisibility(View.VISIBLE);
        }
        else if (type.equals("sticker")){
            holder.name.setText(hName);
            holder.message.setVisibility(View.GONE);
            holder.myname.setText("Me");
            holder.sticker.setVisibility(View.VISIBLE);
            holder.play.setVisibility(View.GONE);
            holder.media.setVisibility(View.GONE);
            Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.sticker);
        } else if (type.equals("story")){
            holder.name.setText(hName);
            holder.message.setVisibility(View.GONE);
            holder.myname.setText("Me");
            holder.sticker.setVisibility(View.GONE);
            holder.play.setVisibility(View.GONE);
            holder.media.setVisibility(View.GONE);
            holder.stories_ly.setVisibility(View.VISIBLE);
            holder.stories_text.setText(msg);
            Glide.with(context).asBitmap().centerCrop().load(modelChats.get(position).getTimestamp()).into(holder.sory_img);
        }
        else if (type.equals("story_dis")){
            holder.name.setText(hName);
            holder.message.setVisibility(View.GONE);
            holder.myname.setText("Me");
            holder.sticker.setVisibility(View.GONE);
            holder.play.setVisibility(View.GONE);
            holder.media.setVisibility(View.GONE);
            holder.stories_ly.setVisibility(View.VISIBLE);
            holder.stories_text.setText(msg);
            Glide.with(context).asBitmap().centerCrop().load(modelChats.get(position).getTimestamp()).into(holder.sory_img);
        }

        holder.stories_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("story_dis")){
                    Intent intent = new Intent(context, ViewDiscoveryActivity.class);
                    intent.putExtra("userid", hisId);
                    context.startActivity(intent);
                }else if (type.equals("story")){
                    Intent intent = new Intent(context, ViewStoryActivity.class);
                    intent.putExtra("userid", hisId);
                    context.startActivity(intent);
                }

            }
        });

        if (position == modelChats.size()-1){
            if (mChat.isIsSeen()) {
                holder.seen.setText("Seen");
            }else {
                holder.seen.setText("Delivered");
            }
        }else {
            holder.seen.setVisibility(View.GONE);
        }

    }

    private void deleteMessage(int position) {

        String timeStamp = modelChats.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(timeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().removeValue();
                    Toast.makeText(context, "Deleted",
                            Toast.LENGTH_LONG).show();
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
        return modelChats.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView play;
        final ImageView media;
        final ImageView sticker;
        final TextView name;
        final TextView message;
        final TextView myname;
        final TextView seen;
        ImageView sory_img;
        LinearLayout stories_ly;
        TextView stories_text;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            play = itemView.findViewById(R.id.play);
            media = itemView.findViewById(R.id.media);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.msg);
            myname = itemView.findViewById(R.id.myname);
            sticker = itemView.findViewById(R.id.sticker);
            seen = itemView.findViewById(R.id.seen);

            sory_img = itemView.findViewById(R.id.story_img);
            stories_ly = itemView.findViewById(R.id.story);
            stories_text = itemView.findViewById(R.id.story_msg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (modelChats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}

