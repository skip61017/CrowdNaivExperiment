package com.mslab.experience.crowdnaivexperiment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;

public class MoveAndDraw extends View {
	class PathData{
		public String name;
		public int id;
		public int color;
		public LinkedList<UserPath> Path = new LinkedList<>();
		public PathData(String name, int id, int color){
			this.name = name;
			this.id = id;
			this.color = color;
		}
	}
	private boolean drawRadFlag;
	private boolean firsttime;
	private float ux,uy;
	private double toR,length;
	private int lengthPerMove= 80,radar = 50;
	//0 self,1 watch
	private LinkedList<PathData> AllUserPath = new LinkedList<>();
	private Paint pat1 = new Paint();
//	private Paint pat3 = new Paint();

	private Paint paint_array[]= new Paint[7];
    private boolean DisplayMode;
	private float UserOrein=90;
	private float Ox=0,Oy=0;
	private double distance2center;
	private double Rad2center;
	private boolean paint[] = new boolean[7];
	public MoveAndDraw(Context context){
		super(context);
		// TODO Auto-generated constructor stub
		drawRadFlag = false;
		firsttime = true;
		PathData UserPath = new PathData("User",0,-1);
		AllUserPath.add(UserPath);
		PathData WatchPath = new PathData("Watch",1,3);
		AllUserPath.add(WatchPath);
		for(int i = 0;i<7;i++){
			paint_array[i] = new Paint();
			paint_array[i].setStrokeWidth(15);
			paint_array[i].setStyle(Paint.Style.STROKE);
		}
		paint_array[0].setColor(Color.RED);//
		paint_array[1].setColor(Color.rgb(Integer.valueOf("F7",16), Integer.valueOf("50",16), Integer.valueOf("00",16)));
		paint_array[2].setColor(Color.YELLOW);//
		paint_array[3].setColor(Color.GREEN);//
		paint_array[4].setColor(Color.BLUE);
		paint_array[5].setColor(Color.rgb(Integer.valueOf("8F",16), Integer.valueOf("45",16), Integer.valueOf("86",16)));
		paint_array[6].setColor(Color.rgb(Integer.valueOf("3A",16), Integer.valueOf("00",16), Integer.valueOf("6F",16)));
		paint[0] = true;
		paint[2] = true;
		paint[3] = true;


	}
	@Override
	public void onDraw(Canvas cs) {
		ux = cs.getWidth() / 2;
		uy = cs.getHeight() / 2;
//		Log.d("","" + ((float)cs.getHeight()/(float)1011));
		if (drawRadFlag) {
			pat1.setColor(Color.LTGRAY);
			pat1.setStrokeWidth(30);
			pat1.setStyle(Paint.Style.STROKE);
			for (int i = 0; i < 11; i++) {
				if (i == 7) {
					cs.drawCircle(ux, uy, 5 + i * (float)radar*((float)cs.getHeight()/(float)1011), paint_array[2]);
				} else if (i == 9) {
					cs.drawCircle(ux, uy, 5 + i *(float) radar*((float)cs.getHeight()/(float)1011), paint_array[0]);
				}

			}
			pat1.setStrokeWidth(15);
			cs.drawLine((float) (cs.getWidth() / 2.0), 0, (float) (cs.getWidth() / 2.0), cs.getHeight(), pat1);
			cs.drawLine(0, (float) (cs.getHeight() / 2.0), cs.getWidth(), (float) (cs.getHeight() / 2.0), pat1);
		}


		for(PathData User : AllUserPath){
			if(User.id != 0){

				float startpointX = ux- (float)(distance2center*((float)cs.getHeight()/(float)1011)* Math.cos(Rad2center + Math.toRadians(90-UserOrein)));
				float startpointY = uy + (float) (distance2center*((float)cs.getHeight()/(float)1011)* Math.sin(Rad2center + Math.toRadians(90 - UserOrein))) ;
				float nextpointX = ux - (float)(distance2center*((float)cs.getHeight()/(float)1011)* Math.cos(Rad2center + Math.toRadians(90-UserOrein)));
				float nextpointY = uy + (float) (distance2center*((float)cs.getHeight()/(float)1011)* Math.sin(Rad2center + Math.toRadians(90 - UserOrein))) ;
				int i =0;
				for(UserPath PathOfUser : User.Path){
					toR = Math.toRadians(PathOfUser.orein) - Math.toRadians(UserOrein + 90);
					length = PathOfUser.steps*lengthPerMove*((float)cs.getHeight()/(float)1011);
					nextpointX -= Math.cos(toR) *length / 8.0;
					nextpointY += Math.sin(toR)*length / 8.0;
					if(DisplayMode){
						if(i == User.Path.size()-1)
							cs.drawCircle(nextpointX, nextpointY, 5, paint_array[User.color]);
					}
					else{
						cs.drawLine(startpointX,startpointY,nextpointX,nextpointY,paint_array[User.color]);
						if(i == User.Path.size()-1){
							cs.drawCircle(nextpointX, nextpointY, 5, paint_array[User.color]);
//							cs.drawLine(ux, uy,  ux + (float) (500*Math.cos(Math.atan2(nextpointY-uy,nextpointX-ux) - Math.toRadians(15.0)) )
//									, uy +(float) (500*Math.sin(Math.atan2(nextpointY-uy,nextpointX-ux) - Math.toRadians(15.0)) ), pat3);
//							cs.drawLine(ux, uy, ux + (float) (500*Math.cos(Math.atan2(nextpointY-uy,nextpointX-ux) + Math.toRadians(15.0)) )
//									, uy +(float) (500*Math.sin(Math.atan2(nextpointY-uy,nextpointX-ux) + Math.toRadians(15.0)) ), pat3);
						}
					}
					startpointX = nextpointX;
					startpointY = nextpointY;
//					Log.d("",""+ Math.sqrt(Math.pow(startpointX - ux, 2) + Math.pow(startpointY - uy, 2)));
					if( Math.sqrt(Math.pow(startpointX - ux, 2) + Math.pow(startpointY - uy, 2)) > 350){
						Message ms = new Message();
						ms.what = 100;
						MainActivity.UI_Handler.sendMessage(ms);
					}
					i++;
				}
				if(User.Path.size() == 0){
					cs.drawCircle(ux - (float)(distance2center*((float)cs.getHeight()/(float)1011)* Math.cos(Rad2center + Math.toRadians(90-UserOrein)))
							, uy + (float) (distance2center*((float)cs.getHeight()/(float)1011) * Math.sin(Rad2center + Math.toRadians(90 - UserOrein)))  , 5, paint_array[User.color]);
				}
			}
		}

	}

	public void addPath(int steps,float Orein,int id){

		UserPath UP = new UserPath(steps,Orein);
		AllUserPath.get(id).Path.add(UP);
		this.invalidate();
	}
    public void setDisplayMode(){
        DisplayMode = !DisplayMode;

		this.invalidate();
    }
	public int addNewUser(String name){
		int UserColor=-2;
		for(int i = 0;i<7;i++){
			if(!paint[i]) {
				UserColor = i;
				paint[i] = true;
				break;
			}
			if(i == 6){
				Log.d("MoveAndDraw", "addNewUser(String name) error");
				return -1;
			}
		}
		PathData PD = new PathData(name,AllUserPath.size(),UserColor);
		AllUserPath.add(PD);
		return AllUserPath.size();
	}


	public void setUserOrein(float Orein){
		UserOrein = Orein;
		this.invalidate();
	}
	public void resetUserOrein(){
		UserOrein = 90;
		this.invalidate();
	}
	public void addOwnPath(int steps,float orein){

		length = steps*lengthPerMove;
		Ox += Math.cos(Math.toRadians(orein))*length / 8.0;
		Oy += Math.sin(Math.toRadians(orein))*length / 8.0;

		distance2center = Math.sqrt(Math.pow(Ox, 2) + Math.pow(Oy, 2));
		Rad2center = Math.atan2(Oy, Ox);

		UserPath UP = new UserPath(steps,orein);
		AllUserPath.get(0).Path.add(UP);
		this.invalidate();
	}

	public void resetownPath(){

		Ox = 0;
		Oy = 0;

		distance2center = Math.sqrt(Math.pow(Ox, 2) + Math.pow(Oy, 2));
		Rad2center = Math.atan2(Oy, Ox);

		AllUserPath.get(0).Path.clear();
		this.invalidate();
	}

	public void resetPath(int id){

		AllUserPath.get(id).Path.clear();
		this.invalidate();
	}

	public void setOrienPath(double dift_orien,double dift_length,int id){
		for(UserPath us :AllUserPath.get(id).Path ){
			us.orein += dift_orien;
		}
		length = length*dift_length;
		this.invalidate();
	}
}