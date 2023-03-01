package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.comix.rounded.RoundedCornerImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.spacester.chatsnapsupdate.adapter.AdapterChat;
import com.spacester.chatsnapsupdate.GetTimeAgo;
import com.spacester.chatsnapsupdate.model.ModelChat;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.notification.Data;
import com.spacester.chatsnapsupdate.notification.Sender;
import com.spacester.chatsnapsupdate.notification.Token;
import com.spacester.chatsnapsupdate.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rebus.bottomdialog.BottomDialog;

@SuppressWarnings("ALL")
public class ChatActivity extends AppCompatActivity implements View.OnClickListener  {

    //Strings
    String hisId;
    String myId;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    BottomSheetDialog bottomDialog;
    ImageView one,two,three,four,five,six,seven,eight,nine,ten,eleven,twele,thirteen,fourteen,
            fifteen,sixteen,seventeen,eightteen,noneteen,twentee,twentyone,twentytwo,twentythree,twentyfour,
            twentyfive,twentysix,twentyseven,twentyeight,twentynine,thirty,thirtione,thirtitwo,
            thirtithree,thirtifour,thirtifive,thirtisix,thirtiseven,thirtieight,thirtinine,fourty;

    //Id
    RecyclerView rv;
    RoundedCornerImageView dp;
    ImageView back,camera,stickers,send;
    TextView mName,block_text;
    EditText password;
    ImageView online,typing;
    ConstraintLayout block;

    String timestamp;

    private static String name;

    public static String getName() {
        return name;
    }


    public ChatActivity(){

    }

    //Others
    private static final int PICK_VIDEO_REQUEST = 1;
    AdapterChat adapterChat;
    List<ModelChat> nChat;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private BottomDialog dialog;

    ValueEventListener valueEventListener;
    DatabaseReference chatRef;
    DatabaseReference chatlist;

   private RequestQueue requestQueue;
    private boolean notify = false;

    DatabaseReference notificationRef;

    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;

    String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        hisId = getIntent().getStringExtra("hisId");

        timestamp  = ""+System.currentTimeMillis();

        //Id
        rv = findViewById(R.id.rv);
        dp = findViewById(R.id.circleImageView2);
        online = findViewById(R.id.online);
        back = findViewById(R.id.back);
        camera = findViewById(R.id.camera);
        block = findViewById(R.id.block);
        stickers = findViewById(R.id.stickers);
        send = findViewById(R.id.send);
        block_text = findViewById(R.id.block_text);
        mName = findViewById(R.id.name);
        typing = findViewById(R.id.typing);
        final TextView last_seen = findViewById(R.id.last_seen);
        password = findViewById(R.id.password);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        stickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.show();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.show();

            }
        });

        ImageView call = findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String timeStamp = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", timeStamp);
                hashMap.put("caller", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("revicer", hisId);
                hashMap.put("type", "calling");

                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(myId);
                dataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), " is calling you");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                FirebaseDatabase.getInstance().getReference().child("VideoCall").child(timeStamp).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(), CallingActivity.class);
                        intent.putExtra("id", timeStamp);
                        intent.putExtra("dp", photo);
                        intent.putExtra("name", name);
                        intent.putExtra("hisId", hisId);
                        startActivity(intent);
                    }
                });
            }
        });

        //OnCLick
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Firebase
        //Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        myId = currentUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);


        //HisInfo
        //UserDisplay
        Query query = databaseReference.orderByChild("id").equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                     name = ""+ ds.child("name").getValue();
                     photo = ""+ ds.child("photo").getValue();
                    String status = ""+ ds.child("status").getValue();
                    String typingStatus = ""+ ds.child("typingTo").getValue();

                    if (status.equals("online")) {
                        online.setVisibility(View.VISIBLE);
                        last_seen.setVisibility(View.GONE);
                    }else {
                        online.setVisibility(View.GONE);
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        last_seen.setVisibility(View.VISIBLE);
                        long lastTime = Long.parseLong(status);
                        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                        last_seen.setText("Active " + lastSeenTime);
                    }

                    if (typingStatus.equals(myId)){
                        typing.setVisibility(View.VISIBLE);
                    }else {

                        typing.setVisibility(View.GONE);
                    }

                    mName.setText(name);

                    try {
                        Picasso.get().load(photo).placeholder(R.drawable.avatar).into(dp);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.avatar).into(dp);
                    }

                    mName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
                            intent.putExtra("hisId", hisId);
                            startActivity(intent);
                        }
                    });

                    dp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
                            intent.putExtra("hisId", hisId);
                            startActivity(intent);
                        }
                    });

                    readMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        //EditText
        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    send.setVisibility(View.GONE);
                    stickers.setVisibility(View.VISIBLE);
                    checkTypingStatus("noOne");
                } else {
                    stickers.setVisibility(View.GONE);
                    send.setVisibility(View.VISIBLE);
                    checkTypingStatus(hisId);

                    notify = true;

                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(myId);
                    dataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ModelUser user = dataSnapshot.getValue(ModelUser.class);
                            if (notify){
                                sendNotification(hisId, Objects.requireNonNull(user).getName(), name + " is typing");

                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });



        //Send Msg
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String message = password.getText().toString().trim();
                    sendMessage(message);
                password.setText("");
            }
        });

        //Chatlist
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(myId)
                .child(hisId);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef1.child("id").setValue(hisId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(hisId)
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
                Toast.makeText(getApplicationContext(), error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
        seenMessage();
        checkBlocked();
        imBLockedOrNot();
        createBottomSheetDialog();
        createBottomDialog();
         notificationRef = FirebaseDatabase.getInstance().getReference("Users").child(myId);
        chatRef = FirebaseDatabase.getInstance().getReference();

        chatlist  = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(hisId)
                .child(myId);
    }


    private void createBottomSheetDialog(){
        if (bottomSheetDialog == null){
            View view = LayoutInflater.from(this).inflate(R.layout.media_bottom_sheet, null);
            constraintLayout3 = view.findViewById(R.id.constraintLayout3);
            delete = view.findViewById(R.id.delete);
            constraintLayout3.setOnClickListener(this);
            delete.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
    }


    private void imBLockedOrNot (){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisId).child("BlockedUsers").orderByChild("id").equalTo(myId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                block.setVisibility(View.VISIBLE);
                                block_text.setText("You are blocked by this user");
                                Toast.makeText(getApplicationContext(), "You are blocked by this user",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkBlocked() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myId).child("BlockedUsers").orderByChild("id").equalTo(hisId).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                block_text.setText("This user is blocked by you");
                                block.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void seenMessage() {
        final String hisUid = getIntent().getStringExtra("hisId");
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelChat modelChat = snapshot.getValue(ModelChat.class);
                    if (Objects.requireNonNull(modelChat).getReceiver().equals(myId) && modelChat.getSender().equals(hisUid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkTypingStatus(String typing){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(myId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        databaseReference.updateChildren(hashMap);
    }
    private void checkOnlineStatus(String status){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(myId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        databaseReference.updateChildren(hashMap);
    }
    private void sendMessage(final String message) {
        final String timeStamp = ""+System.currentTimeMillis();
        final String hisUid = getIntent().getStringExtra("hisId");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myId);
        hashMap.put("receiver", hisUid);
        hashMap.put("msg", message);
        hashMap.put("isSeen", false);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("type", "text");
        databaseReference1.child("Chats").push().setValue(hashMap);

        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(myId);
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification(hisUid, Objects.requireNonNull(user).getName(), "Sent a message");

                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createBottomDialog(){
        if ( bottomDialog == null){
            View view = LayoutInflater.from(this).inflate(R.layout.sticker, null);

            one = view.findViewById(R.id.one);
            two = view.findViewById(R.id.two);
            three = view.findViewById(R.id.three);
            four = view.findViewById(R.id.four);
            five = view.findViewById(R.id.five);
            six = view.findViewById(R.id.six);
            seven = view.findViewById(R.id.seven);
            eight = view.findViewById(R.id.eight);
            nine = view.findViewById(R.id.nine);
            ten = view.findViewById(R.id.ten);
            eleven = view.findViewById(R.id.eleven);
            twele = view.findViewById(R.id.twele);
            thirteen = view.findViewById(R.id.thirteen);
            fourteen = view.findViewById(R.id.fourteen);
            fifteen = view.findViewById(R.id.fifteen);
            sixteen = view.findViewById(R.id.sixteen);
            seventeen = view.findViewById(R.id.seventeen);
            eightteen = view.findViewById(R.id.eightteen);
            noneteen = view.findViewById(R.id.noneteen);
            twentee = view.findViewById(R.id.twentee);
            twentyone = view.findViewById(R.id.twentyone);
            twentytwo = view.findViewById(R.id.twentytwo);
            twentythree = view.findViewById(R.id.twentythree);
            twentyfour = view.findViewById(R.id.twentyfour);
            twentyfive = view.findViewById(R.id.twentyfive);
            twentysix = view.findViewById(R.id.twentysix);
            twentyseven = view.findViewById(R.id.twentyseven);
            twentyeight = view.findViewById(R.id.twentyeight);
            twentynine = view.findViewById(R.id.twentynine);
            thirty = view.findViewById(R.id.thirty);
            thirtione = view.findViewById(R.id.thirtione);
            thirtitwo = view.findViewById(R.id.thirtitwo);
            thirtithree = view.findViewById(R.id.thirtithree);
            thirtifour = view.findViewById(R.id.thirtifour);
            thirtifive = view.findViewById(R.id.thirtifive);
            thirtisix = view.findViewById(R.id.thirtisix);
            thirtiseven = view.findViewById(R.id.thirtiseven);
            thirtieight = view.findViewById(R.id.thirtieight);
            thirtinine = view.findViewById(R.id.thirtinine);
            fourty = view.findViewById(R.id.fourty);

            one.setOnClickListener(this);
            two.setOnClickListener(this);
            three.setOnClickListener(this);
            four.setOnClickListener(this);
            five.setOnClickListener(this);
            six.setOnClickListener(this);
            seven.setOnClickListener(this);
            eight.setOnClickListener(this);
            nine.setOnClickListener(this);
            ten.setOnClickListener(this);
            eleven.setOnClickListener(this);
            twele.setOnClickListener(this);
            thirteen.setOnClickListener(this);
            fourteen.setOnClickListener(this);
            fifteen.setOnClickListener(this);
            sixteen.setOnClickListener(this);
            seventeen.setOnClickListener(this);
            eightteen.setOnClickListener(this);
            noneteen.setOnClickListener(this);
            twentee.setOnClickListener(this);
            twentyone.setOnClickListener(this);
            twentytwo.setOnClickListener(this);
            twentythree.setOnClickListener(this);
            twentyfour.setOnClickListener(this);
            twentyfive.setOnClickListener(this);
            twentysix.setOnClickListener(this);
            twentyseven.setOnClickListener(this);
            twentyeight.setOnClickListener(this);
            twentynine.setOnClickListener(this);
            thirty.setOnClickListener(this);
            thirtione.setOnClickListener(this);
            thirtitwo.setOnClickListener(this);
            thirtithree.setOnClickListener(this);
            thirtifour.setOnClickListener(this);
            thirtifive.setOnClickListener(this);
            thirtisix.setOnClickListener(this);
            thirtiseven.setOnClickListener(this);
            thirtieight.setOnClickListener(this);
            thirtinine.setOnClickListener(this);
            fourty.setOnClickListener(this);

            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F015-in%20love.png?alt=media&token=2cd4371f-165b-4d92-a4cb-b1ddb793b40d").placeholder(R.drawable.ic_smile_placeholder).into(one);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F016-love.png?alt=media&token=f8680f41-6540-4e26-8a9e-e61315ebdae7").placeholder(R.drawable.ic_smile_placeholder).into(two);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F017-angry.png?alt=media&token=193f357c-1942-40fc-a1d1-419eb0731b90").placeholder(R.drawable.ic_smile_placeholder).into(three);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F018-evil.png?alt=media&token=507952cc-153b-4a8b-8de4-e1b1cfa71170").placeholder(R.drawable.ic_smile_placeholder).into(four);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F019-worried.png?alt=media&token=9353a022-a840-41ec-8c39-5b3f5aa9b0fa").placeholder(R.drawable.ic_smile_placeholder).into(five);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F020-happy.png?alt=media&token=ec8a5af2-b438-4e2c-8d14-161ef9333d89").placeholder(R.drawable.ic_smile_placeholder).into(six);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F021-unamused.png?alt=media&token=aad596cd-f08f-47b8-93de-bcbc2233490f").placeholder(R.drawable.ic_smile_placeholder).into(seven);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F022-angry.png?alt=media&token=c9fcfd56-d9f1-4ec9-a759-a05f298ed7b0").placeholder(R.drawable.ic_smile_placeholder).into(eight);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F023-scared.png?alt=media&token=e69fc8bf-042b-41f1-990f-9f4aa4970a0f").placeholder(R.drawable.ic_smile_placeholder).into(nine);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F024-confused.png?alt=media&token=0e09d6ed-ea5c-49de-b99d-bb86d03b8997").placeholder(R.drawable.ic_smile_placeholder).into(ten);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F025-worried.png?alt=media&token=561c18df-5b88-405c-8bfd-22dce2e50c48").placeholder(R.drawable.ic_smile_placeholder).into(eleven);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F026-zombie.png?alt=media&token=7c844f4f-58d8-46c6-8c57-e6ea7c34ccc8").placeholder(R.drawable.ic_smile_placeholder).into(twele);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F027-happy.png?alt=media&token=82970788-7ea4-4fcf-a0be-7f8b649237f1").placeholder(R.drawable.ic_smile_placeholder).into(thirteen);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F028-worried.png?alt=media&token=34319d65-5fcc-49de-8784-25cf7bccf6e2").placeholder(R.drawable.ic_smile_placeholder).into(fourteen);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F029-love.png?alt=media&token=b484de4a-3415-4025-923e-2fa1d9e28967").placeholder(R.drawable.ic_smile_placeholder).into(fifteen);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F030-amazed.png?alt=media&token=c6803253-a543-4a69-a27e-c956c3e3f8f4").placeholder(R.drawable.ic_smile_placeholder).into(sixteen);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F031-worried.png?alt=media&token=0abf13fc-6998-4deb-bb92-bd501b32f2cc").placeholder(R.drawable.ic_smile_placeholder).into(seventeen);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F032-crying.png?alt=media&token=53615f34-3d81-43d2-8a30-10a13ccd9753").placeholder(R.drawable.ic_smile_placeholder).into(eightteen);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F033-angry.png?alt=media&token=4181bda1-019a-48d0-9112-f331a6fc4601").placeholder(R.drawable.ic_smile_placeholder).into(noneteen);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F034-love.png?alt=media&token=624297a0-676d-4b3a-87b3-ac7fce89150b").placeholder(R.drawable.ic_smile_placeholder).into(twentee);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F035-happy.png?alt=media&token=7ce04e1f-f746-46c2-944c-51a089b73354").placeholder(R.drawable.ic_smile_placeholder).into(twentyone);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F036-laughing.png?alt=media&token=c1e5bbda-a7de-484d-9c17-29ed5ac85bfd").placeholder(R.drawable.ic_smile_placeholder).into(twentytwo);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F037-evil.png?alt=media&token=d8f30643-8686-4eb8-83ca-1fbd778f13db").placeholder(R.drawable.ic_smile_placeholder).into(twentythree);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F038-heart%20eyes.png?alt=media&token=ee5fba96-8265-4042-b6ba-18f166876601").placeholder(R.drawable.ic_smile_placeholder).into(twentyfour);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F039-sad.png?alt=media&token=dbe390cf-f943-42ed-b79a-acdb65a0d9d6").placeholder(R.drawable.ic_smile_placeholder).into(twentyfive);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F040-sad.png?alt=media&token=7e7b5c47-6620-40d0-b862-46793387ea03").placeholder(R.drawable.ic_smile_placeholder).into(twentysix);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F041-amazed.png?alt=media&token=f8c8366c-edbb-44f9-887d-a4c3aadc529e").placeholder(R.drawable.ic_smile_placeholder).into(twentyseven);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F042-freak.png?alt=media&token=c9d40cf4-03a9-4229-a5e4-59fec2fbfd72").placeholder(R.drawable.ic_smile_placeholder).into(twentyeight);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F043-angry.png?alt=media&token=c1f65bf9-0e4d-4b02-add8-52ad057a590e").placeholder(R.drawable.ic_smile_placeholder).into(twentynine);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F044-happy.png?alt=media&token=4273eb51-3484-4440-bc2c-f8382072edce").placeholder(R.drawable.ic_smile_placeholder).into(thirty);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F045-sad.png?alt=media&token=3f7b373f-f9b5-45b3-acb9-10bba70a973f").placeholder(R.drawable.ic_smile_placeholder).into(thirtione);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F046-goofy.png?alt=media&token=de735951-d101-49a6-bd7f-ff7724bc3740").placeholder(R.drawable.ic_smile_placeholder).into(thirtitwo);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F047-amazed.png?alt=media&token=2e5e6fe7-d5c8-4fd0-bd81-90e63d1fad5f").placeholder(R.drawable.ic_smile_placeholder).into(thirtithree);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F048-happy.png?alt=media&token=7d452d6e-d726-4f98-98ca-d8c04e1055fa").placeholder(R.drawable.ic_smile_placeholder).into(thirtifour);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F049-grumpy.png?alt=media&token=ab5ddb0a-4c5a-4fef-9c72-f260fdda35dd").placeholder(R.drawable.ic_smile_placeholder).into(thirtifive);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F050-shocked.png?alt=media&token=6ef5bef2-b594-4313-a206-d5b501b3fe6a").placeholder(R.drawable.ic_smile_placeholder).into(thirtisix);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2FExcited.png?alt=media&token=dd70bfc8-b9cc-43fc-b931-91ae13501f71").placeholder(R.drawable.ic_smile_placeholder).into(thirtiseven);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2Fangry.png?alt=media&token=1dcd409f-433f-40de-a9e3-567bcb41211d").placeholder(R.drawable.ic_smile_placeholder).into(thirtieight);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2Fannoyed.png?alt=media&token=aaffdd5a-258a-40c2-951c-14e6ba51e3c4").placeholder(R.drawable.ic_smile_placeholder).into(thirtinine);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2Fbored.png?alt=media&token=60f32f9d-7ef4-4e89-8fa8-b55ad68f7f83").placeholder(R.drawable.ic_smile_placeholder).into(fourty);


            bottomDialog = new BottomSheetDialog(this);
            bottomDialog.setContentView(view);
        }
    }

    private void readMessage() {
        final String hisUid = getIntent().getStringExtra("hisId");
        nChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelChat chat = snapshot.getValue(ModelChat.class);
                    if (Objects.requireNonNull(chat).getReceiver().equals(myId) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myId)){
                        nChat.add(chat);
                    }

                    adapterChat = new AdapterChat(ChatActivity.this, nChat);
                    rv.setAdapter(adapterChat);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ChatActivity.this, "Storage permission Allowed",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ChatActivity.this, "Storage permission is required",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Uri image_uri = Objects.requireNonNull(data).getData();
            sendImage(image_uri);
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri video_uri = data.getData();
            sendVideo(video_uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendVideo(Uri video_uri) {
        Toast.makeText(ChatActivity.this, "Sending....",
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
                    hashMap.put("receiver", hisId);
                    hashMap.put("msg", downloadUri);
                    hashMap.put("isSeen", false);

                    hashMap.put("timestamp", timeStamp);
                    hashMap.put("type", "video");
                    databaseReference.child("Chats").push().setValue(hashMap);


                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(myId);
                    dataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ModelUser user = dataSnapshot.getValue(ModelUser.class);
                            if (notify){
                                sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a video");

                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(hisId)
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
                            Toast.makeText(ChatActivity.this, error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private String getfileExt(Uri video_uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(video_uri));
    }


    private void sendImage(Uri image_uri) {
        Toast.makeText(ChatActivity.this, "Sending....",
                Toast.LENGTH_LONG).show();
        notify = true;
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
                    hashMap.put("receiver", hisId);
                    hashMap.put("msg", downloadUri);
                    hashMap.put("isSeen", false);
                    hashMap.put("timestamp", timeStamp);
                    hashMap.put("type", "image");
                    databaseReference.child("Chats").push().setValue(hashMap);

                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(myId);
                    dataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ModelUser user = dataSnapshot.getValue(ModelUser.class);
                            if (notify){
                                sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a Image");

                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(hisId)
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
                            Toast.makeText(ChatActivity.this, error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    protected void onStart() {
        checkOnlineStatus("online");
        if (hisId.equals(myId)){
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        }
        super.onStart();
    }
    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = ""+System.currentTimeMillis();
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
        databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.constraintLayout3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickImageFromGallery();
                    }
                }
                else {
                    pickImageFromGallery();
                }
                break;
            case R.id.delete:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        chooseVideo();
                    }
                }
                else {
                    chooseVideo();
                }
                break;
            case R.id.one:
                bottomDialog.cancel();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", myId);
                hashMap.put("receiver", hisId);
                hashMap.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F015-in%20love.png?alt=media&token=2cd4371f-165b-4d92-a4cb-b1ddb793b40d");
                hashMap.put("isSeen", false);
                hashMap.put("type", "sticker");
                hashMap.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap);
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.two:
                bottomDialog.cancel();
                HashMap<String, Object> hashMap2 = new HashMap<>();
                hashMap2.put("sender", myId);
                hashMap2.put("receiver", hisId);
                hashMap2.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F016-love.png?alt=media&token=f8680f41-6540-4e26-8a9e-e61315ebdae7");
                hashMap2.put("isSeen", false);
                hashMap2.put("type", "sticker");
                hashMap2.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap2);
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.three:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                bottomDialog.cancel();
                HashMap<String, Object> hashMap3 = new HashMap<>();
                hashMap3.put("sender", myId);
                hashMap3.put("receiver", hisId);
                hashMap3.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F017-angry.png?alt=media&token=193f357c-1942-40fc-a1d1-419eb0731b90");
                hashMap3.put("isSeen", false);
                hashMap3.put("timestamp", timestamp);
                hashMap3.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap3);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.four:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap4 = new HashMap<>();
                hashMap4.put("sender", myId);
                hashMap4.put("receiver", hisId);
                hashMap4.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F018-evil.png?alt=media&token=507952cc-153b-4a8b-8de4-e1b1cfa71170");
                hashMap4.put("isSeen", false);
                hashMap4.put("type", "sticker");
                hashMap4.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap4);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.five:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap5 = new HashMap<>();
                hashMap5.put("sender", myId);
                hashMap5.put("receiver", hisId);
                hashMap5.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F019-worried.png?alt=media&token=9353a022-a840-41ec-8c39-5b3f5aa9b0fa");
                hashMap5.put("isSeen", false);
                hashMap5.put("timestamp", timestamp);
                hashMap5.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap5);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.six:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap6 = new HashMap<>();
                hashMap6.put("sender", myId);
                hashMap6.put("receiver", hisId);
                hashMap6.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F020-happy.png?alt=media&token=ec8a5af2-b438-4e2c-8d14-161ef9333d89");
                hashMap6.put("isSeen", false);
                hashMap6.put("timestamp", timestamp);
                hashMap6.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap6);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.seven:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap7 = new HashMap<>();
                hashMap7.put("sender", myId);
                hashMap7.put("receiver", hisId);
                hashMap7.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F021-unamused.png?alt=media&token=aad596cd-f08f-47b8-93de-bcbc2233490f");
                hashMap7.put("isSeen", false);
                hashMap7.put("timestamp", timestamp);
                hashMap7.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap7);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.eight:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap8 = new HashMap<>();
                hashMap8.put("sender", myId);
                hashMap8.put("receiver", hisId);
                hashMap8.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F022-angry.png?alt=media&token=c9fcfd56-d9f1-4ec9-a759-a05f298ed7b0");
                hashMap8.put("isSeen", false);
                hashMap8.put("timestamp", timestamp);
                hashMap8.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap8);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.nine:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap9 = new HashMap<>();
                hashMap9.put("sender", myId);
                hashMap9.put("receiver", hisId);
                hashMap9.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F023-scared.png?alt=media&token=e69fc8bf-042b-41f1-990f-9f4aa4970a0f");
                hashMap9.put("isSeen", false);
                hashMap9.put("timestamp", timestamp);
                hashMap9.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap9);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.ten:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap10 = new HashMap<>();
                hashMap10.put("sender", myId);
                hashMap10.put("receiver", hisId);
                hashMap10.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F024-confused.png?alt=media&token=0e09d6ed-ea5c-49de-b99d-bb86d03b8997");
                hashMap10.put("isSeen", false);
                hashMap10.put("type", "sticker");
                hashMap10.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap10);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.eleven:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap11 = new HashMap<>();
                hashMap11.put("sender", myId);
                hashMap11.put("receiver", hisId);
                hashMap11.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F025-worried.png?alt=media&token=561c18df-5b88-405c-8bfd-22dce2e50c48");
                hashMap11.put("isSeen", false);
                hashMap11.put("type", "sticker");
                hashMap11.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap11);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.twele:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap12 = new HashMap<>();
                hashMap12.put("sender", myId);
                hashMap12.put("receiver", hisId);
                hashMap12.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F026-zombie.png?alt=media&token=7c844f4f-58d8-46c6-8c57-e6ea7c34ccc8");
                hashMap12.put("isSeen", false);
                hashMap12.put("type", "sticker");
                hashMap12.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap12);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.thirteen:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap13 = new HashMap<>();
                hashMap13.put("sender", myId);
                hashMap13.put("receiver", hisId);
                hashMap13.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F027-happy.png?alt=media&token=82970788-7ea4-4fcf-a0be-7f8b649237f1");
                hashMap13.put("isSeen", false);
                hashMap13.put("timestamp", timestamp);
                hashMap13.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap13);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.fourteen:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap14 = new HashMap<>();
                hashMap14.put("sender", myId);
                hashMap14.put("receiver", hisId);
                hashMap14.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F028-worried.png?alt=media&token=34319d65-5fcc-49de-8784-25cf7bccf6e2");
                hashMap14.put("isSeen", false);
                hashMap14.put("type", "sticker");
                hashMap14.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap14);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.fifteen:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap15 = new HashMap<>();
                hashMap15.put("sender", myId);
                hashMap15.put("receiver", hisId);
                hashMap15.put("timestamp", timestamp);
                hashMap15.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F029-love.png?alt=media&token=b484de4a-3415-4025-923e-2fa1d9e28967");
                hashMap15.put("isSeen", false);
                hashMap15.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap15);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.sixteen:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap16 = new HashMap<>();
                hashMap16.put("sender", myId);
                hashMap16.put("receiver", hisId);
                hashMap16.put("timestamp", timestamp);
                hashMap16.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F030-amazed.png?alt=media&token=c6803253-a543-4a69-a27e-c956c3e3f8f4");
                hashMap16.put("isSeen", false);
                hashMap16.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap16);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.seventeen:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap17 = new HashMap<>();
                hashMap17.put("sender", myId);
                hashMap17.put("receiver", hisId);
                hashMap17.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F031-worried.png?alt=media&token=0abf13fc-6998-4deb-bb92-bd501b32f2cc");
                hashMap17.put("isSeen", false);
                hashMap17.put("timestamp", timestamp);
                hashMap17.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap17);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.eightteen:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap18 = new HashMap<>();
                hashMap18.put("sender", myId);
                hashMap18.put("receiver", hisId);
                hashMap18.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F032-crying.png?alt=media&token=53615f34-3d81-43d2-8a30-10a13ccd9753");
                hashMap18.put("isSeen", false);
                hashMap18.put("timestamp", timestamp);
                hashMap18.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap18);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.noneteen:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap19 = new HashMap<>();
                hashMap19.put("sender", myId);
                hashMap19.put("timestamp", timestamp);
                hashMap19.put("receiver", hisId);
                hashMap19.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F033-angry.png?alt=media&token=4181bda1-019a-48d0-9112-f331a6fc4601");
                hashMap19.put("isSeen", false);
                hashMap19.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap19);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.twentee:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap20 = new HashMap<>();
                hashMap20.put("sender", myId);
                hashMap20.put("receiver", hisId);
                hashMap20.put("timestamp", timestamp);
                hashMap20.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F034-love.png?alt=media&token=624297a0-676d-4b3a-87b3-ac7fce89150b");
                hashMap20.put("isSeen", false);
                hashMap20.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap20);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.twentyone:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap21 = new HashMap<>();
                hashMap21.put("sender", myId);
                hashMap21.put("receiver", hisId);
                hashMap21.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F035-happy.png?alt=media&token=7ce04e1f-f746-46c2-944c-51a089b73354");
                hashMap21.put("isSeen", false);
                hashMap21.put("type", "sticker");
                hashMap21.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap21);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.twentytwo:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap22 = new HashMap<>();
                hashMap22.put("sender", myId);
                hashMap22.put("receiver", hisId);
                hashMap22.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F036-laughing.png?alt=media&token=c1e5bbda-a7de-484d-9c17-29ed5ac85bfd");
                hashMap22.put("isSeen", false);
                hashMap22.put("timestamp", timestamp);
                hashMap22.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap22);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.twentythree:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap23 = new HashMap<>();
                hashMap23.put("sender", myId);
                hashMap23.put("receiver", hisId);
                hashMap23.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F037-evil.png?alt=media&token=d8f30643-8686-4eb8-83ca-1fbd778f13db");
                hashMap23.put("isSeen", false);
                hashMap23.put("timestamp", timestamp);
                hashMap23.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap23);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.twentyfour:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap24 = new HashMap<>();
                hashMap24.put("sender", myId);
                hashMap24.put("receiver", hisId);
                hashMap24.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F038-heart%20eyes.png?alt=media&token=ee5fba96-8265-4042-b6ba-18f166876601");
                hashMap24.put("isSeen", false);
                hashMap24.put("type", "sticker");
                hashMap24.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap24);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.twentyfive:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap25 = new HashMap<>();
                hashMap25.put("sender", myId);
                hashMap25.put("receiver", hisId);
                hashMap25.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F039-sad.png?alt=media&token=dbe390cf-f943-42ed-b79a-acdb65a0d9d6");
                hashMap25.put("isSeen", false);
                hashMap25.put("type", "sticker");
                hashMap25.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap25);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.twentysix:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap26 = new HashMap<>();
                hashMap26.put("sender", myId);
                hashMap26.put("receiver", hisId);
                hashMap26.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F040-sad.png?alt=media&token=7e7b5c47-6620-40d0-b862-46793387ea03");
                hashMap26.put("isSeen", false);
                hashMap26.put("type", "sticker");
                hashMap26.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap26);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.twentyseven:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap27 = new HashMap<>();
                hashMap27.put("sender", myId);
                hashMap27.put("receiver", hisId);
                hashMap27.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F041-amazed.png?alt=media&token=f8c8366c-edbb-44f9-887d-a4c3aadc529e");
                hashMap27.put("isSeen", false);
                hashMap27.put("timestamp", timestamp);
                hashMap27.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap27);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.twentyeight:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap28 = new HashMap<>();
                hashMap28.put("sender", myId);
                hashMap28.put("receiver", hisId);
                hashMap28.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F042-freak.png?alt=media&token=c9d40cf4-03a9-4229-a5e4-59fec2fbfd72");
                hashMap28.put("isSeen", false);
                hashMap28.put("timestamp", timestamp);
                hashMap28.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap28);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.twentynine:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap29 = new HashMap<>();
                hashMap29.put("sender", myId);
                hashMap29.put("receiver", hisId);
                hashMap29.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F043-angry.png?alt=media&token=c1f65bf9-0e4d-4b02-add8-52ad057a590e");
                hashMap29.put("isSeen", false);
                hashMap29.put("type", "sticker");
                hashMap29.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap29);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.thirty:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap30 = new HashMap<>();
                hashMap30.put("sender", myId);
                hashMap30.put("receiver", hisId);
                hashMap30.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F044-happy.png?alt=media&token=4273eb51-3484-4440-bc2c-f8382072edce");
                hashMap30.put("isSeen", false);
                hashMap30.put("type", "sticker");
                hashMap30.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap30);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.thirtione:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap31 = new HashMap<>();
                hashMap31.put("sender", myId);
                hashMap31.put("receiver", hisId);
                hashMap31.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F045-sad.png?alt=media&token=3f7b373f-f9b5-45b3-acb9-10bba70a973f");
                hashMap31.put("isSeen", false);
                hashMap31.put("timestamp", timestamp);
                hashMap31.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap31);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.thirtitwo:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap32 = new HashMap<>();
                hashMap32.put("sender", myId);
                hashMap32.put("receiver", hisId);
                hashMap32.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F046-goofy.png?alt=media&token=de735951-d101-49a6-bd7f-ff7724bc3740");
                hashMap32.put("isSeen", false);
                hashMap32.put("timestamp", timestamp);
                hashMap32.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap32);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.thirtithree:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap33 = new HashMap<>();
                hashMap33.put("sender", myId);
                hashMap33.put("receiver", hisId);
                hashMap33.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F047-amazed.png?alt=media&token=2e5e6fe7-d5c8-4fd0-bd81-90e63d1fad5f");
                hashMap33.put("isSeen", false);
                hashMap33.put("type", "sticker");
                hashMap33.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap33);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.thirtifour:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap34 = new HashMap<>();
                hashMap34.put("sender", myId);
                hashMap34.put("receiver", hisId);
                hashMap34.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F048-happy.png?alt=media&token=7d452d6e-d726-4f98-98ca-d8c04e1055fa");
                hashMap34.put("isSeen", false);
                hashMap34.put("timestamp", timestamp);
                hashMap34.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap34);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.thirtifive:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap35 = new HashMap<>();
                hashMap35.put("sender", myId);
                hashMap35.put("receiver", hisId);
                hashMap35.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F049-grumpy.png?alt=media&token=ab5ddb0a-4c5a-4fef-9c72-f260fdda35dd");
                hashMap35.put("isSeen", false);
                hashMap35.put("timestamp", timestamp);
                hashMap35.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap35);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.thirtisix:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap36 = new HashMap<>();
                hashMap36.put("sender", myId);
                hashMap36.put("receiver", hisId);
                hashMap36.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F050-shocked.png?alt=media&token=6ef5bef2-b594-4313-a206-d5b501b3fe6a");
                hashMap36.put("isSeen", false);
                hashMap36.put("type", "sticker");
                hashMap36.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap36);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.thirtiseven:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap37 = new HashMap<>();
                hashMap37.put("sender", myId);
                hashMap37.put("receiver", hisId);
                hashMap37.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2FExcited.png?alt=media&token=dd70bfc8-b9cc-43fc-b931-91ae13501f71");
                hashMap37.put("isSeen", false);
                hashMap37.put("timestamp", timestamp);
                hashMap37.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap37);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.thirtieight:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap38 = new HashMap<>();
                hashMap38.put("sender", myId);
                hashMap38.put("receiver", hisId);
                hashMap38.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2Fangry.png?alt=media&token=1dcd409f-433f-40de-a9e3-567bcb41211d");
                hashMap38.put("isSeen", false);
                hashMap38.put("type", "sticker");
                hashMap38.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap38);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.thirtinine:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap39 = new HashMap<>();
                hashMap39.put("sender", myId);
                hashMap39.put("receiver", hisId);
                hashMap39.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2Fannoyed.png?alt=media&token=aaffdd5a-258a-40c2-951c-14e6ba51e3c4");
                hashMap39.put("isSeen", false);
                hashMap39.put("type", "sticker");
                hashMap39.put("timestamp", timestamp);
                chatRef.child("Chats").push().setValue(hashMap39);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.fourty:
                notify = true;
                notificationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisId, Objects.requireNonNull(user).getName(), "Sent a sticker");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                bottomDialog.cancel();
                HashMap<String, Object> hashMap40 = new HashMap<>();
                hashMap40.put("sender", myId);
                hashMap40.put("receiver", hisId);
                hashMap40.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2Fbored.png?alt=media&token=60f32f9d-7ef4-4e89-8fa8-b55ad68f7f83");
                hashMap40.put("isSeen", false);
                hashMap40.put("timestamp", timestamp);
                hashMap40.put("type", "sticker");
                chatRef.child("Chats").push().setValue(hashMap40);
                chatlist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatlist.child("id").setValue(myId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
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
                            @SuppressWarnings("RedundantThrows")
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}