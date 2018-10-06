package com.mslab.experience.crowdnaivexperiment;

import android.hardware.Sensor;

/**
 * Created by Lab-Li on 2015/6/5.
 */
public class Data_Sensor {
    public int accuracy;
    public Sensor sensor;
    public long timestamp;
    public float[] values;
    Data_Sensor(Sensor sensor, float[] values, long timestamp, int accuracy){
        this.sensor = sensor;
        this.values = values.clone();
        this.timestamp = timestamp;
        this.accuracy = accuracy;
    }
}
