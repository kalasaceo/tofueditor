package com.daasuu.camerarecorder;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.daasuu.camerarecorder.videoTrimmer.utils.FileUtils;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.kalasa.library.R;

import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daasuu.camerarecorder.videoTrimmer.HgLVideoTrimmer;
import com.daasuu.camerarecorder.videoTrimmer.interfaces.OnHgLVideoListener;
import com.daasuu.camerarecorder.videoTrimmer.interfaces.OnTrimVideoListener;
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.model.ColorSwatch;
import com.simform.videooperations.Common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class TrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnHgLVideoListener {
    private static final int ADD_TEXT_REQUEST_CODE = 71;
    private static final int ADD_STICKER_REQUEST_CODE = 72;
    private static final int ADD_EXTRACT_AUDIO_REQUEST_CODE = 77;
    private static final int ADD_FF_VIDEO_REQUEST_CODE = 79;
    private static final int ADD_REVERSE_REQUEST_CODE = 76;
    private static final int ADD_BACK_MUSIC = 74;
    private static final int ADD_GIFF_BOX_CODE = 89;
    private static final int ADD_VIDEO_WITH_GIF_REQUEST_CODE = 31;
    private String g_storypath="empty";
    private MyImageView thisImage=null;
    private Boolean islocal=false;
    private String thiscolorHEX = "#000000";
    private String gifH = "empty";
    private String gifW = "empty";
    private RelativeLayout frameLayout;
    private boolean bgif = false;
    private HgLVideoTrimmer mVideoTrimmer;
    private String v_backmusic="";
    private String backmusic_path="empty";
    private boolean isbackMusic = false;
    private boolean bisText = false;
    private String GifPath="empty";
    private static final int SELECT_MUSIC= 777;
    private TextView edit_text_on_video=null;
    private ProgressDialog mProgressDialog;
    MediaPlayer mediaPlayer;
    private int max_width=200;
    private int thistime=10;
    private int max_height=200;
    MediaPlayer player = new MediaPlayer();
    String thisLocalVideopath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);
        findViewById(R.id.edit_text_on_video);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        edit_text_on_video=findViewById(R.id.edit_text_on_video);
        Intent extraIntent = getIntent();
        String path = "";
        if (extraIntent != null) {
            thistime = Integer.parseInt(extraIntent.getStringExtra("PassedVideoTime"));
            String thisdata=extraIntent.getStringExtra("PassedVideoPath").toString();
            thisLocalVideopath=thisdata.toString();
            if (thisdata != null) {
            }
        }
        findViewById(R.id.btnExtractAudio)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //ExtractAudioFUN();
                            }
                        }
                );
        findViewById(R.id.btnMotion)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MotionFUN();
                            }
                        }
                );
        findViewById(R.id.btnCombineVideos)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //CombineVideosFUN();
                            }
                        }
                );
        findViewById(R.id.audio3)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                findViewById(R.id.edit_text_on_video).setVisibility(View.VISIBLE);
                            }
                        }
                );
        findViewById(R.id.edit_text_add)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String getstr=edit_text_on_video.getText().toString();
                                if(bisText= !Objects.equals(getstr, "Enjoy your Stories"))
                                {
                                    AddTextOnVideoFUN();
                                }
                                else if(isbackMusic){
                                    DownloadBackMusicOnVideoFUN();
                                }
                                else if(bgif)
                                {
                                    addgif(thisLocalVideopath);
                                }
                                else
                                    {

                                }
                            }
                        }
                );
        findViewById(R.id.audio5)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //ImageToVideoFUN();
                            }
                        }
                );
        findViewById(R.id.btCancel)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onCancelClicked();
                            }
                        }
                );

        findViewById(R.id.btnAddWaterMarkOnVideo)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                OpenGiffBox();
                            }
                        }
                );
        findViewById(R.id.btnReverseVideo)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ReverseVideoFUN();
                            }
                        }
                );
        findViewById(R.id.btnCombineImageVideo)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //CombineImagesFUN();
                            }
                        }
                );
        findViewById(R.id.btnMergeImageAndAudio)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //MergeImageAndAudioFUN();
                            }
                        }
                );
        findViewById(R.id.btnMergeVideoAndAudio)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MergeVideoAndAudioFUN();
                            }
                        }
                );
        findViewById(R.id.audio1)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //CutVideoFUN();
                            }
                        }
                );
        findViewById(R.id.btnVideoConvertIntoGIF)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //VideoConvertIntoGIFFUN();
                            }
                        }
                );
        findViewById(R.id.btnRemoveAudioFromVideo)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //RemoveAudioFromVideoFUN();
                            }
                        }
                );
        findViewById(R.id.btnVideoRotateFlip)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //VideoRotateFlipFUN();
                            }
                        }
                );
        findViewById(R.id.music1)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-0933b34a-22d1-4958-9878-b568c2d6545f");
                            }
                        }
                );
        findViewById(R.id.music2)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-85138d38-40b6-419d-bdd9-a36047653cbc");
                            }
                        }
                );
        findViewById(R.id.btn_musicin)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent_upload = new Intent();
                                intent_upload.setType("audio/*");
                                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent_upload,SELECT_MUSIC);
                            }
                        }
                );
        findViewById(R.id.music3)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-1273652f-cf79-49c2-b569-cadfb3df8bdd");
                            }
                        }
                );
        findViewById(R.id.music4)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-492fb18e-9d15-4af6-9893-15d9aa746feb");
                            }
                        }
                );
        findViewById(R.id.music5)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-66473f33-539a-485e-9e6b-daa2611960fe");
                            }
                        }
                );
        findViewById(R.id.music6)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-f46857f0-7c4a-483a-adfa-a916a27697fb");
                            }
                        }
                );
        findViewById(R.id.music7)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-41a2c611-6258-4a42-b7ea-4784fe695788");
                            }
                        }
                );
        findViewById(R.id.music8)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-0196e0d8-61a5-4186-84ad-4dbe6a783159");
                            }
                        }
                );
        findViewById(R.id.music9)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-4f52fee8-a472-4397-9d96-1965ccc3307a");
                            }
                        }
                );
        findViewById(R.id.music10)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-f46857f0-7c4a-483a-adfa-a916a27697fb");
                            }
                        }
                );
        findViewById(R.id.edit_btn_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPicker();
            }
        });
        edit_text_on_video.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                if (hasFocus){
                    findViewById(R.id.edit_btn_color).setVisibility(View.VISIBLE);
                }
                else
                {
                    findViewById(R.id.edit_btn_color).setVisibility(View.GONE);
                }
            }
        });
        findViewById(R.id.music11)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-d6ede476-7350-4895-846f-f44a991be513");
                            }
                        }
                );
        findViewById(R.id.music12)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayAudioFromLink("https://mystuff.bublup.com/api/v1/uploads/001-i-d2af69a8-db72-487f-a3cc-889a01ce7ae1");
                            }
                        }
                );
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));
        mVideoTrimmer = ((HgLVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {
             //mVideoTrimmer.setMaxDuration(thistime);
            mVideoTrimmer.setMaxDuration(thistime);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnHgLVideoListener(this);
            //mVideoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
            path= thisLocalVideopath;
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }
    @Override
    public void onTrimStarted() {
        mProgressDialog.show();
    }
    @Override
    public void getResult(final Uri contentUri) {
        mProgressDialog.cancel();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
        try {
            String path = contentUri.getPath();
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
            intent.setDataAndType(Uri.fromFile(file), "video/*");
            startActivity(intent);
            finish();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(TrimmerActivity.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
    private void playUriOnVLC(Uri uri) {
        int vlcRequestCode = 42;
        Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
        vlcIntent.setPackage("org.videolan.vlc");
        vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
        vlcIntent.putExtra("title", "Kung Fury");
        vlcIntent.putExtra("from_start", false);
        vlcIntent.putExtra("position", 90000l);
        startActivityForResult(vlcIntent, vlcRequestCode);
    }
    private void onCancelClicked() {
        findViewById(R.id.hjid).setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(R.id.hjid), "translationY", -10f);
        animation.setDuration(250);
        animation.start();
        /*if (mOnTrimVideoListener != null) {
            mOnTrimVideoListener.cancelAction();
        }*/
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(R.id.hjid), "translationY", 700f);
                animation.setDuration(250);
                animation.start();
            }
        }, 5000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_MUSIC && resultCode == RESULT_OK ) {
            Uri thisuri=data.getData();
            backmusic_path=FileUtils.getPath(this, thisuri);
            isbackMusic=true;
            islocal=true;
        }
        else  if(requestCode == ADD_GIFF_BOX_CODE && resultCode == RESULT_OK ) {
            if (data.hasExtra("newgiffpath")) {
                if (!Objects.equals(data.getExtras().getString("newgiffpath"), "empty")) {
                    String newgiff=data.getExtras().getString("newgiffpath");
                    gifH = data.getExtras().getString("gifheight");
                    gifW = data.getExtras().getString("gifwidth");
                    bgif = true;
                    AddStickersFromDevice(newgiff);
                }
            }
        }
        else if(requestCode == ADD_TEXT_REQUEST_CODE && resultCode == RESULT_OK ) {
            if (data.hasExtra("returnKey1")) {
                VideoView Vplayer=findViewById(R.id.video_loader);
                if(!Objects.equals(data.getExtras().getString("returnKey1"), "none")) {
                    g_storypath= data.getExtras().getString("returnKey1");
                    finish();
                }
            }
        }
        else  if(requestCode == ADD_VIDEO_WITH_GIF_REQUEST_CODE && resultCode == RESULT_OK ) {
            if (data.hasExtra("videowithgif")) {
                if (!Objects.equals(data.getExtras().getString("videowithgif"), "empty")) {
                    g_storypath=data.getExtras().getString("videowithgif");
                    finish();
                }
            }
        }
        else if(requestCode == ADD_BACK_MUSIC && resultCode == RESULT_OK ) {
            if (data.hasExtra("returnKey2")) {
                VideoView Vplayer=findViewById(R.id.video_loader);
                if(!Objects.equals(data.getExtras().getString("returnKey2"), "none")) {
                    isbackMusic=true;
                    g_storypath=data.getExtras().getString("returnKey2");
                    finish();
                }
            }
        }
        else if(requestCode == ADD_STICKER_REQUEST_CODE && resultCode == RESULT_OK ) {
            if (data.hasExtra("returnKey2")) {
                VideoView Vplayer = findViewById(R.id.video_loader);
                if(!Objects.equals(data.getExtras().getString("returnKey2"), "none")) {
                Vplayer.setVideoPath(data.getExtras().getString("returnKey2"));
            }
            }
        }
        else if(requestCode == ADD_EXTRACT_AUDIO_REQUEST_CODE && resultCode == RESULT_OK ) {
            if (data.hasExtra("returnKey7")) {
                if(!Objects.equals(data.getExtras().getString("returnKey7"), "none")) {
                }
            }
        }
        else if(requestCode == ADD_FF_VIDEO_REQUEST_CODE && resultCode == RESULT_OK ) {
            if (data.hasExtra("returnKey9")) {
                VideoView Vplayer = findViewById(R.id.video_loader);
                if(!Objects.equals(data.getExtras().getString("returnKey9"), "none")) {
                    Vplayer.setVideoPath(data.getExtras().getString("returnKey9"));
                }
            }
        }
    }
    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }
    private void AddStickersFromDevice(String url)
    {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SetNewGifView(url);
            }
        }, 1000);
    }
    @Override
    public void finish() {
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
        }
        Intent data = new Intent();
        data.putExtra("returnKey", g_storypath);
        this.setResult(RESULT_OK, data);
        super.finish();
    }
    void SetNewGifView(String url_path)
    {
        GifPath=url_path;
        ScreenDensity();
        thisImage=new MyImageView(this,url_path);
        frameLayout = findViewById(R.id.layout_surface_view);
        frameLayout.addView(thisImage);
    }
    public void addgif(String inputVideo)
    {
        Intent intent = new Intent(this, AddGiffOnVideoActivity.class);
        intent.putExtra("LocalVideoTime", thistime);
        intent.putExtra("LocalVideoPath", inputVideo);
        intent.putExtra("StickerPath", GifPath);
        intent.putExtra("StickerH", gifH);
        intent.putExtra("StickerW", gifW);
        startActivityForResult(intent, ADD_VIDEO_WITH_GIF_REQUEST_CODE);
    }
    private void ScreenDensity()
    {
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outmetrics = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        display.getMetrics(outmetrics);
        max_width = outmetrics.widthPixels;
        max_height = outmetrics.heightPixels;
    }
    @Override
    public void onError(final String message) {
        mProgressDialog.cancel();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }
    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }
    public void PlayAudioFromLink(String url) {
        v_backmusic=url;
        isbackMusic=true;
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void CombineImagesFUN()
    {
        Intent intent = new Intent(this, CombineImagesActivity.class);
        startActivity(intent);
    }
    public void ExtractAudioFUN()
    {
        Intent intent = new Intent(this, ExtractAudioActivity.class);
        String pathtopass= thisLocalVideopath;
        intent.putExtra("LocalVideoPath", pathtopass);
        startActivityForResult(intent, ADD_EXTRACT_AUDIO_REQUEST_CODE);
    }
    public void MotionFUN()
    {
        Toast.makeText(this, "slow motion mode", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, FastAndSlowVideoMotionActivity.class);
        String timetopass;
        int dura= ((VideoView) findViewById(R.id.video_loader)).getDuration();
        timetopass= String.valueOf(dura/1000);
        intent.putExtra("LocalVideoTime", timetopass);
        intent.putExtra("LocalVideoPath", thisLocalVideopath);
        startActivityForResult(intent, ADD_FF_VIDEO_REQUEST_CODE);
    }
    public void CombineVideosFUN()
    {
        Intent intent = new Intent(this, CombineVideosActivity.class);
        startActivity(intent);
    }
    public void AddTextOnVideoFUN()
    {
        Intent intent = new Intent(this, AddTextOnVideoActivity.class);
        String vText=edit_text_on_video.getText().toString();
        String timetopass;
        int dura= ((VideoView) findViewById(R.id.video_loader)).getDuration();
        timetopass= String.valueOf(dura/1000);
        intent.putExtra("LocalVideoTime", timetopass);
        intent.putExtra("LocalVideoPath", thisLocalVideopath);
        intent.putExtra("LocalVideoText", vText);
        intent.putExtra("LocalTextColor", thiscolorHEX);
        startActivityForResult(intent, ADD_TEXT_REQUEST_CODE);
    }
    public void DownloadBackMusicOnVideoFUN()
    {
        if(islocal) {
            AddBackMusicOnVideoFUN();
        }
        else
        {
            DownloadMusic(v_backmusic);
        }
    }
    public void AddBackMusicOnVideoFUN()
    {
        Intent intent = new Intent(this, MergeAudioVideoActivity.class);
        String timetopass;
        int dura= ((VideoView) findViewById(R.id.video_loader)).getDuration();
        timetopass= String.valueOf(dura/1000);
        intent.putExtra("PassedVideoTime", timetopass);
        intent.putExtra("PassedVideoPath", thisLocalVideopath);
        intent.putExtra("PassedMusicTime", backmusic_path);
        startActivityForResult(intent, ADD_BACK_MUSIC);
    }

    void DownloadMusic(String url)
    {
        Glide.with(TrimmerActivity.this).asFile()
                .load(url)
                .apply(new RequestOptions()
                        .override(Target.SIZE_ORIGINAL))
                .into(new Target<File>() {
                    @Override
                    public void onStart() {
                    }
                    @Override
                    public void onStop() {
                    }
                    @Override
                    public void onDestroy() {
                    }
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                    }
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    }
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        storeMusic(resource);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                    @Override
                    public void getSize(@NonNull SizeReadyCallback cb) {
                    }
                    @Override
                    public void removeCallback(@NonNull SizeReadyCallback cb) {
                    }
                    @Override
                    public void setRequest(@Nullable Request request) {
                    }
                    @Nullable
                    @Override
                    public Request getRequest() {
                        return null;
                    }
                });
    }
    private void storeMusic(File image) {
        backmusic_path="storage/emulated/0/Movies/.required/"+ Calendar.getInstance().getTimeInMillis()+".mp3";
        File pictureFile = new File(backmusic_path);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream output = new FileOutputStream(pictureFile);
            FileInputStream input = new FileInputStream(image);
            FileChannel inputChannel = input.getChannel();
            FileChannel outputChannel = output.getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            output.close();
            input.close();
            AddBackMusicOnVideoFUN();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void ImageToVideoFUN()
    {
        Intent intent = new Intent(this, ImageToVideoConvertActivity.class);
        startActivity(intent);
    }
    public void AddWaterMarkOnVideoFUN()
    {
        Intent intent = new Intent(this, AddWaterMarkOnVideoActivity.class);
        String pathtopass= thisLocalVideopath;
        String stickertopass= Environment.getExternalStorageDirectory().getPath() + "/Movies/pic.png";
        intent.putExtra("LocalVideoPath", pathtopass);
        intent.putExtra("StickerPath", stickertopass);
        startActivityForResult(intent, ADD_STICKER_REQUEST_CODE);
    }
    public void ReverseVideoFUN()
    {
        Intent intent = new Intent(this, ReverseVideoActivity.class);
        String timetopass;
        int dura= ((VideoView) findViewById(R.id.video_loader)).getDuration();
        timetopass= String.valueOf(dura/1000);
        intent.putExtra("LocalVideoTime", timetopass);
        intent.putExtra("LocalVideoPath", thisLocalVideopath);
        startActivityForResult(intent, ADD_REVERSE_REQUEST_CODE);
    }
    public void CombineImageVideoFUN()
    {
        Intent intent = new Intent(this, CombineImageAndVideoActivity.class);
        startActivity(intent);
    }
    public void MergeImageAndAudioFUN()
    {
        Intent intent = new Intent(this, MergeImageAndMP3Activity.class);
        startActivity(intent);
    }
    public void MergeVideoAndAudioFUN()
    {
        findViewById(R.id.musichjid).setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(R.id.musichjid), "translationY", -10f);
        animation.setDuration(250);
        animation.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(R.id.musichjid), "translationY", 700f);
                animation.setDuration(250);
                animation.start();
            }
        }, 7000);
}
    public void CutVideoFUN()
    {
        Intent intent = new Intent(this, CutVideoUsingTimeActivity.class);
        startActivity(intent);
    }
    public void OpenGiffBox()
    {
        Intent intent = new Intent(this, SearchGiffActivity.class);
        startActivityForResult(intent, ADD_GIFF_BOX_CODE);
    }
    public void VideoConvertIntoGIFFUN()
    {
        Intent intent = new Intent(this, VideoToGifActivity.class);

        startActivity(intent);
    }
    public void RemoveAudioFromVideoFUN()
    {
        Intent intent = new Intent(this, RemoveAudioFromVideoActivity.class);
        startActivity(intent);
    }
    private void ColorPicker()
    {
        new MaterialColorPickerDialog
                .Builder(this)
                .setTitle("Pick Theme")
                .setColorShape(ColorShape.CIRCLE)
                .setColorSwatch(ColorSwatch._300)
                .setColorSwatch(ColorSwatch._300)
                .setDefaultColor("#002884")
                .setColorListener(new ColorListener() {
                    @Override
                    public void onColorSelected(int color, @NotNull String colorHex) {
                        thiscolorHEX=colorHex;
                        edit_text_on_video.setTextColor((Color.parseColor(colorHex)));
                    }
                })
                .show();
    }
    public void VideoRotateFlipFUN()
    {
        Intent intent = new Intent(this, VideoRotateFlipActivity.class);
        startActivity(intent);
    }
}
