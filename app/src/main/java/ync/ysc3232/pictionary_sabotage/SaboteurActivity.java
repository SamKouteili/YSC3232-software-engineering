package ync.ysc3232.pictionary_sabotage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SaboteurActivity extends AppCompatActivity {

    private Button saboteur_button;
    private DrawerView saboteur_view;


    private long timeLeftToDraw = 20000; //10 seconds
    private CountDownTimer countDownTimer;
    private TextView countdownText;

    private CountDownTimer sabotageTimer;
    private CountDownTimer waitTimer;
    private final long waitTime = 5000;
    private final long sabotageTime = 3000;
    private boolean can_sabotage;
    private final String go = "GO!";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saboteur);

        saboteur_view = (DrawerView) findViewById(R.id.saboteur_view);
        countdownText = findViewById(R.id.countDown_draw);
        can_sabotage = false;
        saboteur_button = (Button) findViewById(R.id.sabotage_button);

        saboteur_button.setOnClickListener(view -> {

            if (can_sabotage){
                saboteur_view.setCanDraw(true);
                sabotageTimer();
            }

        });

        startTimer();
        waitTimer();

    }

    public void waitTimer() {
        waitTimer = new CountDownTimer(waitTime, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) (l + 1000) / 1000;

                //Update text
                String updatedCountDown;
                updatedCountDown = "" + seconds;
                saboteur_button.setText(updatedCountDown);

            }

            @Override
            public void onFinish() {
                can_sabotage = true;
                saboteur_button.setBackgroundResource(R.drawable.green_button);
                saboteur_button.setText(go);
            }
        }.start();
    }

    public void sabotageTimer() {
        sabotageTimer = new CountDownTimer(sabotageTime, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) (l + 1000) / 1000;

                //Update text
                String updatedCountDown;
                updatedCountDown = "" + seconds;
                saboteur_button.setText(updatedCountDown);

            }

            @Override
            public void onFinish() {
                saboteur_view.setCanDraw(false);
                can_sabotage = false;
                saboteur_button.setBackgroundResource(R.drawable.red_button);
                // call waitTimer() when sabotageTimer finishes
                waitTimer();
            }
        }.start();
    }


    public void startTimer() {
        new CountDownTimer(timeLeftToDraw, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftToDraw = l;
                int seconds = (int) (timeLeftToDraw + 1000) / 1000;

                //Update text
                String updatedCountDownText;
                updatedCountDownText = "" + seconds;
                countdownText.setText(updatedCountDownText);

            }

            @Override
            public void onFinish() {
                waitTimer.cancel();
                sabotageTimer.cancel();

                Intent intent = new Intent(SaboteurActivity.this, RandomWordGenerator.class);
                startActivity(intent);

            }
        }.start();
    }

}