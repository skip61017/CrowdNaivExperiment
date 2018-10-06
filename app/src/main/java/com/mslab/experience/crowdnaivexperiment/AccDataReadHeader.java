package com.mslab.experience.crowdnaivexperiment;

public class AccDataReadHeader {
	int TheFirstSPIndex;
	int TheSecondSPIndex;
	int[] WindowSize = new int[3];
	AccDataReadHeader()
	{
		TheFirstSPIndex = 0;
		TheSecondSPIndex = 0;
		
		for(int i = 0;i<3;i++)
		{
			WindowSize[i] = 0;
		}
	}
}
