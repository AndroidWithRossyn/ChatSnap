package com.spacester.chatsnapsupdate.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.MediaView;
import com.spacester.chatsnapsupdate.model.ModelGroupChat;
import com.spacester.chatsnapsupdate.R;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.MyHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private final Context context;
    private final ArrayList<ModelGroupChat> modelGroupChats;
    FirebaseUser firebaseUser;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String myName,myId;

    public AdapterGroupChat(Context context, ArrayList<ModelGroupChat> modelGroupChats) {
        this.context = context;
        this.modelGroupChats = modelGroupChats;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_group_right ,parent,false);
            return new MyHolder(view);
        }
            View view = LayoutInflater.from(context).inflate(R.layout.chat_group_left ,parent,false);
            return new MyHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        ModelGroupChat model = modelGroupChats.get(position);
        final String msg = model.getMsg();
        final String type = model.getType();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        myId = currentUser.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip =  ClipData.newPlainText("text", msg);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Message Copied",Toast.LENGTH_SHORT).show();

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


        if (type.equals("text")){
            holder.myname.setText("Me");
            holder.message.setText(msg);
            holder.sticker.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);
            holder.media.setVisibility(View.GONE);
            holder.play.setVisibility(View.GONE);
        }
        else if (type.equals("image")){
            holder.myname.setText("Me");
            holder.sticker.setVisibility(View.GONE);
            holder.message.setVisibility(View.GONE);
            holder.play.setVisibility(View.GONE);
            holder.media.setVisibility(View.VISIBLE);
            Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.media);
        }else if (type.equals("video")){
            holder.sticker.setVisibility(View.GONE);
            holder.message.setVisibility(View.GONE);
            holder.myname.setText("Me");
            Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.media);
            holder.play.setVisibility(View.VISIBLE);
            holder.media.setVisibility(View.VISIBLE);
        }
        else if (type.equals("sticker")){
            holder.message.setVisibility(View.GONE);
            holder.myname.setText("Me");
            holder.sticker.setVisibility(View.VISIBLE);
            holder.play.setVisibility(View.GONE);
            holder.media.setVisibility(View.GONE);
            Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.sticker);
        }

    setUserName(model , holder);

    }

    private void setUserName(ModelGroupChat model, final MyHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("id").equalTo(model.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            holder.name.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelGroupChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (modelGroupChats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    static class  MyHolder extends RecyclerView.ViewHolder{

        final ImageView play;
        final ImageView media;
        final ImageView sticker;
        final TextView name;
        final TextView message;
        final TextView myname;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            play = itemView.findViewById(R.id.play);
            media = itemView.findViewById(R.id.media);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.msg);
            myname = itemView.findViewById(R.id.myname);
            sticker = itemView.findViewById(R.id.sticker);

        }
    }
}
