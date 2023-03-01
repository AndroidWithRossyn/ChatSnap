package com.spacester.chatsnapsupdate.videoCall;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.Image;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.ChatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ai.deepar.ar.ARErrorType;
import ai.deepar.ar.AREventListener;
import ai.deepar.ar.DeepAR;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class VideoCallActivity extends PermissionsActivity implements AREventListener {

    private static final String TAG = "VideoCallActivity";
    private CameraGrabber cameraGrabber;
    private DeepAR deepAR;
    private GLSurfaceView surfaceView;
    private int defaultCameraDevice = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private DeepARRenderer renderer;
    private RtcEngine mRtcEngine;
    private boolean callInProgress;
    private int cameraDevice = defaultCameraDevice;

    private FrameLayout remoteViewContainer;
    private boolean mMuted;
    String name;

    ArrayList<String> masks;
    ArrayList<String> effects;
    ArrayList<String> filters;

    private int activeFilterType = 0;

    String hisId;

    private int currentMask=0;
    private int currentEffect=0;
    private int currentFilter=0;

    boolean everythingVisibility = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deepAR = new DeepAR(this);
        deepAR.setLicenseKey("2d6c8b8e8cfad8541e6cd7c27513bc5c2665c4f81b496e8a2462cd8a14bdc0b0376dd374ef5b8c59");
        deepAR.initialize(this, this);
        setContentView(R.layout.activity_video_call);
        callInProgress = false;
        remoteViewContainer = (FrameLayout) findViewById(R.id.remote_video_view_container);

        name = getIntent().getStringExtra("name");
        hisId = getIntent().getStringExtra("hisId");
        initializeFilters();

        final RelativeLayout everthing = findViewById(R.id.everthing);

        ImageView face= findViewById(R.id.face);
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (everythingVisibility){
                    everthing.setVisibility(View.GONE);
                    everythingVisibility = false;
                }else {
                    everthing.setVisibility(View.VISIBLE);
                    everythingVisibility = true;
                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("VideoCall").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("ended")){
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("hisId", hisId);
                    startActivity(intent);
                    finish();
                    Toast.makeText(VideoCallActivity.this, "Ended", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final RadioButton radioMasks = findViewById(R.id.masks);
        final RadioButton radioEffects = findViewById(R.id.effects);
        final RadioButton radioFilters = findViewById(R.id.filters);

        ImageButton previousMask = findViewById(R.id.previousMask);

        ImageButton nextMask = findViewById(R.id.nextMask);

        previousMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoPrevious();

            }
        });

        nextMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNext();
            }
        });

        radioMasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEffects.setChecked(false);
                radioFilters.setChecked(false);
                activeFilterType = 0;

            }
        });
        radioEffects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioMasks.setChecked(false);
                radioFilters.setChecked(false);
                activeFilterType = 1;
            }
        });
        radioFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEffects.setChecked(false);
                radioMasks.setChecked(false);
                activeFilterType = 2;
            }
        });

    }

    private String getFilterPath(String filterName) {
        if (filterName.equals("none")) {
            return null;
        }
        return "file:///android_asset/" + filterName;
    }


    private void gotoNext() {
        if (activeFilterType == 0) {
            currentMask = (currentMask + 1) % masks.size();
            deepAR.switchEffect("mask", getFilterPath(masks.get(currentMask)));
        } else if (activeFilterType == 1) {
            currentEffect = (currentEffect + 1) % effects.size();
            deepAR.switchEffect("effect", getFilterPath(effects.get(currentEffect)));
        } else if (activeFilterType == 2) {
            currentFilter = (currentFilter + 1) % filters.size();
            deepAR.switchEffect("filter", getFilterPath(filters.get(currentFilter)));
        }
    }

    private void gotoPrevious() {
        if (activeFilterType == 0) {
            currentMask = (currentMask - 1) % masks.size();
            deepAR.switchEffect("mask", getFilterPath(masks.get(currentMask)));
        } else if (activeFilterType == 1) {
            currentEffect = (currentEffect - 1) % effects.size();
            deepAR.switchEffect("effect", getFilterPath(effects.get(currentEffect)));
        } else if (activeFilterType == 2) {
            currentFilter = (currentFilter - 1) % filters.size();
            deepAR.switchEffect("filter", getFilterPath(filters.get(currentFilter)));
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        checkMultiplePermissions(
                Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH),
                "The app needs camera, external storage and record audio permissions",
                100,
                new PermissionsActivity.MultiplePermissionsCallback() {
                    @Override
                    public void onAllPermissionsGranted() {
                        setup();
                    }

                    @Override
                    public void onPermissionsDenied(List<String> deniedPermissions) {
                        Log.d("VideoCallActivity", "Permissions Denied!");
                    }
                });
    }

    void setup() {
        cameraGrabber = new CameraGrabber();
        cameraGrabber.initCamera(new CameraGrabberListener() {
            @Override
            public void onCameraInitialized() {
                cameraGrabber.setFrameReceiver(deepAR);
                cameraGrabber.startPreview();
            }

            @Override
            public void onCameraError(String errorMsg) {
                Log.e("Error", errorMsg);
            }
        });

        initializeEngine();
        setupVideoConfig();

        surfaceView = new GLSurfaceView(this);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8,8,8,8,16,0);
        renderer = new DeepARRenderer(deepAR, mRtcEngine);

        surfaceView.setEGLContextFactory(new DeepARRenderer.MyContextFactory(renderer));

        surfaceView.setRenderer(renderer);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        FrameLayout local = findViewById(R.id.localPreview);
        local.addView(surfaceView);


        final ImageView btn = findViewById(R.id.startCall);
        mRtcEngine.setExternalVideoSource(true, true, true);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callInProgress) {
                    callInProgress = false;
                    renderer.setCallInProgress(false);
                    mRtcEngine.leaveChannel();
                    onRemoteUserLeft();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("type", "ended");
                    FirebaseDatabase.getInstance().getReference().child("VideoCall").child(name).updateChildren(hashMap);

                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("hisId", hisId);
                    startActivity(intent);
                    finish();
                    Toast.makeText(VideoCallActivity.this, "Ended", Toast.LENGTH_SHORT).show();

                }
            }
        });

        ImageView flip = findViewById(R.id.flip);
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraDevice = cameraGrabber.getCurrCameraDevice() ==  Camera.CameraInfo.CAMERA_FACING_FRONT ?  Camera.CameraInfo.CAMERA_FACING_BACK :  Camera.CameraInfo.CAMERA_FACING_FRONT;
                cameraGrabber.changeCameraDevice(cameraDevice);
            }
        });

        ImageView mute = findViewById(R.id.mute);
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMuted = !mMuted;
                // Stops/Resumes sending the local audio stream.
                mRtcEngine.muteLocalAudioStream(mMuted);
            }
        });

            callInProgress = true;
            joinChannel();


    }

    void setRemoteViewWeight(float weight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) remoteViewContainer.getLayoutParams();
        params.weight = weight;
        remoteViewContainer.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (surfaceView != null) {
            surfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (surfaceView != null) {
            surfaceView.onPause();
        }
    }

    @Override
    protected void onStop() {
        cameraGrabber.setFrameReceiver(null);
        cameraGrabber.stopPreview();
        cameraGrabber.releaseCamera();
        cameraGrabber = null;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deepAR.release();
        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onWarning(int warn) {
            Log.e(TAG, "warning: " + warn);
        }

        @Override
        public void onError(int err) {
            Log.e(TAG, "error: " + err);
        }

        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "onJoinChannelSuccess");
                    renderer.setCallInProgress(true);
                }
            });
        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "onFirstRemoteVideoDecoded");
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "onUserOffline");
                    onRemoteUserLeft();
                }
            });
        }
    };

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), "48cde4b2c7d74abbb7c2c794e04b1f80", mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
    }

    private void setupVideoConfig() {
        mRtcEngine.enableVideo();

        mRtcEngine.setExternalVideoSource(true, true, true);

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                // Agora seems to work best with "Square" resolutions (Aspect Ratio 1:1)
                // At least when used in combination with DeepAR
                VideoEncoderConfiguration.VD_480x480,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));


    }


    private void joinChannel() {
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        mRtcEngine.joinChannel(null, name, "Extra Optional Data", 0);
    }

    private void setupRemoteVideo(int uid) {

        if (remoteViewContainer.getChildCount() >= 1) {
            return;
        }
        setRemoteViewWeight(1.f);

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        remoteViewContainer.addView(surfaceView);

        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        surfaceView.setTag(uid);
    }

    private void onRemoteUserLeft() {

        remoteViewContainer.removeAllViews();
        setRemoteViewWeight(0.f);

    }

    @Override
    public void screenshotTaken(Bitmap bitmap) {

    }

    @Override
    public void videoRecordingStarted() {

    }

    @Override
    public void videoRecordingFinished() {

    }

    @Override
    public void videoRecordingFailed() {

    }

    @Override
    public void videoRecordingPrepared() {

    }

    @Override
    public void shutdownFinished() {

    }

    @Override
    public void initialized() {
    }

    @Override
    public void faceVisibilityChanged(boolean b) {

    }

    @Override
    public void imageVisibilityChanged(String s, boolean b) {

    }

    @Override
    public void frameAvailable(Image image) {

    }

    @Override
    public void error(ARErrorType arErrorType, String s) {

    }

    @Override
    public void effectSwitched(String s) {

    }

    private void initializeFilters() {
        masks = new ArrayList<>();
        masks.add("none");
        masks.add("aviators");
        masks.add("bigmouth");
        masks.add("dalmatian");
        masks.add("flowers");
        masks.add("koala");
        masks.add("lion");
        masks.add("smallface");
        masks.add("teddycigar");
        masks.add("kanye");
        masks.add("tripleface");
        masks.add("sleepingmask");
        masks.add("fatify");
        masks.add("obama");
        masks.add("mudmask");
        masks.add("pug");
        masks.add("slash");
        masks.add("twistedface");
        masks.add("grumpycat");

        effects = new ArrayList<>();
        effects.add("none");
        effects.add("fire");
        effects.add("rain");
        effects.add("heart");
        effects.add("blizzard");

        filters = new ArrayList<>();
        filters.add("none");
        filters.add("filmcolorperfection");
        filters.add("tv80");
        filters.add("drawingmanga");
        filters.add("sepia");
        filters.add("bleachbypass");
    }


}
