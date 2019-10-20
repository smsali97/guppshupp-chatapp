package ua.naiksoftware.stompclientexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.R;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stompclientexample.model.ChatMessage;
import ua.naiksoftware.stompclientexample.model.User;



public class MainActivity extends AppCompatActivity {

    EditText editText;
    StompClient mStompClient;

    public static final String ANDROID_EMULATOR_LOCALHOST = "10.0.2.2";
    public static final String SERVER_PORT = "8080";

    public static String currentUsername = "smsali97";
    public static int ctr = 0;

    private ListView messagesView;
    ChatMessageAdapter messageAdapter;

    private PopupWindow window;
    private String TAG = "Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This is where we write the mesage
        editText = (EditText) findViewById(R.id.editText);

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + ANDROID_EMULATOR_LOCALHOST
                + ":" + SERVER_PORT + "/ws/websocket");

        mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {

                case OPENED:
                    Log.d("TAG", "Stomp connection opened");
                    break;

                case ERROR:
                    Log.e("TAG", "Error", lifecycleEvent.getException());
                    break;

                case CLOSED:
                    Log.d("TAG", "Stomp connection closed");
                    break;
            }
        });

        mStompClient.connect();

        User user1 = new User();
        user1.setUsername("mmaazt");
        user1.setPassword("abc123");
        User user2 = new User();
        user2.setUsername("smsali97");
        user2.setPassword("abc123");
        mStompClient.send("/app/chat.register", new Gson().toJson(user1)).subscribe();
        mStompClient.send("/app/chat.register", new Gson().toJson(user2)).subscribe();

        messageAdapter = new ChatMessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        editText = (EditText) findViewById(R.id.editText);
        mStompClient.topic("/topic/public")
                .subscribe((message) -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String json = message.getPayload();
                            Log.d("PAYLOAD",json);

                            ChatMessage cm = new Gson().fromJson(json,ChatMessage.class);

                            if (cm.getType().equals(ChatMessage.MessageType.LEAVE) ) return;
                            if (cm.getTimestamp() == null) {
                                SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yy");
                                cm.setTimestamp(df.format(new Date()));
                            }
                            messageAdapter.add(cm);
                            // scroll the ListView to the last added element
                            messagesView.setSelection(messagesView.getCount() - 1);
                        }
                    });
        });

    }

    public void sendMessage(View view) {

        ChatMessage message = new ChatMessage();
        User user = new User();
        user.setUsername(currentUsername);

        user.setPassword("abc123");

        message.setSender(user);
        message.setContent(editText.getText().toString());
        message.setType(ChatMessage.MessageType.CHAT);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yy");
        message.setTimestamp(df.format(new Date()));
        editText.getText().clear();

        Log.d("ch",new Gson().toJson(message));

        mStompClient.send("/app/chat.send", new Gson().toJson(message)).subscribe();


    }

    public void ShowPopupWindow(View view){
        try {
            ImageView jigar, yaar, lush, oye, scene_on_hai, chill_karo, my_image;

            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup, null);
            window = new PopupWindow(layout, 910, 850, true);

            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setOutsideTouchable(true);
            window.showAtLocation(layout, Gravity.CENTER, 40, 60);
            //  window.showAtLocation(layout, 17, 100, 100);

            jigar = (ImageView) layout.findViewById(R.id.jigar);
            yaar = (ImageView) layout.findViewById(R.id.yaar);
            lush = (ImageView) layout.findViewById(R.id.lush);
            oye = (ImageView) layout.findViewById(R.id.oye);
            scene_on_hai = (ImageView) layout.findViewById(R.id.scene_on_hai);
            chill_karo = (ImageView) layout.findViewById(R.id.chill_karo);

            ChatMessage message = new ChatMessage();
            User user = new User();
            user.setUsername(currentUsername);
            user.setPassword("abc123");
            message.setSender(user);

            jigar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, " jigar call press ");

                    message.setType(ChatMessage.MessageType.STICKER);
                    message.setContent("jigar");

                    mStompClient.send("/app/chat.send", new Gson().toJson(message)).subscribe();
                    window.dismiss();
                }

            });
            yaar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, " yaar  touch");
                    message.setType(ChatMessage.MessageType.STICKER);
                    message.setContent("yaar");

                    mStompClient.send("/app/chat.send", new Gson().toJson(message)).subscribe();
                    window.dismiss();

                }

            });
            lush.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, " lush select touch");
                    message.setType(ChatMessage.MessageType.STICKER);
                    message.setContent("lush");

                    mStompClient.send("/app/chat.send", new Gson().toJson(message)).subscribe();
                    window.dismiss();

                }

            });
            chill_karo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, " chill karo  touch");
                    message.setType(ChatMessage.MessageType.STICKER);
                    message.setContent("chill_karo");

                    mStompClient.send("/app/chat.send", new Gson().toJson(message)).subscribe();
                    window.dismiss();

                    ;
                }

            });
            oye.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, " oye gallery");
                    message.setType(ChatMessage.MessageType.STICKER);
                    message.setContent("oye");

                    mStompClient.send("/app/chat.send", new Gson().toJson(message)).subscribe();
                    window.dismiss();

                }

            });
            scene_on_hai.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, " scene close hai  touch");
                    message.setType(ChatMessage.MessageType.STICKER);
                    message.setContent("scene_on_hai");

                    mStompClient.send("/app/chat.send", new Gson().toJson(message)).subscribe();
                    window.dismiss();
                }
            });

        }catch (Exception e){

        }
    }

}
