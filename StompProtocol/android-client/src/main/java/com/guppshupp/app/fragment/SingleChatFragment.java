package com.guppshupp.app.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ua.naiksoftware.R;

import com.guppshupp.app.services.S3Services;
import com.guppshupp.app.adapter.ChatMessageAdapter;
import com.guppshupp.app.model.ChatMessage;
import com.guppshupp.app.model.User;
import com.guppshupp.app.util.ChatUtil;

import static com.guppshupp.app.util.ChatUtil.ANDROID_EMULATOR_LOCALHOST;
import static com.guppshupp.app.util.ChatUtil.SERVER_PORT;
import static com.guppshupp.app.util.ChatUtil.currentUsername;
import static com.guppshupp.app.util.ChatUtil.mStompClient;


public class SingleChatFragment extends Fragment {

    private ListView messagesView;
    ChatMessageAdapter messageAdapter;
    S3Services s3;

    private PopupWindow window;
    private ImageButton messageButton;
    private  ImageButton fileButton;
    private  ImageButton stickerButton;
    EditText editText;

    private User receiver;
    private String TAG = "PRIVATE-CHAT";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String SECRET_KEY = "";
        String ACCESS_KEY = "";
        View v = inflater.inflate(R.layout.single_chat_fragment,container,false);


        SingleChatFragment sc = this;

        String url = String.format("http://%s:%s/credentials",ANDROID_EMULATOR_LOCALHOST,SERVER_PORT);
        StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String SECRET_KEY, ACCESS_KEY;
                String[] messages = new Gson().fromJson(response, String[].class);
                SECRET_KEY = messages[0];
                ACCESS_KEY = messages[1];

                s3 = new S3Services(sc.getContext(),SECRET_KEY,ACCESS_KEY);

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast unsucmsg = Toast.makeText(getContext(), "Couldn't connnect to S3!", Toast.LENGTH_SHORT);
                unsucmsg.show();
            }

        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                return MyData;
            }
        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        MyRequestQueue.add(MyStringRequest);

        editText = (EditText) v.findViewById(R.id.editText);
        messageButton = (ImageButton) v.findViewById(R.id.send_button);
        fileButton = (ImageButton) v.findViewById(R.id.file_button);
        stickerButton = (ImageButton) v.findViewById(R.id.sticker_button);
        messageAdapter = new ChatMessageAdapter(v.getContext());
        messagesView = (ListView) v.findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        editText = (EditText) v.findViewById(R.id.editText);
        fileButton = (ImageButton) v.findViewById(R.id.file_button);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);

        fab.setOnClickListener(this::showPrompt);
        messageButton.setOnClickListener(this::sendMessage);
        stickerButton.setOnClickListener(this::ShowPopupWindow);
        fileButton.setOnClickListener(this::onClick);

        subscribeOncomingMessages();
        Toast t = Toast.makeText(getContext(),"Click on floating chat bubble to begin chat..", Toast.LENGTH_LONG);
        t.show();
        return v;
    }


    private void showPrompt(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Please enter the username with whom you want to chat");

        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Username");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            validateUser(input.getText().toString());
            dialog.cancel();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }

    private void validateUser(String username) {
        String url = String.format("http://%s:%s/user?username=%s",ANDROID_EMULATOR_LOCALHOST,SERVER_PORT,username);

        if (!ChatUtil.isConnected) {
            Toast t = Toast.makeText(getContext(),"Couldnt establish connection with server! :( ", Toast.LENGTH_LONG);
            t.show();
            return;
        }



        StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                receiver = new Gson().fromJson(response,User.class);

                if (receiver != null) {
                    Toast t = Toast.makeText(getContext(),"Setting up chat..", Toast.LENGTH_SHORT);
                    t.show();

                    messageAdapter = new ChatMessageAdapter(getContext());
                    messagesView.setAdapter(messageAdapter);

                    getPreviousMessages();
                }
                else {
                    Toast t = Toast.makeText(getContext(),"Username does not exist!", Toast.LENGTH_SHORT);
                    t.show();
                }


            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast unsucmsg = Toast.makeText(getContext(), "Couldn't verify it right now", Toast.LENGTH_SHORT);
                unsucmsg.show();
            }

        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                return MyData;
            }
        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        MyRequestQueue.add(MyStringRequest);
    }

    public void sendMessage(View view) {

        if (receiver == null) {
            Toast t = Toast.makeText(getContext(),"Please select a person to send the text first",Toast.LENGTH_LONG);
            t.show();
            return;
        }

        ChatMessage message = new ChatMessage();
        User user = new User();
        user.setUsername(currentUsername);



        message.setSender(user);
        message.setReceiver(receiver);
        message.setContent(editText.getText().toString());
        message.setType(ChatMessage.MessageType.CHAT);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yy");
        message.setTimestamp(df.format(new Date()));
        editText.getText().clear();

        Log.d("PRIVATE-CHAT-MESSAGE",new Gson().toJson(message));

        mStompClient.send("/app/chat.send-private", new Gson().toJson(message)).subscribe();
    }

    public void ShowPopupWindow(View view){
        try {
            if (receiver == null) {
                Toast t = Toast.makeText(getContext(),"Please select a person to send the text first",Toast.LENGTH_LONG);
                t.show();
                return;
            }
            ImageView jigar, yaar, lush, oye, scene_on_hai, chill_karo, my_image;

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            message.setSender(user);
            message.setReceiver(receiver);

            jigar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, " jigar call press ");

                    message.setType(ChatMessage.MessageType.STICKER);
                    message.setContent("jigar");

                    mStompClient.send("/app/chat.send-private", new Gson().toJson(message)).subscribe();
                    window.dismiss();
                }

            });
            yaar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, " yaar  touch");
                    message.setType(ChatMessage.MessageType.STICKER);
                    message.setContent("yaar");

                    mStompClient.send("/app/chat.send-private", new Gson().toJson(message)).subscribe();
                    window.dismiss();

                }

            });
            lush.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, " lush select touch");
                    message.setType(ChatMessage.MessageType.STICKER);
                    message.setContent("lush");

                    mStompClient.send("/app/chat.send-private", new Gson().toJson(message)).subscribe();
                    window.dismiss();

                }

            });
            chill_karo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, " chill karo  touch");
                    message.setType(ChatMessage.MessageType.STICKER);
                    message.setContent("chill_karo");

                    mStompClient.send("/app/chat.send-private", new Gson().toJson(message)).subscribe();
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

                    mStompClient.send("/app/chat.send-private", new Gson().toJson(message)).subscribe();
                    window.dismiss();

                }

            });
            scene_on_hai.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, " scene close hai  touch");
                    message.setType(ChatMessage.MessageType.STICKER);
                    message.setContent("scene_on_hai");

                    mStompClient.send("/app/chat.send-private", new Gson().toJson(message)).subscribe();
                    window.dismiss();
                }
            });

        }catch (Exception e){

        }
    }

    private void getPreviousMessages() {
        String url = "http://" + ANDROID_EMULATOR_LOCALHOST + ":" + SERVER_PORT + "/chatMessages/private";

        if (receiver == null) {
            Toast t = Toast.makeText(getContext(),"Please select a person to send the text first",Toast.LENGTH_LONG);
            t.show();
            return;
        }

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                ChatMessage[] messages = new Gson().fromJson(response, ChatMessage[].class);

                for (ChatMessage message : messages) {
                    messageAdapter.add(message);
                    // scroll the ListView to the last added element
                    messagesView.setSelection(messagesView.getCount() - 1);
                }

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast unsucmsg = Toast.makeText(getContext(), "Couldn't load previous messages!", Toast.LENGTH_SHORT);
                unsucmsg.show();
            }

        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("user1", currentUsername);
                MyData.put("user2", receiver.getUsername());
                return MyData;
            }
        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        MyRequestQueue.add(MyStringRequest);
    }

    private void subscribeOncomingMessages() {
        mStompClient.topic("/topic/private")
                .subscribe((message) -> {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (receiver == null) return;

                            String json = message.getPayload();
                            Log.d("PAYLOAD", json);

                            ChatMessage cm = new Gson().fromJson(json, ChatMessage.class);

                            boolean b1 = cm.getSender().getUsername().equals(currentUsername) && cm.getReceiver().getUsername().equals(receiver.getUsername());
                            boolean b2 = cm.getSender().getUsername().equals(receiver.getUsername()) && cm.getReceiver().getUsername().equals(currentUsername);

                            if (! (b1 || b2)) return;

                            if (cm.getType().equals(ChatMessage.MessageType.LEAVE)) return;
                            if (cm.getTimestamp() == null) {
                                SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yy");
                                cm.setTimestamp(df.format(new Date()));
                            }
                            cm.setTimestamp(ChatUtil.getCurrentTimeinKarachi(cm.getTimestamp()));
                            messageAdapter.add(cm);
                            // scroll the ListView to the last added element
                            messagesView.setSelection(messagesView.getCount() - 1);
                        }
                    });
                });
    }

    private void showChoosingFile() {

        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(getContext(),properties);
        dialog.setTitle("Select a File");

        SingleChatFragment scf = this;

        View view = this.getView();
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                Log.d("File picker file picked", files[0]);
                s3.upload(new File(files[0]),scf);
            }
        });
        dialog.show();
    }

    public void onClick(View view) {
        if (receiver == null) {
            Toast t = Toast.makeText(getContext(),"Please select a person to send the text first",Toast.LENGTH_LONG);
            t.show();
            return;
        }

        int i = view.getId();

        if (i == R.id.file_button) {

            showChoosingFile();
        }
    }


    public void addLink(String url, String name) {
        String formattedUrl = String.format("Sent a file: <a href=%s>%s</a>",url,name);
        User user = new User();
        user.setUsername(currentUsername);
        ChatMessage cm = new ChatMessage();
        cm.setSender(user);
        cm.setReceiver(receiver);
        cm.setType(ChatMessage.MessageType.FILE);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yy");
        cm.setTimestamp(df.format(new Date()));
        cm.setContent(formattedUrl);
        mStompClient.send("/app/chat.send-private", new Gson().toJson(cm)).subscribe();
    }



}
