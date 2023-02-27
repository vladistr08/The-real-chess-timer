package com.example.therealchesstimer

import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.set
import java.sql.Time
import java.util.*

class MainActivity : AppCompatActivity() {

    enum class TimeStatus {
        ON, OFF, RUNNING,PAUSED
    }

    private var increment = 0;
    private class TimeType {

        constructor(sec: Int, stat: TimeStatus){
            seconds = sec;
            status = stat;
        }

        public var seconds = 0;
        public lateinit var status: TimeStatus;

        public fun getCurrentTimeString(): String{
            timeObj = Time(0, 0, seconds);
            if(seconds > 3600){
                return timeObj.toString();
            }
            else{
                return timeObj.toString().substring(3);
            }
        }
        private lateinit var timeObj: Time;
    }

    private var isStartOfGame: Boolean = true;

    private fun timeRunOut(): Boolean{
        return (timePlayer1.seconds == 0 || timePlayer2.seconds == 0) && gameStatus == TimeStatus.RUNNING;
    }

    private fun isNumeric(stringToCheck: String): Boolean{
        if(stringToCheck.isEmpty() || stringToCheck.length > 5 || stringToCheck[0] == '0')
            return false;
        return stringToCheck.all { char -> char.isDigit() }
    }

    private var gameStatus: TimeStatus = TimeStatus.PAUSED;

    private lateinit var timePlayer1: TimeType;
    private lateinit var timePlayer2: TimeType;

    private lateinit var buttonP1: Button;
    private lateinit var buttonP2: Button;

    private lateinit var settingsForm: EditText;
    private lateinit var settingsFormIncrement: EditText;
    private lateinit var settingsfFormCloseSaveButton: Button;
    private lateinit var settingsFormCloseButton: Button;

    private lateinit var settingsButton: Button;
    private lateinit var pauseButton: Button;
    private lateinit var playButton: Button;

    private val handler = Handler();
    private val updateTime = object: Runnable{
        override fun run() {
            if(timePlayer1.seconds == 0 && timePlayer2.seconds == 0){
                Log.i("Info", "Start of game");
                timePlayer1.seconds = 60 * 5;
                timePlayer2.seconds = 60 * 5;
            }
            else if(timeRunOut()){
                Log.i("Info", "Time out");
                gameStatus = TimeStatus.PAUSED;
                if(timePlayer1.seconds <= 0){
                    buttonP1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#d86868"));
                }
                else{
                    buttonP2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#d86868"));
                }
            }
            else{
                Log.i("Info", "Game running");
                if(gameStatus == TimeStatus.RUNNING){
                    if(timePlayer1.status == TimeStatus.ON){
                        timePlayer1.seconds -= 1;
                        buttonP1.text = timePlayer1.getCurrentTimeString();
                    }
                    else if(timePlayer2.status == TimeStatus.ON){
                        timePlayer2.seconds -= 1;
                        buttonP2.text = timePlayer2.getCurrentTimeString();
                    }
                }
            }
            handler.postDelayed(this, 1000);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonP1 = findViewById<Button>(R.id.Player1);
        buttonP2 = findViewById<Button>(R.id.Player2);

        settingsForm = findViewById<EditText>(R.id.time);
        settingsFormIncrement = findViewById<EditText>(R.id.increment);
        settingsfFormCloseSaveButton = findViewById<Button>(R.id.button3);
        settingsFormCloseButton = findViewById<Button>(R.id.button4);

        settingsForm.hint = "Ammount of time";
        settingsFormIncrement.hint = "Increment";

        settingsButton = findViewById<Button>(R.id.settings);
        pauseButton = findViewById<Button>(R.id.pause);
        playButton = findViewById<Button>(R.id.play);

        timePlayer1 = TimeType(0, TimeStatus.ON);
        timePlayer2 = TimeType(0, TimeStatus.OFF);

        settingsfFormCloseSaveButton.setOnClickListener{
            val userInput = settingsForm.text.toString();
            val incrementInput = settingsFormIncrement.text.toString()
            if(userInput.isEmpty() || incrementInput.isEmpty()){
                Toast.makeText(this, "You did not enter a valid input!", Toast.LENGTH_SHORT).show();
            }
            else if(isNumeric(userInput)){
                increment = incrementInput.toInt()
                timePlayer1.seconds = userInput.toInt() * 60;
                timePlayer2.seconds = userInput.toInt() * 60;

                timePlayer1.status = TimeStatus.OFF;
                timePlayer2.status = TimeStatus.OFF;

                buttonP2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1B25"));
                buttonP1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1B25"));

                buttonP1.text = timePlayer1.getCurrentTimeString();
                buttonP2.text = timePlayer2.getCurrentTimeString();
                settingsForm.visibility = View.INVISIBLE;
                settingsfFormCloseSaveButton.visibility = View.INVISIBLE;
                settingsFormIncrement.visibility = View.INVISIBLE
                settingsFormCloseButton.visibility = View.INVISIBLE
                isStartOfGame = true;
            }
            else{
                settingsForm.setHint("Try again!");
                settingsForm.setText("");
            }
        }

        settingsFormCloseButton.setOnClickListener{
            settingsForm.visibility = View.INVISIBLE;
            settingsfFormCloseSaveButton.visibility = View.INVISIBLE;
            settingsFormIncrement.visibility = View.INVISIBLE;
            settingsFormCloseButton.visibility = View.INVISIBLE;
        }

        settingsButton.setOnClickListener{
            Log.i("Info", "Settings");
            gameStatus = TimeStatus.PAUSED
            isStartOfGame = true
            settingsForm.visibility = View.VISIBLE;
            settingsFormIncrement.visibility = View.VISIBLE;
            settingsfFormCloseSaveButton.visibility = View.VISIBLE;
            settingsFormCloseButton.visibility = View.VISIBLE;
        }

        pauseButton.setOnClickListener{
            Log.i("Info", "Pause");
            buttonP1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1B25"));
            buttonP2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1B25"));
            gameStatus = TimeStatus.PAUSED;
        }

        playButton.setOnClickListener{
            Log.i("Info", "Play");
            gameStatus = TimeStatus.RUNNING;
            isStartOfGame = false;

            if(timeRunOut()) {
                gameStatus = TimeStatus.PAUSED;
                if (timePlayer1.seconds <= 0) {
                    buttonP1.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#d86868"));
                } else {
                    buttonP2.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#d86868"));
                }
            }
            else{
                if(timePlayer1.status == TimeStatus.ON){
                    buttonP1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#123f43"));
                    buttonP2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1B25"));
                }
                else{
                    buttonP1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1B25"));
                    buttonP2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#123f43"));
                }
            }
        }
        var mMediaPlayer: MediaPlayer? = null;
        mMediaPlayer = MediaPlayer.create(this, R.raw.click)
        buttonP1.setOnClickListener{
            Log.i("Info", "P1 pressed");
            mMediaPlayer.start();
            if(isStartOfGame){
                isStartOfGame = false;
                gameStatus = TimeStatus.RUNNING;
                timePlayer1.status = TimeStatus.OFF
                timePlayer2.status = TimeStatus.ON
                buttonP1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1B25"));
                buttonP2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#123f43"));
            }
            if(gameStatus == TimeStatus.RUNNING){
                if(timePlayer1.status == TimeStatus.ON){
                    buttonP1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1B25"));
                    buttonP2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#123f43"));
                    timePlayer1.status = TimeStatus.OFF;
                    timePlayer2.status = TimeStatus.ON;
                    timePlayer1.seconds += increment;
                    buttonP1.text = timePlayer1.getCurrentTimeString()
                }
            }
        }

        buttonP2.setOnClickListener{
            Log.i("Info", "P2 pressed");
            mMediaPlayer.start();
            if(isStartOfGame){
                isStartOfGame = false;
                gameStatus = TimeStatus.RUNNING;
                timePlayer2.status = TimeStatus.OFF
                timePlayer1.status = TimeStatus.ON
                buttonP2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1B25"));
                buttonP1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#123f43"));
            }
            if(gameStatus == TimeStatus.RUNNING){
                if(timePlayer2.status == TimeStatus.ON){
                    buttonP2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A1B25"));
                    buttonP1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#123f43"));
                    timePlayer1.status = TimeStatus.ON;
                    timePlayer2.status = TimeStatus.OFF;
                    timePlayer2.seconds += increment;
                    buttonP2.text = timePlayer2.getCurrentTimeString()
                }
            }
        }

        handler.postDelayed(updateTime, 1000);

    }
}