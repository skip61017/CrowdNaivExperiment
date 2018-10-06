package com.mslab.experience.crowdnaivexperiment;

public class OutputDataFunction {
	//�x�s�����| �bSD�d�W
    /*File myDir = new File(Environment.getExternalStorageDirectory().toString()+"/DebugSensorData"+HourForDir+MinuteForDir+SecondForDir);
	
    public void SaveData2SDCard()
	{
		try{
			if(!myDir.exists())
			{
				myDir.mkdirs();
			}
			
			FileOutputStream fosX = new FileOutputStream(fileGyroX,true);
			FileOutputStream fosY = new FileOutputStream(fileGyroY,true);
			FileOutputStream fosZ = new FileOutputStream(fileGyroZ,true);
			FileOutputStream fosT = new FileOutputStream(fileGyroT,true);
				
			BufferedOutputStream buffoutX = new BufferedOutputStream(fosX);
			BufferedOutputStream buffoutY = new BufferedOutputStream(fosY);
			BufferedOutputStream buffoutZ = new BufferedOutputStream(fosZ);
			BufferedOutputStream buffoutT = new BufferedOutputStream(fosT);
			String strX = String.valueOf(TempAccForXRotation[0]);
			String strY = String.valueOf(TempAccForXRotation[1]);
			String strZ = String.valueOf(TempAccForXRotation[2]);
			//String strT = String.valueOf(Gyrodt);
			String strT = String.valueOf(TACFG.SensorDataTimeST);
			//����
			String next = "\r\n";
		
			buffoutX.write(strX.getBytes());
			buffoutX.write(next.getBytes());
			buffoutX.close();
			buffoutY.write(strY.getBytes());
			buffoutY.write(next.getBytes());
			buffoutY.close();
			buffoutZ.write(strZ.getBytes());
			buffoutZ.write(next.getBytes());
			buffoutZ.close();
			buffoutT.write(strT.getBytes());
			buffoutT.write(next.getBytes());
			buffoutT.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}*/
}
