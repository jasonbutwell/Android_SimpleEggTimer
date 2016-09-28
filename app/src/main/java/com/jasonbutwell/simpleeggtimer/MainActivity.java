package com.jasonbutwell.simpleeggtimer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SeekBar timerSeekbar;
    TextView timerTextView;
    Button controllerButton;
    boolean counterActive = false;
    CountDownTimer countDownTimer;

    // our constants
    final static String TimerText = "00:00";
    final static String TimerTextLabelStart = "Start the Timer";
    final static String TimerTextLabelStop = "Stop the Timer";

    // initial timer maximum and start value
    final static int timerMaximumDuration = 10 * 60;
    final static int timerStartValue = 0;

    // reset display aspects
    public void resetTimerDisplay() {
        timerTextView.setText(TimerText);
        timerSeekbar.setProgress(timerStartValue);
    }

    // reset our actual timer
    public void resetTimer() {
        timerSeekbar.setEnabled(true);
        countDownTimer.cancel();
        controllerButton.setText(TimerTextLabelStart);
        counterActive = false;
    }

    // start the timer
    public void startTimer() {
        counterActive = true;
        timerSeekbar.setEnabled(false);
        controllerButton.setText(TimerTextLabelStop);
    }

    // update the display based on the amount of seconds left
    public void updateTimer( int secondsLeft ) {

        // extract the minutes and seconds from the total seconds left
        int minutes = (int) secondsLeft / 60;
        int seconds = (int) secondsLeft - minutes * 60;

        // convert the values to strings using Integer.toString()
        String minutesString = Integer.toString(minutes);
        String secondsString = Integer.toString(seconds);

        // check the length of both minutes and seconds to see if we need to add a leading zero
        if ( minutesString.length() == 1 ) minutesString = "0" + minutesString;
        if ( secondsString.length() == 1 ) secondsString = "0" + secondsString;

        // update the timers textview to the concatenated string we want
        timerTextView.setText(minutesString + ":" + secondsString );

        // Note: An added feature. Sets the seekbar progress as the timer counts down in real time
        timerSeekbar.setProgress(secondsLeft);
    }

    public void controlTimer( View view ) {

        // Is there a positive value to set and is the counter not already active?
        if (timerSeekbar.getProgress() > 0 && counterActive == false ) {

            // Initialises the timer to start
            startTimer();

            // create a new countdowntimer based on the value set by the timer seek bar
            // We set this to tick every 1000 milliseconds or every second.
            // We add an offset here of a tenth of a second to allow the timer to start after we interact with the app

            countDownTimer = new CountDownTimer((timerSeekbar.getProgress() * 1000) + (1000 / 10), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // called every tick of our timer
                    updateTimer((int) millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    // Called when our timer finishes.
                    //Log.i("Timer", "Finished");
                    resetTimerDisplay();
                    resetTimer();

                    // play the air horn sound using a MediaPlayer
                    // We need getApplicationContext() here. this will not work!
                    MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
                    mplayer.start();
                }
            }.start(); // start the timer here
        }
        else if (counterActive == true ) {
            // if the timer is already running, we stop / reset the timer
            resetTimer();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // sets the content view to use our detault XML Layout for this app
        setContentView(R.layout.activity_main);

        // obtain references to the seekbar and timers textview
        timerSeekbar = (SeekBar) findViewById(R.id.timerSeekBar);
        timerTextView = (TextView) findViewById(R.id.timerTextView);

        // Get a reference to the start / stop button
        controllerButton = (Button) findViewById(R.id.controllerButton);

        // set the maximum value of the seekbar and set the progress to the start value
        timerSeekbar.setMax(timerMaximumDuration);
        timerSeekbar.setProgress(timerStartValue);

        // call update timer to initialise the timers display
        updateTimer( timerStartValue );

        // here we call the controlTimer method if the button is clicked using a Listener
        controllerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick( View view ) {
                //Log.i("Button Pressed", "Pressed");
                controlTimer( view );
            }
        }
        );

        // Here we handle the changes that the timerbar goes through
        // When the progress of the seekbar is changed we call our updateTimer method with the current progress value
        timerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTimer(progress);
            }

            // stub - not needed

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            // stub - not needed

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
