package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.videoCall.VideoCallActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciver);

        final String his = getIntent().getStringExtra("hisId");
        final String id = getIntent().getStringExtra("id");

        final CircleImageView mPhoto = findViewById(R.id.circleImageView3);

        final TextView mName = findViewById(R.id.name);

        FirebaseDatabase.getInstance().getReference("Users").child(his).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String photo = snapshot.child("photo").getValue().toString();

                mName.setText(name);
                Picasso.get().load(photo).into(mPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageView ans = findViewById(R.id.ans);
        ans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("type", "ans");
                FirebaseDatabase.getInstance().getReference().child("VideoCall").child(id).updateChildren(hashMap);

                Intent intent = new Intent(ReceiverActivity.this, VideoCallActivity.class);
                intent.putExtra("name", id);
                intent.putExtra("hisId", his);
                startActivity(intent);
                finish();
            }
        });

        ImageView cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("type", "ended");
                FirebaseDatabase.getInstance().getReference().child("VideoCall").child(id).updateChildren(hashMap);

                Toast.makeText(ReceiverActivity.this, "Ended", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("hisId", his);
                startActivity(intent);
                finish();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("type", "ended");
                FirebaseDatabase.getInstance().getReference().child("VideoCall").child(id).updateChildren(hashMap);

                Toast.makeText(ReceiverActivity.this, "Ended", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("hisId", his);
                startActivity(intent);
                finish();
            }
        },40000);


    }
}