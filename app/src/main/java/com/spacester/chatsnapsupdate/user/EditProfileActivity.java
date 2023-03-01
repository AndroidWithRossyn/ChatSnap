package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.comix.rounded.RoundedCornerImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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
import com.spacester.chatsnapsupdate.Adpref;
import com.spacester.chatsnapsupdate.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("StatementWithEmptyBody")
public class EditProfileActivity extends AppCompatActivity  implements View.OnClickListener  {

    private FirebaseAuth mAuth;

    ConstraintLayout name_layout, username_layout;

    String dbImage;

    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;

    ImageView edit, settings, menu;
    RoundedCornerImageView profile_image;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        name_layout = findViewById(R.id.viewstories);
        username_layout = findViewById(R.id.myfriend);

        name_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name name = new Name();
                name.show(getSupportFragmentManager(), "name");
            }
        });

        username_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username username = new username();
                username.show(getSupportFragmentManager(), "username");
            }
        });


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


        edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });
        settings = findViewById(R.id.back);
        menu = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        profile_image = findViewById(R.id.roundedImageView);

        createBottomSheetDialog();

        //display
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dbImage = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
                try {
                    Picasso.get().load(dbImage).into(profile_image);
                }
                catch (Exception e ){
                    Picasso.get().load(R.drawable.avatar).into(profile_image);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePathName = "profile_images/" + ""+mAuth.getUid();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
                storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadImageUri = uriTask.getResult();
                        if (uriTask.isSuccessful()){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("photo", ""+downloadImageUri);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(Objects.requireNonNull(mAuth.getUid())).updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(EditProfileActivity.this, "Profile photo updated",
                                                    Toast.LENGTH_LONG).show();
                                            menu.setVisibility(View.INVISIBLE);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditProfileActivity.this, e.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                            menu.setVisibility(View.INVISIBLE);
                                        }
                                    });

                        }
                    }
                });
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
        //noinspection SwitchStatementWithTooFewBranches
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                    Toast.makeText(EditProfileActivity.this, "Storage permission Allowed",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(EditProfileActivity.this, "Storage permission is required",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        bottomSheetDialog.cancel();
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
             image_uri = Objects.requireNonNull(data).getData();
            Picasso.get().load(image_uri).into(profile_image);
            menu.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createBottomSheetDialog(){
        if (bottomSheetDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.edit_bottom_sheet, null);
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
        switch (v.getId()){
            case R.id.constraintLayout3:
                //Check Permission
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


                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(dbImage);
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //ProfileSet
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("photo", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/avatar.png?alt=media&token=53a1d14a-875a-4ab0-ba34-f4c611a02e65");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(Objects.requireNonNull(mAuth.getUid())).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        menu.setVisibility(View.INVISIBLE);
                                        Toast.makeText(EditProfileActivity.this, "Profile photo deleted",
                                                Toast.LENGTH_LONG).show();
                                        bottomSheetDialog.cancel();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        menu.setVisibility(View.INVISIBLE);
                                        Toast.makeText(EditProfileActivity.this, e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                        bottomSheetDialog.cancel();
                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });



                break;
        }
    }
    }
