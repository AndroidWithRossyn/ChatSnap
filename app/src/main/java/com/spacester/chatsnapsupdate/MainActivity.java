package com.spacester.chatsnapsupdate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.spacester.chatsnapsupdate.adapter.MainPagerAdapter;
import com.spacester.chatsnapsupdate.notification.Token;
import com.spacester.chatsnapsupdate.user.ReceiverActivity;
import com.spacester.chatsnapsupdate.view.TabView;

import java.io.IOException;
import java.util.Objects;

@SuppressWarnings({"ALL", "deprecation"})
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private FirebaseAuth mAuth;
    private String userId;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    final int CAMERA_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //camera
        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        //Permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        mAuth = FirebaseAuth.getInstance();
        final View background = findViewById(R.id.am_bg_view);
        ViewPager viewPager = findViewById(R.id.am_view_pager);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabView tabView = findViewById(R.id.am_tabView);
        tabView.setUpWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        final int colorBlue = ContextCompat.getColor(this, R.color.blue);
        final int colorPurple = ContextCompat.getColor(this, R.color.purple);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0){
                    background.setBackgroundColor(colorBlue);
                    background.setAlpha(1 - positionOffset);
                }else if (position == 1){
                    background.setBackgroundColor(colorPurple);
                    background.setAlpha(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

       Query query = FirebaseDatabase.getInstance().getReference().child("VideoCall").orderByChild("revicer").equalTo(mAuth.getCurrentUser().getUid());
       query.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   for (DataSnapshot db : snapshot.getChildren()){
                       String id = db.child("id").getValue().toString();
                       String his = db.child("caller").getValue().toString();
                       String type = db.child("type").getValue().toString();
                       if (type.equals("calling")){
                           Intent intent = new Intent(getApplicationContext(), ReceiverActivity.class);
                           intent.putExtra("id", id);
                           intent.putExtra("hisId", his);
                           startActivity(intent);
                       }

                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }
    @Override
    public void onStart() {
        super.onStart();
            userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            updateToken(FirebaseInstanceId.getInstance().getToken());
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    if (username.isEmpty()){

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        Camera.Parameters parameters;
        parameters = camera.getParameters();
        camera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                surfaceHolder.addCallback(this);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                restartApp();
            } else {
                Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void restartApp() {
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        Objects.requireNonNull(i).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(userId).setValue(mToken);
    }
}