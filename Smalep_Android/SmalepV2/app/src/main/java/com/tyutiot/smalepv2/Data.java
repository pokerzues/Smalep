package com.tyutiot.smalepv2;

import android.app.Application;

/**
 * This is a class that is used to record cmd/sensor/urgent datas.
 */
public class Data extends Application {
    public int[] cmd = new int[6];
    public int[] sensor = new int[2];
    public int urgent;

    /**
     * general set mathods
     */
    public void setCmd(int i, int val) {
        this.cmd[i] = val;
    }

    public void setSensor(int i, int val) {
        this.sensor[i] = val;
    }

    /**
     * general get mathods
     */
    public int getCmd(int i) {
        return this.cmd[i];
    }

    public int getSensor(int i) {
        return this.sensor[i];
    }

    public int getUrgent() {
        return this.urgent;
    }

    public void setUrgent(int val) {
        this.urgent = val;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cmd[0] = 2;
        cmd[1] = 100;
        cmd[2] = 2;
        cmd[3] = 2;
        cmd[4] = 100;
        cmd[5] = 2;
        sensor[0] = 99;
        sensor[1] = 99;
        urgent = 1;
    }
}
