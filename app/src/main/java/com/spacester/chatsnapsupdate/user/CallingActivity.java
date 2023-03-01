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

public class CallingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        String name = getIntent().getStringExtra("name");
        final String id = getIntent().getStringExtra("id");
        final String dp = getIntent().getStringExtra("dp");
        final String hisId = getIntent().getStringExtra("hisId");

        CircleImageView mPhoto = findViewById(R.id.circleImageView3);
        Picasso.get().load(dp).into(mPhoto);

        TextView mName = findViewById(R.id.name);
        mName.setText(name);

        FirebaseDatabase.getInstance().getReference().child("VideoCall").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("ended")){
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("hisId", hisId);
                    startActivity(intent);
                    finish();
                    Toast.makeText(CallingActivity.this, "Declined", Toast.LENGTH_SHORT).show();
                }else if (type.equals("ans")){
                    Intent intent = new Intent(CallingActivity.this, VideoCallActivity.class);
                    intent.putExtra("name", id);
                    intent.putExtra("hisId", hisId);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageView cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("type", "ended");
                FirebaseDatabase.getInstance().getReference().child("VideoCall").child(id).updateChildren(hashMap);

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("hisId", hisId);
                startActivity(intent);
                finish();
                Toast.makeText(CallingActivity.this, "Ended", Toast.LENGTH_SHORT).show();

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("hisId", hisId);
                startActivity(intent);
                finish();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("type", "ended");
                FirebaseDatabase.getInstance().getReference().child("VideoCall").child(id).updateChildren(hashMap);


                Toast.makeText(CallingActivity.this, "Not answered", Toast.LENGTH_SHORT).show();
            }
        },40000);

    }
}