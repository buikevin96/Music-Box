package com.example.kevinbui.musicbox;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MediaPlayer mediaPlayer;
    private ImageView artistImage;
    private TextView leftTime;
    private TextView rightTime;
    private SeekBar seekBar;
    private Button prevButton, playButton, nextButton;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss"); // Format of the song
                int currentPos = mediaPlayer.getCurrentPosition();// hold the current position of media player
                int duration = mediaPlayer.getDuration(); // hold the duration of the song

                leftTime.setText(dateFormat.format(new Date(currentPos))); // Set beginning time for song
                rightTime.setText(dateFormat.format(new Date(duration - currentPos))); // Set the end time for song
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    // Sets up all of our UI components
    public void setUpUI(){

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music);


        artistImage = (ImageView)findViewById(R.id.imageView);
        leftTime = (TextView)findViewById(R.id.leftTime);
        rightTime = (TextView)findViewById(R.id.rightTime);
        seekBar = (SeekBar)findViewById(R.id.mSeekBar);
        prevButton = (Button)findViewById(R.id.prevButton);
        playButton = (Button)findViewById(R.id.playButton);
        nextButton = (Button)findViewById(R.id.nextButton);

        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.prevButton:
                prevMusic();
                break;

            case R.id.playButton:
                if (mediaPlayer.isPlaying()){
                    pauseMusic();
                } else {
                    startMusic();
                }


                break;

            case R.id.nextButton:
                nextMusic();
                break;
        }
    }

    // Take everything back to 0
    public void prevMusic(){
        if (mediaPlayer != null){
            mediaPlayer.seekTo(0);
            //Toast.makeText(MainActivity.this, "PREVIOUS", Toast.LENGTH_LONG).show();
        }
    }

    public void nextMusic(){
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(mediaPlayer.getDuration() -1000);
            //Toast.makeText(MainActivity.this, "NEXT", Toast.LENGTH_LONG).show();
        }
    }

    public void pauseMusic(){
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }

    }

    public void startMusic(){
        if (mediaPlayer != null){
            mediaPlayer.start();
            updateThread();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }

    }

    // Thread for when application starts with no component, a new linux process begins
    // New thread to update our seekbar and other things in our application
    public void updateThread(){
        thread = new Thread(){
            @Override
            public void run() {

                try {
                    while (mediaPlayer != null && mediaPlayer.isPlaying()) {

                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // SeekBar follows the current Position of the song
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax); // New max of seekbar is the duration of the song
                                seekBar.setProgress(newPosition); // The current position of the song is the current progress of the seekbar

                                // Update the text
                                leftTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss")
                                        .format(new Date(mediaPlayer.getCurrentPosition()))));
                                rightTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss")
                                        .format(new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()))));
                            }
                        });
                    }
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}
