package com.mslab.experience.crowdnaivexperiment;

public class DataTypeForStartPoint
{
	float ST;//Start time
	float FT;//Final time
	float MCCR;//Max Correlation Cofficient
	int Index;//LinkedList Index
	int UpOrDown;
	
	//�ݧ襤
	boolean Finish;//The starting point finish
	boolean Dead;//The starting point dead
	boolean CheckFirstOnlyOne;//First check
	boolean CheckSecondOnlyOne;//Second check
	
	DataTypeForStartPoint()
	{
		ST = 0;
		FT = 0;
		MCCR = 0;
		Index = 0;
		Finish = false;
		Dead = false;
		CheckFirstOnlyOne = false;
		CheckSecondOnlyOne = false;
		UpOrDown = -1;
	}
	
	DataTypeForStartPoint(int i)
	{
	}
}