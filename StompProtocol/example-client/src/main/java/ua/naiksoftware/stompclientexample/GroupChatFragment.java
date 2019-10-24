package ua.naiksoftware.stompclientexample;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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

import com.amazonaws.mobile.client.AWSMobileClient;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ua.naiksoftware.R;
import ua.naiksoftware.stompclientexample.model.ChatMessage;
import ua.naiksoftware.stompclientexample.model.User;

import static android.app.Activity.RESULT_OK;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.ANDROID_EMULATOR_LOCALHOST;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.SERVER_PORT;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.currentUsername;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.mStompClient;



public class GroupChatFragment extends Fragment {

    EditText editText;

    private static int CHOOSING_IMAGE_REQUEST = 1234;


    private ListView messagesView;
    ChatMessageAdapter messageAdapter;

    private PopupWindow window;
    private  ImageButton messageButton;
    private  ImageButton fileButton;
    private  ImageButton stickerButton;
    private String TAG = "PUBLIC-CHAT";


    private Uri fileUri;
    S3Services s3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.group_chat, container, false);
        AWSMobileClient.getInstance().initialize(this.getContext()).execute();

        s3 = new S3Services(this.getContext(),"AKIA47LH6J6I7EJAREGG","3j5ObIgtwO7RHRhvmFlqPMBc1pZLrZ18CphPkQPB");

        editText = (EditText) v.findViewById(R.id.editText);
        messageButton = (ImageButton) v.findViewById(R.id.send_button);
        fileButton = (ImageButton) v.findViewById(R.id.file_button);
        stickerButton = (ImageButton) v.findViewById(R.id.sticker_button);
        messageAdapter = new ChatMessageAdapter(v.getContext());
        messagesView = (ListView) v.findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        editText = (EditText) v.findViewById(R.id.editText);

        getPreviousMessages();
        subscribeOncomingMessages();

        messageButton.setOnClickListener(this::sendMessage);
        stickerButton.setOnClickListener(this::ShowPopupWindow);
        fileButton.setOnClickListener(this::onClick);
        return v;
    }

    private void subscribeOncomingMessages() {
        mStompClient.topic("/topic/public")
                .subscribe((message) -> {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String json = message.getPayload();
                            Log.d("PAYLOAD", json);

                            ChatMessage cm = new Gson().fromJson(json, ChatMessage.class);

                            if (cm.getType().equals(ChatMessage.MessageType.LEAVE)) return;
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

    private void getPreviousMessages() {
        String url = "http://" + ANDROID_EMULATOR_LOCALHOST + ":" + SERVER_PORT + "/chatMessages/public";

        StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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
                return MyData;
            }
        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        MyRequestQueue.add(MyStringRequest);
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


    private void showChoosingFile() {
        /*Intent intent = new Intent();
        intent.setType("image/*, pdf/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);*/
        //intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        Intent intent = new Intent(this.getContext(), FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowImages(true)
                .enableImageCapture(true)
                .setMaxSelection(1)
                .setSkipZeroSizeFiles(true)
                .build());
        startActivityForResult(intent, CHOOSING_IMAGE_REQUEST);
        //startActivityForResult(Intent.createChooser(intent, "Select File"),  CHOOSING_IMAGE_REQUEST);
    }

    public void onClick(View view) {
        int i = view.getId();

        if (i == R.id.file_button) {
            showChoosingFile();
//        } else if (i == R.id.btn_upload) {
//            uploadFile();
//        } else if (i == R.id.btn_download) {
//            downloadFile();
//        }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            Log.d("File picker result",  files.size() + "");
            //fileUri = data.getData();
            //File file = new File(fileUri.getPath());//create path from uri

            //s3.upload(file);
        }

    }


}

