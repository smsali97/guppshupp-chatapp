package ua.naiksoftware.stompclientexample;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import ua.naiksoftware.R;
import ua.naiksoftware.stompclientexample.model.User;
import ua.naiksoftware.stompclientexample.util.ChatUtil;

import static ua.naiksoftware.stompclientexample.util.ChatUtil.ANDROID_EMULATOR_LOCALHOST;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.SERVER_PORT;
import static ua.naiksoftware.stompclientexample.util.ChatUtil.isConnected;

public class LoginActivity extends AppCompatActivity {

    Button login, createAcc;
    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_really);



        login=(Button) findViewById(R.id.login);
        createAcc=(Button) findViewById(R.id.cacc);
        username=(EditText) findViewById(R.id.usrnme);
        password=(EditText) findViewById(R.id.pwd);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View view){
        String pd = getMd5(password.getText().toString());
        String uname = username.getText().toString();

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);



        String url = "http://" + ANDROID_EMULATOR_LOCALHOST + ":" + SERVER_PORT + "/checkPassword";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Success s = new Gson().fromJson(response,Success.class);

                if (s.isSuccess() && isConnected) {
                    Toast sucmsg = Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT);
                    View v = sucmsg.getView();
                    view.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                    sucmsg.show();

                    ChatUtil.currentUsername = uname;
                    Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast unsucmsg= Toast.makeText( getApplicationContext(), "Login Unsuccessful!", Toast.LENGTH_SHORT);
                    View v = unsucmsg.getView();
                    view.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                    unsucmsg.show();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast unsucmsg= Toast.makeText( getApplicationContext(), "Login Unsuccessful!", Toast.LENGTH_SHORT);
                View v = unsucmsg.getView();
                view.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                unsucmsg.show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("Content-Type", "application/json");
                MyData.put("username", uname);
                MyData.put("password", pd); //Add the data you'd like to send to the server.
                return MyData;
            }
        };


        MyRequestQueue.add(MyStringRequest);


    }
    public void createAccount(View view) {
        String pd = password.getText().toString();
        String uname = username.getText().toString();
        String warning = "";
        boolean hasUpper = !pd.equals(pd.toLowerCase());
        boolean length6 = pd.length() >= 6;
        boolean hasLower = !pd.equals(pd.toUpperCase());
        boolean hasSpecial = !pd.matches("[A-Za-z0-9 ]*");//Checks at least one char is not alpha numeric
        Toast msg;
        if (!length6) {
            warning += "The length of password should be at least 6!\n";
        }
        if (!hasUpper) {
            warning += "The password should have at least one uppercase character!\n";
        }
        if (!hasLower) {
            warning += "The password should have at least one lowercase character!";
        }
        if (!hasSpecial) {
            warning += "The password should have at least one special character!\n";
        }
        warning = warning.length() == 0 ? "User registered successfully!" : warning;

        User user2 = new User();
        user2.setUsername(uname);
        user2.setPassword(getMd5(pd));

        msg= Toast.makeText(getApplicationContext(), warning, Toast.LENGTH_LONG);
        View v = msg.getView();
        view.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        msg.show();

        if (ChatUtil.isConnected && warning.equals("User registered successfully!") ) {

            ChatUtil.mStompClient.send("/app/chat.register", new Gson().toJson(user2)).subscribe();

        ChatUtil.currentUsername = uname;
        Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
        startActivity(intent);
        finish();
        }
        else {
            if (!isConnected) warning += "\nSocket seems to be closed. Try again later!";
            msg.cancel();
            msg= Toast.makeText(getApplicationContext(), warning, Toast.LENGTH_LONG);
            v = msg.getView();
            view.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            msg.show();
        }

    }

    public static String getMd5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}


class Success {
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private boolean success;


}