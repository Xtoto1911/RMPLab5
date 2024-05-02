package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.os.Bundle;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.*;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.media.MediaPlayer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.text.InputFilter;

public class MainActivity  extends AppCompatActivity implements OnCheckedChangeListener  {


    private Timer myTimer;
    private MediaPlayer mPlayer;
    private boolean status;
    private EditText minField, secField;
    private TextView outText;
    int secunde, minute;
    private ToggleButton tbtn;
    private Button btn;
    AnimationDrawable frameAnimation;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.animationView);
        img.setBackgroundResource(R.drawable.loading);
        frameAnimation = (AnimationDrawable) img.getBackground();
        status = true;
        minField = (EditText)findViewById(R.id.min);
        secField = (EditText)findViewById(R.id.sec);
        minField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2)});
        secField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2)});
        outText = (TextView)findViewById(R.id.output);
        tbtn = (ToggleButton)findViewById(R.id.rezim);
        tbtn.setOnCheckedChangeListener(this);
        btn = (Button)findViewById(R.id.Rerun);
        mPlayer= MediaPlayer.create(this, R.raw.sound);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                outText.setText(minField.getText().toString() + ":" + secField.getText().toString());
            }
        };
        minField.addTextChangedListener(textWatcher);
        secField.addTextChangedListener(textWatcher);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            TextChanged(buttonView);
        }
        else {
            Cancele(buttonView);
        }
    }

    private void timerTick() {
        this.runOnUiThread(doTask);
    }

    private Runnable doTask = new Runnable() {
        public void run()
        {
            if (secunde >0)
                secunde--;
            else
            {
                if(minute>0) {
                    minute--;
                    secunde=59;
                }
                else
                {
                    if (myTimer != null) {
                        try {
                            mPlayer.prepare();
                            mPlayer.seekTo(0);
                        }
                        catch (Exception e) {}
                        mPlayer.start();
                        myTimer.cancel();
                        myTimer = null;
                        frameAnimation.stop();
                        status = true;
                        btn.setEnabled(true);
                        minField.setEnabled(true);
                        secField.setEnabled(true);
                        tbtn.setChecked(false);
                    }
                }
            }
            outText.setText(minute + ":" + secunde);
        }
    };

    private void Error() {
        Toast toast = Toast.makeText(getApplicationContext(), "Неверные данные", Toast.LENGTH_SHORT);
        toast.show();
        minField.setText("");
        secField.setText("");
        tbtn.setChecked(false);
    }

    public void TextChanged(View view)
    {
        try {
            if (status) {
                mPlayer.stop();
                minute = Integer.parseInt(minField.getText().toString());
                secunde = Integer.parseInt(secField.getText().toString());
                if ((minute > 59 || minute < 0) ||(secunde > 59 || secunde < 0))
                    Error();
                else
                {
                    Starte(view);
                    minField.setEnabled(false);
                    secField.setEnabled(false);
                    btn.setEnabled(false);
                    status = false;
                }
            }
            else Starte(view);
        }
        catch (Exception e) {
            Error();
        }
    }

    public void Starte(View v) {
        frameAnimation.start();
        myTimer = new Timer();
        btn.setEnabled(false);
        myTimer.schedule(new TimerTask() {
            public void run() {
                timerTick();
            }
        }, 0, 1000);
    }

    public void Cancele(View v) {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer = null;
            frameAnimation.stop();
            btn.setEnabled(true);
        }
    }

    public void ReStart(View view)
    {
        Cancele(view);
        minField.setEnabled(true);
        secField.setEnabled(true);
        minField.setText("0");
        secField.setText("0");
        mPlayer.stop();
        status = true;
        tbtn.setChecked(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            frameAnimation.stop();
            myTimer.cancel();
            myTimer = null;
        }
    }
}