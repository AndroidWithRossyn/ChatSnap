package com.spacester.chatsnapsupdate.faceFilters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.user.ImageViewActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import ai.deepar.ar.ARErrorType;
import ai.deepar.ar.AREventListener;
import ai.deepar.ar.CameraResolutionPreset;
import ai.deepar.ar.DeepAR;

@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal", "deprecation", "NullableProblems", "RedundantSuppression"})
public class FaceFilters extends AppCompatActivity implements SurfaceHolder.Callback, AREventListener {

    private CameraGrabber cameraGrabber;
    private int defaultCameraDevice = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int cameraDevice = defaultCameraDevice;
    private DeepAR deepAR;

    private int currentMask=0;
    private int currentEffect=0;
    private int currentFilter=0;

    private int screenOrientation;

    private boolean isFlashOn;

    ArrayList<String> masks;
    ArrayList<String> effects;
    ArrayList<String> filters;

    Camera.Parameters parameters;


    private int activeFilterType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_face);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO },
                    1);
        } else {
            // Permission has already been granted
            initialize();
        }
        setupCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupCamera();
        cameraDevice = cameraGrabber.getCurrCameraDevice() ==  Camera.CameraInfo.CAMERA_FACING_FRONT ?  Camera.CameraInfo.CAMERA_FACING_BACK :  Camera.CameraInfo.CAMERA_FACING_FRONT;
        cameraGrabber.changeCameraDevice(cameraDevice);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return; // no permission
                }
                initialize();
            }
        }
    }

    private void initialize() {
        setContentView(R.layout.activity_face);
        initializeDeepAR();
        initializeFilters();
        initalizeViews();
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

    private void initalizeViews() {

        ImageButton back =findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageButton previousMask = findViewById(R.id.previousMask);

        ImageButton nextMask = findViewById(R.id.nextMask);

        ImageButton flash = findViewById(R.id.flash);

        final RadioButton radioMasks = findViewById(R.id.masks);
        final RadioButton radioEffects = findViewById(R.id.effects);
        final RadioButton radioFilters = findViewById(R.id.filters);

        SurfaceView arView = findViewById(R.id.surface);

        arView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deepAR.onClick();
            }
        });

        arView.getHolder().addCallback(this);

        // Surface might already be initialized, so we force the call to onSurfaceChanged
        arView.setVisibility(View.GONE);
        arView.setVisibility(View.VISIBLE);

        ImageButton screenshotBtn = findViewById(R.id.recordButton);
        screenshotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deepAR.takeScreenshot();
            }
        });

        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn){
                    turnOffFlash();
                }else {
                    turnOnFlash();
                }
            }
        });


        ImageButton switchCamera = findViewById(R.id.switchCamera);
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraDevice = cameraGrabber.getCurrCameraDevice() ==  Camera.CameraInfo.CAMERA_FACING_FRONT ?  Camera.CameraInfo.CAMERA_FACING_BACK :  Camera.CameraInfo.CAMERA_FACING_FRONT;
                cameraGrabber.changeCameraDevice(cameraDevice);
            }
        });

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
    /*
            get interface orientation from
            https://stackoverflow.com/questions/10380989/how-do-i-get-the-current-orientation-activityinfo-screen-orientation-of-an-a/10383164
         */
    private int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    //noinspection DuplicateBranchesInSwitch,DuplicateBranchesInSwitch
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    //noinspection DuplicateBranchesInSwitch,DuplicateBranchesInSwitch
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }
    private void initializeDeepAR() {
        deepAR = new DeepAR(this);
        deepAR.setLicenseKey("4ccafc911a9194f89c2f008bf2f31554890d85e6fb8196c83f75727038f4c284a5278a82c906c768");
        deepAR.initialize(this, this);
        setupCamera();
    }

    private void setupCamera() {
        cameraGrabber = new CameraGrabber(cameraDevice);
        screenOrientation = getScreenOrientation();

        switch (screenOrientation) {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                cameraGrabber.setScreenOrientation(90);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                cameraGrabber.setScreenOrientation(270);
                break;
            default:
                cameraGrabber.setScreenOrientation(0);
                break;
        }

        // Available 1080p, 720p and 480p resolutions
        cameraGrabber.setResolutionPreset(CameraResolutionPreset.P1280x720);

        final Activity context = this;
        cameraGrabber.initCamera(new CameraGrabberListener() {
            @Override
            public void onCameraInitialized() {
                cameraGrabber.setFrameReceiver(deepAR);
                cameraGrabber.startPreview();
            }

            @Override
            public void onCameraError(String errorMsg) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Camera error");
                builder.setMessage(errorMsg);
                builder.setCancelable(true);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
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
    protected void onStop() {
        super.onStop();
        if (cameraGrabber == null) {
            return;
        }
        cameraGrabber.setFrameReceiver(null);
        cameraGrabber.stopPreview();
        cameraGrabber.releaseCamera();
        cameraGrabber = null;
    }

    private void turnOnFlash() {
        if (!isFlashOn){
            if (cameraGrabber.getCamera() == null  || cameraGrabber.getCamera().getParameters() == null){
                return;
            }
            isFlashOn = true;
            parameters = cameraGrabber.getCamera().getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cameraGrabber.getCamera().setParameters(parameters);
            cameraGrabber.startPreview();
        }
    }

    private void turnOffFlash() {
        if (isFlashOn){
            if (cameraGrabber.getCamera() == null  || cameraGrabber.getCamera().getParameters() == null){
                return;
            }
            isFlashOn = false;
            parameters = cameraGrabber.getCamera().getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cameraGrabber.getCamera().setParameters(parameters);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deepAR == null) {
            return;
        }
        deepAR.setAREventListener(null);
        deepAR.release();
        deepAR = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If we are using on screen rendering we have to set surface view where DeepAR will render
        deepAR.setRenderSurface(holder.getSurface(), width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (deepAR != null) {
            deepAR.setRenderSurface(null, 0, 0);
        }
    }

    @Override
    public void screenshotTaken(Bitmap bitmap) {
        CharSequence now = DateFormat.format("yyyy_MM_dd_hh_mm_ss", new Date());
        try {
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/DeepAR_" + now + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            MediaScannerConnection.scanFile(FaceFilters.this, new String[]{imageFile.toString()}, null, null);
            Toast.makeText(FaceFilters.this, "Image saved", Toast.LENGTH_SHORT).show();

            Uri uri = Uri.fromFile(imageFile);

            Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
            intent.putExtra("uri", uri.toString());
            intent.putExtra("type", "img");
            startActivity(intent);


        } catch (Throwable e) {
            e.printStackTrace();
        }

    }


    @Override
    public void videoRecordingStarted() { }

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
}
