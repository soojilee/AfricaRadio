package com.leegacy.sooji.africaradio.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leegacy.sooji.africaradio.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by soo-ji on 16-04-20.
 */
public class RecordActivity extends AppCompatActivity implements View.OnClickListener {
    private Button startButton;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private Button playButton;
    private Button stopButton;
    private Button pauseButton;
    private MediaPlayer myMediaPlayer;
    private boolean first;
    private SeekBar seekBar;
    private Handler seekHandler;
    private TextView nextButton;
    private TextView deleteButton;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        init();


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

        uid = getIntent().getStringExtra(SignInActivity.UID);

//        myMediaPlayer = new MediaPlayer();
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekHandler = new Handler();

        startButton = (Button) findViewById(R.id.recordStartButton);
        playButton = (Button) findViewById(R.id.recordPlayButton);
        stopButton = (Button) findViewById(R.id.recordStopButton);
        pauseButton = (Button) findViewById(R.id.recordPauseButton);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        first = true;

        nextButton = (TextView) findViewById(R.id.nextButton);
        deleteButton = (TextView) findViewById(R.id.deleteButton);
        nextButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    protected void seekUpdation(){
        seekBar.setProgress(myMediaPlayer.getCurrentPosition());
        if(myMediaPlayer.getCurrentPosition() >= seekBar.getMax()){
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
    public void finish() {
        super.finish();
        seekHandler.removeCallbacks(run);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextButton:
                Intent intent = new Intent(this, PostActivity.class);
                intent.putExtra("audioFile", outputFile);
                intent.putExtra(SignInActivity.UID, uid);
                finish();
                startActivity(intent);
                break;
            case R.id.deleteButton:
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

                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
                break;
            case R.id.recordStopButton:
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;

                stopButton.setEnabled(false);
                startButton.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
                break;
            case R.id.recordPlayButton:

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

//                    try {
                       // myMediaPlayer.prepareAsync();
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
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();

                break;
            case R.id.recordPauseButton:
                myMediaPlayer.pause();
                first = false;
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                break;


        }
    }
}
