package com.spacester.chatsnapsupdate.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.notification.Data;
import com.spacester.chatsnapsupdate.notification.Sender;
import com.spacester.chatsnapsupdate.notification.Token;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.ChatActivity;
import com.spacester.chatsnapsupdate.user.SendActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ALL")
public class AdapterSendUsers extends RecyclerView.Adapter<AdapterSendUsers.MyHolder>{

    final Context context;
    final List<ModelUser> userList;
    String myId;
    Uri image_uri,video_uri;

    String mediaUri, mediaType;

    private final RequestQueue requestQueue;
    private boolean notify = false;


    public AdapterSendUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        requestQueue = Volley.newRequestQueue(context);
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        myId = currentUser.getUid();

        try {
            Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(holder.avatar);
        }catch (Exception e){
            Picasso.get().load(R.drawable.avatar).into(holder.avatar);
        }

        mediaUri = SendActivity.getMedia_uri();
        mediaType = SendActivity.getType();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaType.equals("img")){
                    image_uri = Uri.parse(mediaUri);
                    imBLockedOrNot(hisUID);
                }else
                if (mediaType.equals("video")){
                    video_uri = Uri.parse(mediaUri);
                    imBLockedOrNotVid(hisUID);
                }

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisId", hisUID);
                context.startActivity(intent);
                return false;
            }
        });

    }

    private void imBLockedOrNot(final String hisUID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUID).child("BlockedUsers").orderByChild("id").equalTo(myId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                Toast.makeText(context, "You are blocked by this user",
                                        Toast.LENGTH_LONG).show();

                                return;
                            }
                        }
                        notify = true;

                        Toast.makeText(context, "Sending...",
                                Toast.LENGTH_LONG).show();
                        final String timeStamp = ""+System.currentTimeMillis();
                        String filenameAndPath = "ChatImages/"+"post_"+System.currentTimeMillis();
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
                        ref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());
                                String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                                if (uriTask.isSuccessful()){

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("sender", myId);
                                    hashMap.put("receiver", hisUID);
                                    hashMap.put("msg", downloadUri);
                                    hashMap.put("isSeen", false);
                                    hashMap.put("timestamp", timeStamp);
                                    hashMap.put("type", "image");
                                    databaseReference.child("Chats").push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(context, "Sent",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(myId);
                                    dataRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ModelUser user = dataSnapshot.getValue(ModelUser.class);
                                            if (notify){
                                                sendNotification(hisUID, Objects.requireNonNull(user).getName(), "Sent a image");

                                            }
                                            notify = false;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                                            .child(hisUID)
                                            .child(myId);


                                    chatRef2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (!snapshot.exists()){
                                                chatRef2.child("id").setValue(myId);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(context, error.getMessage(),
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void imBLockedOrNotVid(final String hisUID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUID).child("BlockedUsers").orderByChild("id").equalTo(myId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                Toast.makeText(context, "You are blocked by this user",
                                        Toast.LENGTH_LONG).show();

                                return;
                            }
                        }

                        Toast.makeText(context, "Sending...",
                                Toast.LENGTH_LONG).show();
                        notify = true;
                        final String timeStamp = ""+System.currentTimeMillis();
                        String filenameAndPath = "ChatImages/"+"post_"+System.currentTimeMillis();
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
                        ref.putFile(video_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());
                                String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                                if (uriTask.isSuccessful()){

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("sender", myId);
                                    hashMap.put("receiver", hisUID);
                                    hashMap.put("msg", downloadUri);
                                    hashMap.put("isSeen", false);
                                    hashMap.put("timestamp", timeStamp);
                                    hashMap.put("type", "video");
                                    databaseReference.child("Chats").push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(context, "Sent",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(myId);
                                    dataRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ModelUser user = dataSnapshot.getValue(ModelUser.class);
                                            if (notify){
                                                sendNotification(hisUID, Objects.requireNonNull(user).getName(), "Sent a video");

                                            }
                                            notify = false;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                    final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                                            .child(hisUID)
                                            .child(myId);

                                    Toast.makeText(context, "Sent",
                                            Toast.LENGTH_LONG).show();

                                    chatRef2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (!snapshot.exists()){
                                                chatRef2.child("id").setValue(myId);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(context, error.getMessage(),
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

    private void sendNotification(final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myId, name + " : " + message, "New Message", hisId, R.drawable.logo);
                    Sender sender = new Sender(data, Objects.requireNonNull(token).getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON_RESPONSE", "onResponse" + response.toString());

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse" + error.toString());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAANDu7kPI:APA91bFtJB-Gf0jGN4IzrJ8WPk24tbH4GyQEJn1dxCHFA8WpddAYFxeA5bt-ZYDIj-Yv60_6J2FhTKEPVcArD0DFJMxoCgBLaI0PimA7fm2fkGN12yZdyw1RDP0ih4IcfqHBrls6gZd5");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
