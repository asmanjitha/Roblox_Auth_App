package com.example.robolex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    String api_key = "5yxJTxrURRo4oedfOoRAIqTyHEXINpm4pOvHql81K2AbPEDa00EF20Ap5E6c";
    String url = "http://3.129.210.150:5000/get_notifications";
    String username;
    String profile_pic_url;
    ArrayList<String> notif_msgs;
    ArrayList<String> notif_datetimes;
    ArrayList<String> notif_pics;

    String[] msgs;
    String[] datetimes;
    String[] pics;

    ListView listView;
    ProgressBar progressBar;
    ImageView profilePic;

    CustomAdapter adapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notif_datetimes = new ArrayList<String>();
        notif_msgs = new ArrayList<String>();
        notif_pics = new ArrayList<String>();

        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar5);
        profilePic = findViewById(R.id.profilepic2);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            profile_pic_url = extras.getString("profile_pic_url");
        }
        if(username != null){
            loadUserPic();
            runRequest();
        }
    }

    public void goBack(View view){
        Intent intent = new Intent(getApplicationContext(), ConnectedStateActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("profile_pic_url", profile_pic_url);
        startActivity(intent);
    }

    private void loadUserPic(){
        Picasso.get().load(profile_pic_url).resize(75, 75).centerCrop().into(profilePic);
    }

    private void runRequest() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("api_key", api_key);
            final String requestBody = jsonBody.toString();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response_) {
                    if (! isFinishing()) {;
                        try {
                            JSONArray totalRecs = response_.getJSONArray("notifications");
                            int arrayLength = totalRecs.length();
                            for(int i = 0; i < arrayLength; i++)
                            {
                                JSONArray values = totalRecs.getJSONArray(i);
                                notif_datetimes.add(values.getString(3));
                                notif_msgs.add(values.getString(4));
                                notif_pics.add(values.getString(5));
                            }
                            datetimes = new String[arrayLength];
                            msgs = new String[arrayLength];
                            pics = new String[arrayLength];

                            datetimes = notif_datetimes.toArray(datetimes);
                            msgs = notif_msgs.toArray(msgs);
                            pics = notif_pics.toArray(pics);
                            context = getApplicationContext();
                            Log.d("ROBOLEX:", String.valueOf(datetimes.length));
                            adapter = new CustomAdapter(context, msgs, pics, datetimes);
                            listView.setAdapter(adapter);
                            if(progressBar != null){
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    runRequest();
                                }
                            }, 1000);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ROBOLEX: ", error.toString());
//                    Toast toast = Toast.makeText(getApplicationContext(), "Loading data", Toast.LENGTH_SHORT);
//                    toast.show();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runRequest();
                        }
                    }, 1000);
                }
            });

            queue.add(request);
        } catch (JSONException e) {
            Log.e("JSON Exception", e.toString());
//            Toast toast = Toast.makeText(getApplicationContext(), "Loading data", Toast.LENGTH_SHORT);
//            toast.show();
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runRequest();
                }
            }, 1000);
        }
    }
}


class CustomAdapter extends ArrayAdapter<String>{
    Context context;
    String[] msgs;
    String[] images;
    String[] datetimes;
    CustomAdapter(Context context, String[] msgs, String[] images, String[] datetimes){
        super(context, R.layout.notification_card, R.id.Notif_msg, msgs);
        this.context = context;
        this.msgs = msgs;
        this.images = images;
        this.datetimes = datetimes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.notification_card, parent, false);

        ImageView notif_pic = row.findViewById(R.id.notif_image);
        TextView notif = row.findViewById(R.id.Notif_msg);
        TextView date = row.findViewById(R.id.notif_date);

        Picasso.get().load(images[position]).resize(100, 100).centerCrop().into(notif_pic);
        notif.setText(msgs[position]);
        date.setText(datetimes[position]);

        return row;
    }
}

