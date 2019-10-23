package ua.naiksoftware.stompclientexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;


import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.amazonaws.services.s3.AmazonS3Client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ua.naiksoftware.R;
import ua.naiksoftware.stompclientexample.model.ChatMessage;
import ua.naiksoftware.stompclientexample.model.User;

import com.amazonaws.mobile.client.AWSMobileClient;

import static ua.naiksoftware.stompclientexample.util.ChatUtil.ANDROID_EMULATOR_LOCALHOST;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.SERVER_PORT;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.currentUsername;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.mStompClient;
import java.io.File;

public class GroupChatActivity extends AppCompatActivity {

    EditText editText;


    private ListView messagesView;
    ChatMessageAdapter messageAdapter;

    private PopupWindow window;
<<<<<<< HEAD
    private String TAG = "PUBLIC-CHAT";
=======
    private String TAG = "Main Activity";
    S3Services s3;
>>>>>>> f00b305b48a8e95f40bf8bd1ee33926ab0ad0e11

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat);
        // This is where we write the mesage
        editText = (EditText) findViewById(R.id.editText);
        AWSMobileClient.getInstance().initialize(this).execute();

        s3 = new S3Services(getApplication(),"","");

        File file = new File(".\\LICENSE");
        s3.upload(file);

        messageAdapter = new ChatMessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        editText = (EditText) findViewById(R.id.editText);

        String url = "http://" + ANDROID_EMULATOR_LOCALHOST + ":" + SERVER_PORT + "/chatMessages/public";

        StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                ChatMessage[] messages = new Gson().fromJson(response, ChatMessage[].class);

                for (ChatMessage message: messages) {
                    messageAdapter.add(message);
                    // scroll the ListView to the last added element
                    messagesView.setSelection(messagesView.getCount() - 1);
                }

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast unsucmsg= Toast.makeText( getApplicationContext(), "Couldn't load previous messages!", Toast.LENGTH_SHORT);
                unsucmsg.show();
            }

        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                return MyData;
            }
        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        MyRequestQueue.add(MyStringRequest);


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


        message.setSender(user);
        message.setContent(editText.getText().toString());
        message.setType(ChatMessage.MessageType.CHAT);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yy");
        message.setTimestamp(df.format(new Date()));
        editText.getText().clear();

        Log.d("PUBLIC-CHAT-MESSAGE",new Gson().toJson(message));

        mStompClient.send("/app/chat.send", new Gson().toJson(message)).subscribe();
    }

    public void ShowPopupWindow(View view){
        try {
            ImageView jigar, yaar, lush, oye, scene_on_hai, chill_karo, my_image;

            LayoutInflater inflater = (LayoutInflater) GroupChatActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
