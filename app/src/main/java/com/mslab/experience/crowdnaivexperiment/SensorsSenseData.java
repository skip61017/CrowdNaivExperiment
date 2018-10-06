package com.mslab.experience.crowdnaivexperiment;

public class SensorsSenseData {

	float[] SensorData = new float[3];//data
	float SensorDataTimeET;//event time
	float SensorDataTimeST;//second
	
	SensorsSenseData()
	{
		//initialize
		for(int i = 0;i<3;i++)
			SensorData[i] = 0;
		SensorDataTimeET = 0;
		SensorDataTimeST = 0;
	}
}

