package ua.naiksoftware.stompclientexample;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;


import com.google.gson.Gson;

import java.util.ArrayList;

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
    public static String currentUsername1 = "smsali97";
    public static String currentUsername2 = "mmaazt";
    public static int ctr = 0;

    private ListView messagesView;
    ChatMessageAdapter messageAdapter;

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
//        mStompClient.send("/chat.register", new Gson().toJson(user1)).subscribe();
//        mStompClient.send("/chat.register", new Gson().toJson(user2)).subscribe();

        messageAdapter = new ChatMessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        editText = (EditText) findViewById(R.id.editText);
        mStompClient.topic("/topic/public").subscribe(message -> {
            String json = message.getPayload();
            ChatMessage cm = new Gson().fromJson(json,ChatMessage.class);

            if (!cm.getType().equals(ChatMessage.MessageType.CHAT)) return;
            messageAdapter.add(cm);
            // scroll the ListView to the last added element
            messagesView.setSelection(messagesView.getCount() - 1);
        });


    }

    public void sendMessage(View view) {

        ChatMessage message = new ChatMessage();
        User user = new User();
        if (ctr++ % 2 == 0) user.setUsername(currentUsername1);
        else user.setUsername(currentUsername2);

        user.setPassword("abc123");

        message.setSender(user);
        message.setContent(editText.getText().toString());
        message.setType(ChatMessage.MessageType.CHAT);
        editText.getText().clear();

        Log.d("ch",new Gson().toJson(message));

        mStompClient.send("/chat.send", new Gson().toJson(message)).subscribe();


    }

}
