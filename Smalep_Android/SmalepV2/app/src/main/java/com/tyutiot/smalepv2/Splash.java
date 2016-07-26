package com.tyutiot.smalepv2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * This is the activity of "Splash".
 */
public class Splash extends AppCompatActivity {

    /**
     * Return Data coming from server
     */
    private String returnstr = null;

    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Screen portrait only
         */
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /**
         * hide actionbar & statebar
         */
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        data = (Data) getApplication();

        NetThread netThread = new NetThread();
        netThread.execute("http://www.tyutiot.com/android.php?token=sp199539&type=0");
    }

    /**
     * analyse server's Return Data & record to data
     */
    public void setData(String str) {
        char[] Rcv = str.toCharArray();
        int chari = 0;
        int Reverse = 0;
        int Counti = 0;
        while (true) {
            if (Rcv[chari] == '{') {
                Reverse = 0;
                Counti = 0;
            } else if (Rcv[chari] != '{' && Rcv[chari] != '+' && Rcv[chari] != '}') {
                Reverse = Reverse * 10 + Integer.parseInt(String.valueOf(Rcv[chari]));
            } else if (Rcv[chari] == '+' || Rcv[chari] == '}') {
                if (Counti == 0) {
                    data.setSensor(0, Reverse);
                } else if (Counti == 1) {
                    data.setSensor(1, Reverse);
                } else if (Counti == 2) {
                    data.setUrgent(Reverse);
                } else if (Counti == 3) {
                    data.setCmd(0, Reverse);
                } else if (Counti == 4) {
                    data.setCmd(1, Reverse);
                } else if (Counti == 5) {
                    data.setCmd(2, Reverse);
                } else if (Counti == 6) {
                    data.setCmd(3, Reverse);
                } else if (Counti == 7) {
                    data.setCmd(4, Reverse);
                } else if (Counti == 8) {
                    data.setCmd(5, Reverse);
                }
                Reverse = 0;
                Counti++;
            }
            if (Rcv[chari] == '}') {
                break;
            }
            chari++;

        }

    }

    /**
     * Initial Network AsynTask inner class
     */
    class NetThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {

                HttpURLConnection huc;
                URL url = new URL(new String(params[0]));
                huc = (HttpURLConnection) url.openConnection();
                huc.setConnectTimeout(3000);
                huc.setDoInput(true);
                huc.setRequestMethod("GET");
                if (huc.getResponseCode() == 200) {
                    Scanner sc = new Scanner(huc.getInputStream());
                    result = sc.next();
                    sc.close();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            returnstr = s;
            /**
             * define shortest show time
             */
            final int SPLASH_DISPLAY_DELAY = 1000;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (returnstr != null) {
                        Toast toast = Toast.makeText(getApplicationContext(), "欢迎回来", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 200);
                        toast.show();
                        setData(returnstr);

                        Intent mainIntent = new Intent(Splash.this, Main.class);
                        Splash.this.startActivity(mainIntent);

                        Splash.this.finish();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "请检查网络后重启应用", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 200);
                        toast.show();
                    }

                }
            }, SPLASH_DISPLAY_DELAY);

        }

    }
}
