package com.spacester.chatsnapsupdate.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
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

@SuppressWarnings("ALL")
public class EditGroupActivity extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout name_layout, username_layout, delete_layout;
    ImageView edit, settings, menu;
    RoundedCornerImageView profile_image;
    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;

    static EditGroupActivity INSTANCE;

    String dbImage;
    private Uri image_uri;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    String GroupId,myGroupRole;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INSTANCE=this;
        setContentView(R.layout.activity_edit_group);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Intent intent = getIntent();
        GroupId = intent.getStringExtra("groupId");

        name_layout = findViewById(R.id.viewstories);
        username_layout = findViewById(R.id.myfriend);
        delete_layout = findViewById(R.id.delete);
        edit = findViewById(R.id.edit);
        menu = findViewById(R.id.settings);
        settings = findViewById(R.id.back);
        profile_image = findViewById(R.id.roundedImageView);

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


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        createBottomSheetDialog();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(GroupId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dbImage = Objects.requireNonNull(dataSnapshot.child("gIcon").getValue()).toString();
                try {
                    Picasso.get().load(dbImage).into(profile_image);
                }
                catch (Exception e ){
                    Picasso.get().load(R.drawable.avatar).into(profile_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


        name_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gname gname = new Gname();
                gname.show(getSupportFragmentManager(), "name");
            }
        });

        username_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddMembersActivity.class);
                intent.putExtra("groupId", GroupId);
                startActivity(intent);

            }
        });

        delete_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Groups");
                ref2.child(GroupId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(getApplicationContext(), CreateGroupActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "You deleted the group",
                                        Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePathName = "group_profile_images/" + ""+GroupId;
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
                storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadImageUri = uriTask.getResult();
                        if (uriTask.isSuccessful()){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("gIcon", ""+downloadImageUri);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                            reference.child(GroupId).updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            menu.setVisibility(View.INVISIBLE);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            menu.setVisibility(View.INVISIBLE);

                                        }
                                    });
                        }
                    }
                });

            }
        });

        loadMyGroupRole();
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
                                delete_layout.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static EditGroupActivity getActivityInstance()
    {
        return INSTANCE;
    }

    public String getGroupId()
    {
        return this.GroupId;
    }

    private void createBottomSheetDialog() {
        if (bottomSheetDialog == null){
            View view = LayoutInflater.from(this).inflate(R.layout.edit_bottom_sheet, null);
            constraintLayout3 = view.findViewById(R.id.constraintLayout3);
            delete = view.findViewById(R.id.delete);
            constraintLayout3.setOnClickListener(this);
            delete.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
    }
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                    Toast.makeText(EditGroupActivity.this, "Storage permission Allowed",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(EditGroupActivity.this, "Storage permission is required",
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
                        hashMap.put("photo", "https://firebasestorage.googleapis.com/v0/b/chatsnap-2b74b.appspot.com/o/group.jpg?alt=media&token=07e29b7f-99b6-4a8d-b0a0-70c5bffc5648");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                        reference.child(GroupId).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        menu.setVisibility(View.INVISIBLE);
                                        bottomSheetDialog.cancel();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        menu.setVisibility(View.INVISIBLE);
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