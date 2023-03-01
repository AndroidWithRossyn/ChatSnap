package com.spacester.chatsnapsupdate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import com.squareup.picasso.Picasso;

@SuppressWarnings("ALL")
public class MediaView extends AppCompatActivity {

    ImageView image;
    VideoView vine;
    String type,uri;
    @SuppressWarnings("Convert2Lambda")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_view);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        uri = intent.getStringExtra("uri");

        image = findViewById(R.id.image);
        vine = findViewById(R.id.videoView);


        if (type.equals("image")){
            image.setVisibility(View.VISIBLE);
           try {
               Picasso.get().load(uri).into(image);
           }catch (Exception ignored){

           }
        }
        if (type.equals("video")){
            image.setVisibility(View.GONE);
            vine.setVisibility(View.VISIBLE);

            Uri vineUri = Uri.parse(uri);
            vine.setVideoURI(vineUri);
            vine.start();

            MediaController mediaController = new MediaController(MediaView.this);
            mediaController.setAnchorView(vine);
            vine.setMediaController(mediaController);

            //noinspection Convert2Lambda
            vine.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });

        }


    }

}