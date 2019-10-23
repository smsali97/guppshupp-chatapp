package ua.naiksoftware.stompclientexample;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import ua.naiksoftware.R;
import ua.naiksoftware.stompclientexample.model.ChatMessage;
import ua.naiksoftware.stompclientexample.util.ChatUtil;

import static ua.naiksoftware.stompclientexample.util.ChatUtil.ANDROID_EMULATOR_LOCALHOST;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.SERVER_PORT;

public class SingleChatFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.single_chat_fragment,container,false);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);

        fab.setOnClickListener(this::showPrompt);


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
        String url = "http://" + ANDROID_EMULATOR_LOCALHOST + ":" + SERVER_PORT + "/checkUsername";

        if (!ChatUtil.isConnected) {
            Toast t = Toast.makeText(getContext(),"Couldnt establish connection with server! :( ", Toast.LENGTH_LONG);
            t.show();
            return;
        }



        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Success success = new Gson().fromJson(response,Success.class);

                if (success.isSuccess()) {
                    Toast t = Toast.makeText(getContext(),"Username does exist!", Toast.LENGTH_SHORT);
                    t.show();
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
                MyData.put("username", username);
                return MyData;
            }
        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        MyRequestQueue.add(MyStringRequest);
    }
}
