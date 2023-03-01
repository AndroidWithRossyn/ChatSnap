package com.spacester.chatsnapsupdate.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.spacester.chatsnapsupdate.photoeditor.EditImageActivity;
import com.spacester.chatsnapsupdate.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("ALL")
public class ImageViewActivity extends AppCompatActivity {

    ImageView back,edit,download,image;
    Button button;
    VideoView videoView;
    String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_view);
        back = findViewById(R.id.back);
        edit = findViewById(R.id.edit);
        download = findViewById(R.id.download);
        image = findViewById(R.id.image);
        button= findViewById(R.id.button);
        videoView = findViewById(R.id.video);

         uri = getIntent().getStringExtra("uri");
        final String type = getIntent().getStringExtra("type");

        if (Objects.requireNonNull(type).equals("img")){
            final Uri img = Uri.parse(uri);
            image.setImageURI(img);
            image.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            download.setVisibility(View.VISIBLE);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ImageViewActivity.this, EditImageActivity.class);
                    intent.putExtra("uri", img.toString());
                    startActivity(intent);
                }
            });
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Saving....",
                            Toast.LENGTH_LONG).show();
                    //to get the image from the ImageView (say iv)
                    BitmapDrawable draw = (BitmapDrawable) image.getDrawable();
                    Bitmap bitmap = draw.getBitmap();

                    FileOutputStream outStream = null;
                    //noinspection deprecation
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/ChatSnap");
                    dir.mkdirs();
                    String fileName = String.format("%d.jpg", System.currentTimeMillis());
                    File outFile = new File(dir, fileName);
                    try {
                        outStream = new FileOutputStream(outFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    try {
                        Objects.requireNonNull(outStream).flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        outStream.close();
                        Toast.makeText(getApplicationContext(), "Image Saved",
                                Toast.LENGTH_LONG).show();
                        download.setImageResource(R.drawable.ic_check);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        if (type.equals("video")){
            videoView.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            final Uri video_uri = Uri.parse(uri);
            videoView.setVideoURI(video_uri);
            edit.setVisibility(View.GONE);

            videoView.start();

            MediaController mediaController = new MediaController(ImageViewActivity.this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });

            download.setVisibility(View.GONE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageViewActivity.this, SendActivity.class);
                intent.putExtra("uri", uri.toString());
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
}