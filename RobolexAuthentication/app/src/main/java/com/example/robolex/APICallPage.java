package com.example.robolex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class APICallPage extends AppCompatActivity {
    String username;
    String cookie;
    TextView username_txt;
    TextView cookie_txt;
    TextView response;
    String url = "http://3.129.210.150:5000/save_security_cookie";
    String api_key = "5yxJTxrURRo4oedfOoRAIqTyHEXINpm4pOvHql81K2AbPEDa00EF20Ap5E6c";
    ImageView greenBackground;
    ImageView redBackground;
    ImageView redButton;
    ImageView greenButton;
    TextView statusText;
    JSONArray jArray;
    String profile_pic_url;
    ImageView profilepicASM;
    boolean connectionPassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectionPassed = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            cookie = extras.getString("cookie");
            profile_pic_url = extras.getString("profile_pic_url");

//            Picasso.get().load(profile_pic_url).into(profilepic);
        }
        setContentView(R.layout.activity_apicall_page);

        profilepicASM = findViewById(R.id.profilepicASM);

        if (username != null && cookie != null) {
            username_txt = (TextView) findViewById(R.id.textView2);
            response = (TextView) findViewById(R.id.textView5);
            username_txt.setText(username);
            if(profile_pic_url!= null && profilepicASM!= null){
                loadUserPic();
            }
            runRequest();
        }

        redButton = findViewById(R.id.redButton);
        redBackground = findViewById(R.id.redBackground);
        statusText = findViewById(R.id.statusText);

    }

    private void loadUserPic(){
        Picasso.get().load(profile_pic_url).resize(50, 50).centerCrop().into(profilepicASM);
    }



    private void runRequest() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("api_key", api_key);
            jsonBody.put("sec_cookie", cookie);
            final String requestBody = jsonBody.toString();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response_) {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("status", "security cookie saved");
                    } catch (JSONException e) {
                        Log.e("ROBOLEX:","JSON Exception "+ e.toString());
                    }
                    if (! isFinishing()) {
                        if(response_.toString().equals(json.toString())){
                            statusText.setText("CONNECTED");
                            Log.d("ROBOLEX:", "Status matched");
                            if(!connectionPassed){
                                Log.d("ROBOLEX: ", "Chaging activity");
                                connectionPassed = true;
                                changeActivity();
                            }

                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    response.setText(error.toString());
//                    Toast toast = Toast.makeText(getApplicationContext(), "Network error occured", Toast.LENGTH_SHORT);
//                    toast.show();
                }
            });

            queue.add(request);
        } catch (JSONException e) {
            Log.e("JSON Exception", e.toString());
//            Toast toast = Toast.makeText(getApplicationContext(), "An error occured", Toast.LENGTH_SHORT);
//            toast.show();
        }
    }

    public void changeActivity(){
        Intent i = new Intent(getApplicationContext(), ConnectedStateActivity.class);
        i.putExtra("username", username);
        i.putExtra("profile_pic_url", profile_pic_url);
        startActivity(i);
    }

    public void reRunConnecting(View view){
        if (username != null && cookie != null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Connecting to server", Toast.LENGTH_SHORT);
            runRequest();
            toast.show();

        }
    }

}