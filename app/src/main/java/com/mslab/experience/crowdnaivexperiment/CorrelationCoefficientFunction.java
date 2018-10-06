package com.mslab.experience.crowdnaivexperiment;

import java.util.LinkedList;

public class CorrelationCoefficientFunction
{	
	/*******************************************************************************************************
	 * Function name : Debug
	 * Writing date  : 2013/08/15
	 * Input         : float[] x
	 * Output        : int
	 * Purpose       : Debug 
	 *******************************************************************************************************/
	public int Debug(float[] x)
	{		
		return(x.length);
	}
	
	/*******************************************************************************************************
	 * Function name : CorrelationCofficient
	 * Writing date  : 2013/08/15
	 * Input         : double *x,double *y,int Index
	 * Output        : Float
	 * Purpose       : Do correlation coefficient and return the result. 
	 *******************************************************************************************************/
	//Something error in here,don't use this.
	public float CorrelationCofficient(float[] x,float[] y,int Index,float XSum,float YSum)
	{
		float XAverage,YAverage,Numerator,Denominator,TempDX,TempDY;
		Numerator = Denominator = TempDX = TempDY = 0;
		
		XAverage = XSum / Index;
		YAverage = YSum / Index;
		
		for(int i = 0;i < Index; i++)
		{
			Numerator = Numerator + (x[i] - XAverage) * (y[i] - YAverage);
			TempDX = TempDX + FloatPow((x[i] - XAverage),2);
			TempDY = TempDY + FloatPow((y[i] - YAverage),2);
		}
		Denominator = (float) (Math.sqrt(TempDX) * Math.sqrt(TempDY));
		
		return(Numerator / Denominator);
	}
	
	/*******************************************************************************************************
	 * Function name : FindTheStartWalkingPoint
	 * Writing date  : 2013/08/15
	 * Input         : double *x,int i
	 * Output        : int
	 * Purpose       : We find the Starting point of walking when accel
	 *******************************************************************************************************/
	/*int FindTheStartingWalkingPoint(float[] x,int i)
	{
		if(((x[x.length-2]<0.2)&&(x[x.length-1]>0.2))||((x[x.length-2]> -0.2)&&(x[x.length-1]< -0.2)))
		{
			return i;
		}
		return 0;
	}*/
	boolean FindTheStartingWalkingPoint(float BeforeAcceleration,float NowAcceleration,int[] TempUpOrDown)
	{
		if((BeforeAcceleration<0.2)&&(NowAcceleration>=0.2))
		{
			//���T��
			//TempUpOrDown[0] = 1;
			TempUpOrDown[0] = -1;
			return true;
		}
		else if((BeforeAcceleration>-0.2)&&(NowAcceleration<=(-0.2)))
		{
			//���T��
			//TempUpOrDown[0] = 0;
			TempUpOrDown[0] = -1;
			return true;
		}
		return false;
	}
	
	/*******************************************************************************************************
	 * Function name : AbsoluteValue
	 * Writing date  : 2013/08/15
	 * Input         : float v
	 * Output        : float
	 * Purpose       : Change the value to postive
	 *******************************************************************************************************/
	float AbsoluteValue(float v)
	{
		if(v>=0)
		{
			return v;
		}else
		{
			return -v;
		}
	}

	/*******************************************************************************************************
	 * Function name : FloatPow
	 * Writing date  : 2013/08/15
	 * Input         : float v
	 * Output        : float
	 * Purpose       : Change the value to postive
	 *******************************************************************************************************/
	public float FloatPow(float x,int y)
	{
		float result = 1;
		for(int i = 0;i<y;i++)
		{
			result = result * x;
		}
		return result;
	}
	
	/*******************************************************************************************************
	 * Function name : LinearInterpolation
	 * Writing date  : 2013/09/18
	 * Input         : float BeforeX,float NowX,float X,float BeforeY,float NowY
	 * Output        : float Y
	 * Purpose       : Do Linear Interpolation
	 *******************************************************************************************************/
	public float LinearInterpolation(float BeforeX,float NowX,float X,float BeforeY,float NowY)
	{
		float Y = 0;
		Y = BeforeY + (NowY - BeforeY) * ((X - BeforeX) / (NowX - BeforeX));
	    return Y;
	}

	/*******************************************************************************************************
	 * Function name : CorrelationCofficient
	 * Writing date  : 2013/08/15
	 * Input         : double *x,double *y,int Index
	 * Output        : Float
	 * Purpose       : Do correlation coefficient and return the result. 
	 *******************************************************************************************************/
	public float CorrelationCoefficient(LinkedList<AccelerationData> LinearInterpolationAccelerationData, int FirstIndex, int SecondIndex)
	{           
		float XSum,YSum,XAverage,YAverage,Numerator,Denominator,TempDX,TempDY;
		XSum = YSum = Numerator = Denominator = TempDX = TempDY = 0;
		
		
		AccelerationData X = new AccelerationData();
		AccelerationData Y = new AccelerationData();
		
		 
		for(int i = 0;i <= (SecondIndex-FirstIndex);i++)
		{
			X = LinearInterpolationAccelerationData.get(FirstIndex + i);
			Y = LinearInterpolationAccelerationData.get(SecondIndex + i);
			XSum = XSum + X.AccelerationData;
			YSum = YSum + Y.AccelerationData;
		}
		
		XAverage = XSum / (SecondIndex-FirstIndex+1);
		YAverage = YSum / (SecondIndex-FirstIndex+1);
		//return(X.AccelerationData);
		//return(Y.AccelerationData);
		//return(XSum);
		//return(YSum);
		//return(XAverage);
		//return(YAverage);
		//return(TempDX);
		
		for(int i = 0;i <= (SecondIndex-FirstIndex); i++)
		{
			X = LinearInterpolationAccelerationData.get(FirstIndex + i);
			Y = LinearInterpolationAccelerationData.get(SecondIndex + i);
			
			Numerator = Numerator + ((X.AccelerationData - XAverage) * (Y.AccelerationData - YAverage));
			TempDX = TempDX + FloatPow((X.AccelerationData - XAverage),2);
			TempDY = TempDY + FloatPow((Y.AccelerationData - YAverage),2);
		}
		Denominator = (float) (Math.sqrt(TempDX * TempDY));
		//Denominator = (float) (Math.sqrt(TempDX) * Math.sqrt(TempDY));
		if(TempDX == 0)
		{
			return(-2);
		}
		//return YAverage;
		return(Numerator / Denominator);
	}
	
	/*******************************************************************************************************
	 * Function name : GaussianLowerbound
	 * Writing date  : 2013/08/15
	 * Input         : double *x,double *y,int Index
	 * Output        : Float
	 * Purpose       : Do correlation coefficient and return the result. 
	 *******************************************************************************************************/
	//int GaussianLowerbound()
}
