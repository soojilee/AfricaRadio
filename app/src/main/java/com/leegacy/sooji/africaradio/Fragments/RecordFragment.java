package com.leegacy.sooji.africaradio.Fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leegacy.sooji.africaradio.Activities.PostActivity;
import com.leegacy.sooji.africaradio.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by soo-ji on 16-06-10.
 */
public class RecordFragment extends Fragment implements View.OnClickListener{
    private Button startButton;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private ImageView playButton;
    private Button stopButton;
    private ImageView pauseButton;
    private MediaPlayer myMediaPlayer;
    private boolean first;
    private SeekBar seekBar;
    private Handler seekHandler;
    private TextView nextButton;
    private TextView deleteButton;
    private String uid;
    private View root;
    private RelativeLayout seekbarContainer;


//    @Override
//    protected void onResume() {
//        super.onResume();
//        Firebase authRef = new Firebase("https://blazing-inferno-7470.firebaseio.com");
//        authRef.addAuthStateListener(new Firebase.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(AuthData authData) {
//                if (authData != null) {
//                    // user is logged in
//                    uid = authData.getUid();
//                } else {
//                    // user is not logged in
//                    Toast.makeText(getActivity(), "User Not Logged In", Toast.LENGTH_LONG);
//                }
//            }
//        });
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.record_fragment, null);
        init();
        return root;
    }


    protected void init(){
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        File file = new File(outputFile);
        if(file.exists()){
            file.delete();
        }
        myAudioRecorder.setOutputFile(outputFile);



//        myMediaPlayer = new MediaPlayer();
        seekBar = (SeekBar) root.findViewById(R.id.seek_bar);
        seekHandler = new Handler();

        startButton = (Button) root.findViewById(R.id.recordStartButton);
        playButton = (ImageView) root.findViewById(R.id.recordPlayView);
        stopButton = (Button) root.findViewById(R.id.recordStopButton);
        pauseButton = (ImageView) root.findViewById(R.id.recordPauseView);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        seekbarContainer = (RelativeLayout) root.findViewById(R.id.seekbarContainer);
        seekbarContainer.setVisibility(View.INVISIBLE);
        seekbarContainer.setEnabled(false);
        pauseButton.setVisibility(View.INVISIBLE);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        first = true;

        nextButton = (TextView) root.findViewById(R.id.nextButton);
        deleteButton = (TextView) root.findViewById(R.id.deleteButton);
        nextButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }


    protected void seekUpdation(){
        seekBar.setProgress(myMediaPlayer.getCurrentPosition());
        if(myMediaPlayer.getCurrentPosition() >= seekBar.getMax()){ //play is finished
            pauseButton.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setEnabled(false);
            playButton.setEnabled(true);
            myMediaPlayer.release();
            myMediaPlayer = null;
            first = true;

        }

        seekHandler.postDelayed(run, 100); //update seek handle every 0.5 seconds
    }

    Runnable run = new Runnable() { @Override public void run() { seekUpdation(); } };

    @Override
    public void onDestroy() {
        super.onDestroy();
        seekHandler.removeCallbacks(run);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextButton:
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra("audioFile", outputFile);


                startActivity(intent);
                break;
            case R.id.deleteButton:
                myMediaPlayer.release();
                myMediaPlayer = null;
                init();
                break;
            case R.id.recordStartButton:
                try {

                    myAudioRecorder.prepare();

                    myAudioRecorder.start();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                startButton.setEnabled(false);
                stopButton.setEnabled(true);

                Toast.makeText(getActivity(), "Recording started", Toast.LENGTH_LONG).show();
                break;
            case R.id.recordStopButton:
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;

                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                seekbarContainer.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                startButton.setVisibility(View.GONE);

                Toast.makeText(getActivity(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
                break;
            case R.id.recordPlayView:

                if (first) {
                    try {
                        //myMediaPlayer = MediaPlayer.create(this, outputFile,);
                        myMediaPlayer = new MediaPlayer();
                        myMediaPlayer.setDataSource(outputFile);
                        myMediaPlayer.prepare();
                        seekBar.setMax(myMediaPlayer.getDuration());
                        seekUpdation();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    try
//                    myMediaPlayer.prepareAsync();
////                    if (!restart) {
//                        myMediaPlayer.prepare();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }

                myMediaPlayer.start();
                playButton.setEnabled(false);
                pauseButton.setEnabled(true);
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Playing audio", Toast.LENGTH_LONG).show();

                break;
            case R.id.recordPauseView:
                myMediaPlayer.pause();
                first = false;
                playButton.setEnabled(true);
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                pauseButton.setEnabled(false);
                break;


        }
    }
}
