package com.tyutiot.smalepv2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * This is the main activity of the application.
 */
public class Main extends AppCompatActivity {
    /**
     * define CardSwitch textsize
     */
    private final int
            smalltext = 15,
            largetext = 25;
    private SwipeRefreshLayout srl;
    private TextView tvlog, tvtemp, tvtempsymbol, tvhumidity, tvhumiditysymbol, tvurgent;
    private LinearLayout
            swlight, lightswll,
            swfan, fanswll,
            swhumidifier, humidifierswll,
            swheater, heaterswll;
    private TextView
            lighttv1, lighttv2, lighttv3, lighttv4, lighttv5,
            fantv1, fantv2, fantv3, fantv4, fantv5,
            humidifiertv1, humidifiertv2, humidifiertv3, humidifiertv4, humidifiertv5,
            heatertv1, heatertv2, heatertv3, heatertv4, heatertv5;
    private ImageView ivurgent;
    private SeekBar sblight, sbfan;
    private ScrollView sv;
    private Data data;
    private int tempVal;
    private int swtype = 0;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = (Data) getApplication();

        swlight = (LinearLayout) this.findViewById(R.id.swlight);
        lightswll = (LinearLayout) this.findViewById(R.id.lightswll);
        lightswll.setTranslationX(90);
        lighttv1 = (TextView) this.findViewById(R.id.lighttv1);
        lighttv2 = (TextView) this.findViewById(R.id.lighttv2);
        lighttv3 = (TextView) this.findViewById(R.id.lighttv3);
        lighttv4 = (TextView) this.findViewById(R.id.lighttv4);
        lighttv5 = (TextView) this.findViewById(R.id.lighttv5);
        lighttv1.setTextSize(smalltext);
        lighttv2.setTextSize(smalltext);
        lighttv3.setTextSize(largetext);
        lighttv4.setTextSize(smalltext);
        lighttv5.setTextSize(smalltext);

        swfan = (LinearLayout) this.findViewById(R.id.swfan);
        fanswll = (LinearLayout) this.findViewById(R.id.fanswll);
        fanswll.setTranslationX(90);
        fantv1 = (TextView) this.findViewById(R.id.fantv1);
        fantv2 = (TextView) this.findViewById(R.id.fantv2);
        fantv3 = (TextView) this.findViewById(R.id.fantv3);
        fantv4 = (TextView) this.findViewById(R.id.fantv4);
        fantv5 = (TextView) this.findViewById(R.id.fantv5);
        fantv1.setTextSize(smalltext);
        fantv2.setTextSize(smalltext);
        fantv3.setTextSize(largetext);
        fantv4.setTextSize(smalltext);
        fantv5.setTextSize(smalltext);

        swhumidifier = (LinearLayout) this.findViewById(R.id.swhumidifier);
        humidifierswll = (LinearLayout) this.findViewById(R.id.humidifierswll);
        humidifierswll.setTranslationX(90);
        humidifiertv1 = (TextView) this.findViewById(R.id.humidifiertv1);
        humidifiertv2 = (TextView) this.findViewById(R.id.humidifiertv2);
        humidifiertv3 = (TextView) this.findViewById(R.id.humidifiertv3);
        humidifiertv4 = (TextView) this.findViewById(R.id.humidifiertv4);
        humidifiertv5 = (TextView) this.findViewById(R.id.humidifiertv5);
        humidifiertv1.setTextSize(smalltext);
        humidifiertv2.setTextSize(smalltext);
        humidifiertv3.setTextSize(largetext);
        humidifiertv4.setTextSize(smalltext);
        humidifiertv5.setTextSize(smalltext);

        swheater = (LinearLayout) this.findViewById(R.id.swheater);
        heaterswll = (LinearLayout) this.findViewById(R.id.heaterswll);
        heaterswll.setTranslationX(90);
        heatertv1 = (TextView) this.findViewById(R.id.heatertv1);
        heatertv2 = (TextView) this.findViewById(R.id.heatertv2);
        heatertv3 = (TextView) this.findViewById(R.id.heatertv3);
        heatertv4 = (TextView) this.findViewById(R.id.heatertv4);
        heatertv5 = (TextView) this.findViewById(R.id.heatertv5);
        heatertv1.setTextSize(smalltext);
        heatertv2.setTextSize(smalltext);
        heatertv3.setTextSize(largetext);
        heatertv4.setTextSize(smalltext);
        heatertv5.setTextSize(smalltext);

        tvlog = (TextView) this.findViewById(R.id.tvlog);

        tvtemp = (TextView) this.findViewById(R.id.tvtemp);
        tvtempsymbol = (TextView) this.findViewById(R.id.tvtempsymbol);
        tvhumidity = (TextView) this.findViewById(R.id.tvhumidity);
        tvhumiditysymbol = (TextView) this.findViewById(R.id.tvhumidititysymbol);
        tvurgent = (TextView) this.findViewById(R.id.tvurgent);

        sblight = (SeekBar) this.findViewById(R.id.sblight);
        sbfan = (SeekBar) this.findViewById(R.id.sbfan);

        ivurgent = (ImageView) this.findViewById(R.id.ivurgent);

        sv = (ScrollView) this.findViewById(R.id.sv);

        setData();

        /**
         * PullToRefresh
         */
        srl = (SwipeRefreshLayout) findViewById(R.id.srl);
        srl.setProgressBackgroundColorSchemeResource(R.color.bgcolor);
        srl.setColorSchemeResources(R.color.processcolor);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rFlashOut();
                ServerConnection conn = new ServerConnection(2, 0);
                conn.doServerConnection();
                conn = new ServerConnection(2, 1);
                conn.doServerConnection();
                conn = new ServerConnection(3);
                conn.doServerConnection();
            }
        });

        /**
         * CardSwitch of light/fan/humidifier/heater
         */
        swlight.setOnTouchListener(new View.OnTouchListener() {
            float x1, x2;
            float y1, y2;
            boolean keep;
            int type = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    type = 0;
                    x1 = event.getX();
                    y1 = event.getY();
                    sv.requestDisallowInterceptTouchEvent(true);
                    srl.setEnabled(false);
                    keep = true;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    x2 = event.getX();
                    y2 = event.getY();
                    if (Math.abs(x1 - x2) > 20) {
                        if (type == 0) {
                            type = 1;
                            keep = true;
                        }
                    }
                    if (Math.abs(y1 - y2) > 20) {
                        if (type == 0) {
                            type = 2;
                            sv.requestDisallowInterceptTouchEvent(false);
                            srl.setEnabled(true);
                            keep = false;
                        }
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (type == 1) {
                        x2 = event.getX();
                        if (x2 - x1 > 100) {
                            switchChange(0, 0);
                            swtype = 1;
                            swRight(lightswll, lighttv1, lighttv2, lighttv3, lighttv4, lighttv5);
//                            Snackbar.make(tvlight, "水平右划", Snackbar.LENGTH_SHORT).show();
                        } else if (x1 - x2 > 100) {
                            switchChange(0, 1);
                            swtype = 2;
                            swLeft(lightswll, lighttv1, lighttv2, lighttv3, lighttv4, lighttv5);
//                            Snackbar.make(tvlight, "水平左滑", Snackbar.LENGTH_SHORT).show();
                        }
                        sv.requestDisallowInterceptTouchEvent(false);
                        srl.requestDisallowInterceptTouchEvent(false);
                        keep = true;
                    }
                    srl.setEnabled(true);
                }
                return keep;
            }
        });

        swfan.setOnTouchListener(new View.OnTouchListener() {
            float x1, x2;
            float y1, y2;
            boolean keep;
            int type = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    type = 0;
                    x1 = event.getX();
                    y1 = event.getY();
                    sv.requestDisallowInterceptTouchEvent(true);
                    srl.setEnabled(false);
                    keep = true;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    x2 = event.getX();
                    y2 = event.getY();
                    if (Math.abs(x1 - x2) > 20) {
                        if (type == 0) {
                            type = 1;
                            keep = true;
                        }
                    }
                    if (Math.abs(y1 - y2) > 20) {
                        if (type == 0) {
                            type = 2;
                            sv.requestDisallowInterceptTouchEvent(false);
                            srl.setEnabled(true);
                            keep = false;
                        }
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (type == 1) {
                        x2 = event.getX();
                        if (x2 - x1 > 100) {
                            switchChange(3, 0);
                            swtype = 1;
                            swRight(fanswll, fantv1, fantv2, fantv3, fantv4, fantv5);
//                            Snackbar.make(tvlight, "水平右划", Snackbar.LENGTH_SHORT).show();
                        } else if (x1 - x2 > 100) {
                            switchChange(3, 1);
                            swtype = 2;
                            swLeft(fanswll, fantv1, fantv2, fantv3, fantv4, fantv5);
//                            Snackbar.make(tvlight, "水平左滑", Snackbar.LENGTH_SHORT).show();
                        }
                        sv.requestDisallowInterceptTouchEvent(false);
                        srl.requestDisallowInterceptTouchEvent(false);
                        keep = true;
                    }
                    srl.setEnabled(true);
                }
                return keep;
            }
        });

        swhumidifier.setOnTouchListener(new View.OnTouchListener() {
            float x1, x2;
            float y1, y2;
            boolean keep;
            int type = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    type = 0;
                    x1 = event.getX();
                    y1 = event.getY();
                    sv.requestDisallowInterceptTouchEvent(true);
                    srl.setEnabled(false);
                    keep = true;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    x2 = event.getX();
                    y2 = event.getY();
                    if (Math.abs(x1 - x2) > 20) {
                        if (type == 0) {
                            type = 1;
                            keep = true;
                        }
                    }
                    if (Math.abs(y1 - y2) > 20) {
                        if (type == 0) {
                            type = 2;
                            sv.requestDisallowInterceptTouchEvent(false);
                            srl.setEnabled(true);
                            keep = false;
                        }
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (type == 1) {
                        x2 = event.getX();
                        if (x2 - x1 > 100) {
                            switchChange(2, 0);
                            swtype = 1;
                            swRight(humidifierswll, humidifiertv1, humidifiertv2, humidifiertv3, humidifiertv4, humidifiertv5);
//                            Snackbar.make(tvlight, "水平右划", Snackbar.LENGTH_SHORT).show();
                        } else if (x1 - x2 > 100) {
                            switchChange(2, 1);
                            swtype = 2;
                            swLeft(humidifierswll, humidifiertv1, humidifiertv2, humidifiertv3, humidifiertv4, humidifiertv5);
//                            Snackbar.make(tvlight, "水平左滑", Snackbar.LENGTH_SHORT).show();
                        }
                        sv.requestDisallowInterceptTouchEvent(false);
                        srl.requestDisallowInterceptTouchEvent(false);
                        keep = true;
                    }
                    srl.setEnabled(true);
                }
                return keep;
            }
        });

        swheater.setOnTouchListener(new View.OnTouchListener() {
            float x1, x2;
            float y1, y2;
            boolean keep;
            int type = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    type = 0;
                    x1 = event.getX();
                    y1 = event.getY();
                    sv.requestDisallowInterceptTouchEvent(true);
                    srl.setEnabled(false);
                    keep = true;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    x2 = event.getX();
                    y2 = event.getY();
                    if (Math.abs(x1 - x2) > 20) {
                        if (type == 0) {
                            type = 1;
                            keep = true;
                        }
                    }
                    if (Math.abs(y1 - y2) > 20) {
                        if (type == 0) {
                            type = 2;
                            sv.requestDisallowInterceptTouchEvent(false);
                            srl.setEnabled(true);
                            keep = false;
                        }
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (type == 1) {
                        x2 = event.getX();
                        if (x2 - x1 > 100) {
                            switchChange(5, 0);
                            swtype = 1;
                            swRight(heaterswll, heatertv1, heatertv2, heatertv3, heatertv4, heatertv5);
//                            Snackbar.make(tvlight, "水平右划", Snackbar.LENGTH_SHORT).show();
                        } else if (x1 - x2 > 100) {
                            switchChange(5, 1);
                            swtype = 2;
                            swLeft(heaterswll, heatertv1, heatertv2, heatertv3, heatertv4, heatertv5);
//                            Snackbar.make(tvlight, "水平左滑", Snackbar.LENGTH_SHORT).show();
                        }
                        sv.requestDisallowInterceptTouchEvent(false);
                        srl.requestDisallowInterceptTouchEvent(false);
                        keep = true;
                    }
                    srl.setEnabled(true);
                }
                return keep;
            }
        });

        /**
         * SeekBar of light level/fan level
         */
        sblight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                srl.setEnabled(false);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                srl.setEnabled(true);
                ServerConnection conn = new ServerConnection(1, 1, sblight.getProgress());
                conn.doServerConnection();
            }
        });

        sbfan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                srl.setEnabled(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                srl.setEnabled(true);
                ServerConnection conn = new ServerConnection(1, 4, sbfan.getProgress());
                conn.doServerConnection();
            }
        });

        /**
         * BaiduPush initialize
         */
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "48APTh0WnjEkT8IvUGjjiwGO");

    }

    /**
     * Menu Action
     * AboutUs
     * VersionInfo
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_aboutus) {
            Intent aboutusIntent = new Intent(Main.this, AboutUs.class);
            Main.this.startActivity(aboutusIntent);
            return true;
        }else if(id==R.id.action_versioninfo){
            Intent versioninfoIntent = new Intent(Main.this, VersionInfo.class);
            Main.this.startActivity(versioninfoIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Double Click To Quit
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Snackbar.make(tvlog, "再按一次退出程序", Snackbar.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * SeekBar Animations
     */
    public void sbFlashOut(SeekBar sb) {
        SeekBarWrapper sbw = new SeekBarWrapper(sb);
        ObjectAnimator.ofFloat(sb, "scaleX", 1, 0).setDuration(100).start();
        ObjectAnimator.ofInt(sbw, "Height", 0).setDuration(300).start();
    }

    public void sbFlashIn(SeekBar sb) {
        SeekBarWrapper sbw = new SeekBarWrapper(sb);
        ObjectAnimator.ofFloat(sb, "scaleX", 0, 1).setDuration(300).start();
        ObjectAnimator.ofInt(sbw, "Height", 50).setDuration(200).start();
    }

    /**
     * Pull To Refresh Animations
     */
    public void rFlashOut() {
        ObjectAnimator.ofFloat(tvtemp, "alpha", 1, 0).setDuration(100).start();
        ObjectAnimator.ofFloat(tvtempsymbol, "alpha", 1, 0).setDuration(100).start();
        ObjectAnimator.ofFloat(tvhumidity, "alpha", 1, 1, 0.5f, 0).setDuration(150).start();
        ObjectAnimator.ofFloat(tvhumiditysymbol, "alpha", 1, 1, 0.5f, 0).setDuration(150).start();
        ObjectAnimator.ofFloat(tvurgent, "alpha", 1, 1, 0).setDuration(200).start();
    }

    public void rFlashIn() {
        ObjectAnimator.ofFloat(tvtemp, "alpha", 0, 1).setDuration(100).start();
        ObjectAnimator.ofFloat(tvtempsymbol, "alpha", 0, 1).setDuration(100).start();
        ObjectAnimator.ofFloat(tvhumidity, "alpha", 0, 0, 0.5f, 1).setDuration(150).start();
        ObjectAnimator.ofFloat(tvhumiditysymbol, "alpha", 0, 0, 0.5f, 1).setDuration(150).start();
        ObjectAnimator.ofFloat(tvurgent, "alpha", 0, 0, 1).setDuration(200).start();
    }

    /**
     * Card Switch Animations
     */
    public void swRight(final LinearLayout swl, final TextView tv1, final TextView tv2, final TextView tv3, final TextView tv4, final TextView tv5) {

        ObjectAnimator.ofFloat(tv1, "alpha", 0, 1).start();
        ObjectAnimator.ofFloat(tv2, "TextSize", smalltext, largetext).start();
        ObjectAnimator.ofFloat(tv3, "TextSize", largetext, smalltext).start();
        ObjectAnimator.ofFloat(tv4, "alpha", 1, 0).start();
        ObjectAnimator swlanim = new ObjectAnimator().ofFloat(swl, "TranslationX", 90, 180);
        swlanim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                swl.setTranslationX(90);

                tv5.setText(tv4.getText());
                tv4.setText(tv3.getText());
                tv3.setText(tv2.getText());
                tv2.setText(tv1.getText());
                tv1.setText(tv4.getText());

                tv1.setTextSize(smalltext);
                tv2.setTextSize(smalltext);
                tv3.setTextSize(largetext);
                tv4.setTextSize(smalltext);
                tv5.setTextSize(smalltext);

                tv1.setAlpha(0);
                tv2.setAlpha(1);
                tv3.setAlpha(1);
                tv4.setAlpha(1);
                tv5.setAlpha(0);
            }
        });
        swlanim.start();

    }

    public void swLeft(final LinearLayout swl, final TextView tv1, final TextView tv2, final TextView tv3, final TextView tv4, final TextView tv5) {

        ObjectAnimator.ofFloat(tv5, "alpha", 0, 1).start();
        ObjectAnimator.ofFloat(tv4, "TextSize", smalltext, largetext).start();
        ObjectAnimator.ofFloat(tv3, "TextSize", largetext, smalltext).start();
        ObjectAnimator.ofFloat(tv2, "alpha", 1, 0).start();
        ObjectAnimator swlanim = new ObjectAnimator().ofFloat(swl, "TranslationX", 90, 0);
        swlanim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                swl.setTranslationX(90);

                tv1.setText(tv2.getText());
                tv2.setText(tv3.getText());
                tv3.setText(tv4.getText());
                tv4.setText(tv5.getText());
                tv5.setText(tv2.getText());

                tv1.setTextSize(smalltext);
                tv2.setTextSize(smalltext);
                tv3.setTextSize(largetext);
                tv4.setTextSize(smalltext);
                tv5.setTextSize(smalltext);

                tv1.setAlpha(0);
                tv2.setAlpha(1);
                tv3.setAlpha(1);
                tv4.setAlpha(1);
                tv5.setAlpha(0);
            }
        });
        swlanim.start();

    }

    /**
     * SetData Action
     */
    public void setData() {

        if (data.getCmd(0) == 0) {
            sblight.getLayoutParams().height = 0;
            lighttv1.setText("打开");
            lighttv2.setText("自动");
            lighttv3.setText("关闭");
            lighttv4.setText("打开");
            lighttv5.setText("自动");
        } else if (data.getCmd(0) == 1) {
            sblight.getLayoutParams().height = 50;
            lighttv1.setText("自动");
            lighttv2.setText("关闭");
            lighttv3.setText("打开");
            lighttv4.setText("自动");
            lighttv5.setText("关闭");
        } else {
            sblight.getLayoutParams().height = 0;
            lighttv1.setText("关闭");
            lighttv2.setText("打开");
            lighttv3.setText("自动");
            lighttv4.setText("关闭");
            lighttv5.setText("打开");
        }
        sblight.setProgress(data.getCmd(1));


        if (data.getCmd(2) == 0) {
            humidifiertv1.setText("打开");
            humidifiertv2.setText("自动");
            humidifiertv3.setText("关闭");
            humidifiertv4.setText("打开");
            humidifiertv5.setText("自动");
        } else if (data.getCmd(2) == 1) {
            humidifiertv1.setText("自动");
            humidifiertv2.setText("关闭");
            humidifiertv3.setText("打开");
            humidifiertv4.setText("自动");
            humidifiertv5.setText("关闭");
        } else {
            humidifiertv1.setText("关闭");
            humidifiertv2.setText("打开");
            humidifiertv3.setText("自动");
            humidifiertv4.setText("关闭");
            humidifiertv5.setText("打开");
        }


        if (data.getCmd(3) == 0) {
            sbfan.getLayoutParams().height = 0;
            fantv1.setText("打开");
            fantv2.setText("自动");
            fantv3.setText("关闭");
            fantv4.setText("打开");
            fantv5.setText("自动");
        } else if (data.getCmd(3) == 1) {
            sbfan.getLayoutParams().height = 50;
            fantv1.setText("自动");
            fantv2.setText("关闭");
            fantv3.setText("打开");
            fantv4.setText("自动");
            fantv5.setText("关闭");
        } else {
            sbfan.getLayoutParams().height = 0;
            fantv1.setText("关闭");
            fantv2.setText("打开");
            fantv3.setText("自动");
            fantv4.setText("关闭");
            fantv5.setText("打开");
        }
        sbfan.setProgress(data.getCmd(4));


        if (data.getCmd(5) == 0) {
            heatertv1.setText("打开");
            heatertv2.setText("自动");
            heatertv3.setText("关闭");
            heatertv4.setText("打开");
            heatertv5.setText("自动");
        } else if (data.getCmd(5) == 1) {
            heatertv1.setText("自动");
            heatertv2.setText("关闭");
            heatertv3.setText("打开");
            heatertv4.setText("自动");
            heatertv5.setText("关闭");
        } else {
            heatertv1.setText("关闭");
            heatertv2.setText("打开");
            heatertv3.setText("自动");
            heatertv4.setText("关闭");
            heatertv5.setText("打开");
        }

        tvtemp.setText(String.valueOf(data.getSensor(0)));
        tvhumidity.setText(String.valueOf(data.getSensor(1)));

        if (data.getUrgent() == 0) {
            ivurgent.setImageResource(R.drawable.safe);
            tvurgent.setText("安全");
        } else {
            ivurgent.setImageResource(R.drawable.alert);
            tvurgent.setText("警报");
        }

    }

    /**
     * Card Switch Action
     */
    public void switchChange(int no, int type) {
        tempVal = data.getCmd(no);

        if (type == 0) {
            if (tempVal == 0) {
                tempVal = 2;
            } else {
                tempVal--;
            }
        } else if (type == 1) {
            if (tempVal == 2) {
                tempVal = 0;
            } else {
                tempVal++;
            }
        }

        ServerConnection conn = new ServerConnection(1, no, tempVal);
        conn.doServerConnection();
    }

    public static class SeekBarWrapper {
        private SeekBar sb;

        public SeekBarWrapper(SeekBar ll) {
            this.sb = ll;
        }

        public int getHeight() {
            return sb.getLayoutParams().height;
        }

        public void setHeight(int val) {
            sb.getLayoutParams().height = val;
            sb.requestLayout();
        }
    }

    /**
     * Network Asynctask inner class
     */
    public class ServerConnection {
        private String cmdStr;
        private int type;
        private int no;
        private int val;

        public ServerConnection(int type, int no, int val) {
            this.type = type;
            this.no = no;
            this.val = val;
            cmdStr = "http://www.tyutiot.com/android.php?token=sp199539&type=" + this.type + "&no=" + this.no + "&val=" + this.val;
        }

        public ServerConnection(int type, int no) {
            this.type = type;
            this.no = no;
            cmdStr = "http://www.tyutiot.com/android.php?token=sp199539&type=" + this.type + "&no=" + this.no;
        }

        public ServerConnection(int type) {
            this.type = type;
            cmdStr = "http://www.tyutiot.com/android.php?token=sp199539&type=" + this.type;
        }

        public void doServerConnection() {
            NetThread netThread = new NetThread();
            netThread.execute(this.cmdStr);
        }

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

                if (s != null) {
                    if (type == 1) {
                        swtype = 0;
                        int i = data.getCmd(no);
                        data.setCmd(no, val);
                        if (no == 0) {
                            if (tempVal == 1) {
                                sbFlashIn(sblight);
                            } else {
                                if (i == 1) {
                                    sbFlashOut(sblight);
                                }
                            }
                        } else if (no == 3) {
                            if (tempVal == 1) {
                                sbFlashIn(sbfan);
                            } else {
                                if (i == 1) {
                                    sbFlashOut(sbfan);
                                }
                            }
                        }
                    } else if (type == 2) {
                        data.setSensor(no, Integer.parseInt(s));
                        if (no == 0) {
                            tvtemp.setText(s);
                        }
                        if (no == 1) {
                            tvhumidity.setText(s);
                        }
                    } else if (type == 3) {
                        data.setUrgent(Integer.parseInt(s));
                        if (data.getUrgent() == 0) {
                            ivurgent.setImageResource(R.drawable.safe);
                            tvurgent.setText("安全");
                        } else {
                            ivurgent.setImageResource(R.drawable.alert);
                            tvurgent.setText("警报");
                        }
                        rFlashIn();
                        srl.setRefreshing(false);
                    }
                } else {
                    if (type == 1) {
                        if (no == 0) {
                            if (swtype == 1) {
                                lighttv1.setText(lighttv2.getText());
                                lighttv2.setText(lighttv3.getText());
                                lighttv3.setText(lighttv4.getText());
                                lighttv4.setText(lighttv5.getText());
                                lighttv5.setText(lighttv2.getText());
                            } else if (swtype == 2) {
                                lighttv5.setText(lighttv4.getText());
                                lighttv4.setText(lighttv3.getText());
                                lighttv3.setText(lighttv2.getText());
                                lighttv2.setText(lighttv1.getText());
                                lighttv1.setText(lighttv4.getText());
                            }
                            swtype = 0;
                        } else if (no == 1) {
                            ObjectAnimator.ofInt(sblight, "Progress", data.getCmd(1)).setDuration(200).start();
                        } else if (no == 2) {
                            if (swtype == 1) {
                                humidifiertv1.setText(humidifiertv2.getText());
                                humidifiertv2.setText(humidifiertv3.getText());
                                humidifiertv3.setText(humidifiertv4.getText());
                                humidifiertv4.setText(humidifiertv5.getText());
                                humidifiertv5.setText(humidifiertv2.getText());
                            } else if (swtype == 2) {
                                humidifiertv5.setText(humidifiertv4.getText());
                                humidifiertv4.setText(humidifiertv3.getText());
                                humidifiertv3.setText(humidifiertv2.getText());
                                humidifiertv2.setText(humidifiertv1.getText());
                                humidifiertv1.setText(humidifiertv4.getText());
                            }
                            swtype = 0;
                        } else if (no == 3) {
                            if (swtype == 1) {
                                fantv1.setText(fantv2.getText());
                                fantv2.setText(fantv3.getText());
                                fantv3.setText(fantv4.getText());
                                fantv4.setText(fantv5.getText());
                                fantv5.setText(fantv2.getText());
                            } else if (swtype == 2) {
                                fantv5.setText(fantv4.getText());
                                fantv4.setText(fantv3.getText());
                                fantv3.setText(fantv2.getText());
                                fantv2.setText(fantv1.getText());
                                fantv1.setText(fantv4.getText());
                            }
                            swtype = 0;
                        } else if (no == 4) {
                            ObjectAnimator.ofInt(sbfan, "Progress", data.getCmd(4)).setDuration(200).start();
                        } else if (no == 5) {
                            if (swtype == 1) {
                                heatertv1.setText(heatertv2.getText());
                                heatertv2.setText(heatertv3.getText());
                                heatertv3.setText(heatertv4.getText());
                                heatertv4.setText(heatertv5.getText());
                                heatertv5.setText(heatertv2.getText());
                            } else if (swtype == 2) {
                                heatertv5.setText(heatertv4.getText());
                                heatertv4.setText(heatertv3.getText());
                                heatertv3.setText(heatertv2.getText());
                                heatertv2.setText(heatertv1.getText());
                                heatertv1.setText(heatertv4.getText());
                            }
                            swtype = 0;
                        }
                    } else {
                        rFlashIn();
                        srl.setRefreshing(false);
                    }
                    Snackbar.make(tvlog, "请检查网络!", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }
}
