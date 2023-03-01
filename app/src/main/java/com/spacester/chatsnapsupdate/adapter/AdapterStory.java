package com.spacester.chatsnapsupdate.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.model.ModelStory;
import com.spacester.chatsnapsupdate.model.ModelUser;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.AddStoryActivity;
import com.spacester.chatsnapsupdate.user.ViewStoryActivity;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class AdapterStory extends RecyclerView.Adapter<AdapterStory.ViewHolder> {

    private final Context context;
    final List<ModelStory> modelStories;

    public AdapterStory(Context context, List<ModelStory> modelStories) {
        this.context = context;
        this.modelStories = modelStories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
     if (i == 0){
         View view = LayoutInflater.from(context).inflate(R.layout.add_story, parent, false);
         return new ViewHolder(view);
     }else {
         View view = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false);
         return new ViewHolder(view);
     }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final ModelStory story = modelStories.get(position);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userInfo(viewHolder, story.getUserid(), position);
        if (viewHolder.getAdapterPosition() != 0){
            seenStory(viewHolder, story.getUserid());

        }
        if (viewHolder.getAdapterPosition() == 0){
            myStory(viewHolder.addstory_text, viewHolder.story_plus, false);

        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.getAdapterPosition() == 0){
                    myStory(viewHolder.addstory_text, viewHolder.story_plus, true);

                }else {
                    Intent intent = new Intent(context, ViewStoryActivity.class);
                 intent.putExtra("userid", story.getUserid());
               context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelStories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public final ImageView story_photo;
        public final ImageView story_plus;
        public final ImageView story_photo_seen;
        public final TextView story_username;
        public final TextView addstory_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            story_photo = itemView.findViewById(R.id.story_photo);
            story_plus = itemView.findViewById(R.id.story_plus);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            story_username = itemView.findViewById(R.id.story_username);
            addstory_text = itemView.findViewById(R.id.addstory_text);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }
        return 1;
    }

    private void userInfo (final ViewHolder viewHolder, String userId, final int pos){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser = snapshot.getValue(ModelUser.class);
                Glide.with(context).load(Objects.requireNonNull(modelUser).getPhoto()).centerCrop().into(viewHolder.story_photo);
                if (pos != 0){
                    Glide.with(context).load(modelUser.getPhoto()).centerCrop().into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(modelUser.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void myStory(final TextView textView, final ImageView imageView, final boolean click){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelStory story = snapshot1.getValue(ModelStory.class);
                    if (timecurrent > Objects.requireNonNull(story).getTimestart() && timecurrent < story.getTimeend()){
                        count++;
                    }
                }

                if (click){
                    if (count > 0){
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, ViewStoryActivity.class);
                                intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context , AddStoryActivity.class);
                               context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }else {
                     Intent intent = new Intent(context , AddStoryActivity.class);
                      context.startActivity(intent);
                    }

                }else {
                 if (count > 0){
                     textView.setText("My Story");
                     imageView.setVisibility(View.GONE);
                 }else {
                     textView.setText("Add Story");
                     imageView.setVisibility(View.VISIBLE);
                 }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void seenStory(final ViewHolder viewHolder, String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
           if (!snapshot1.child("views").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists() && System.currentTimeMillis() < Objects.requireNonNull(snapshot1.getValue(ModelStory.class)).getTimeend()){
               i++;
           }
                }
                if (i > 0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                }else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
