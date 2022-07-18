package com.example.robolex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectedStateActivity extends AppCompatActivity {

    String username;
    TextView username_txt;
    ImageView profile_pic;
    String profile_pic_url;
    ImageView greenButton;
    ImageView notificationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_state);
        getSupportActionBar().hide();
        Bundle extras = getIntent().getExtras();

        profile_pic = findViewById(R.id.userImage);
        greenButton = findViewById(R.id.greenButton);
        notificationButton = findViewById(R.id.notificationBtn);

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNotifications();
            }
        });


        if (extras != null) {
            username = extras.getString("username");
            profile_pic_url = extras.getString("profile_pic_url");
        }
        if (username != null ) {
            username_txt = (TextView) findViewById(R.id.textView2);
            username_txt.setText(username);
            Picasso.get().load(profile_pic_url).into(profile_pic);
        }
    }


    public void loadNotifications(){
        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        if(username != null){
            intent.putExtra("username", username);
            intent.putExtra("profile_pic_url", profile_pic_url);
        }
        startActivity(intent);

    }

    public void loadImage(View view){
        Log.d("ROBOLEX:", profile_pic_url);
        Picasso.get().load(profile_pic_url).into(profile_pic);
    }

//    public void loadImage(){
//        Log.d("ROBOLEX:", profile_pic_url);
//        Picasso.get().load("https://tr.rbxcdn.com/1e2f4a217f3aebe1c013af7f95cc88f9/150/150/AvatarHeadshot/Png").into(profile_pic);
//    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("ROBOLEX: ", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }


}