package com.mslab.experience.crowdnaivexperiment;

/**
 * Created by Lab-Li on 2015/6/15.
 */
public class Data_Gyroscope {
    public float Orein,StartTime,EndTime,Delta_Time;
    public float[] Gravity_Value = new float[3];

    public Data_Gyroscope(final float Orein,final float StartTime,final float Delta_Time,final float EndTime,final float[] Gravity_Value){
        this.Orein = Orein;
        this.StartTime = StartTime;
        this.Delta_Time = Delta_Time;
        this.EndTime = EndTime;
        this.Gravity_Value = Gravity_Value;
    }
}
