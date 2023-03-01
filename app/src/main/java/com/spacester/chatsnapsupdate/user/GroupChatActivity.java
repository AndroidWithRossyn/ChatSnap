package com.spacester.chatsnapsupdate.user;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comix.rounded.RoundedCornerImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.spacester.chatsnapsupdate.adapter.AdapterGroupChat;
import com.spacester.chatsnapsupdate.model.ModelGroupChat;
import com.spacester.chatsnapsupdate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import rebus.bottomdialog.BottomDialog;

@SuppressWarnings("ALL")
public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener   {

    //String
    String GroupId, myGroupRole;
    private String userId;
    private FirebaseAuth mAuth;

    //Id
    RecyclerView rv;
    RoundedCornerImageView dp;
    ImageView back,camera,stickers,send;
    TextView mName;
    EditText password;


    BottomSheetDialog bottomDialog;
    ImageView one,two,three,four,five,six,seven,eight,nine,ten,eleven,twele,thirteen,fourteen,
            fifteen,sixteen,seventeen,eightteen,noneteen,twentee,twentyone,twentytwo,twentythree,twentyfour,
            twentyfive,twentysix,twentyseven,twentyeight,twentynine,thirty,thirtione,thirtitwo,
            thirtithree,thirtifour,thirtifive,thirtisix,thirtiseven,thirtieight,thirtinine,fourty;

    //Others
    private static final int PICK_VIDEO_REQUEST = 1;
    private ArrayList<ModelGroupChat> groupChats;
    private AdapterGroupChat adapterGroupChat;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private BottomDialog dialog;
    DatabaseReference chatRef;


    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        //Id
        rv = findViewById(R.id.rv);
        dp = findViewById(R.id.circleImageView2);
        back = findViewById(R.id.back);
        camera = findViewById(R.id.camera);
        stickers = findViewById(R.id.stickers);
        send = findViewById(R.id.send);
        mName = findViewById(R.id.name);
        password = findViewById(R.id.password);


        //Firebase
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Intent intent = getIntent();
        GroupId = intent.getStringExtra("groupId");

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


        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);

        //EditText
        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    send.setVisibility(View.GONE);
                    stickers.setVisibility(View.VISIBLE);
                } else {
                    stickers.setVisibility(View.GONE);
                    send.setVisibility(View.VISIBLE);
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
                String message = password.getText().toString().trim();
                sendMessage(message);
                password.setText("");
            }
        });




        loadGroupInfo();
        createBottomSheetDialog();
        loadGroupMessage();
        loadMyGroupRole();
        createBottomDialog();

        chatRef = FirebaseDatabase.getInstance().getReference("Groups").child(GroupId).child("Message");
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

    private void loadMyGroupRole() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Participants")
                .orderByChild("id").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            myGroupRole = ""+ds.child("role").getValue();
                            if(myGroupRole.equals("creator")){

                            }
                            if (myGroupRole.equals("admin")){

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadGroupMessage() {
        groupChats = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Message")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        groupChats.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelGroupChat modelGroupChat = ds.getValue(ModelGroupChat.class);
                            groupChats.add(modelGroupChat);
                        }
                        adapterGroupChat = new AdapterGroupChat(GroupChatActivity.this, groupChats);
                        rv.setAdapter(adapterGroupChat);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendMessage(String message) {

        String timestamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", mAuth.getUid());
        hashMap.put("msg", message);
        hashMap.put("type", "text");
        hashMap.put("timestamp", timestamp);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Message").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        password.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(GroupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                             name = ""+ds.child("gName").getValue();
                            String icon = ""+ds.child("gIcon").getValue();

                            mName.setText(name);
                            try {
                                Picasso.get().load(icon).placeholder(R.drawable.group).into(dp);
                            }catch (Exception e){
                                Picasso.get().load(R.drawable.group).into(dp);
                            }

                            mName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), GroupProfile.class);
                                    intent.putExtra("groupId", GroupId);
                                    startActivity(intent);
                                }
                            });
                            dp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), GroupProfile.class);
                                    intent.putExtra("groupId", GroupId);
                                    startActivity(intent);
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }
    private String getfileExt(Uri video_uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(video_uri));
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(GroupChatActivity.this, "Storage permission Allowed",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(GroupChatActivity.this, "Storage permission is required",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            assert data != null;
            Uri image_uri = data.getData();
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
        Toast.makeText(GroupChatActivity.this, "Sending...",
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
                                    password.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GroupChatActivity.this, e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatActivity.this, e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendImage(Uri image_uri) {
        Toast.makeText(GroupChatActivity.this, "Sending...",
                Toast.LENGTH_LONG).show();
        final String timeStamp = ""+System.currentTimeMillis();
        String filenameAndPath = "GroupChatImages/"+"post_"+System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                if (uriTask.isSuccessful()){

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", mAuth.getUid());
                    hashMap.put("msg", downloadUri);
                    hashMap.put("type", "image");
                    hashMap.put("timestamp", timeStamp);


                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                    ref.child(GroupId).child("Message").child(timeStamp)
                            .setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    password.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GroupChatActivity.this,e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatActivity.this,e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });


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
                String timestamp = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", mAuth.getUid());
                hashMap.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F015-in%20love.png?alt=media&token=2cd4371f-165b-4d92-a4cb-b1ddb793b40d");
                hashMap.put("type", "sticker");
                hashMap.put("timestamp", timestamp);
                chatRef.child(timestamp)
                        .setValue(hashMap);

                break;
            case R.id.two:
                bottomDialog.cancel();
                String timestamp2 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap2 = new HashMap<>();
                hashMap2.put("sender", mAuth.getUid());
                hashMap2.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F016-love.png?alt=media&token=f8680f41-6540-4e26-8a9e-e61315ebdae7");
                hashMap2.put("type", "sticker");
                hashMap2.put("timestamp", timestamp2);
                chatRef.child(timestamp2)
                        .setValue(hashMap2);

                break;
            case R.id.three:
                bottomDialog.cancel();
                String timestamp3 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap3 = new HashMap<>();
                hashMap3.put("sender", mAuth.getUid());
                hashMap3.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F017-angry.png?alt=media&token=193f357c-1942-40fc-a1d1-419eb0731b90");
                hashMap3.put("type", "sticker");
                hashMap3.put("timestamp", timestamp3);
                chatRef.child(timestamp3)
                        .setValue(hashMap3);
                break;
            case R.id.four:
                bottomDialog.cancel();
                String timestamp4 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap4 = new HashMap<>();
                hashMap4.put("sender", mAuth.getUid());
                hashMap4.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F018-evil.png?alt=media&token=507952cc-153b-4a8b-8de4-e1b1cfa71170");
                hashMap4.put("type", "sticker");
                hashMap4.put("timestamp", timestamp4);
                chatRef.child(timestamp4)
                        .setValue(hashMap4);
                break;
            case R.id.five:
                bottomDialog.cancel();
                String timestamp5 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap5 = new HashMap<>();
                hashMap5.put("sender", mAuth.getUid());
                hashMap5.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F019-worried.png?alt=media&token=9353a022-a840-41ec-8c39-5b3f5aa9b0fa");
                hashMap5.put("type", "sticker");
                hashMap5.put("timestamp", timestamp5);
                chatRef.child(timestamp5)
                        .setValue(hashMap5);

                break;
            case R.id.six:
                bottomDialog.cancel();
                String timestamp6 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap6 = new HashMap<>();
                hashMap6.put("sender", mAuth.getUid());
                hashMap6.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F020-happy.png?alt=media&token=ec8a5af2-b438-4e2c-8d14-161ef9333d89");
                hashMap6.put("type", "sticker");
                hashMap6.put("timestamp", timestamp6);
                chatRef.child(timestamp6)
                        .setValue(hashMap6);
                break;
            case R.id.seven:
                bottomDialog.cancel();
                String timestamp7 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap7 = new HashMap<>();
                hashMap7.put("sender", mAuth.getUid());
                hashMap7.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F021-unamused.png?alt=media&token=aad596cd-f08f-47b8-93de-bcbc2233490f");
                hashMap7.put("type", "sticker");
                hashMap7.put("timestamp", timestamp7);
                chatRef.child(timestamp7)
                        .setValue(hashMap7);

                break;
            case R.id.eight:
                bottomDialog.cancel();
                String timestamp8 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap8 = new HashMap<>();
                hashMap8.put("sender", mAuth.getUid());
                hashMap8.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F022-angry.png?alt=media&token=c9fcfd56-d9f1-4ec9-a759-a05f298ed7b0");
                hashMap8.put("type", "sticker");
                hashMap8.put("timestamp", timestamp8);
                chatRef.child(timestamp8)
                        .setValue(hashMap8);
                break;
            case R.id.nine:
                bottomDialog.cancel();
                String timestamp9 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap9 = new HashMap<>();
                hashMap9.put("sender", mAuth.getUid());
                hashMap9.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F023-scared.png?alt=media&token=e69fc8bf-042b-41f1-990f-9f4aa4970a0f");
                hashMap9.put("type", "sticker");
                hashMap9.put("timestamp", timestamp9);
                chatRef.child(timestamp9)
                        .setValue(hashMap9);
                break;
            case R.id.ten:
                bottomDialog.cancel();
                String timestamp10 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap10 = new HashMap<>();
                hashMap10.put("sender", mAuth.getUid());
                hashMap10.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F024-confused.png?alt=media&token=0e09d6ed-ea5c-49de-b99d-bb86d03b8997");
                hashMap10.put("type", "sticker");
                hashMap10.put("timestamp", timestamp10);
                chatRef.child(timestamp10)
                        .setValue(hashMap10);
                break;
            case R.id.eleven:
                bottomDialog.cancel();
                String timestamp11 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap11 = new HashMap<>();
                hashMap11.put("sender", mAuth.getUid());
                hashMap11.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F025-worried.png?alt=media&token=561c18df-5b88-405c-8bfd-22dce2e50c48");
                hashMap11.put("type", "sticker");
                hashMap11.put("timestamp", timestamp11);
                chatRef.child(timestamp11)
                        .setValue(hashMap11);
                break;
            case R.id.twele:
                bottomDialog.cancel();
                String timestamp12 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap12 = new HashMap<>();
                hashMap12.put("sender", mAuth.getUid());
                hashMap12.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F026-zombie.png?alt=media&token=7c844f4f-58d8-46c6-8c57-e6ea7c34ccc8");
                hashMap12.put("type", "sticker");
                hashMap12.put("timestamp", timestamp12);
                chatRef.child(timestamp12)
                        .setValue(hashMap12);
                break;
            case R.id.thirteen:
                bottomDialog.cancel();
                String timestamp13 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap13 = new HashMap<>();
                hashMap13.put("sender", mAuth.getUid());
                hashMap13.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F027-happy.png?alt=media&token=82970788-7ea4-4fcf-a0be-7f8b649237f1");
                hashMap13.put("type", "sticker");
                hashMap13.put("timestamp", timestamp13);
                chatRef.child(timestamp13)
                        .setValue(hashMap13);
                break;
            case R.id.fourteen:
                bottomDialog.cancel();
                String timestamp14 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap14 = new HashMap<>();
                hashMap14.put("sender", mAuth.getUid());
                hashMap14.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F028-worried.png?alt=media&token=34319d65-5fcc-49de-8784-25cf7bccf6e2");
                hashMap14.put("type", "sticker");
                hashMap14.put("timestamp", timestamp14);
                chatRef.child(timestamp14)
                        .setValue(hashMap14);
                break;
            case R.id.fifteen:
                bottomDialog.cancel();
                String timestamp15 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap15 = new HashMap<>();
                hashMap15.put("sender", mAuth.getUid());
                hashMap15.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F029-love.png?alt=media&token=b484de4a-3415-4025-923e-2fa1d9e28967");
                hashMap15.put("type", "sticker");
                hashMap15.put("timestamp", timestamp15);
                chatRef.child(timestamp15)
                        .setValue(hashMap15);
                break;
            case R.id.sixteen:
                bottomDialog.cancel();
                String timestamp16 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap16 = new HashMap<>();
                hashMap16.put("sender", mAuth.getUid());
                hashMap16.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F030-amazed.png?alt=media&token=c6803253-a543-4a69-a27e-c956c3e3f8f4");
                hashMap16.put("type", "sticker");
                hashMap16.put("timestamp", timestamp16);
                chatRef.child(timestamp16)
                        .setValue(hashMap16);
                break;
            case R.id.seventeen:
                String timestamp17 = ""+System.currentTimeMillis();
                bottomDialog.cancel();
                HashMap<String, Object> hashMap17 = new HashMap<>();
                hashMap17.put("sender", mAuth.getUid());
                hashMap17.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F031-worried.png?alt=media&token=0abf13fc-6998-4deb-bb92-bd501b32f2cc");
                hashMap17.put("type", "sticker");
                hashMap17.put("timestamp", timestamp17);
                chatRef.child(timestamp17)
                        .setValue(hashMap17);
                break;
            case R.id.eightteen:
                String timestamp18 = ""+System.currentTimeMillis();
                bottomDialog.cancel();
                HashMap<String, Object> hashMap18 = new HashMap<>();
                hashMap18.put("sender", mAuth.getUid());
                hashMap18.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F032-crying.png?alt=media&token=53615f34-3d81-43d2-8a30-10a13ccd9753");
                hashMap18.put("type", "sticker");
                hashMap18.put("timestamp", timestamp18);
                chatRef.child(timestamp18)
                        .setValue(hashMap18);
                break;
            case R.id.noneteen:
                bottomDialog.cancel();
                String timestamp19 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap19 = new HashMap<>();
                hashMap19.put("sender", mAuth.getUid());
                hashMap19.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F033-angry.png?alt=media&token=4181bda1-019a-48d0-9112-f331a6fc4601");
                hashMap19.put("type", "sticker");
                hashMap19.put("timestamp", timestamp19);
                chatRef.child(timestamp19)
                        .setValue(hashMap19);
                break;
            case R.id.twentee:
                bottomDialog.cancel();
                String timestamp20 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap20 = new HashMap<>();
                hashMap20.put("sender", mAuth.getUid());
                hashMap20.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F034-love.png?alt=media&token=624297a0-676d-4b3a-87b3-ac7fce89150b");
                hashMap20.put("type", "sticker");
                hashMap20.put("timestamp", timestamp20);
                chatRef.child(timestamp20)
                        .setValue(hashMap20);
                break;
            case R.id.twentyone:
                bottomDialog.cancel();
                String timestamp21 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap21 = new HashMap<>();
                hashMap21.put("sender", mAuth.getUid());
                hashMap21.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F035-happy.png?alt=media&token=7ce04e1f-f746-46c2-944c-51a089b73354");
                hashMap21.put("type", "sticker");
                hashMap21.put("timestamp", timestamp21);
                chatRef.child(timestamp21)
                        .setValue(hashMap21);
                break;
            case R.id.twentytwo:
                bottomDialog.cancel();
                String timestamp22 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap22 = new HashMap<>();
                hashMap22.put("sender", mAuth.getUid());
                hashMap22.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F036-laughing.png?alt=media&token=c1e5bbda-a7de-484d-9c17-29ed5ac85bfd");
                hashMap22.put("type", "sticker");
                hashMap22.put("timestamp", timestamp22);
                chatRef.child(timestamp22)
                        .setValue(hashMap22);
                break;
            case R.id.twentythree:
                bottomDialog.cancel();
                String timestamp23 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap23 = new HashMap<>();
                hashMap23.put("sender", mAuth.getUid());
                hashMap23.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F037-evil.png?alt=media&token=d8f30643-8686-4eb8-83ca-1fbd778f13db");
                hashMap23.put("type", "sticker");
                hashMap23.put("timestamp", timestamp23);
                chatRef.child(timestamp23)
                        .setValue(hashMap23);
                break;
            case R.id.twentyfour:
                bottomDialog.cancel();
                String timestamp24 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap24 = new HashMap<>();
                hashMap24.put("sender", mAuth.getUid());
                hashMap24.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F038-heart%20eyes.png?alt=media&token=ee5fba96-8265-4042-b6ba-18f166876601");
                hashMap24.put("type", "sticker");
                hashMap24.put("timestamp", timestamp24);
                chatRef.child(timestamp24)
                        .setValue(hashMap24);
                break;
            case R.id.twentyfive:
                bottomDialog.cancel();
                String timestamp25 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap25 = new HashMap<>();
                hashMap25.put("sender", mAuth.getUid());
                hashMap25.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F039-sad.png?alt=media&token=dbe390cf-f943-42ed-b79a-acdb65a0d9d6");
                hashMap25.put("type", "sticker");
                hashMap25.put("timestamp", timestamp25);
                chatRef.child(timestamp25)
                        .setValue(hashMap25);
                break;
            case R.id.twentysix:
                bottomDialog.cancel();
                String timestamp26 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap26 = new HashMap<>();
                hashMap26.put("sender", mAuth.getUid());
                hashMap26.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F040-sad.png?alt=media&token=7e7b5c47-6620-40d0-b862-46793387ea03");
                hashMap26.put("type", "sticker");
                hashMap26.put("timestamp", timestamp26);
                chatRef.child(timestamp26)
                        .setValue(hashMap26);

                break;
            case R.id.twentyseven:
                bottomDialog.cancel();
                String timestamp27 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap27 = new HashMap<>();
                hashMap27.put("sender", mAuth.getUid());
                hashMap27.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F041-amazed.png?alt=media&token=f8c8366c-edbb-44f9-887d-a4c3aadc529e");
                hashMap27.put("type", "sticker");
                hashMap27.put("timestamp", timestamp27);
                chatRef.child(timestamp27)
                        .setValue(hashMap27);
                break;
            case R.id.twentyeight:
                bottomDialog.cancel();
                String timestamp28 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap28 = new HashMap<>();
                hashMap28.put("sender", mAuth.getUid());
                hashMap28.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F042-freak.png?alt=media&token=c9d40cf4-03a9-4229-a5e4-59fec2fbfd72");
                hashMap28.put("type", "sticker");
                hashMap28.put("timestamp", timestamp28);
                chatRef.child(timestamp28)
                        .setValue(hashMap28);
                break;
            case R.id.twentynine:
                bottomDialog.cancel();
                String timestamp29 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap29 = new HashMap<>();
                hashMap29.put("sender", mAuth.getUid());
                hashMap29.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F043-angry.png?alt=media&token=c1f65bf9-0e4d-4b02-add8-52ad057a590e");
                hashMap29.put("type", "sticker");
                hashMap29.put("timestamp", timestamp29);
                chatRef.child(timestamp29)
                        .setValue(hashMap29);
                break;
            case R.id.thirty:
                bottomDialog.cancel();
                String timestamp30 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap30 = new HashMap<>();
                hashMap30.put("sender", mAuth.getUid());
                hashMap30.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F044-happy.png?alt=media&token=4273eb51-3484-4440-bc2c-f8382072edce");
                hashMap30.put("type", "sticker");
                hashMap30.put("timestamp", timestamp30);
                chatRef.child(timestamp30)
                        .setValue(hashMap30);
                break;
            case R.id.thirtione:
                bottomDialog.cancel();
                String timestamp31 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap31 = new HashMap<>();
                hashMap31.put("sender", mAuth.getUid());
                hashMap31.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F045-sad.png?alt=media&token=3f7b373f-f9b5-45b3-acb9-10bba70a973f");
                hashMap31.put("type", "sticker");
                hashMap31.put("timestamp", timestamp31);
                chatRef.child(timestamp31)
                        .setValue(hashMap31);
                break;
            case R.id.thirtitwo:
                bottomDialog.cancel();
                String timestamp32 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap32 = new HashMap<>();
                hashMap32.put("sender", mAuth.getUid());
                hashMap32.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F046-goofy.png?alt=media&token=de735951-d101-49a6-bd7f-ff7724bc3740");
                hashMap32.put("type", "sticker");
                hashMap32.put("timestamp", timestamp32);
                chatRef.child(timestamp32)
                        .setValue(hashMap32);
                break;
            case R.id.thirtithree:
                bottomDialog.cancel();
                String timestamp33 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap33 = new HashMap<>();
                hashMap33.put("sender", mAuth.getUid());
                hashMap33.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F047-amazed.png?alt=media&token=2e5e6fe7-d5c8-4fd0-bd81-90e63d1fad5f");
                hashMap33.put("type", "sticker");
                hashMap33.put("timestamp", timestamp33);
                chatRef.child(timestamp33)
                        .setValue(hashMap33);
                break;
            case R.id.thirtifour:
                bottomDialog.cancel();
                String timestamp34 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap34 = new HashMap<>();
                hashMap34.put("sender", mAuth.getUid());
                hashMap34.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F048-happy.png?alt=media&token=7d452d6e-d726-4f98-98ca-d8c04e1055fa");
                hashMap34.put("type", "sticker");
                hashMap34.put("timestamp", timestamp34);
                chatRef.child(timestamp34)
                        .setValue(hashMap34);
                break;
            case R.id.thirtifive:
                bottomDialog.cancel();
                String timestamp35 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap35 = new HashMap<>();
                hashMap35.put("sender", mAuth.getUid());
                hashMap35.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F049-grumpy.png?alt=media&token=ab5ddb0a-4c5a-4fef-9c72-f260fdda35dd");
                hashMap35.put("type", "sticker");
                hashMap35.put("timestamp", timestamp35);
                chatRef.child(timestamp35)
                        .setValue(hashMap35);
                break;
            case R.id.thirtisix:
                bottomDialog.cancel();
                String timestamp36 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap36 = new HashMap<>();
                hashMap36.put("sender", mAuth.getUid());
                hashMap36.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2F050-shocked.png?alt=media&token=6ef5bef2-b594-4313-a206-d5b501b3fe6a");
                hashMap36.put("type", "sticker");
                hashMap36.put("timestamp", timestamp36);
                chatRef.child(timestamp36)
                        .setValue(hashMap36);
                break;
            case R.id.thirtiseven:
                bottomDialog.cancel();
                String timestamp37 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap37 = new HashMap<>();
                hashMap37.put("sender", mAuth.getUid());
                hashMap37.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2FExcited.png?alt=media&token=dd70bfc8-b9cc-43fc-b931-91ae13501f71");
                hashMap37.put("type", "sticker");
                hashMap37.put("timestamp", timestamp37);
                chatRef.child(timestamp37)
                        .setValue(hashMap37);
                break;
            case R.id.thirtieight:
                bottomDialog.cancel();
                String timestamp38 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap38 = new HashMap<>();
                hashMap38.put("sender", mAuth.getUid());
                hashMap38.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2Fangry.png?alt=media&token=1dcd409f-433f-40de-a9e3-567bcb41211d");
                hashMap38.put("type", "sticker");
                hashMap38.put("timestamp", timestamp38);
                chatRef.child(timestamp38)
                        .setValue(hashMap38);
                break;
            case R.id.thirtinine:
                bottomDialog.cancel();
                String timestamp39 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap39 = new HashMap<>();
                hashMap39.put("sender", mAuth.getUid());
                hashMap39.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2Fannoyed.png?alt=media&token=aaffdd5a-258a-40c2-951c-14e6ba51e3c4");
                hashMap39.put("type", "sticker");
                hashMap39.put("timestamp", timestamp39);
                chatRef.child(timestamp39)
                        .setValue(hashMap39);
                break;
            case R.id.fourty:
                bottomDialog.cancel();
                String timestamp40 = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap40 = new HashMap<>();
                hashMap40.put("sender", mAuth.getUid());
                hashMap40.put("msg", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/Stickers%2Fbored.png?alt=media&token=60f32f9d-7ef4-4e89-8fa8-b55ad68f7f83");
                hashMap40.put("type", "sticker");
                hashMap40.put("timestamp", timestamp40);
                chatRef.child(timestamp40)
                        .setValue(hashMap40);
                break;
        }
    }


}