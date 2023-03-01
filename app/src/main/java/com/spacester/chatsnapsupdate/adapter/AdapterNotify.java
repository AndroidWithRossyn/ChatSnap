package com.spacester.chatsnapsupdate.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.GetTimeAgo;
import com.spacester.chatsnapsupdate.model.ModelNotification;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.GroupChatActivity;
import com.spacester.chatsnapsupdate.user.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class AdapterNotify extends RecyclerView.Adapter<AdapterNotify.Holder>  {

    private final Context context;
    private final ArrayList<ModelNotification> notifications;
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAuth mAuth;
    private String userId;

    public AdapterNotify(Context context, ArrayList<ModelNotification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        final ModelNotification modelNotification = notifications.get(position);
        String notification = modelNotification.getNotification();
        final String timestamp = modelNotification.getTimestamp();
        final String senderUid = modelNotification.getsUid();
        final String postId = modelNotification.getpId();

        GetTimeAgo getTimeAgo = new GetTimeAgo();
        long lastTime = Long.parseLong(timestamp);
        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, context);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("id").equalTo(senderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String mName = ""+ds.child("name").getValue();
                            String image = ""+ds.child("photo").getValue();

                            modelNotification.setsName(mName);
                            modelNotification.setsImage(image);
                            holder.name.setText(mName);
                            try {
                                Picasso.get().load(image).placeholder(R.drawable.avatar).into(holder.circleImageView);
                            }catch (Exception e){
                                Picasso.get().load(image).placeholder(R.drawable.avatar).into(holder.circleImageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.username.setText(notification +" - "+ lastSeenTime);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!postId.isEmpty()){
                    Intent intent = new Intent(context, GroupChatActivity.class);
                    intent.putExtra("groupId", postId);
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("hisId", senderUid);
                    context.startActivity(intent);
                }
            }
        });

     holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View v) {
             AlertDialog.Builder builder = new AlertDialog.Builder(context);
             builder.setTitle("Delete");
             builder.setMessage("Are you sure to delete this notification?");
             builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                     ref.child(userId).child("Notifications").child(timestamp).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void aVoid) {

                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {

                         }
                     });
                 }
             }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
                 }
             });
             builder.create().show();
             return false;
         }
     });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class Holder extends RecyclerView.ViewHolder{

        final CircleImageView circleImageView;
        final TextView username;
        final TextView name;

        public Holder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
        }
    }
}
