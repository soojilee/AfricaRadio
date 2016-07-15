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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leegacy.sooji.africaradio.Activities.PostActivity;
import com.leegacy.sooji.africaradio.CircularSeekBar;
import com.leegacy.sooji.africaradio.R;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by soo-ji on 16-06-10.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {
    private ImageView startButton;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private ImageView playButton;
    private ImageView stopButton;
    private ImageView pauseButton;
    private MediaPlayer myMediaPlayer;

    private CircularSeekBar seekBar;
    private Handler seekHandler;
    private FrameLayout nextButton;
    private ImageView deleteButton;
    private String uid;
    private View root;
    private RelativeLayout seekbarContainer;
    private boolean recording = false;
    private boolean playing = false;
    private int seconds = 0;
    private TextView recordTimer;
    private TextView tapToRecord;
    private Timer timer;
    private Timer timerPlay;


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
        root = inflater.inflate(R.layout.fragment_record, null);
        init();
        return root;
    }


    protected void init() {
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        File file = new File(outputFile);
        if (file.exists()) {
            file.delete();
        }
        myAudioRecorder.setOutputFile(outputFile);


//        myMediaPlayer = new MediaPlayer();
        seekBar = (CircularSeekBar) root.findViewById(R.id.seek_bar);
        seekBar.setCircleProgressColor(getResources().getColor(R.color.deepRed));
        seekBar.setIsTouchEnabled(false);
        seekBar.setPointerColor(getResources().getColor(R.color.deepRed));
        seekBar.setPointerColor(getResources().getColor(R.color.transparent));
        seekBar.setPointerHaloColor(getResources().getColor(R.color.transparent));
        seekBar.setCircleColor(getResources().getColor(R.color.mediumGrey));
        seekBar.setCircleFillColor(getResources().getColor(R.color.white));


        seekHandler = new Handler();

        startButton = (ImageView) root.findViewById(R.id.recordStartButton);
        playButton = (ImageView) root.findViewById(R.id.recordPlayView);
        startButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        playButton.setVisibility(View.INVISIBLE);
        playButton.setEnabled(false);


        nextButton = (FrameLayout) root.findViewById(R.id.nextButton);
        deleteButton = (ImageView) root.findViewById(R.id.deleteButton);
        nextButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        recordTimer = (TextView) root.findViewById(R.id.timerRecord);
        recordTimer.setVisibility(View.INVISIBLE);
        tapToRecord = (TextView) root.findViewById(R.id.tapToRecord);
    }


    protected void seekUpdation() {
        seekBar.setProgress(myMediaPlayer.getCurrentPosition());
        if (myMediaPlayer.getCurrentPosition() >= seekBar.getMax()) { //play is finished
            startButton.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.VISIBLE);
            startButton.setEnabled(false);
            playButton.setEnabled(true);
            myMediaPlayer.release();
            myMediaPlayer = null;


        }

        seekHandler.postDelayed(run, 100); //update seek handle every 0.5 seconds
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
//        seekHandler.removeCallbacks(run);
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
                if (!recording && !playing) {
                    try {

                        myAudioRecorder.prepare();

                        myAudioRecorder.start();
                        recordTimer.setVisibility(View.VISIBLE);
                        tapToRecord.setVisibility(View.INVISIBLE);
                        StartTimer();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
//                startButton.setEnabled(false);
//                stopButton.setEnabled(true);
                    recording = true;


                    Toast.makeText(getActivity(), "Recording started", Toast.LENGTH_LONG).show();
                } else if (recording) {
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;
                    timer.cancel();
                    timer.purge();
                    timer = null;
//                    recordTimer.setVisibility(View.INVISIBLE);
                    seconds = 0;

//                    stopButton.setEnabled(false);
//                    startButton.setEnabled(true);
//                    seekbarContainer.setVisibility(View.VISIBLE);
//                    stopButton.setVisibility(View.GONE);
                    startButton.setVisibility(View.INVISIBLE);
                    startButton.setEnabled(false);
                    playButton.setEnabled(true);
                    playButton.setVisibility(View.VISIBLE);
                    recording = false;
                    Toast.makeText(getActivity(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
                } else if (playing) {

                    timerPlay.cancel();
                    timerPlay.purge();
                    timerPlay = null;
                    myMediaPlayer.release();
                    myMediaPlayer = null;

                }
                break;
            case R.id.recordPlayView:

                try {
                    //myMediaPlayer = MediaPlayer.create(this, outputFile,);
                    myMediaPlayer = new MediaPlayer();
                    myMediaPlayer.setDataSource(outputFile);
                    myMediaPlayer.prepare();
                    myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            startButton.setEnabled(false);
                            startButton.setVisibility(View.INVISIBLE);
                            playButton.setEnabled(true);
                            playButton.setVisibility(View.VISIBLE);
                            timerPlay.cancel();
                            timerPlay.purge();
                            timerPlay=null;
                            seconds = 0;


                        }
                    });
                    seekBar.setMax(myMediaPlayer.getDuration());
                    seekUpdation();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                timerPlay = new Timer();
                timerPlay.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seconds++;
                                if (seconds < 0) {
                                    recordTimer.setText("something wrong from onClick seconds: " + seconds);
                                    recordTimer.postInvalidate();
                                } else {
                                    //stuff that updates ui
                                    recordTimer.setText(convertSecondsToTime(seconds));
                                    recordTimer.postInvalidate();
                                }

                            }
                        });

                    }
                }, 0, 1000);
                myMediaPlayer.start();
                playing = true;
                playButton.setEnabled(false);
                startButton.setEnabled(true);
                playButton.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Playing audio", Toast.LENGTH_LONG).show();


        }
    }

    private void StartTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seconds++;
                        if (seconds < 0) {
                            recordTimer.setText("something wrong from onClick seconds: " + seconds);
                            recordTimer.postInvalidate();
                        } else {
                            //stuff that updates ui
                            recordTimer.setText(convertSecondsToTime(seconds));
                            recordTimer.postInvalidate();
                        }

                    }
                });

            }
        }, 0, 1000);
    }

    public static String convertSecondsToTime(int seconds) {
        int hour = 0;
        int minute = 0;
        int second = 0;

        hour = (seconds / 60) / 60;
        minute = (int) ((((double) seconds) / 60) % 60);
        second = seconds % 60;
        //Log.e(TAG, "hour: " + hour + " min: " + minute + "second: " + second);

        return formatTimeString(minute) + ":" + formatTimeString(second);

        //return "ERROR convertSecondsToTime is not working";
    }

    private static String formatTimeString(int n) {
        if (n < 10) {
            return "0" + n;
        }
        return "" + n;
    }
}
