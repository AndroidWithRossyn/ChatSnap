package com.spacester.chatsnapsupdate.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ALL")
public class Gname extends BottomSheetDialogFragment {

    EditText mName;
    ImageView settings,menu;
    private DatabaseReference mDatabase;
    String GroupId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_name, container, false);
        mName = v.findViewById(R.id.name);
        settings = v.findViewById(R.id.settings);
        menu = v.findViewById(R.id.menu);

        GroupId= EditGroupActivity.getActivityInstance().getGroupId();

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(GroupId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("gName").getValue()).toString();
                mName.setText(name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = mName.getText().toString().trim();
                if(TextUtils.isEmpty(name)) {

                } else {

                    Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Groups").orderByChild("gName").equalTo(name);
                    usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            addUsername(name);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        return v;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addUsername(String name) {
        Map hashMap = new HashMap();
        hashMap.put("gName", name);
        mDatabase.updateChildren(hashMap);

    }


}