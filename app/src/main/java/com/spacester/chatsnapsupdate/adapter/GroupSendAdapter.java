package com.spacester.chatsnapsupdate.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.spacester.chatsnapsupdate.model.ModelGroups;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.GroupChatActivity;
import com.spacester.chatsnapsupdate.user.SendActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class GroupSendAdapter extends RecyclerView.Adapter<GroupSendAdapter.MyHolder> {

    final Context context;
    final List<ModelGroups> modelGroups;

    Uri image_uri,video_uri;
    String myId;
    private FirebaseAuth mAuth;
    String mediaUri, mediaType;

    public GroupSendAdapter(Context context, List<ModelGroups> modelGroups) {
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
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        myId = currentUser.getUid();

        holder.mName.setText(GroupName);
        try {
            Picasso.get().load(GroupIcon).placeholder(R.drawable.group).into(holder.avatar);
        }catch (Exception e){
            Picasso.get().load(R.drawable.group).into(holder.avatar);
        }

        mediaUri = SendActivity.getMedia_uri();
        mediaType = SendActivity.getType();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaType.equals("img")){
                    image_uri = Uri.parse(mediaUri);



                    Toast.makeText(context, "Sending...",
                            Toast.LENGTH_LONG).show();
                    final String timeStamp = ""+System.currentTimeMillis();
                    String filenameAndPath = "GroupChatImages/"+"post_"+System.currentTimeMillis();
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
                    ref.putFile(video_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                            if (uriTask.isSuccessful()){

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sender", mAuth.getUid());
                                hashMap.put("msg", downloadUri);
                                hashMap.put("type", "video");
                                hashMap.put("timestamp", timeStamp);


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                                ref.child(GroupId).child("Message").child(timeStamp)
                                        .setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Sent",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });



                }else
                if (mediaType.equals("video")){
                    video_uri = Uri.parse(mediaUri);

                    Toast.makeText(context, "Sending...",
                            Toast.LENGTH_LONG).show();
                    final String timeStamp = ""+System.currentTimeMillis();
                    String filenameAndPath = "GroupChatImages/"+"post_"+System.currentTimeMillis();
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
                    ref.putFile(video_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                            if (uriTask.isSuccessful()){

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sender", mAuth.getUid());
                                hashMap.put("msg", downloadUri);
                                hashMap.put("type", "video");
                                hashMap.put("timestamp", timeStamp);


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                                ref.child(GroupId).child("Message").child(timeStamp)
                                        .setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Sent",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });



                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId", GroupId);
                context.startActivity(intent);
                return false;
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
