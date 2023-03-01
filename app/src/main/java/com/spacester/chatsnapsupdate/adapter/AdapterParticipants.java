package com.spacester.chatsnapsupdate.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comix.rounded.RoundedCornerImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class AdapterParticipants extends RecyclerView.Adapter<AdapterParticipants.HolderParticipantsAdd>{
    private final Context context;
    private final List<ModelUser> userList;
    private final String groupId;
    private final String myGroupRole;
    String userId;

    public AdapterParticipants(Context context, List<ModelUser> userList, String groupId, String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

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
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                ref.child(groupId).child("Participants").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    String hisPrevRole = ""+snapshot.child("role").getValue();
                                    String[] options;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Choose Option");
                                    if (myGroupRole.equals("creator")){
                                     if (hisPrevRole.equals("participant"))
                                        {
                                            options = new String[]{"Remove user"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which==0){
                                                        removeParticiapnts(modelUser);

                                                    }
                                                }
                                            }).show();

                                        }
                                    }
                                }
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Add user")
                                            .setMessage("Add this user in this group?")
                                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    addParticiapnts(modelUser);
                                                }
                                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(context, error.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        holder.username.setText(mUsername);
    }

    private void addParticiapnts(final ModelUser modelUser) {

        String timestamp = ""+System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", modelUser.getId());
        hashMap.put("role", "participant");
        hashMap.put("timestamp", ""+timestamp);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "User added",
                                Toast.LENGTH_LONG).show();
                        addToHisNotification(""+modelUser.getId(),"","Added in a group");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    private void removeParticiapnts(final ModelUser modelUser) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "User removed",
                                Toast.LENGTH_LONG).show();
                        addToHisNotification(""+modelUser.getId(),"","Removed from a group");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void addToHisNotification(String hisUid, String pId, String notification){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", groupId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", notification);
        hashMap.put("sUid", hisUid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
