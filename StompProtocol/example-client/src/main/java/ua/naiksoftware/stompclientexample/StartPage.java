package ua.naiksoftware.stompclientexample;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import ua.naiksoftware.R;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stompclientexample.util.ChatUtil;

import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.OPENED;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.SERVER_PORT;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.mStompClient;

public class StartPage extends AppCompatActivity {
    ProgressBar progressBar;
    int progressStatus;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        Toolbar toolbar = findViewById(R.id.toolbar);

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            }
        }, 6000);

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + ChatUtil.ANDROID_EMULATOR_LOCALHOST
                + ":" + SERVER_PORT + "/ws");

        mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    switch (lifecycleEvent.getType()) {

                        case OPENED:
                            ChatUtil.isConnected = true;
                            Toast toast = Toast.makeText(getApplicationContext(), "Thora Gupp Shupp hojaye!", Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                            ((TextView)view.findViewById(android.R.id.message)).setTextColor(Color.WHITE);
                            view.getBackground().setColorFilter(Color.parseColor("#c691ff"), PorterDuff.Mode.SRC_IN);

                            toast.show();
                            Log.d("TAG", "Stomp connection opened");
                            Intent intent = new Intent(StartPage.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                            break;

                        case ERROR:
                            Toast toast2 = Toast.makeText(getApplicationContext(), "Couldnt connect! :(", Toast.LENGTH_LONG);
                            View view2 = toast2.getView();
                            view2.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);

                            toast2.show();

                            ChatUtil.isConnected = false;
                            Log.e("TAG", "Error", lifecycleEvent.getException());
                            break;

                        case CLOSED:
                            final Toast toast3 = Toast.makeText(getApplicationContext(), "Oops! Somebody closed the socket", Toast.LENGTH_LONG);
                            final View view3 = toast3.getView();
                            view3.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

                            toast3.show();

                            ChatUtil.isConnected = false;
                            Log.d("TAG", "Stomp connection closed");

                            mStompClient.reconnect();
                            break;
                    }
                }
            });
        });

        mStompClient.connect();






    }
}