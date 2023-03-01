package com.spacester.chatsnapsupdate.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.spacester.chatsnapsupdate.model.ModelStory;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.notification.Data;
import com.spacester.chatsnapsupdate.notification.Sender;
import com.spacester.chatsnapsupdate.notification.Token;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

@SuppressWarnings("SameParameterValue")
public class ViewStoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long pressTime = 0L;
    final long limit = 500L;

    StoriesProgressView storiesProgressView;
    ImageView imageView,imageView2;
    TextView textView;
    CircleImageView pic;
    TextView username;

    LinearLayout r_seen;
    TextView seen_number;
    ImageView story_delete;
    List<String> images;
    List<String> storyids;
    String userid;

    EditText sendMessage;

    private RequestQueue requestQueue;

    private boolean notify = false;

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener(){
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_story_view);
        userid = getIntent().getStringExtra("userid");

        getStories(userid);
        userInfo(userid);

        r_seen = findViewById(R.id.r_seen);
        seen_number = findViewById(R.id.seen_number);
        story_delete = findViewById(R.id.story_delete);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.text);
        View reverse =  findViewById(R.id.reverse);
        View skip =  findViewById(R.id.skip);
        storiesProgressView =  findViewById(R.id.stories);
        pic =  findViewById(R.id.pic);
        username =  findViewById(R.id.username);

        r_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        sendMessage = findViewById(R.id.sendMessage);
        imageView2 = findViewById(R.id.imageView2);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = sendMessage.getText().toString();
                if (msg.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Type Something", Toast.LENGTH_LONG).show();
                }else {
                    notify = true;
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    hashMap.put("receiver", userid);
                    hashMap.put("msg", msg);
                    hashMap.put("isSeen", false);
                    hashMap.put("timestamp", images.get(counter));
                    hashMap.put("type", "story");
                    databaseReference1.child("Chats").push().setValue(hashMap);

                    final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(userid);
                    chatRef1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                chatRef1.child("id").setValue(userid);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(userid)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    chatRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                chatRef2.child("id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    Toast.makeText(getApplicationContext(), "Message sent",
                            Toast.LENGTH_LONG).show();

                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    dataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ModelUser user = dataSnapshot.getValue(ModelUser.class);
                            if (notify){
                                sendNotification(userid, Objects.requireNonNull(user).getName(), "Sent a message");

                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        ConstraintLayout message = findViewById(R.id.message);

        sendMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                storiesProgressView.pause();


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        r_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);

        if (userid.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
            r_seen.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);
            message.setVisibility(View.INVISIBLE);
        }
        r_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewStoryActivity.this, StoryViewerActivity.class);
                intent.putExtra("id",userid);
                intent.putExtra("storyid",storyids.get(counter));
                startActivity(intent);
            }
        });

        story_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                        .child(userid).child(storyids.get(counter));
                reference.removeValue();
            }
        });

        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);


    }

    private void sendNotification(final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), name + " : " + message, "New Message", hisId, R.drawable.logo);
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

    @Override
    public void onNext() {
        Glide.with(getApplicationContext()).load(images.get(++counter)).into(imageView);
        addView(storyids.get(counter));
        seenNumber(storyids.get(counter));
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        Glide.with(getApplicationContext()).load(images.get(--counter)).into(imageView);
        seenNumber(storyids.get(counter));
    }

    @Override
    public void onComplete() {
        finish();

    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }


    private void getStories(String userid){
        images = new ArrayList<>();

        storyids = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                images.clear();
                storyids.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelStory modelStory = snapshot1.getValue(ModelStory.class);
                    long timecurrent = System.currentTimeMillis();
                    if (timecurrent > Objects.requireNonNull(modelStory).getTimestart() && timecurrent < modelStory.getTimeend()){
                        images.add(modelStory.imageUri);

                        storyids.add(modelStory.storyid);
                    }
                }
                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(ViewStoryActivity.this);
                storiesProgressView.startStories(counter);
                Glide.with(getApplicationContext()).load(images.get(counter)).into(imageView);
                addView(storyids.get(counter));
                seenNumber(storyids.get(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void userInfo(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final ModelUser modelUser = snapshot.getValue(ModelUser.class);
                Glide.with(getApplicationContext()).load(Objects.requireNonNull(modelUser).getPhoto()).into(pic);
                username.setText(modelUser.getUsername());
                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ViewStoryActivity.this, UserProfileActivity.class);
                        intent.putExtra("hisId", modelUser.getId());
                        startActivity(intent);
                    }
                });
                pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ViewStoryActivity.this, UserProfileActivity.class);
                        intent.putExtra("hisId", modelUser.getId());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addView(String storyid){
        FirebaseDatabase.getInstance().getReference("Story").child(userid)
                .child(storyid).child("views").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(true);
    }
    private void seenNumber(String storyid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid).child(storyid).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                seen_number.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}