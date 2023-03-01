package com.spacester.chatsnapsupdate.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.spacester.chatsnapsupdate.Adpref;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.faceFilters.FaceFilters;

import java.util.Objects;

@SuppressWarnings("ALL")
public class AddStoryActivity extends AppCompatActivity implements View.OnClickListener  {

    Button gallery, snap;
    ImageView back;
    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);
        back = findViewById(R.id.back);
        gallery = findViewById(R.id.gallery);
        snap = findViewById(R.id.snap);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Adpref adpref;
        adpref = new Adpref(Objects.requireNonNull(getApplicationContext()));
        if (adpref.loadAdsModeState()){
            mAdView.setVisibility(View.VISIBLE);

        }else {
            mAdView.setVisibility(View.GONE);
        }


        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        Intent intent = new Intent(AddStoryActivity.this, FaceFilters.class);
                        startActivity(intent);
                    }
                }
                else {
                    Intent intent = new Intent(AddStoryActivity.this, FaceFilters.class);
                    startActivity(intent);
                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        createBottomSheetDialog();
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          bottomSheetDialog.show();
            }
        });

    }
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //Handel Permission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(AddStoryActivity.this, "Storage permission Allowed",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(AddStoryActivity.this, "Storage permission is required",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Uri image_uri = Objects.requireNonNull(data).getData();
            Intent intent = new Intent(AddStoryActivity.this, ImageViewActivity.class);
            intent.putExtra("uri", Objects.requireNonNull(image_uri).toString());
            intent.putExtra("type", "img");
            intent.putExtra("angle", "0");
            startActivity(intent);
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri video_uri = data.getData();
            Intent intent = new Intent(AddStoryActivity.this, ImageViewActivity.class);
            intent.putExtra("uri", video_uri.toString());
            intent.putExtra("type", "video");
            intent.putExtra("angle", "0");
            startActivity(intent);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
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
        }
    }
}