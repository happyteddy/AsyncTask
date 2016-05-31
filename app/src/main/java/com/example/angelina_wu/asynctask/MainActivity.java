package com.example.angelina_wu.asynctask;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String IMAGE_PATH = "https://developer.android.com/images/home/kk-hero.jpg";
    private AsyncTask myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void execute (View view){
        if (myTask == null) {
            myTask = new MyAsyncTask();
        }
        myTask.execute(IMAGE_PATH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myTask != null) {
            Log.d("test", "onDestroy");
            myTask.cancel(true);
        }
    }

    public  class MyAsyncTask extends AsyncTask<String, Integer, Bitmap>{
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMax(100);
            mProgressDialog.setTitle("Message");
            mProgressDialog.setMessage(" Loading ...");
            mProgressDialog.setCancelable(false);//不能取消這個彈出框，等下載完成之後再讓彈出框消失
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            Log.d("test", "show");
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String urlStr = params[0];
            try {
                URL url = new URL(urlStr);

                int totalSize = url.openConnection().getContentLength();
                long currentSize = 0;
                byte[] data = new byte[1024];
                URLConnection connection = url.openConnection();
                while (connection.getInputStream().read(data) != -1) {
                    currentSize += 1024;
                    if (totalSize > 0) // only if currentSize length is known
                        publishProgress((int) (currentSize * 100 / totalSize));
                }

                return BitmapFactory.decodeStream(url.openConnection().getInputStream()); //Decode an input stream into a bitmap.
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (!isCancelled()) {
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
                mProgressDialog.dismiss();
            }
        }
    }
}
