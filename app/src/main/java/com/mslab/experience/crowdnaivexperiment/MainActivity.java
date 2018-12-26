package com.mslab.experience.crowdnaivexperiment;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hereapps.ibeacon.IBeacon;
import com.hereapps.ibeacon.IBeaconLibrary;
import com.hereapps.ibeacon.IBeaconListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener,OnClickListener, RadioGroup.OnCheckedChangeListener {

    protected MoveAndDraw freeDraw;
    public static MHandler UI_Handler;
    Timer timer = new Timer();
    private int C;
    private static final float AverageSensingTimeInterval20ms = (float) 0.02;
    private static final float CorrelationCoefficientThreshold = (float) 0.5;
    private static final float Walking2StepTimeLowerBound800ms = (float) 0.8;
    private static final float Walking2StepTimeLowerBound1600ms = (float) 1.6;
    private static final float AdditionalWindowSizes = (float) 15;
    private static final float CorrelationCoefficientThreshold3 = (float) 0.3;
    private static final float NS2S = 1.0f / 1000000000.0f;

    LinkedList<AccelerationData> Check_Compensation_DataY = new LinkedList<>();
    LinkedList<AccelerationData> Check_Compensation_DataZ = new LinkedList<>();
    LinkedList<CalculateTheStepsFromTwoAxisDataType> GlobalStepsList = new LinkedList<>();
    LinkedList<AccelerationData> State21AccelerationDataY = new LinkedList<>();
    LinkedList<AccelerationData> State21AccelerationDataZ = new LinkedList<>();
    LinkedList<AccelerationData> State2AccelerationDataY = new LinkedList<>();
    LinkedList<AccelerationData> State2TempAccelerationDataY = new LinkedList<>();
    AccDataReadHeader AccDHY = new AccDataReadHeader();
    LinkedList<AccelerationData> State2AccelerationData = new LinkedList<>();
    LinkedList<AccelerationData> State2TempAccelerationData = new LinkedList<>();
    AccDataReadHeader AccDH = new AccDataReadHeader();
    LinkedList<AccelerationData> YTempForSecondAccelerationData = new LinkedList<>();
    LinkedList<AccelerationData> ZTempForSecondAccelerationData = new LinkedList<>();
    LinkedList<AccelerationData> ZFirstFindSimilarWave = new LinkedList<>();
    LinkedList<AccelerationData> ZToState2SimilarWave = new LinkedList<>();
    LinkedList<AccelerationData> YFirstFindSimilarWave = new LinkedList<>();
    CorrelationCoefficientFunction CC = new CorrelationCoefficientFunction();
    LinkedList<DataTypeForStartPoint> YStartPoint = new LinkedList<>();
    LinkedList<DataTypeForStartPoint> ZStartPoint = new LinkedList<>();
    LinkedList<AccelerationData> YLinearInterpolationAccelerationData = new LinkedList<>();
    LinkedList<AccelerationData> ZLinearInterpolationAccelerationData = new LinkedList<>();
    LinkedList<SensorsSenseData> AccelerationDataLinkedList = new LinkedList<>();
    LinkedList<TheAngleCalculatedFromGravity> AngleDataList = new LinkedList<>();

    float Compensation_Upper = (float)1;
    float Compensation_Lower = (float)-1;
    float Compensation_Time = (float)2;
    boolean No_RunY = false;
    boolean No_RunZ = false;
    int[] Compensation_Steps = new int[2];
    boolean First_Compensation = true;
    int YBase = 0;
    int ZBase = 0;
    float[] Unstable_Wave_Start_Time = new float[2];
    float[] Unstable_Wave_End_Time = new float[2];
    boolean[] Need_To_Calculate_The_Steps_For_Unstable_Wave = new boolean[2];
    float[] Window_Size_For_Unstable_Wave = new float[2];
    int[] Unstable_Wave_Steps = new int[2];
    int iii = 0;
    int jjj = 0;
    int YTMCCI1 = 0;
    float YTMCC1 = -1;
    int ZTMCCI1 = 0;
    float ZTMCC1 = -1;
    int State2SomeBug = 0;
    int DebugState = 0;
    int DebugState7 = 0;
    boolean[] SearchTheSimilarWaveFromPast = new boolean[2];
    boolean State2FirstRun = true;
    boolean State2FirstRunY = true;
    int[] TheState2WindowSize = new int[2];
    int TheMaxCCIndexY = 0;
    float TheMaxCCY = -1;
    boolean C7FindTheWaveY = false;
    boolean RecordTheS7SecondWaveY = false;
    int CutWaveStateY = 0;
    int TheMaxCCIndex = 0;
    float TheMaxCC = -1;
    boolean C7FindTheWave = false;
    boolean RecordTheS7SecondWave = false;
    int CutWaveState = 0;
    int[] ToState2WindowSize = {0,0};
    boolean SenseTimeNotSame = false;
    boolean SenseSizeNotSame = false;
    boolean[] StartWalking = new boolean[3];
    float LinearInterpolationTime = 0;
    float BeforeTime = 0;
    float NowTime = 0;
    float[] BeforeAcc = new float[3];
    float[] NowAcc = new float[3];
    float[] Acc_last_LIAccData = new float[3];
    boolean[] FirstDoLI = new boolean[3];
    int[] SPIndex = new int[3];
    int[] WalkingStep = new int[3];

    boolean[] FirstPutDataToLI = new boolean[3];
    long Acc_last_timestamp = 0,Grav_last_timestamp = 0;
    boolean Accfirst = true,Gravfirst = true;
    float Accdt = 0,Gravdt = 0;
    boolean bool_FirstTimeToDoLinearInterpolation = true;
    boolean SenseDataCollectNotFinish = false;
    float XAngle;
    float YAngle;

    float[] Last_Grav = new float[3];
    private float Value_Gyroscope[] = new float[3];
    private float Delta[] = new float[3];
    private double Degree_Axis[] = new double[3];
    private float[] Grav_values = new float[3];
    private float Delta_Time;
    private float AllInZ=90;
    private long TimeStamp_Gravity;
    private float[] Value_Gravity;
    private Handler Sensor_Handler;
    private HandlerThread Sensor_Thread;
    private long TimeStamp_Gyroscope;
    private float[] Last_Gyro;
    private float StartTime_Gyro;
    private float EndTime_Gyro;
    private int currentstep;
    private boolean Gyro_Start;
    private int CheckStepChange;
    private boolean First_StepsGet = true;
    private float Last_Orein=90;
    //****************************************************
    private final Object mPauseLock = new Object();
    private SensorManager sensorManager;

    private final LinkedList<Data_3_AXIS> data_sensorList =  new LinkedList<>();

    List<Data_Gyroscope> DataOfOrein =  Collections.synchronizedList(new LinkedList<Data_Gyroscope>());
    private boolean StartSensor;
    private float resetOrien = 0;
    private TextView TV_Step_Count,TV_Start,TV_Orien,TV_Dist,TV_step,TV_pkData,TV_navi,TV_bcon;
    private EditText editText, editText_dest;
    private Button BT_Start,BT_Arrived;
    private Button BT_bcon1, BT_bcon2,BT_bcon3;
    private RadioGroup radioGroup;
    private LinearLayout freeDrawLayout;
    private LinearLayout layout_len;
    private LinearLayout layout_btns;
    private ScrollView scrollView;
    private boolean StartAPP = false;
    private int reset_count = 0;
    private Data_Gyroscope data_gyroscope;
    private Handler mHandler;
    
    /************************************************************/
    private final static int Default = 0;
    private final static int IsArrived = 1;
    private final static int IsCanceled = 2;
    private final int REQUEST_ENABLE_BT = 0xa01;
    private final int PERMISSION_REQUEST_COARSE_LOCATION = 0xb01;
    private static BluetoothAdapter mBtAdapter;
    private IBeaconLibrary iBeaconLibrary;

    private double x = 0;
    private double y = 21;
    private double length = 0.7;

    private String dest;
    private String route_id;
    private String bpre = "start";
    private String bcur = "start";
    private double last_orien = 90;
    private double sg_distance = 0;
    private double last_distance = 0;
    private JSONObject jsonData, pkData;
    private JSONArray sgList;
    private IBeacon prev_beacon;
    private IBeacon curr_beacon;
    private double enter_point_px = 0;
    private double prev_diff_px = 0;
    private double exit_point = 0;
    
    
    private Calendar CalendarForDir = Calendar.getInstance();
    int SecondForDir = CalendarForDir.get(Calendar.SECOND);
    int MinuteForDir = CalendarForDir.get(Calendar.MINUTE);
    int HourForDir = CalendarForDir.get(Calendar.HOUR_OF_DAY);

    int year = CalendarForDir.get(Calendar.YEAR);                      //取出年
    int month = CalendarForDir.get(Calendar.MONTH) + 1;           //取出月，月份的編號是由0~11 故+1
    int day = CalendarForDir.get(Calendar.DAY_OF_MONTH);
    File myDir;

    File File_PDR;
    private static final float THESHOLD_TURN=3;
    private static final double THESHOLD_CHECK_PHONE_POSE = 7;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_Start:{
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        TipHelper.PlaySound(getBaseContext());
                        if(StartAPP){
                            BT_Start.setText("Start");
                            editText_dest.setEnabled(true);
                            saveSegment();
                            createJsonData(IsCanceled);
                            showDetailOfPkData();
                            layout_btns.setVisibility(View.GONE);
                            iBeaconLibrary.stopScan();
                            BT_Arrived.setVisibility(View.GONE);
                            TV_bcon.setText(TV_bcon.getText() + "Stop scanning.\n");
                        }
                        else{
                            jsonData = new JSONObject();
                            pkData = new JSONObject();
                            sgList = new JSONArray();
                            route_id = getRouteId();
                            BT_Start.setText("Stop");
                            editText_dest.setEnabled(false);
                            layout_btns.setVisibility(View.VISIBLE);
                            BT_Arrived.setVisibility(View.VISIBLE);
                            iBeaconLibrary.startScan();
                            TV_bcon.setText(TV_bcon.getText() + "Start scanning...\n");
                        }
                        StartAPP = !StartAPP;

                        length = Double.valueOf(editText.getText().toString());
                        sg_distance = 0;
                        last_distance = 0;

                        layout_len.setVisibility(View.GONE);
                        dest = editText_dest.getText().toString();
                        TV_step.setText(TV_step.getText() + "Destination: " + dest + "\n");
                    }
                }, 1000);

                break;
            }

            case R.id.button_Arrived:
                bpre = bcur;
                bcur = dest;
                BT_Start.setText("Start");
                editText_dest.setEnabled(true);
                saveSegment();
                createJsonData(IsArrived);
                showDetailOfPkData();
                layout_btns.setVisibility(View.GONE);
                BT_Arrived.setVisibility(View.GONE);
                StartAPP = ! StartAPP;
                iBeaconLibrary.stopScan();
                TV_bcon.setText(TV_bcon.getText() + "Arrived.\n\n");
                sg_distance = 0;
                last_distance = 0;
                postThread.start();
                break;

            case R.id.button_bcon1:
                bpre = bcur;
                bcur = "bcon1";
                saveSegment();
                last_orien = Last_Orein;
                sg_distance = 0;
                createJsonData(Default);
                showDetailOfPkData();
                TV_step.setText("You're now at " + bcur + ".");
                break;

            case R.id.button_bcon2:
                bpre = bcur;
                bcur = "bcon2";
                saveSegment();
                last_orien = Last_Orein;
                sg_distance = 0;
                createJsonData(Default);
                showDetailOfPkData();
                TV_step.setText("You're now at " + bcur + ".");
                break;

            case R.id.button_bcon3:
                bpre = bcur;
                bcur = "67AB3E49-6";
                saveSegment();
                last_orien = Last_Orein;
                sg_distance = 0;
                createJsonData(Default);
                showDetailOfPkData();
                TV_step.setText("You're now at " + bcur + ".");
                postThread.start();
                break;

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
        switch (checkId) {
            case R.id.rButton_step:
                TV_step.setVisibility(View.VISIBLE);
                TV_pkData.setVisibility(View.GONE);
                TV_navi.setVisibility(View.GONE);
                TV_bcon.setVisibility(View.GONE);
                break;

            case R.id.rButton_pkData:
                TV_step.setVisibility(View.GONE);
                TV_pkData.setVisibility(View.VISIBLE);
                TV_navi.setVisibility(View.GONE);
                TV_bcon.setVisibility(View.GONE);
                break;

            case R.id.rButton_navi:
                TV_step.setVisibility(View.GONE);
                TV_pkData.setVisibility(View.GONE);
                TV_navi.setVisibility(View.VISIBLE);
                TV_bcon.setVisibility(View.GONE);
                break;

            case R.id.rButton_bcon:
                TV_step.setVisibility(View.GONE);
                TV_pkData.setVisibility(View.GONE);
                TV_navi.setVisibility(View.GONE);
                TV_bcon.setVisibility(View.VISIBLE);
                break;

        }
    }


    class Data_3_AXIS {
        public int types;
        public float values[];
        public long timestamp;
        public Data_3_AXIS(int types, float values[], long timestamps) {
            this.types = types;
            this.values = values;
            this.timestamp = timestamps;
        }
    }
    LinkedList<PDRinfo> PDR_arrayList = new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        scrollView = findViewById(R.id.scrollView);

        UI_Handler = new MHandler(this);
        freeDrawLayout = (LinearLayout) findViewById(R.id.drawLayout);
        freeDraw = new MoveAndDraw(MainActivity.this);
        freeDrawLayout.addView(freeDraw);
        findview();
        String formatStr = "%02d";
        String formatAns = String.format(formatStr, 10);
//        Log.i("myDir",Environment.getExternalStorageDirectory().toString()+"/DebugData/Date_"+year+month+day+"_"+HourForDir+MinuteForDir+SecondForDir);
        myDir = new File(Environment.getExternalStorageDirectory().toString()+"/DebugData/Date_"+year+String.format(formatStr, month)+String.format(formatStr, day)
                +"_"+String.format(formatStr, HourForDir)+":"+String.format(formatStr, MinuteForDir)+":"+String.format(formatStr, SecondForDir));
        File_PDR = new File(myDir,"PDR.txt");
        if(!myDir.exists())
            myDir.mkdirs();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        initBtAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler = new Handler();
        startSensor();
        if(!StartAPP) {
            iBeaconLibrary.startScan();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSensor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
            }
        }
    }

    private void findview(){
        TV_Step_Count = (TextView)findViewById(R.id.textView_Step_Count);
        TV_Orien = (TextView)findViewById(R.id.textView_Orientation);
        TV_Dist = (TextView)findViewById(R.id.textView_distance);
        TV_Start = (TextView)findViewById(R.id.textView_Start);
        editText = (EditText)findViewById(R.id.editText);
        editText_dest = (EditText)findViewById(R.id.editText_dest);
        TV_step = (TextView)findViewById(R.id.textView_step);
        TV_pkData = (TextView)findViewById(R.id.textView_pkData);
        TV_navi = (TextView)findViewById(R.id.textView_navi);
        TV_bcon = (TextView)findViewById(R.id.textView_beacon);
        BT_Start = (Button) findViewById(R.id.button_Start);
        BT_Start.setOnClickListener(this);
        BT_Arrived = (Button)findViewById(R.id.button_Arrived);
        BT_Arrived.setOnClickListener(this);
        layout_btns = (LinearLayout)findViewById(R.id.layout_buttons);
        layout_len = (LinearLayout)findViewById(R.id.layout_stepLen);
        radioGroup = (RadioGroup)findViewById(R.id.RadioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        
        BT_bcon1 = (Button)findViewById(R.id.button_bcon1);
        BT_bcon2 = (Button)findViewById(R.id.button_bcon2);
        BT_bcon3 = (Button)findViewById(R.id.button_bcon3);
        BT_bcon1.setOnClickListener(this);
        BT_bcon2.setOnClickListener(this);
        BT_bcon3.setOnClickListener(this);
    }
    
    private String mDecimalFormat(double num) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(num);
    }

    private String mDecimalFormat(float num) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(num);
    }

    private String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
    }

    /***************************************************************/
    /*****************                            ******************/
    /*****************    backend_data methods    ******************/
    /*****************                            ******************/
    /***************************************************************/

    private void createJsonData(int status) {
        try{
            pkData.put("user_id", getUserId());
            pkData.put("route_id", route_id);
            pkData.put("destination", dest);
            pkData.put("bpre", bpre);
            pkData.put("bcur", bcur);
            pkData.put("sgList", sgList);

            jsonData.put("route_id", route_id);
            if (status == IsArrived) {
                jsonData.put("isArrived", true);
                jsonData.put("isCanceled", false);
            }
            else if (status == IsCanceled) {
                jsonData.put("isArrived", false);
                jsonData.put("isCanceled", true);
            }
            else {
                jsonData.put("isArrived", false);
                jsonData.put("isCanceled", false);
            }
            jsonData.put("pkData", pkData);
            sgList = new JSONArray();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetailOfPkData() {
        String[] pkDataDetail = (pkData + "\n").split(",");
        String dataText = "";
        for (int i = 0; i < pkDataDetail.length; i++) {
            dataText += pkDataDetail[i] + "\n";
        }
        TV_pkData.setText(dataText);
    }

    private void saveSegment() {
        try {
            if (last_distance != 0 && last_distance != sg_distance) {
                JSONObject sg = new JSONObject();
                sg.put("direction", mDecimalFormat(last_orien));
                sg.put("distance", mDecimalFormat(last_distance));
                sgList.put(sg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private String getUserId() {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId.toUpperCase();
    }

    @NonNull
    private String getRouteId() {
        String[] uuid = UUID.randomUUID().toString().split("-");
        String uniqueId = uuid[0] + uuid[1] + uuid[2];
        return uniqueId.toUpperCase();
    }

    private Thread postThread = new Thread() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                URL url = new URL("http://163.13.127.174:9000/postTraj");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Content-Type", "application/json");
                byte[] data = jsonData.toString().getBytes();
                Log.d("PostTest", data.toString());
                conn.setRequestProperty("Content-length", String.valueOf(data.length));
                conn.getOutputStream().write(data);

                int code = conn.getResponseCode();
                Log.d("PostTest", "Response code: " + code);
                if (code == 200) {
                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuffer sb = new StringBuffer();
                    String len = null;

                    while ((len = br.readLine()) != null) {
                        sb.append(len);
                    }

                    String result = sb.toString();

                    setResultInAnyThread(result);
                }
                else {
                    TV_navi.setText("Connecting failed.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void setResultInAnyThread(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("PostTest", "In runOnUiThread");
                JSONObject jsonObject;
                JSONArray naviArray;
                String status, navi, naviResult = "";
                try {
                    jsonObject = new JSONObject(result);
                    status = jsonObject.get("status").toString();
                    navi = jsonObject.get("navi").toString();
                    Log.d("PostTest", "Navi: " + navi);
                    if (navi != "null") {
                        naviArray = jsonObject.getJSONArray("navi");
                        for (int i = 0; i < naviArray.length(); i++) {
                            JSONArray sgList = naviArray.getJSONArray(i);
                            naviResult += "Step" + i + ":\n";
                            for (int j = 0; j < sgList.length(); j++) {
                                JSONObject sg = sgList.getJSONObject(j);
                                naviResult += sg + "\n";
                            }
                        }
                    }
                    Toast toast = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT);
                    toast.show();
                    TV_navi.setText(naviResult);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /***************************************************************/
    /*****************                            ******************/
    /*****************     BLE/ibeacon methods    ******************/
    /*****************                            ******************/
    /***************************************************************/

    private void initBtAdapter() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Log.d("BtDetection", "Cannot support bluetooth service.");
            return;
        }

        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        iBeaconLibrary = IBeaconLibrary.getInstance();
        iBeaconLibrary.setListener(iBeaconListener);
        iBeaconLibrary.setBluetoothAdapter(this);
        iBeaconLibrary.stopScan();
    }

    private IBeaconListener iBeaconListener = new IBeaconListener() {
        @Override
        public void beaconEnter(IBeacon iBeacon) {

        }

        @Override
        public void beaconExit(IBeacon iBeacon) {

        }

        @Override
        public void beaconFound(IBeacon iBeacon) {
            if (curr_beacon == null) {
                curr_beacon = iBeacon;
                String text = "First found beacon " + getBeaconId(iBeacon) + ".\n"
                        + "Beacon dis(px): " + iBeacon.getProximity() + ".\n";
                TV_bcon.setText(TV_bcon.getText() + text + "\n");
                enter_point_px = iBeacon.getProximity();
            }
            
            String text = "Current beacon: " + getBeaconId(curr_beacon) + "\n"
                    + "Prev beacon: " + getBeaconId(prev_beacon) + "\n\n"
                    + "Found beacon " + getBeaconId(iBeacon) + " at " + getCurrentTime() + ".\n"
                    + "Dis(px): " + iBeacon.getProximity()
                    + ", Sg_dis: " + mDecimalFormat(sg_distance)
                    + ", Diff: " + prev_diff_px + "\n";

            TV_bcon.setText(TV_bcon.getText() + text + "\n");
            if (prev_beacon == null || !prev_beacon.equals(curr_beacon)) {
                if (iBeacon.getProximity() - enter_point_px > prev_diff_px && prev_diff_px != 0) {
                    text = "Exit beacon" + getBeaconId(iBeacon) + ".\n"
                            + "Beacon dis(px): " + iBeacon.getProximity() + ".\n";
                    TV_bcon.setText(TV_bcon.getText() + text + "\n");
                    prev_beacon = curr_beacon;
                    curr_beacon = null;
                    prev_diff_px = 0;

                    bpre = bcur;
                    bcur = getBeaconId(iBeacon);
                    saveSegment();
                    last_orien = Last_Orein;
                    sg_distance = 0;
                    createJsonData(Default);
                    showDetailOfPkData();
                    TV_step.setText(TV_step.getText() + "You're now at " + bcur + ".\n");
                    postThread.start();

                } else {
                    prev_diff_px = iBeacon.getProximity() - enter_point_px;
                }
            }
            else if (prev_beacon != null) {
                text = "prev_beacon = curr_beacon";
                TV_bcon.setText(TV_bcon.getText() + text + "\n");
            }
            
        }

        @Override
        public void scanState(int state) {
            if (StartAPP && (state == IBeaconLibrary.SCAN_END_EMPTY || state == IBeaconLibrary.SCAN_END_SUCCESS)) {
//                String[] stateList = {"", "SCAN_STARTED", "SCAN_END_EMPTY", "SCAN_END_SUCCESS"};
//                String text = "state: " + stateList[state] + " --> stop scanning";
//                TV_bcon.setText(TV_bcon.getText() + text + "\n");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iBeaconLibrary.startScan();
                    }
                }, 500);
            }
        }

        @Override
        public void operationError(int status) {
            Log.i(IBeaconLibrary.LOG_TAG, "Bluetooth error: " + status);
        }
    };

    private String getBeaconId(IBeacon iBeacon) {
        if (iBeacon == null)
            return "Empty";
        String[] beacon_uuid = iBeacon.getUuidHexStringDashed().split("-");
        return beacon_uuid[1] + beacon_uuid[2] + iBeacon.getMajor() + "-" + iBeacon.getMinor();
    }


    /***************************************************************/

    private void startSensor() {
        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor linearAcclerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (sensorManager != null) {
            if (linearAcclerometerSensor != null) {
                sensorManager.registerListener(this, linearAcclerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
                Toast.makeText(this, "Sensor of LinearAcclerometer isn't support", Toast.LENGTH_SHORT).show();
            }
            if (gravitySensor != null) {
                sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
                Toast.makeText(this, "Sensor of Gravity isn't support", Toast.LENGTH_SHORT).show();
            }if (gyroscopeSensor != null) {
                sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
                Toast.makeText(this, "Sensor of Gravity isn't support", Toast.LENGTH_SHORT).show();
            }
        }
        Sensor_Thread = new HandlerThread("SensorData");
        Sensor_Thread.start();
        Sensor_Handler = new Handler(Sensor_Thread.getLooper());

    }

    private void stopSensor() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (Sensor_Handler != null) {
            Sensor_Handler.removeCallbacks(Run_Sensor);
        }
        if (Sensor_Thread != null) {
            Sensor_Thread.quit();
        }

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(StartAPP){
            if(!StartSensor){
                StartSensor = true;
                Sensor_Handler.post(Run_Sensor);
            }
            Data_3_AXIS data_sensor = new Data_3_AXIS(event.sensor.getType(),event.values.clone(),event.timestamp);
            synchronized (mPauseLock) {
                data_sensorList.add(data_sensor);
                if(data_sensorList.size() > 5) {
                    mPauseLock.notify();
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public void CalculateTheStepsFromTwoAxis(int Axis,CalculateTheStepsFromTwoAxisDataType NowSteps)
    {

        if(Axis == 1)//y axis
        {
            if(GlobalStepsList.size() > 0)
            {
                int Temp = GlobalStepsList.size() - 1;

                while(YBase <= GlobalStepsList.get(Temp).Steps)
                {
                    if(NowSteps.StartTime >= GlobalStepsList.get(Temp).EndTime)
                    {
                        YBase = GlobalStepsList.get(Temp).Steps;

                        break;
                    }
                    Temp = Temp - 1;

                    if(Temp < 0)
                    {
                        break;
                    }
                }

                CalculateTheStepsFromTwoAxisDataType TempData = new CalculateTheStepsFromTwoAxisDataType();
                YBase = YBase + NowSteps.Steps;
                TempData.Steps = YBase;
                TempData.StartTime = NowSteps.StartTime;
                TempData.EndTime = NowSteps.EndTime;

                int Sort = GlobalStepsList.size() - 1;
                while(TempData.Steps < GlobalStepsList.get(Sort).Steps)
                {
                    Sort = Sort - 1;
                    if(Sort < 0)
                    {
                        break;
                    }
                }

                GlobalStepsList.add(Sort + 1, TempData);
                while(GlobalStepsList.size() > 10)
                {
                    GlobalStepsList.removeFirst();
                }
            }else
            {
                CalculateTheStepsFromTwoAxisDataType TempData = new CalculateTheStepsFromTwoAxisDataType();
                YBase = YBase + NowSteps.Steps;
                TempData.Steps = YBase;
                TempData.StartTime = NowSteps.StartTime;
                TempData.EndTime = NowSteps.EndTime;

                GlobalStepsList.add(TempData);
            }
        }else if(Axis == 2)//y axis
        {
            if(GlobalStepsList.size() > 0)
            {
                int Temp = GlobalStepsList.size() - 1;

                while(ZBase <= GlobalStepsList.get(Temp).Steps)
                {
                    if(NowSteps.StartTime >= GlobalStepsList.get(Temp).EndTime)
                    {
                        ZBase = GlobalStepsList.get(Temp).Steps;

                        break;
                    }
                    Temp = Temp - 1;

                    if(Temp < 0)
                    {
                        break;
                    }
                }

                CalculateTheStepsFromTwoAxisDataType TempData = new CalculateTheStepsFromTwoAxisDataType();
                ZBase = ZBase + NowSteps.Steps;
                TempData.Steps = ZBase;
                TempData.StartTime = NowSteps.StartTime;
                TempData.EndTime = NowSteps.EndTime;
                int Sort = GlobalStepsList.size() - 1;
                while(TempData.Steps < GlobalStepsList.get(Sort).Steps)
                {
                    Sort = Sort - 1;
                    if(Sort < 0)
                    {
                        break;
                    }
                }
                GlobalStepsList.add(Sort + 1, TempData);
                while(GlobalStepsList.size() > 10)
                {
                    GlobalStepsList.removeFirst();
                }
            }else
            {
                CalculateTheStepsFromTwoAxisDataType TempData = new CalculateTheStepsFromTwoAxisDataType();
                ZBase = ZBase + NowSteps.Steps;
                TempData.Steps = ZBase;
                TempData.StartTime = NowSteps.StartTime;
                TempData.EndTime = NowSteps.EndTime;
                GlobalStepsList.add(TempData);
            }
        }
    }

    public void CalculateTheUnstableSteps(int i)
    {
        if(Need_To_Calculate_The_Steps_For_Unstable_Wave[i])
        {
            if(i == 0)
            {
                if(((Unstable_Wave_End_Time[i] - Unstable_Wave_Start_Time[i]) < 5)&&((Unstable_Wave_End_Time[i] - Unstable_Wave_Start_Time[i]) > 0))
                {
                    Unstable_Wave_Steps[i] =(int)(((Unstable_Wave_End_Time[i] - Unstable_Wave_Start_Time[i]) / (Window_Size_For_Unstable_Wave[i]*0.02))*4);
                    Compensation_Steps[i] = Compensation_Steps[i] + Unstable_Wave_Steps[i];
                }
            }else if(i == 1)
            {
                if(((Unstable_Wave_End_Time[i] - Unstable_Wave_Start_Time[i]) < 5)&&((Unstable_Wave_End_Time[i] - Unstable_Wave_Start_Time[i]) > 0))
                {
                    Unstable_Wave_Steps[i] =(int)(((Unstable_Wave_End_Time[i] - Unstable_Wave_Start_Time[i]) / (Window_Size_For_Unstable_Wave[i]*0.02))*2);
                    Compensation_Steps[i] = Compensation_Steps[i] + Unstable_Wave_Steps[i];
                }
            }

            Window_Size_For_Unstable_Wave[i] = 0;
        }
    }

    public void PutStartPointDataToTheLinkedList(LinkedList<DataTypeForStartPoint> StartPoint, DataTypeForStartPoint TempDTFSP)
    {
        if(StartPoint.size() < /*StartPointMaxNumber*/10000)
        {
            StartPoint.add(TempDTFSP);
        }
        else
        {
            StartPoint.removeFirst();

            StartPoint.add(TempDTFSP);
        }
    }

    public int Check_Compensation(LinkedList<AccelerationData> CompensationData,float CompensationEndTime)
    {
        float StartTime = 0;
        float EndTime;
        float Time;
        boolean CompensationStart = false;
        Compensation_Time = /*(float)1.5*/(CompensationEndTime - CompensationData.getFirst().TimeAccelerationData)*((float)0.5);
        for(int i = 0;i<CompensationData.size();i++)
        {
            if((CompensationData.get(i).AccelerationData<Compensation_Upper)&&(CompensationData.get(i).AccelerationData>Compensation_Lower))
            {
                if(!CompensationStart)
                {
                    CompensationStart = true;
                    StartTime = CompensationData.get(i).TimeAccelerationData;
                }
            }else
            {
                if(CompensationStart)
                {
                    CompensationStart = false;
                    EndTime = CompensationData.get(i).TimeAccelerationData;
                    Time = EndTime - StartTime;

                    if(Time > Compensation_Time)
                    {
                        return(-1);
                    }
                }
            }

            if(CompensationData.get(i).TimeAccelerationData >= CompensationEndTime)
            {
                if(CompensationStart)
                {
                    CompensationStart = false;
                    EndTime = CompensationData.get(i).TimeAccelerationData;
                    Time = EndTime - StartTime;

                    if(Time > Compensation_Time)
                    {
                        return(-1);
                    }
                }
                break;
            }
        }
        return(0);
    }



    private float[] lowPass( float input[], float output[] ,float ALPHA) {
        if ( output == null || input == null) {

            return null;
        }
        else{
            for ( int i=0; i<input.length; i++ )
                output[i] = output[i] + ALPHA * (input[i] - output[i]);
            return output;
        }
    }
    private boolean checkfunction(float[] Grav){
        float Delta_Grav_X = Grav[0] - Last_Grav[0];
        float Delta_Grav_Y = Grav[1] - Last_Grav[1];
        float Delta_Grav_Z = Grav[2] - Last_Grav[2];
        Last_Grav[0] = Grav[0];
        Last_Grav[1] = Grav[1];
        Last_Grav[2] = Grav[2];
        if(Math.sqrt(Math.pow(Delta_Grav_X, 2) + Math.pow(Delta_Grav_Y, 2) + Math.pow(Delta_Grav_Z, 2)) > THESHOLD_CHECK_PHONE_POSE
                && Delta_Grav_X>3 && Delta_Grav_Y>3 && Delta_Grav_Z>3){
            return true;
        }
        else {
            return false;
        }
    }

    private void GetCompenstateDR(int NumberOfstep,float ST,float ET){
        float DT = ET-ST;
        float EachStepDT = DT/(float)NumberOfstep;
        boolean is_last_change_pose = false;
        double delta_x, delta_y;
        synchronized(DataOfOrein) {
            Iterator<Data_Gyroscope> iterator = DataOfOrein.iterator();
            for(int i = 0;i<NumberOfstep;i++){
                data_gyroscope = iterator.next();
                while (data_gyroscope.StartTime < ST) {
                    data_gyroscope = iterator.next();
                }

                if( i ==0 && First_StepsGet){
                    resetOrien += (90-data_gyroscope.Orein);
                    PDR_arrayList.add(new PDRinfo(1,data_gyroscope.Orein + resetOrien));

                    Message ms = new Message();
                    Bundle bd = new Bundle();
                    bd.putInt("Step",1);
                    bd.putFloat("Orien",data_gyroscope.Orein + resetOrien);
                    bd.putInt("PathID",1);
                    ms.setData(bd);
                    delta_x = Math.cos(Math.toRadians(data_gyroscope.Orein + resetOrien))*length;
                    delta_y = Math.sin(Math.toRadians(data_gyroscope.Orein + resetOrien))*length;
                    x -= delta_x;
                    y -= delta_y;

                    writePDR("GC_First :"+ ST+ ","+ "," + 1 + "_" + mDecimalFormat(data_gyroscope.Orein + resetOrien) + "_" + mDecimalFormat(data_gyroscope.Orein) + "_" + mDecimalFormat(resetOrien)   + "\r\n");
                    First_StepsGet = false;
                    checkfunction(data_gyroscope.Gravity_Value);
                    Last_Orein = data_gyroscope.Orein;

                    double now_orein = Last_Orein + resetOrien;
                    last_distance = sg_distance;
                    last_orien = Last_Orein;
                    if (Math.abs(last_orien - now_orein) > 45) {
                        sg_distance = 0;
                        saveSegment();
                    }
                    else {
                        sg_distance += Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2));
                    }
                }
//                else if(is_last_change_pose){
//                    resetOrien += (Last_Orein-data_gyroscope.Orein);
//                    PDR_arrayList.add(new PDRinfo(1,(data_gyroscope.Orein + resetOrien)));
//                    freeDraw.addPath(1,(data_gyroscope.Orein + resetOrien) ,1);
//
//                    x -= Math.cos(Math.toRadians((data_gyroscope.Orein + resetOrien) ))*length;
//                    y -= Math.sin(Math.toRadians((data_gyroscope.Orein + resetOrien)))*length;
//
//                    writePDR("GC_Change_L :" + 1 + "_" + mDecimalFormat(data_gyroscope.Orein + resetOrien) + "_" + mDecimalFormat(data_gyroscope.Orein) + "_" + mDecimalFormat(resetOrien)   + "\r\n");
//                    Last_Orein = data_gyroscope.Orein;
//                    reset_count++;
//                    is_last_change_pose = false;
//                }
                else if (checkfunction(data_gyroscope.Gravity_Value)){
                    is_last_change_pose = true;
                    resetOrien += (Last_Orein-data_gyroscope.Orein);
                    PDR_arrayList.add(new PDRinfo(1,(data_gyroscope.Orein + resetOrien)));

                    Message ms = new Message();
                    Bundle bd = new Bundle();
                    bd.putInt("Step",1);
                    bd.putFloat("Orien",data_gyroscope.Orein + resetOrien);
                    bd.putInt("PathID",1);
                    ms.setData(bd);
                    delta_x = Math.cos(Math.toRadians(data_gyroscope.Orein + resetOrien))*length;
                    delta_y = Math.sin(Math.toRadians(data_gyroscope.Orein + resetOrien))*length;
                    x -= delta_x;
                    y -= delta_y;

                    writePDR("GC_Change :"+ ST+ ","+ "," + 1 + "_" + mDecimalFormat(data_gyroscope.Orein + resetOrien) + "_" + mDecimalFormat(data_gyroscope.Orein) + "_" + mDecimalFormat(resetOrien)   + "\r\n");
                    Last_Orein = data_gyroscope.Orein;
                    reset_count++;

                    double now_orein = Last_Orein + resetOrien;
                    last_distance = sg_distance;
                    if (Math.abs(last_orien - now_orein) > 45) {
                        sg_distance = 0;
                        saveSegment();
                        last_orien = now_orein;
                    }
                    else {
                        sg_distance += Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2));
                    }
                }
                else if(Math.abs(Last_Orein - data_gyroscope.Orein) < THESHOLD_TURN){
                    resetOrien += (Last_Orein-data_gyroscope.Orein);
                    PDR_arrayList.add(new PDRinfo(1,data_gyroscope.Orein+resetOrien));

                    Message ms = new Message();
                    Bundle bd = new Bundle();
                    bd.putInt("Step",1);
                    bd.putFloat("Orien",data_gyroscope.Orein + resetOrien);
                    bd.putInt("PathID",1);
                    ms.setData(bd);
                    UI_Handler.sendMessage(ms);
                    delta_x = Math.cos(Math.toRadians(data_gyroscope.Orein + resetOrien))*length;
                    delta_y = Math.sin(Math.toRadians(data_gyroscope.Orein + resetOrien))*length;
                    x -= delta_x;
                    y -= delta_y;
                    writePDR("GC_Less    :"+ ST+ ","+ "," + 1 + "_" + mDecimalFormat(data_gyroscope.Orein + resetOrien) + "_" + mDecimalFormat(data_gyroscope.Orein) + "_" + mDecimalFormat(resetOrien)   + "\r\n");
                    Last_Orein = data_gyroscope.Orein;
                    reset_count++;

                    double now_orein = Last_Orein + resetOrien;
                    last_distance = sg_distance;
                    if (Math.abs(last_orien - now_orein) > 45) {
                        sg_distance = 0;
                        saveSegment();
                        last_orien = now_orein;
                    }
                    else {
                        sg_distance += Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2));
                    }
                }
                else{
                    Last_Orein = data_gyroscope.Orein;
                    PDR_arrayList.add(new PDRinfo(1,(data_gyroscope.Orein + resetOrien)));

                    Message ms = new Message();
                    Bundle bd = new Bundle();
                    bd.putInt("Step",1);
                    bd.putFloat("Orien",data_gyroscope.Orein + resetOrien);
                    bd.putInt("PathID",1);
                    ms.setData(bd);
                    UI_Handler.sendMessage(ms);
                    delta_x = Math.cos(Math.toRadians(data_gyroscope.Orein + resetOrien))*length;
                    delta_y = Math.sin(Math.toRadians(data_gyroscope.Orein + resetOrien))*length;
                    x -= delta_x;
                    y -= delta_y;
                    writePDR("GC_Normal:"+ ST+ ","+ "," + 1 + "_" + mDecimalFormat(data_gyroscope.Orein + resetOrien) + "_" + mDecimalFormat(data_gyroscope.Orein) + "_" + mDecimalFormat(resetOrien)   + "\r\n");

                    double now_orein = Last_Orein + resetOrien;
                    last_distance = sg_distance;
                    if (Math.abs(last_orien - now_orein) > 45) {
                        sg_distance = 0;
                        saveSegment();
                        last_orien = now_orein;
                    }
                    else {
                        sg_distance += Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2));
                    }
                }
                ST += EachStepDT;

            }
        }
    }

    private CalculateTheStepsFromTwoAxisDataType LastAddPath;
    private void checkOrein(){
//        System.out.println("NumberOfstep" + (GlobalStepsList.getLast().Steps -CheckStepChange));
//        System.out.println("ST" + GlobalStepsList.getLast().StartTime);
//        System.out.println("ET" + GlobalStepsList.getLast().EndTime);
        if(CheckStepChange != GlobalStepsList.getLast().Steps){
            if(GlobalStepsList.getLast().Steps - CheckStepChange >= 4){
                if(LastAddPath == null){
                    GetCompenstateDR(GlobalStepsList.getLast().Steps - CheckStepChange
                            , GlobalStepsList.getLast().StartTime, GlobalStepsList.getLast().EndTime);
                }
                else if(GlobalStepsList.getLast().StartTime<LastAddPath.EndTime){
                    GetCompenstateDR(GlobalStepsList.getLast().Steps - CheckStepChange
                            , LastAddPath.EndTime, GlobalStepsList.getLast().EndTime);

                }else{
                    GetCompenstateDR(GlobalStepsList.getLast().Steps - CheckStepChange
                            , GlobalStepsList.getLast().StartTime, GlobalStepsList.getLast().EndTime);
                }
                CheckStepChange = GlobalStepsList.getLast().Steps;
//                DataOfOrein.clear();
            }
            else{
                synchronized(DataOfOrein) {
                    Iterator<Data_Gyroscope> iterator = DataOfOrein.iterator();
                    data_gyroscope = iterator.next();
                    if(LastAddPath == null){
                        while (data_gyroscope.StartTime < GlobalStepsList.getLast().StartTime) {
                            data_gyroscope = iterator.next();
                        }

                    }
                    if(GlobalStepsList.getLast().StartTime < LastAddPath.EndTime){
                        while (data_gyroscope.StartTime < LastAddPath.EndTime) {
                            data_gyroscope = iterator.next();
                        }
                    }
                    else{
                        while (data_gyroscope.StartTime < GlobalStepsList.getLast().StartTime) {
                            data_gyroscope = iterator.next();
                        }

                    }

                    if(checkfunction(data_gyroscope.Gravity_Value) ){
//                        Log.i("LAST" , "LAST:" +Last_Orein );
//                        Log.i("data_gyroscope.Orein" , "data_gyroscope.Orein:" +data_gyroscope.Orein );
//                        Log.i("resetOrien" , "resetOrien:" +resetOrien );
                        resetOrien += (Last_Orein - data_gyroscope.Orein);
                        Last_Orein = data_gyroscope.Orein;
                        reset_count++;
                        writePDR("NO_Change:" + GlobalStepsList.getLast().StartTime+ ","+  (GlobalStepsList.getLast().Steps - CheckStepChange) + "_" + mDecimalFormat(data_gyroscope.Orein + resetOrien) + "_" + mDecimalFormat(data_gyroscope.Orein) + "_" + mDecimalFormat(resetOrien)   + "\r\n");

                    }
                    else if( Math.abs(Last_Orein - data_gyroscope.Orein) < THESHOLD_TURN){
                        resetOrien += (Last_Orein - data_gyroscope.Orein);
                        Last_Orein = data_gyroscope.Orein;
                        reset_count++;
                        writePDR("NO_Less   :"+ GlobalStepsList.getLast().StartTime+ ","+ "," + (GlobalStepsList.getLast().Steps - CheckStepChange) + "_" + mDecimalFormat(data_gyroscope.Orein + resetOrien) + "_" + mDecimalFormat(data_gyroscope.Orein) + "_" + mDecimalFormat(resetOrien)   + "\r\n");

                    }
                    else {
                        Last_Orein = data_gyroscope.Orein;
                        writePDR("NO_Normal:"+ GlobalStepsList.getLast().StartTime+ "," + "," + (GlobalStepsList.getLast().Steps - CheckStepChange) + "_" + mDecimalFormat(data_gyroscope.Orein + resetOrien) + "_" + mDecimalFormat(data_gyroscope.Orein) + "_" + mDecimalFormat(resetOrien)   + "\r\n");

                    }
//
                    PDR_arrayList.add(new PDRinfo(GlobalStepsList.getLast().Steps - CheckStepChange,data_gyroscope.Orein + resetOrien));
                    Message ms = new Message();
                    Bundle bd = new Bundle();
                    bd.putInt("Step",GlobalStepsList.getLast().Steps - CheckStepChange);
                    bd.putFloat("Orien",data_gyroscope.Orein + resetOrien);
                    bd.putInt("PathID",1);
                    ms.setData(bd);
                    UI_Handler.sendMessage(ms);

                    double delta_x, delta_y;
                    delta_x = Math.cos(Math.toRadians(data_gyroscope.Orein + resetOrien))*length*(GlobalStepsList.getLast().Steps - CheckStepChange);
                    delta_y = Math.sin(Math.toRadians(data_gyroscope.Orein + resetOrien))*length*(GlobalStepsList.getLast().Steps - CheckStepChange);
                    x -= delta_x;
                    y -= delta_y;

                    double now_orein = Last_Orein + resetOrien;
                    last_distance = sg_distance;
                    if (Math.abs(last_orien - now_orein) > 45) {
                        saveSegment();
                        last_orien = Last_Orein;
                        sg_distance = 0;
                    }
                    else {
                        sg_distance += Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2));
                    }

//                    PDR_arrayList.add(new PDRinfo(GlobalStepsList.getLast().Steps - CheckStepChange,data_gyroscope.Orein));
//                    freeDraw.addPath(GlobalStepsList.getLast().Steps - CheckStepChange, data_gyroscope.Orein,1);
//                    x -= Math.cos(Math.toRadians(data_gyroscope.Orein))*length*(GlobalStepsList.getLast().Steps - CheckStepChange);
//                    y -= Math.sin(Math.toRadians(data_gyroscope.Orein))*length*(GlobalStepsList.getLast().Steps - CheckStepChange);

                }

                CheckStepChange = GlobalStepsList.getLast().Steps;
//                DataOfOrein.clear();

            }

        }

        Message ms = UI_Handler.obtainMessage();
        ms.what = Constants.Handler.SensorEvent;
        UI_Handler.sendMessage(ms);


    }
    private void writePDR(String s){
        if (true){

            if(!myDir.exists())
                myDir.mkdirs();
            FileOutputStream outputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
//        System.out.println(s);
            try {
                outputStream = new FileOutputStream(File_PDR,true);
                bufferedOutputStream = new BufferedOutputStream(outputStream);
                bufferedOutputStream.write(s.getBytes());
                bufferedOutputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean STEPCOUNT_Y = true;
    private boolean STEPCOUNT_Z = true;
    private Runnable Run_Sensor = new Runnable() {

        Data_3_AXIS event;
        @Override
        public void run() {
            try {
                while(true) {
                    synchronized (mPauseLock) {
                        while (data_sensorList.size() == 0) {
                            mPauseLock.wait();
//                            Log.i("Run_Sensor","PAUSE");
                        }
                        event = data_sensorList.getFirst();
//                        System.out.println(event.types + "," + event.timestamp);
                        data_sensorList.removeFirst();
                    }
                    switch(event.types){
                        case Sensor.TYPE_LINEAR_ACCELERATION:
                        {
                            if(!Accfirst){
                                Accdt = (event.timestamp - Acc_last_timestamp) * NS2S;
                            }
                            else
                            {
                                Accfirst = false;
                                Accdt = 0;
                            }

                            Acc_last_timestamp = event.timestamp;

                            SensorsSenseData AccData = new SensorsSenseData();
                            AccData.SensorDataTimeET = event.timestamp;
                            AccData.SensorDataTimeST = Accdt;
                            System.arraycopy(event.values, 0, AccData.SensorData, 0, 3);
                            AccelerationDataLinkedList.add(AccData);

                        }
                        break;

                        case Sensor.TYPE_GRAVITY:
                        {
                            Grav_values = event.values.clone();
                            Degree_Axis[0] = Math.atan(Grav_values[0] /
                                    Math.sqrt(Math.pow(Grav_values[1],2) +Math.pow(Grav_values[2],2)));
                            Degree_Axis[1] = Math.atan(Grav_values[1] /
                                    Math.sqrt(Math.pow(Grav_values[0],2) +Math.pow(Grav_values[2],2)));
                            Degree_Axis[2] = Math.atan(Grav_values[2] /
                                    Math.sqrt(Math.pow(Grav_values[0],2) +Math.pow(Grav_values[1],2)));
                            TimeStamp_Gravity = event.timestamp;


                            float[] GravValue = event.values.clone();
                            if(!Gravfirst){
                                Gravdt = (event.timestamp - Grav_last_timestamp) * NS2S;
                            }
                            else
                            {
                                Gravfirst = false;
                                Gravdt = 0;
                            }

                            Grav_last_timestamp = event.timestamp;
                            float tempYAngle = 0;

                            tempYAngle = (float) Math.atan(GravValue[0]/GravValue[2]);
                            if(GravValue[2]>=0)
                            {
                                YAngle = tempYAngle;
                            }else
                            {
                                YAngle = (float) (Math.PI + tempYAngle);
                            }

                            float[] YRotation = new float[3];

                            YRotation[0] = (float) ((Math.cos(YAngle) * GravValue[0])+(0 * GravValue[1])+((-Math.sin(YAngle)) * GravValue[2]));
                            YRotation[1] = (0 * GravValue[0])+(1 * GravValue[1])+(0 * GravValue[2]);
                            YRotation[2] = (float) ((Math.sin(YAngle) * GravValue[0])+(0 * GravValue[1])+(Math.cos(YAngle) * GravValue[2]));

                            XAngle = (float) Math.atan(YRotation[1]/YRotation[2]);

                            TheAngleCalculatedFromGravity AngleData = new TheAngleCalculatedFromGravity();
                            AngleData.YAngle = YAngle;
                            AngleData.XAngle = XAngle;
                            AngleData.SensorDataTimeET = event.timestamp;
                            AngleData.SensorDataTimeST = Gravdt;
                            AngleDataList.add(AngleData);

                        }
                        break;

                        case Sensor.TYPE_GYROSCOPE:{
                            if(TimeStamp_Gyroscope == 0){
                                TimeStamp_Gyroscope = event.timestamp;
                                Last_Gyro = event.values;
                                Value_Gyroscope = event.values;
                                StartTime_Gyro = 0;
                                EndTime_Gyro = 0;
//                                writePDR(AllInZ + "," + Value_Gyroscope[0]+ "," + Value_Gyroscope[1]+ "," + Value_Gyroscope[2]+ "," + event.timestamp+"\r\n");
                            }
                            else if(!Gyro_Start){
                                lowPass(event.values.clone(), Value_Gyroscope, 0.2f);
                                Delta_Time = (event.timestamp-TimeStamp_Gyroscope)* NS2S;
                                Last_Gyro[0] = Value_Gyroscope[0];
                                Last_Gyro[1] = Value_Gyroscope[1];
                                Last_Gyro[2] = Value_Gyroscope[2];
                                TimeStamp_Gyroscope = event.timestamp;

                                EndTime_Gyro = StartTime_Gyro + Delta_Time;
                                Data_Gyroscope data_gyroscope = new Data_Gyroscope(90,StartTime_Gyro,Delta_Time,EndTime_Gyro,Grav_values.clone());
                                DataOfOrein.add(data_gyroscope);

//                                Log.d(TAG,"Gyro_Start");
                                StartTime_Gyro = EndTime_Gyro;
                                if(StartTime_Gyro > 0.5f){
                                    Gyro_Start = true;

                                }
//                                writePDR(AllInZ + "," + Value_Gyroscope[0]+ "," + Value_Gyroscope[1]+ "," + Value_Gyroscope[2]+ "," + event.timestamp+"\r\n");
                            }
                            else if(Gyro_Start && (event.timestamp-TimeStamp_Gyroscope)* NS2S>0.02){
                                lowPass(event.values.clone(), Value_Gyroscope, 0.2f);
                                Delta_Time = (event.timestamp-TimeStamp_Gyroscope)* NS2S;

                                Delta[0] = (Last_Gyro[0] + Value_Gyroscope[0]) * Delta_Time/2;
                                Delta[1] = (Last_Gyro[1] + Value_Gyroscope[1]) * Delta_Time/2;
                                Delta[2] = (Last_Gyro[2] + Value_Gyroscope[2]) * Delta_Time/2;

                                AllInZ += Math.toDegrees((Delta[0] * Math.sin(Degree_Axis[0])
                                        + Delta[1] * Math.sin(Degree_Axis[1])
                                        + Delta[2] * Math.sin(Degree_Axis[2])));


                                Message ms = new Message();
                                ms.what = Constants.Handler.GyroEvent;
                                Bundle bd = new Bundle();
                                bd.putFloat("ALLINZ",AllInZ);
                                bd.putFloat("START",StartTime_Gyro);
                                ms.setData(bd);
                                UI_Handler.sendMessage(ms);

                                Last_Gyro[0] = Value_Gyroscope[0];
                                Last_Gyro[1] = Value_Gyroscope[1];
                                Last_Gyro[2] = Value_Gyroscope[2];
                                TimeStamp_Gyroscope = event.timestamp;

                                EndTime_Gyro = StartTime_Gyro + Delta_Time;
                                synchronized(DataOfOrein) {

                                    Data_Gyroscope data_gyroscope = new Data_Gyroscope((AllInZ %360.0f),StartTime_Gyro,Delta_Time,EndTime_Gyro,Grav_values.clone());
                                    DataOfOrein.add(data_gyroscope);
                                    if(DataOfOrein.size()>2000){
                                        DataOfOrein.remove(0);
//                                        System.out.println(DataOfOrein.size());
                                    }
                                }
                                StartTime_Gyro = EndTime_Gyro;
//                                System.out.println(event.types + "," + Value_Gyroscope[0]+ "," + Value_Gyroscope[1]+ "," + Value_Gyroscope[2]+ "," + event.timestamp );
//                                writePDR("ALL in Z" + AllInZ+"\r\n");
//                                writePDR(AllInZ + "," + Value_Gyroscope[0]+ "," + Value_Gyroscope[1]+ "," + Value_Gyroscope[2]+ "," + event.timestamp+"\r\n");
//                                System.out.println(StartTime_Gyro + "," +AllInZ + "," + DataOfOrein.size());
                            }

                            break;
                        }


                    }

                    TheAngleCalculatedFromGravity TACFG = new TheAngleCalculatedFromGravity();
                    SensorsSenseData SSDForAcc = new SensorsSenseData();

                    if(AngleDataList.isEmpty()||AccelerationDataLinkedList.isEmpty())
                    {
                        SenseDataCollectNotFinish = true;
                    }
                    else
                    {
                        SenseDataCollectNotFinish = false;

                        TACFG = AngleDataList.getLast();
                        SSDForAcc = AccelerationDataLinkedList.getLast();

                        if(TACFG.SensorDataTimeET != SSDForAcc.SensorDataTimeET)
                        {
                            SenseTimeNotSame = true;
                        }
                        else
                        {
                            SenseTimeNotSame = false;
                        }

                        if(AngleDataList.size() != AccelerationDataLinkedList.size())
                        {
                            SenseSizeNotSame = true;
                        }
                        else
                        {
                            SenseSizeNotSame = false;
                        }

                        if((SenseTimeNotSame != true)/*&&(SenseSizeNotSame != true)*/&&(SenseDataCollectNotFinish != true))
                        {

                            float[] TempAccForYRotation = new float[3];
                            float[] TempAccForXRotation = new float[3];

                            TempAccForYRotation[0] = (float) ((Math.cos(TACFG.YAngle) * SSDForAcc.SensorData[0])+(0 * SSDForAcc.SensorData[1])+((-Math.sin(TACFG.YAngle)) * SSDForAcc.SensorData[2]));
                            TempAccForYRotation[1] = (0 * SSDForAcc.SensorData[0])+(1 * SSDForAcc.SensorData[1])+(0 * SSDForAcc.SensorData[2]);
                            TempAccForYRotation[2] = (float) ((Math.sin(TACFG.YAngle) * SSDForAcc.SensorData[0])+(0 * SSDForAcc.SensorData[1])+(Math.cos(TACFG.YAngle) * SSDForAcc.SensorData[2]));

                            TempAccForXRotation[0] = (1 * TempAccForYRotation[0])+(0 * TempAccForYRotation[1])+(0 * TempAccForYRotation[2]);
                            TempAccForXRotation[1] = (float) ((0 * TempAccForYRotation[0])+(Math.cos(-TACFG.XAngle) * TempAccForYRotation[1])+(Math.sin(-TACFG.XAngle) * TempAccForYRotation[2]));
                            TempAccForXRotation[2] = (float) ((0 * TempAccForYRotation[0])+((-Math.sin(-TACFG.XAngle)) * TempAccForYRotation[1])+(Math.cos(-TACFG.XAngle) * TempAccForYRotation[2]));

                            AngleDataList.clear();
                            AccelerationDataLinkedList.clear();

                            if(bool_FirstTimeToDoLinearInterpolation)
                            {

                                AccelerationData XAD = new AccelerationData();
                                AccelerationData YAD = new AccelerationData();
                                AccelerationData ZAD = new AccelerationData();

                                XAD.AccelerationData = TempAccForXRotation[0];
                                XAD.TimeAccelerationData = 0;
                                YAD.AccelerationData = TempAccForXRotation[1];
                                YAD.TimeAccelerationData = 0;
                                ZAD.AccelerationData = TempAccForXRotation[2];
                                ZAD.TimeAccelerationData = 0;

                                for(int i = 0;i<3;i++)
                                {
                                    SPIndex[i] = 0;
                                }

                                BeforeTime = 0;
                                NowTime = 0;
                                LinearInterpolationTime = (float)0.02;
                                BeforeAcc[0] = TempAccForXRotation[0];
                                NowAcc[0] = TempAccForXRotation[0];
                                BeforeAcc[1] = TempAccForXRotation[1];
                                NowAcc[1] = TempAccForXRotation[1];
                                BeforeAcc[2] = TempAccForXRotation[2];
                                NowAcc[2] = TempAccForXRotation[2];
                                bool_FirstTimeToDoLinearInterpolation = false;

                                for(int i = 0;i<3;i++)
                                {
                                    FirstPutDataToLI[i] = false;
                                }
                                for(int x=0;x<3;x++)
                                {
                                    FirstDoLI[x] = false;
                                }
                                for(int i = 0;i<3;i++)
                                {
                                    WalkingStep[i] = 0;
                                }

                                TheState2WindowSize[0] = 0;
                                TheState2WindowSize[1] = 0;

                                SearchTheSimilarWaveFromPast[0] = false;
                                SearchTheSimilarWaveFromPast[1] = false;
                            }
                            else
                            {
                                BeforeTime = NowTime;
                                NowTime = NowTime + SSDForAcc.SensorDataTimeST;
                                BeforeAcc[0] = NowAcc[0];
                                NowAcc[0] = TempAccForXRotation[0];
                                BeforeAcc[1] = NowAcc[1];
                                NowAcc[1] = TempAccForXRotation[1];
                                BeforeAcc[2] = NowAcc[2];
                                NowAcc[2] = TempAccForXRotation[2];
                            }

                            while((BeforeTime <= LinearInterpolationTime)&&(NowTime > LinearInterpolationTime))
                            {
                                if(First_Compensation)
                                {
                                    Compensation_Steps[0] = 0;
                                    Compensation_Steps[1] = 0;
                                    First_Compensation = false;
                                }

                                AccelerationData XAD = new AccelerationData();
                                AccelerationData YAD = new AccelerationData();
                                AccelerationData ZAD = new AccelerationData();

                                YAD.AccelerationData = CC.LinearInterpolation(BeforeTime,NowTime,LinearInterpolationTime,BeforeAcc[1],NowAcc[1]);
                                ZAD.AccelerationData = CC.LinearInterpolation(BeforeTime,NowTime,LinearInterpolationTime,BeforeAcc[2],NowAcc[2]);
                                YAD.TimeAccelerationData = LinearInterpolationTime;
                                ZAD.TimeAccelerationData = LinearInterpolationTime;
                                if(StartWalking[1] == true)
                                {
                                    YLinearInterpolationAccelerationData.add(YAD);
                                    SPIndex[1]++;
                                    if(Need_To_Calculate_The_Steps_For_Unstable_Wave[0] == true)
                                    {
                                        Check_Compensation_DataY.add(YAD);
                                    }
                                }
                                if(StartWalking[2] == true)
                                {
                                    ZLinearInterpolationAccelerationData.add(ZAD);
                                    SPIndex[2]++;
                                    if(Need_To_Calculate_The_Steps_For_Unstable_Wave[1] == true)
                                    {
                                        Check_Compensation_DataZ.add(ZAD);
                                    }
                                }

                                int[] tempUpOrDown = {-1};
                                int[] tempUpOrDownY = {-1};

                                if(STEPCOUNT_Y)
                                {
                                    try {
                                        if((CC.FindTheStartingWalkingPoint(Acc_last_LIAccData[1], YAD.AccelerationData, tempUpOrDownY))&&(FirstDoLI[1]))
                                        {
                                            StartWalking[1] = true;
                                            if(!FirstPutDataToLI[1])
                                            {
                                                YLinearInterpolationAccelerationData.add(YAD);
                                                SPIndex[1]++;
                                                FirstPutDataToLI[1] = true;
                                            }

                                            DataTypeForStartPoint DTFSP = new DataTypeForStartPoint();
                                            DTFSP.ST = YAD.TimeAccelerationData;
                                            DTFSP.MCCR = 0;
                                            DTFSP.Finish = false;
                                            DTFSP.Dead = false;
                                            DTFSP.Index = SPIndex[1]-1;
                                            DTFSP.UpOrDown = tempUpOrDownY[0];

                                            if(!YStartPoint.isEmpty())
                                            {
                                                switch(CutWaveStateY)
                                                {
                                                    case 0:
                                                    {
                                                        DataTypeForStartPoint PastSP = new DataTypeForStartPoint();
                                                        PastSP = YStartPoint.getFirst();
                                                        while( (DTFSP.ST - PastSP.ST) > 5)
                                                        {
                                                            int temp = 0;
                                                            if(YStartPoint.size()<=1)
                                                            {
                                                                SPIndex[1] = 1;
                                                                YLinearInterpolationAccelerationData.clear();
                                                                YLinearInterpolationAccelerationData.add(YAD);
                                                                YStartPoint.clear();
                                                                DTFSP.Index = 0;
                                                                No_RunY = true;
                                                                PastSP = YStartPoint.getFirst();
                                                                break;
                                                            }
                                                            else
                                                            {
                                                                temp = YStartPoint.get(1).Index;
                                                                for(int i = 0; i < temp; i++)
                                                                {
                                                                    YLinearInterpolationAccelerationData.removeFirst();
                                                                    SPIndex[1] = SPIndex[1] - 1;
                                                                }

                                                                for(int i = 0;i < YStartPoint.size();i++)
                                                                {
                                                                    YStartPoint.get(i).Index = YStartPoint.get(i).Index - temp;
                                                                }

                                                                DTFSP.Index = DTFSP.Index - temp;

                                                                YStartPoint.removeFirst();
                                                                PastSP = YStartPoint.getFirst();
                                                            }
                                                        }

                                                        if(!No_RunY)
                                                        {
                                                            for(int j = 0;j<YStartPoint.size();j++)
                                                            {
                                                                float TempCC = 0;
                                                                int MidIndex = 0;
                                                                PastSP = YStartPoint.get(j);

                                                                if(((DTFSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound1600ms)&&(DTFSP.UpOrDown == PastSP.UpOrDown))
                                                                {
                                                                    if((DTFSP.Index - PastSP.Index) % 2 == 0)
                                                                    {
                                                                        MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index)/2;
                                                                    }else
                                                                    {
                                                                        MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index -1)/2;
                                                                    }
                                                                    TempCC = CC.CorrelationCoefficient(YLinearInterpolationAccelerationData,PastSP.Index,MidIndex);

                                                                    if(TempCC >= CorrelationCoefficientThreshold)
                                                                    {
                                                                        CutWaveStateY = 1;
                                                                        YFirstFindSimilarWave.clear();
                                                                        for(int i = PastSP.Index;i<=DTFSP.Index;i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = YLinearInterpolationAccelerationData.get(i);
                                                                            YFirstFindSimilarWave.add(TempData);
                                                                        }

                                                                        AccDHY.TheFirstSPIndex = j;
                                                                        AccDHY.TheSecondSPIndex = YStartPoint.size();
                                                                        AccDHY.WindowSize[2] = DTFSP.Index - PastSP.Index;
                                                                        break;
                                                                    }
                                                                    else if(TempCC < CorrelationCoefficientThreshold)
                                                                    {
                                                                        CutWaveStateY = 0;
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    CutWaveStateY = 0;
                                                                }
                                                            }
                                                        }
                                                        No_RunY = false;
                                                    }
                                                    break;

                                                    case 1:
                                                    {

                                                        DataTypeForStartPoint PastSP = new DataTypeForStartPoint();
                                                        float TempCC = 0;
                                                        int MidIndex = 0;
                                                        PastSP = YStartPoint.get(AccDHY.TheSecondSPIndex);

                                                        if(((DTFSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound1600ms)&&(DTFSP.UpOrDown == PastSP.UpOrDown)&&((DTFSP.Index - PastSP.Index)>=(AccDHY.WindowSize[2]-AdditionalWindowSizes))&&((DTFSP.Index - PastSP.Index)<=(AccDHY.WindowSize[2]+AdditionalWindowSizes)))
                                                        {
                                                            if((DTFSP.Index - PastSP.Index) % 2 == 0)
                                                            {
                                                                MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index)/2;
                                                            }else
                                                            {
                                                                MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index -1)/2;
                                                            }
                                                            TempCC = CC.CorrelationCoefficient(YLinearInterpolationAccelerationData,PastSP.Index,MidIndex);

                                                            if(TempCC >= CorrelationCoefficientThreshold)
                                                            {
                                                                YTempForSecondAccelerationData.clear();
                                                                for(int i = 0;i<YFirstFindSimilarWave.size();i++)
                                                                {
                                                                    AccelerationData TempData = new AccelerationData();
                                                                    TempData = YFirstFindSimilarWave.get(i);
                                                                    YTempForSecondAccelerationData.add(TempData);
                                                                }

                                                                for(int i = PastSP.Index + 1;i<=DTFSP.Index;i++)
                                                                {
                                                                    AccelerationData TempData = new AccelerationData();
                                                                    TempData = YLinearInterpolationAccelerationData.get(i);
                                                                    YTempForSecondAccelerationData.add(TempData);
                                                                }
                                                                if((YTempForSecondAccelerationData.size()-1) % 2 == 0)
                                                                {
                                                                    MidIndex = (YTempForSecondAccelerationData.size()-1)/2;
                                                                }else
                                                                {
                                                                    MidIndex = (YTempForSecondAccelerationData.size()-2)/2;
                                                                }

                                                                float TempCCWithFirstCut = 0;

                                                                TempCCWithFirstCut = CC.CorrelationCoefficient(YTempForSecondAccelerationData,0,MidIndex);

                                                                if(TempCCWithFirstCut >= CorrelationCoefficientThreshold3)
                                                                {
                                                                    int TempWindowSize = DTFSP.Index - PastSP.Index;
                                                                    AccDHY.WindowSize[2] = (AccDHY.WindowSize[2] + TempWindowSize)/2;
                                                                    if(Need_To_Calculate_The_Steps_For_Unstable_Wave[0])
                                                                    {
                                                                        Unstable_Wave_End_Time[0] = YTempForSecondAccelerationData.getFirst().TimeAccelerationData;
                                                                        Window_Size_For_Unstable_Wave[0] = AccDHY.WindowSize[2]+1;
                                                                        CalculateTheUnstableSteps(0);
                                                                        if(Check_Compensation(Check_Compensation_DataY,Unstable_Wave_End_Time[0]) == -1)
                                                                        {
                                                                            Unstable_Wave_Steps[0] = 0;
                                                                            Check_Compensation_DataY.clear();
                                                                        }
                                                                    }

                                                                    ToState2WindowSize[0] = DTFSP.Index - PastSP.Index;

                                                                    YStartPoint.clear();
                                                                    YLinearInterpolationAccelerationData.clear();
                                                                    YLinearInterpolationAccelerationData.add(YAD);
                                                                    SPIndex[1] = 1;
                                                                    DTFSP.Index = SPIndex[1]-1;

                                                                    if(Need_To_Calculate_The_Steps_For_Unstable_Wave[0] == true)
                                                                    {
                                                                        if(Unstable_Wave_Steps[0] != 0)
                                                                        {
                                                                            CalculateTheStepsFromTwoAxisDataType PastSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                            PastSteps.Steps = Unstable_Wave_Steps[0];
                                                                            PastSteps.StartTime = Unstable_Wave_Start_Time[0];
                                                                            PastSteps.EndTime = Unstable_Wave_End_Time[0];
                                                                            CalculateTheStepsFromTwoAxis(1,PastSteps);
                                                                        }
                                                                        Unstable_Wave_Steps[0] = 0;
                                                                        Unstable_Wave_Start_Time[0] = 0;
                                                                        Unstable_Wave_End_Time[0] = 0;
                                                                        Need_To_Calculate_The_Steps_For_Unstable_Wave[0] = false;
                                                                    }

                                                                    WalkingStep[1] = WalkingStep[1]+8;
                                                                    CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                    NowSteps.Steps = 8;
                                                                    NowSteps.StartTime = YTempForSecondAccelerationData.getFirst().TimeAccelerationData;
                                                                    NowSteps.EndTime = YTempForSecondAccelerationData.getLast().TimeAccelerationData;
                                                                    CalculateTheStepsFromTwoAxis(1,NowSteps);
                                                                    CutWaveStateY = 2;
                                                                    break;
                                                                }
                                                                else if(TempCCWithFirstCut < CorrelationCoefficientThreshold3)
                                                                {
                                                                    CutWaveStateY = 1;
                                                                    break;
                                                                }
                                                            }
                                                            else if(TempCC < CorrelationCoefficientThreshold)
                                                            {
                                                                CutWaveStateY = 1;
                                                                break;
                                                            }
                                                        }else if(((DTFSP.Index - PastSP.Index)>=(AccDHY.WindowSize[2]+AdditionalWindowSizes)))
                                                        {
                                                            int temp = 0;
                                                            temp = YStartPoint.get(AccDHY.TheFirstSPIndex+1).Index;

                                                            for(int i = 0;i<=temp;i++)
                                                            {
                                                                YLinearInterpolationAccelerationData.removeFirst();
                                                                SPIndex[1] = SPIndex[1] - 1;
                                                            }

                                                            for(int i = 0;i < YStartPoint.size();i++)
                                                            {
                                                                YStartPoint.get(i).Index = YStartPoint.get(i).Index - temp;
                                                            }
                                                            DTFSP.Index = DTFSP.Index - temp;
                                                            for(int i = 0;i <= AccDHY.TheFirstSPIndex;i++)
                                                            {
                                                                YStartPoint.removeFirst();
                                                            }
                                                            CutWaveStateY = 7;
                                                        }
                                                    }
                                                    if(CutWaveStateY != 7)
                                                    {
                                                        break;
                                                    }

                                                    case 7:
                                                    {
                                                        for(int i = 0;i < YStartPoint.size();i++)
                                                        {
                                                            for(int j = i+1;j < YStartPoint.size();j++)
                                                            {
                                                                DataTypeForStartPoint PastSP = new DataTypeForStartPoint();
                                                                DataTypeForStartPoint NowSP = new DataTypeForStartPoint();

                                                                float TempCC = 0;
                                                                int MidIndex = 0;

                                                                PastSP = YStartPoint.get(i);
                                                                NowSP = YStartPoint.get(j);

                                                                if(((NowSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound1600ms)&&(NowSP.UpOrDown == PastSP.UpOrDown))
                                                                {
                                                                    if((NowSP.Index - PastSP.Index) % 2 == 0)
                                                                    {
                                                                        MidIndex = PastSP.Index + (NowSP.Index - PastSP.Index)/2;
                                                                    }else
                                                                    {
                                                                        MidIndex = PastSP.Index + (NowSP.Index - PastSP.Index -1)/2;
                                                                    }

                                                                    TempCC = CC.CorrelationCoefficient(YLinearInterpolationAccelerationData,PastSP.Index,MidIndex);

                                                                    if(TempCC >= CorrelationCoefficientThreshold)
                                                                    {
                                                                        C7FindTheWaveY = true;
                                                                        YFirstFindSimilarWave.clear();
                                                                        for(int k = PastSP.Index;k<=NowSP.Index;k++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = YLinearInterpolationAccelerationData.get(k);
                                                                            YFirstFindSimilarWave.add(TempData);
                                                                        }
                                                                        AccDHY.TheFirstSPIndex = i;
                                                                        AccDHY.TheSecondSPIndex = j;
                                                                        AccDHY.WindowSize[2] = NowSP.Index - PastSP.Index;

                                                                        break;
                                                                    }
                                                                    else if(TempCC < CorrelationCoefficientThreshold)
                                                                    {
                                                                        CutWaveStateY = 7;
                                                                    }
                                                                }
                                                            }

                                                            if(C7FindTheWaveY)
                                                            {
                                                                break;
                                                            }
                                                        }

                                                        if(C7FindTheWaveY)
                                                        {
                                                            C7FindTheWaveY = false;

                                                            for(int j = AccDHY.TheSecondSPIndex + 1 ;j<YStartPoint.size();j++)
                                                            {
                                                                DataTypeForStartPoint PastSP = new DataTypeForStartPoint();
                                                                DataTypeForStartPoint NowSP = new DataTypeForStartPoint();

                                                                float TempCC = 0;
                                                                int MidIndex = 0;

                                                                PastSP = YStartPoint.get(AccDHY.TheSecondSPIndex);
                                                                NowSP = YStartPoint.get(j);

                                                                if(((NowSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound1600ms)&&(NowSP.UpOrDown == PastSP.UpOrDown)&&((NowSP.Index - PastSP.Index)>=(AccDHY.WindowSize[2]-AdditionalWindowSizes))&&((NowSP.Index - PastSP.Index)<=(AccDHY.WindowSize[2]+AdditionalWindowSizes)))
                                                                {
                                                                    if((NowSP.Index - PastSP.Index) % 2 == 0)
                                                                    {
                                                                        MidIndex = PastSP.Index + (NowSP.Index - PastSP.Index)/2;
                                                                    }else
                                                                    {
                                                                        MidIndex = PastSP.Index + (NowSP.Index - PastSP.Index -1)/2;
                                                                    }

                                                                    TempCC = CC.CorrelationCoefficient(YLinearInterpolationAccelerationData,PastSP.Index,MidIndex);

                                                                    if(TempCC >= CorrelationCoefficientThreshold)
                                                                    {
                                                                        YTempForSecondAccelerationData.clear();

                                                                        for(int i = 0;i<YFirstFindSimilarWave.size();i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = YFirstFindSimilarWave.get(i);
                                                                            YTempForSecondAccelerationData.add(TempData);
                                                                        }

                                                                        for(int i = PastSP.Index + 1;i<=NowSP.Index;i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = YLinearInterpolationAccelerationData.get(i);
                                                                            YTempForSecondAccelerationData.add(TempData);
                                                                        }

                                                                        if((YTempForSecondAccelerationData.size()-1) % 2 == 0)
                                                                        {
                                                                            MidIndex = (YTempForSecondAccelerationData.size()-1)/2;
                                                                        }else
                                                                        {
                                                                            MidIndex = (YTempForSecondAccelerationData.size()-2)/2;
                                                                        }

                                                                        float TempCCWithFirstCut = 0;
                                                                        TempCCWithFirstCut = CC.CorrelationCoefficient(YTempForSecondAccelerationData,0,MidIndex);

                                                                        if(TempCCWithFirstCut >= CorrelationCoefficientThreshold3)
                                                                        {
                                                                            int TempWindowSize = NowSP.Index - PastSP.Index;
                                                                            AccDHY.WindowSize[2] = (AccDHY.WindowSize[2] + TempWindowSize)/2;

                                                                            if(Need_To_Calculate_The_Steps_For_Unstable_Wave[0])
                                                                            {
                                                                                Unstable_Wave_End_Time[0] = YTempForSecondAccelerationData.getFirst().TimeAccelerationData;
                                                                                Window_Size_For_Unstable_Wave[0] = AccDHY.WindowSize[2]+1;
                                                                                CalculateTheUnstableSteps(0);
                                                                                if(Check_Compensation(Check_Compensation_DataY,Unstable_Wave_End_Time[0]) == -1)
                                                                                {
                                                                                    Unstable_Wave_Steps[0] = 0;
                                                                                    Check_Compensation_DataY.clear();
                                                                                }
                                                                            }

                                                                            ToState2WindowSize[0] = NowSP.Index - PastSP.Index;

                                                                            int TempIndex = 0;

                                                                            TempIndex = NowSP.Index;

                                                                            for(int i = 0;i<TempIndex;i++)
                                                                            {
                                                                                YLinearInterpolationAccelerationData.removeFirst();
                                                                                SPIndex[1] = SPIndex[1] - 1;
                                                                            }

                                                                            for(int i = 0;i < YStartPoint.size();i++)
                                                                            {
                                                                                YStartPoint.get(i).Index = YStartPoint.get(i).Index - TempIndex;
                                                                            }

                                                                            DTFSP.Index = DTFSP.Index - TempIndex;

                                                                            for(int i = 0;i < j;i++)
                                                                            {
                                                                                YStartPoint.removeFirst();
                                                                            }

                                                                            AccDHY.TheFirstSPIndex = 0;
                                                                            AccDHY.TheSecondSPIndex = 0;

                                                                            CutWaveStateY = 2;

                                                                            if(Need_To_Calculate_The_Steps_For_Unstable_Wave[0])
                                                                            {
                                                                                if(Unstable_Wave_Steps[0] != 0)
                                                                                {
                                                                                    CalculateTheStepsFromTwoAxisDataType PastSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                                    PastSteps.Steps = Unstable_Wave_Steps[0];
                                                                                    PastSteps.StartTime = Unstable_Wave_Start_Time[0];
                                                                                    PastSteps.EndTime = Unstable_Wave_End_Time[0];
                                                                                    CalculateTheStepsFromTwoAxis(1,PastSteps);
                                                                                }
                                                                                Unstable_Wave_Steps[0] = 0;
                                                                                Unstable_Wave_Start_Time[0] = 0;
                                                                                Unstable_Wave_End_Time[0] = 0;
                                                                                Need_To_Calculate_The_Steps_For_Unstable_Wave[0] = false;
                                                                            }

                                                                            WalkingStep[1] = WalkingStep[1] + 8;
                                                                            CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                            NowSteps.Steps = 8;
                                                                            NowSteps.StartTime = YTempForSecondAccelerationData.getFirst().TimeAccelerationData;
                                                                            NowSteps.EndTime = YTempForSecondAccelerationData.getLast().TimeAccelerationData;
                                                                            CalculateTheStepsFromTwoAxis(1,NowSteps);
                                                                            break;
                                                                        }

                                                                    }
                                                                    else if(TempCC < CorrelationCoefficientThreshold)
                                                                    {
                                                                    }
                                                                }
                                                                else if(((NowSP.Index - PastSP.Index)>=(AccDHY.WindowSize[2]+AdditionalWindowSizes)))
                                                                {
                                                                    int TempIndex = 0;
                                                                    TempIndex = YStartPoint.get(AccDHY.TheFirstSPIndex+1).Index;
                                                                    for(int i = 0;i<TempIndex;i++)
                                                                    {
                                                                        YLinearInterpolationAccelerationData.removeFirst();
                                                                        SPIndex[1] = SPIndex[1] - 1;
                                                                    }
                                                                    for(int i = 0;i < YStartPoint.size();i++)
                                                                    {
                                                                        YStartPoint.get(i).Index = YStartPoint.get(i).Index - TempIndex ;
                                                                    }
                                                                    DTFSP.Index = DTFSP.Index - TempIndex;
                                                                    for(int i = 0;i <= AccDHY.TheFirstSPIndex;i++)
                                                                    {
                                                                        YStartPoint.removeFirst();
                                                                    }

                                                                    CutWaveStateY = 7;
                                                                    RecordTheS7SecondWaveY = false;
                                                                    break;
                                                                }

                                                                if((NowSP.Index - PastSP.Index)<=(AccDHY.WindowSize[2]+AdditionalWindowSizes))
                                                                {
                                                                    RecordTheS7SecondWaveY = true;
                                                                }
                                                            }

                                                            if((RecordTheS7SecondWaveY)&&(CutWaveStateY != 2))
                                                            {
                                                                RecordTheS7SecondWaveY = false;

                                                                CutWaveStateY = 1;

                                                                C7FindTheWaveY = false;
                                                            }
                                                        }
                                                        else
                                                        {
                                                            CutWaveStateY = 0;
                                                            C7FindTheWaveY = false;
                                                        }
                                                    }
                                                    break;

                                                    case 2:
                                                    {
                                                        DataTypeForStartPoint PastSP = new DataTypeForStartPoint();
                                                        float TempCC = 0;
                                                        int MidIndex = 0;

                                                        PastSP = YStartPoint.getFirst();

                                                        if(State2FirstRunY)
                                                        {
                                                            if(((DTFSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound1600ms)&&(DTFSP.UpOrDown == PastSP.UpOrDown)&&((DTFSP.Index - PastSP.Index)<=(ToState2WindowSize[0] + AdditionalWindowSizes))&&((DTFSP.Index - PastSP.Index)>=(ToState2WindowSize[0] - AdditionalWindowSizes)))
                                                            {
                                                                if((DTFSP.Index - PastSP.Index) % 2 == 0)
                                                                {
                                                                    MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index)/2;
                                                                }else
                                                                {
                                                                    MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index -1)/2;
                                                                }

                                                                TempCC = CC.CorrelationCoefficient(YLinearInterpolationAccelerationData,PastSP.Index,MidIndex);

                                                                if(YTMCC1 <= TempCC)
                                                                {
                                                                    YTMCC1 = TempCC;
                                                                    YTMCCI1 = YStartPoint.size();
                                                                }

                                                                if((TempCC >= CorrelationCoefficientThreshold))
                                                                {
                                                                    YTMCC1 = -1;
                                                                    YTMCCI1 = 0;
                                                                    WalkingStep[1] = WalkingStep[1]+4;
                                                                    CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                    NowSteps.Steps = 4;
                                                                    NowSteps.StartTime = PastSP.ST;
                                                                    NowSteps.EndTime = DTFSP.ST;
                                                                    CalculateTheStepsFromTwoAxis(1,NowSteps);

                                                                    CutWaveStateY = 2;

                                                                    TheState2WindowSize[0] = DTFSP.Index - PastSP.Index;

                                                                    State2FirstRunY = false;

                                                                    State2AccelerationDataY.clear();
                                                                    for(int i = PastSP.Index;i<=DTFSP.Index;i++)
                                                                    {
                                                                        AccelerationData TempData = new AccelerationData();
                                                                        TempData = YLinearInterpolationAccelerationData.get(i);
                                                                        State2AccelerationDataY.add(TempData);
                                                                    }

                                                                    YStartPoint.clear();
                                                                    YLinearInterpolationAccelerationData.clear();
                                                                    YLinearInterpolationAccelerationData.add(YAD);
                                                                    SPIndex[1] = 1;
                                                                    DTFSP.Index = SPIndex[1]-1;
                                                                }
                                                                else if(TempCC < CorrelationCoefficientThreshold)
                                                                {
                                                                    CutWaveStateY = 2;
                                                                }
                                                            }else if((DTFSP.Index - PastSP.Index)>=(ToState2WindowSize[0] + AdditionalWindowSizes))
                                                            {
                                                                if(YTMCCI1 < YStartPoint.size())
                                                                {
                                                                    int MidIndexT = 0;
                                                                    float TempSCCT = 0;

                                                                    State21AccelerationDataY.clear();
                                                                    for(int i = PastSP.Index;i<=YStartPoint.get(YTMCCI1).Index;i++)
                                                                    {
                                                                        AccelerationData TempData = new AccelerationData();
                                                                        TempData = YLinearInterpolationAccelerationData.get(i);
                                                                        State21AccelerationDataY.add(TempData);
                                                                    }

                                                                    if((State21AccelerationDataY.size()-1) % 2 == 0)
                                                                    {
                                                                        MidIndexT = (State21AccelerationDataY.size()-1)/2;
                                                                    }else
                                                                    {
                                                                        MidIndexT = (State21AccelerationDataY.size()-2)/2;
                                                                    }

                                                                    TempSCCT = CC.CorrelationCoefficient(State21AccelerationDataY,0,MidIndexT);

                                                                    if(TempSCCT >= CorrelationCoefficientThreshold)
                                                                    {
                                                                        YTMCC1 = -1;
                                                                        YTMCCI1 = 0;
                                                                        Unstable_Wave_Start_Time[0] = YStartPoint.get(YTMCCI1).ST;
                                                                        Need_To_Calculate_The_Steps_For_Unstable_Wave[0] = true;

                                                                        CutWaveStateY = 0;

                                                                        WalkingStep[1] = WalkingStep[1]+4;
                                                                        CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                        NowSteps.Steps = 4;
                                                                        NowSteps.StartTime = State21AccelerationDataY.getFirst().TimeAccelerationData;
                                                                        NowSteps.EndTime = State21AccelerationDataY.getLast().TimeAccelerationData;
                                                                        CalculateTheStepsFromTwoAxis(1,NowSteps);

                                                                        int TempIndex = 0;
                                                                        TempIndex = YStartPoint.get(YTMCCI1).Index;
                                                                        for(int i = 0;i<TempIndex;i++)
                                                                        {
                                                                            YLinearInterpolationAccelerationData.removeFirst();
                                                                            SPIndex[1] = SPIndex[1] - 1;
                                                                        }
                                                                        for(int i = 0;i < YStartPoint.size();i++)
                                                                        {
                                                                            YStartPoint.get(i).Index = YStartPoint.get(i).Index - TempIndex ;
                                                                        }
                                                                        DTFSP.Index = DTFSP.Index - TempIndex;
                                                                        for(int i = 0;i < YTMCCI1;i++)
                                                                        {
                                                                            YStartPoint.removeFirst();
                                                                        }

                                                                        State2FirstRunY = true;
                                                                        TheMaxCCIndexY = 0;
                                                                        TheMaxCCY = -1;
                                                                        ToState2WindowSize[0] = 0;
                                                                        Check_Compensation_DataY.clear();
                                                                        for(int i = 0;i<YLinearInterpolationAccelerationData.size();i++)
                                                                        {
                                                                            Check_Compensation_DataY.add(YLinearInterpolationAccelerationData.get(i));
                                                                        }
                                                                        break;
                                                                    }
                                                                    else
                                                                    {
                                                                        YTMCC1 = -1;
                                                                        YTMCCI1 = 0;
                                                                        CutWaveStateY = 0;
                                                                        Unstable_Wave_Start_Time[0] = PastSP.ST;
                                                                        Need_To_Calculate_The_Steps_For_Unstable_Wave[0] = true;
                                                                        YStartPoint.clear();
                                                                        YLinearInterpolationAccelerationData.clear();
                                                                        YLinearInterpolationAccelerationData.add(YAD);
                                                                        SPIndex[1] = 1;
                                                                        DTFSP.Index = SPIndex[1]-1;
                                                                        State2FirstRunY = true;
                                                                        TheMaxCCIndexY = 0;
                                                                        TheMaxCCY = -1;
                                                                        ToState2WindowSize[0] = 0;

                                                                        Check_Compensation_DataY.clear();

                                                                        break;
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    int MidIndexT = 0;
                                                                    float TempSCCT = 0;
                                                                    State21AccelerationDataY.clear();
                                                                    for(int i = PastSP.Index;i<YLinearInterpolationAccelerationData.size();i++)
                                                                    {
                                                                        AccelerationData TempData = new AccelerationData();
                                                                        TempData = YLinearInterpolationAccelerationData.get(i);
                                                                        State21AccelerationDataY.add(TempData);
                                                                    }
                                                                    if((State21AccelerationDataY.size()-1) % 2 == 0)
                                                                    {
                                                                        MidIndexT = (State21AccelerationDataY.size()-1)/2;
                                                                    }else
                                                                    {
                                                                        MidIndexT = (State21AccelerationDataY.size()-2)/2;
                                                                    }
                                                                    TempSCCT = CC.CorrelationCoefficient(State21AccelerationDataY,0,MidIndexT);

                                                                    if(TempSCCT >= CorrelationCoefficientThreshold)
                                                                    {
                                                                        YTMCC1 = -1;
                                                                        YTMCCI1 = 0;
                                                                        Need_To_Calculate_The_Steps_For_Unstable_Wave[0] = true;
                                                                        Unstable_Wave_Start_Time[0] = DTFSP.ST;
                                                                        CutWaveStateY = 0;

                                                                        WalkingStep[1] = WalkingStep[1]+4;
                                                                        CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                        NowSteps.Steps = 4;
                                                                        NowSteps.StartTime = State21AccelerationDataY.getFirst().TimeAccelerationData;
                                                                        NowSteps.EndTime = State21AccelerationDataY.getLast().TimeAccelerationData;
                                                                        CalculateTheStepsFromTwoAxis(1,NowSteps);

                                                                        YStartPoint.clear();
                                                                        YLinearInterpolationAccelerationData.clear();
                                                                        YLinearInterpolationAccelerationData.add(YAD);
                                                                        SPIndex[1] = 1;
                                                                        DTFSP.Index = SPIndex[1]-1;
                                                                        State2FirstRunY = true;
                                                                        TheMaxCCIndexY = 0;
                                                                        TheMaxCCY = -1;
                                                                        ToState2WindowSize[0] = 0;
                                                                        Check_Compensation_DataY.clear();

                                                                        break;
                                                                    }
                                                                    else
                                                                    {
                                                                        YTMCC1 = -1;
                                                                        YTMCCI1 = 0;
                                                                        CutWaveStateY = 0;
                                                                        Unstable_Wave_Start_Time[0] = PastSP.ST;
                                                                        Need_To_Calculate_The_Steps_For_Unstable_Wave[0] = true;
                                                                        YStartPoint.clear();
                                                                        YLinearInterpolationAccelerationData.clear();
                                                                        YLinearInterpolationAccelerationData.add(YAD);
                                                                        SPIndex[1] = 1;
                                                                        DTFSP.Index = SPIndex[1]-1;
                                                                        State2FirstRunY = true;
                                                                        TheMaxCCIndexY = 0;
                                                                        TheMaxCCY = -1;
                                                                        ToState2WindowSize[0] = 0;
                                                                        Check_Compensation_DataY.clear();

                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }else
                                                        {
                                                            if(((DTFSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound1600ms)&&(DTFSP.UpOrDown == PastSP.UpOrDown)&&((DTFSP.Index - PastSP.Index)>=(TheState2WindowSize[0]-AdditionalWindowSizes))&&((DTFSP.Index - PastSP.Index)<=(TheState2WindowSize[0]+AdditionalWindowSizes)))
                                                            {
                                                                if((DTFSP.Index - PastSP.Index) % 2 == 0)
                                                                {
                                                                    MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index)/2;
                                                                }else
                                                                {
                                                                    MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index -1)/2;
                                                                }

                                                                TempCC = CC.CorrelationCoefficient(YLinearInterpolationAccelerationData,PastSP.Index,MidIndex);

                                                                if(TheMaxCCY <= TempCC)
                                                                {
                                                                    TheMaxCCY = TempCC;
                                                                    TheMaxCCIndexY = YStartPoint.size();
                                                                }
                                                                if((TempCC >= CorrelationCoefficientThreshold))
                                                                {
                                                                    State2TempAccelerationDataY.clear();

                                                                    for(int i = 0;i<State2AccelerationDataY.size();i++)
                                                                    {
                                                                        AccelerationData TempData = new AccelerationData();
                                                                        TempData = State2AccelerationDataY.get(i);
                                                                        State2TempAccelerationDataY.add(TempData);
                                                                    }
                                                                    for(int i = PastSP.Index+1;i<=DTFSP.Index;i++)
                                                                    {
                                                                        AccelerationData TempData = new AccelerationData();
                                                                        TempData = YLinearInterpolationAccelerationData.get(i);
                                                                        State2TempAccelerationDataY.add(TempData);
                                                                    }
                                                                    int MidI = 0;

                                                                    if((State2TempAccelerationDataY.size()-1) % 2 == 0)
                                                                    {
                                                                        MidI = (State2TempAccelerationDataY.size()-1)/2;
                                                                    }else
                                                                    {
                                                                        MidI = (State2TempAccelerationDataY.size()-2)/2;
                                                                    }

                                                                    float SecondCheckCC = 0;
                                                                    SecondCheckCC = CC.CorrelationCoefficient(State2TempAccelerationDataY,0,MidI);

                                                                    if( SecondCheckCC >= CorrelationCoefficientThreshold)
                                                                    {
                                                                        WalkingStep[1] = WalkingStep[1]+4;
                                                                        CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                        NowSteps.Steps = 4;
                                                                        NowSteps.StartTime = PastSP.ST;
                                                                        NowSteps.EndTime = DTFSP.ST;
                                                                        CalculateTheStepsFromTwoAxis(1,NowSteps);

                                                                        CutWaveStateY = 2;

                                                                        TheState2WindowSize[0] = DTFSP.Index - PastSP.Index;

                                                                        State2AccelerationDataY.clear();
                                                                        State2TempAccelerationDataY.clear();

                                                                        TheMaxCCY = -1;
                                                                        TheMaxCCIndexY = 0;

                                                                        for(int i = PastSP.Index;i<DTFSP.Index;i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = YLinearInterpolationAccelerationData.get(i);
                                                                            State2AccelerationDataY.add(TempData);
                                                                        }
                                                                        YStartPoint.clear();
                                                                        YLinearInterpolationAccelerationData.clear();
                                                                        YLinearInterpolationAccelerationData.add(YAD);
                                                                        SPIndex[1] = 1;
                                                                        DTFSP.Index = SPIndex[1]-1;
                                                                        break;
                                                                    }
                                                                    else
                                                                    {
                                                                        CutWaveStateY = 2;
                                                                    }
                                                                }
                                                                else if(TempCC < CorrelationCoefficientThreshold)
                                                                {
                                                                    CutWaveStateY = 2;
                                                                }
                                                            }else if(((DTFSP.Index - PastSP.Index)>(TheState2WindowSize[0]+AdditionalWindowSizes)))
                                                            {
                                                                SearchTheSimilarWaveFromPast[0] = true;
                                                            }

                                                            if(SearchTheSimilarWaveFromPast[0])
                                                            {
                                                                SearchTheSimilarWaveFromPast[0] = false;
                                                                if(TheMaxCCIndexY < YStartPoint.size())
                                                                {
                                                                    State2AccelerationDataY.clear();
                                                                    for(int i = PastSP.Index;i<=YStartPoint.get(TheMaxCCIndexY).Index;i++)
                                                                    {
                                                                        AccelerationData TempData = new AccelerationData();
                                                                        TempData = YLinearInterpolationAccelerationData.get(i);
                                                                        State2AccelerationDataY.add(TempData);
                                                                    }

                                                                    if((State2AccelerationDataY.size()-1) % 2 == 0)
                                                                    {
                                                                        MidIndex = 0 + (State2AccelerationDataY.size()-1)/2;
                                                                    }else
                                                                    {
                                                                        MidIndex = 0 + (State2AccelerationDataY.size()-2)/2;
                                                                    }

                                                                    float TempSCC = 0;
                                                                    TempSCC = CC.CorrelationCoefficient(State2AccelerationDataY,0,MidIndex);

                                                                    if(TempSCC >= CorrelationCoefficientThreshold)
                                                                    {
                                                                        Unstable_Wave_Start_Time[0] = YStartPoint.get(TheMaxCCIndexY).ST;
                                                                        Need_To_Calculate_The_Steps_For_Unstable_Wave[0] = true;
                                                                        WalkingStep[1] = WalkingStep[1]+4;
                                                                        CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                        NowSteps.Steps = 4;
                                                                        NowSteps.StartTime = State2AccelerationDataY.getFirst().TimeAccelerationData;
                                                                        NowSteps.EndTime = State2AccelerationDataY.getLast().TimeAccelerationData;
                                                                        CalculateTheStepsFromTwoAxis(1,NowSteps);

                                                                        int TempIndex = 0;
                                                                        TempIndex = YStartPoint.get(TheMaxCCIndexY).Index;
                                                                        for(int i = 0;i<TempIndex;i++)
                                                                        {
                                                                            YLinearInterpolationAccelerationData.removeFirst();
                                                                            SPIndex[1] = SPIndex[1] - 1;
                                                                        }
                                                                        for(int i = 0;i < YStartPoint.size();i++)
                                                                        {
                                                                            YStartPoint.get(i).Index = YStartPoint.get(i).Index - TempIndex ;
                                                                        }
                                                                        DTFSP.Index = DTFSP.Index - TempIndex;
                                                                        for(int i = 0;i < TheMaxCCIndexY;i++)
                                                                        {
                                                                            YStartPoint.removeFirst();
                                                                        }

                                                                        CutWaveStateY = 0;

                                                                        State2FirstRunY = true;

                                                                        TheMaxCCIndexY = 0;
                                                                        TheMaxCCY = -1;
                                                                        Check_Compensation_DataY.clear();
                                                                        for(int i = 0;i<YLinearInterpolationAccelerationData.size();i++)
                                                                        {
                                                                            Check_Compensation_DataY.add(YLinearInterpolationAccelerationData.get(i));
                                                                        }
                                                                        break;
                                                                    }
                                                                    else
                                                                    {
                                                                        Check_Compensation_DataY.clear();
                                                                        for(int i = 0;i<YLinearInterpolationAccelerationData.size();i++)
                                                                        {
                                                                            Check_Compensation_DataY.add(YLinearInterpolationAccelerationData.get(i));
                                                                        }
                                                                        CutWaveStateY = 0;
                                                                        Unstable_Wave_Start_Time[0] = PastSP.ST;
                                                                        Need_To_Calculate_The_Steps_For_Unstable_Wave[0] = true;
                                                                        YStartPoint.clear();
                                                                        YLinearInterpolationAccelerationData.clear();
                                                                        YLinearInterpolationAccelerationData.add(YAD);
                                                                        SPIndex[1] = 1;
                                                                        DTFSP.Index = SPIndex[1]-1;
                                                                        State2FirstRunY = true;
                                                                        TheMaxCCIndexY = 0;
                                                                        TheMaxCCY = -1;

                                                                        break;
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    State2AccelerationDataY.clear();
                                                                    for(int i = PastSP.Index;i<YLinearInterpolationAccelerationData.size();i++)
                                                                    {
                                                                        AccelerationData TempData = new AccelerationData();
                                                                        TempData = YLinearInterpolationAccelerationData.get(i);
                                                                        State2AccelerationDataY.add(TempData);
                                                                    }

                                                                    if((State2AccelerationDataY.size()-1) % 2 == 0)
                                                                    {
                                                                        MidIndex = 0 + (State2AccelerationDataY.size()-1)/2;
                                                                    }else
                                                                    {
                                                                        MidIndex = 0 + (State2AccelerationDataY.size()-2)/2;
                                                                    }
                                                                    float TempSCC = 0;
                                                                    TempSCC = CC.CorrelationCoefficient(State2AccelerationDataY,0,MidIndex);

                                                                    if(TempSCC >= CorrelationCoefficientThreshold)
                                                                    {
                                                                        Unstable_Wave_Start_Time[0] = DTFSP.ST;
                                                                        Need_To_Calculate_The_Steps_For_Unstable_Wave[0] = true;
                                                                        WalkingStep[1] = WalkingStep[1]+4;
                                                                        CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                        NowSteps.Steps = 4;
                                                                        NowSteps.StartTime = State2AccelerationDataY.getFirst().TimeAccelerationData;
                                                                        NowSteps.EndTime = State2AccelerationDataY.getLast().TimeAccelerationData;
                                                                        CalculateTheStepsFromTwoAxis(1,NowSteps);

                                                                        CutWaveStateY = 0;

                                                                        Check_Compensation_DataY.clear();
                                                                        for(int i = 0;i<YLinearInterpolationAccelerationData.size();i++)
                                                                        {
                                                                            if(YLinearInterpolationAccelerationData.get(i).TimeAccelerationData>=State2AccelerationDataY.getLast().TimeAccelerationData)
                                                                            {
                                                                                Check_Compensation_DataY.add(YLinearInterpolationAccelerationData.get(i));
                                                                            }
                                                                        }
                                                                        YStartPoint.clear();
                                                                        YLinearInterpolationAccelerationData.clear();
                                                                        YLinearInterpolationAccelerationData.add(YAD);
                                                                        SPIndex[1] = 1;
                                                                        DTFSP.Index = SPIndex[1]-1;
                                                                        State2FirstRunY = true;

                                                                        TheMaxCCIndexY = 0;
                                                                        TheMaxCCY = -1;


                                                                        break;
                                                                    }
                                                                    else
                                                                    {
                                                                        Check_Compensation_DataY.clear();
                                                                        for(int i = 0;i<YLinearInterpolationAccelerationData.size();i++)
                                                                        {
                                                                            //if(ZLinearInterpolationAccelerationData.get(i).TimeAccelerationData>=State2AccelerationData.getLast().TimeAccelerationData)
                                                                            {
                                                                                Check_Compensation_DataY.add(YLinearInterpolationAccelerationData.get(i));
                                                                            }
                                                                        }
                                                                        CutWaveStateY = 0;
                                                                        Unstable_Wave_Start_Time[0] = PastSP.ST;
                                                                        Need_To_Calculate_The_Steps_For_Unstable_Wave[0] = true;
                                                                        YStartPoint.clear();
                                                                        YLinearInterpolationAccelerationData.clear();
                                                                        YLinearInterpolationAccelerationData.add(YAD);
                                                                        SPIndex[1] = 1;
                                                                        DTFSP.Index = SPIndex[1]-1;
                                                                        State2FirstRunY = true;
                                                                        TheMaxCCIndexY = 0;
                                                                        TheMaxCCY = -1;
                                                                        Check_Compensation_DataY.clear();

                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    break;
                                                }//end switch
                                                PutStartPointDataToTheLinkedList(YStartPoint,DTFSP);
                                            }
                                            else
                                            {
                                                PutStartPointDataToTheLinkedList(YStartPoint,DTFSP);
                                            }
                                        }

                                    }catch( Exception ex)
                                    {
                                        //TVAccX.setText("error happen!:"+String.valueOf(CutWaveStateY));
                                    }
                                }

























                                if(STEPCOUNT_Z)
                                    try
                                    {

                                        if((CC.FindTheStartingWalkingPoint(Acc_last_LIAccData[2], ZAD.AccelerationData, tempUpOrDown))&&(FirstDoLI[2]))
                                        {
                                            StartWalking[2] = true;
                                            if(!FirstPutDataToLI[2])
                                            {
                                                ZLinearInterpolationAccelerationData.add(ZAD);
                                                SPIndex[2]++;
                                                FirstPutDataToLI[2] = true;
                                            }

                                            DataTypeForStartPoint DTFSP = new DataTypeForStartPoint();
                                            DTFSP.ST = ZAD.TimeAccelerationData;
                                            DTFSP.MCCR = 0;//Max Correlation Cofficient
                                            DTFSP.Finish = false;//The starting point finish
                                            DTFSP.Dead = false;//The starting point dead
                                            DTFSP.Index = SPIndex[2]-1;
                                            DTFSP.UpOrDown = tempUpOrDown[0];

                                            //ok
                                            if(true)
                                            {


                                                if(!ZStartPoint.isEmpty())
                                                {
                                                    switch(CutWaveState)
                                                    {
                                                        case 0:
                                                        {
                                                            DataTypeForStartPoint PastSP = new DataTypeForStartPoint();


                                                            PastSP = ZStartPoint.getFirst();


                                                            while(DTFSP.ST - PastSP.ST > 3)
                                                            {
                                                                if(ZStartPoint.size()<=1)
                                                                {
                                                                    ZLinearInterpolationAccelerationData.clear();
                                                                    ZLinearInterpolationAccelerationData.add(ZAD);
                                                                    SPIndex[2] = 1;
                                                                    ZStartPoint.clear();
                                                                    DTFSP.Index = 0;
                                                                    No_RunZ = true;
                                                                    PastSP = ZStartPoint.getFirst();
                                                                    break;
                                                                }else
                                                                {
                                                                    int temp = 0;
                                                                    temp = ZStartPoint.get(1).Index;
                                                                    for(int i = 0; i < temp; i++)
                                                                    {
                                                                        ZLinearInterpolationAccelerationData.removeFirst();
                                                                        SPIndex[2] = SPIndex[2] - 1;
                                                                    }

                                                                    for(int i = 0;i < ZStartPoint.size();i++)
                                                                    {
                                                                        ZStartPoint.get(i).Index = ZStartPoint.get(i).Index - temp;
                                                                    }

                                                                    DTFSP.Index = DTFSP.Index - temp;

                                                                    ZStartPoint.removeFirst();
                                                                    PastSP = ZStartPoint.getFirst();
                                                                }
                                                            }

                                                            if(!No_RunZ)
                                                            {
                                                                for(int j = 0;j<ZStartPoint.size();j++)
                                                                {
                                                                    float TempCC = 0;
                                                                    int MidIndex = 0;

                                                                    PastSP = ZStartPoint.get(j);

                                                                    if( ((DTFSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound800ms) && (DTFSP.UpOrDown == PastSP.UpOrDown) )
                                                                    {

                                                                        if((DTFSP.Index - PastSP.Index) % 2 == 0)
                                                                        {
                                                                            MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index)/2;
                                                                        }else
                                                                        {
                                                                            MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index -1)/2;
                                                                        }
                                                                        TempCC = CC.CorrelationCoefficient(ZLinearInterpolationAccelerationData,PastSP.Index,MidIndex);

                                                                        if(TempCC >= CorrelationCoefficientThreshold)
                                                                        {
                                                                            CutWaveState = 1;

                                                                            ZFirstFindSimilarWave.clear();
                                                                            for(int i = PastSP.Index;i<=DTFSP.Index;i++)
                                                                            {
                                                                                AccelerationData TempData = new AccelerationData();
                                                                                TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                                ZFirstFindSimilarWave.add(TempData);
                                                                            }
                                                                            AccDH.TheFirstSPIndex = j;
                                                                            AccDH.TheSecondSPIndex = ZStartPoint.size();
                                                                            AccDH.WindowSize[2] = DTFSP.Index - PastSP.Index;

                                                                            break;
                                                                        }
                                                                        else if(TempCC < CorrelationCoefficientThreshold)
                                                                        {
                                                                            CutWaveState = 0;
                                                                        }
                                                                    }
                                                                    else
                                                                    {
                                                                        CutWaveState = 0;
                                                                    }
                                                                }
                                                            }
                                                            No_RunZ = false;
                                                        }
                                                        break;

                                                        case 1:
                                                        {
                                                            DataTypeForStartPoint PastSP = new DataTypeForStartPoint();
                                                            float TempCC = 0;
                                                            int MidIndex = 0;

                                                            PastSP = ZStartPoint.get(AccDH.TheSecondSPIndex);
                                                            if(((DTFSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound800ms)&&(DTFSP.UpOrDown == PastSP.UpOrDown)&&((DTFSP.Index - PastSP.Index)>=(AccDH.WindowSize[2]-AdditionalWindowSizes))&&((DTFSP.Index - PastSP.Index)<=(AccDH.WindowSize[2]+AdditionalWindowSizes)))
                                                            {
                                                                if((DTFSP.Index - PastSP.Index) % 2 == 0)
                                                                {
                                                                    MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index)/2;
                                                                }else
                                                                {
                                                                    MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index -1)/2;
                                                                }
                                                                TempCC = CC.CorrelationCoefficient(ZLinearInterpolationAccelerationData,PastSP.Index,MidIndex);
                                                                if(TempCC >= CorrelationCoefficientThreshold)
                                                                {
                                                                    ZTempForSecondAccelerationData.clear();

                                                                    for(int i = 0;i<ZFirstFindSimilarWave.size();i++)
                                                                    {
                                                                        AccelerationData TempData = new AccelerationData();
                                                                        TempData = ZFirstFindSimilarWave.get(i);
                                                                        ZTempForSecondAccelerationData.add(TempData);
                                                                    }
                                                                    for(int i = PastSP.Index + 1;i<=DTFSP.Index;i++)
                                                                    {
                                                                        AccelerationData TempData = new AccelerationData();
                                                                        TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                        ZTempForSecondAccelerationData.add(TempData);
                                                                    }
                                                                    if((ZTempForSecondAccelerationData.size()-1) % 2 == 0)
                                                                    {
                                                                        MidIndex = 0 + (ZTempForSecondAccelerationData.size()-1)/2;
                                                                    }else
                                                                    {
                                                                        MidIndex = 0 + (ZTempForSecondAccelerationData.size()-2)/2;
                                                                    }
                                                                    float TempCCWithFirstCut = 0;
                                                                    TempCCWithFirstCut = CC.CorrelationCoefficient(ZTempForSecondAccelerationData,0,MidIndex);

                                                                    if(TempCCWithFirstCut >= CorrelationCoefficientThreshold3)
                                                                    {
                                                                        int TempWindowSize = DTFSP.Index - PastSP.Index;
                                                                        AccDH.WindowSize[2] = (AccDH.WindowSize[2] + TempWindowSize)/2;
                                                                        if(Need_To_Calculate_The_Steps_For_Unstable_Wave[1] == true)
                                                                        {
                                                                            Unstable_Wave_End_Time[1] = ZTempForSecondAccelerationData.getFirst().TimeAccelerationData;
                                                                            Window_Size_For_Unstable_Wave[1] = AccDH.WindowSize[2]+1;
                                                                            CalculateTheUnstableSteps(1);

                                                                            if(Check_Compensation(Check_Compensation_DataZ,Unstable_Wave_End_Time[1]) == -1)
                                                                            {
                                                                                Unstable_Wave_Steps[1] = 0;
                                                                                Check_Compensation_DataZ.clear();
                                                                            }
                                                                        }
                                                                        ZToState2SimilarWave.clear();
                                                                        for(int i = PastSP.Index;i<=DTFSP.Index;i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                            ZToState2SimilarWave.add(TempData);
                                                                        }
                                                                        ToState2WindowSize[1] = DTFSP.Index - PastSP.Index;
                                                                        ZStartPoint.clear();
                                                                        ZLinearInterpolationAccelerationData.clear();
                                                                        ZLinearInterpolationAccelerationData.add(ZAD);
                                                                        SPIndex[2] = 1;
                                                                        DTFSP.Index = SPIndex[2]-1;
                                                                        CutWaveState = 2;

                                                                        if(Need_To_Calculate_The_Steps_For_Unstable_Wave[1] == true)
                                                                        {
                                                                            if(Unstable_Wave_Steps[1] != 0)
                                                                            {
                                                                                CalculateTheStepsFromTwoAxisDataType PastSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                                PastSteps.Steps = Unstable_Wave_Steps[1];
                                                                                PastSteps.StartTime = Unstable_Wave_Start_Time[1];
                                                                                PastSteps.EndTime = Unstable_Wave_End_Time[1];
                                                                                CalculateTheStepsFromTwoAxis(2,PastSteps);
                                                                            }
                                                                            Unstable_Wave_Steps[1] = 0;
                                                                            Unstable_Wave_Start_Time[1] = 0;
                                                                            Unstable_Wave_End_Time[1] = 0;
                                                                            Need_To_Calculate_The_Steps_For_Unstable_Wave[1] = false;
                                                                        }

                                                                        WalkingStep[2] = WalkingStep[2] + 4;

                                                                        CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                        NowSteps.Steps = 4;
                                                                        NowSteps.StartTime = ZTempForSecondAccelerationData.getFirst().TimeAccelerationData;
                                                                        //Unstable_Wave_Steps[1] = 0;
                                                                        //NowSteps.StartTime = ZTempForSecondAccelerationData.getFirst().TimeAccelerationData;
                                                                        NowSteps.EndTime = ZTempForSecondAccelerationData.getLast().TimeAccelerationData;
                                                                        CalculateTheStepsFromTwoAxis(2,NowSteps);
                                                                        break;
                                                                    }
                                                                    else if(TempCCWithFirstCut < CorrelationCoefficientThreshold3)
                                                                    {
                                                                        //log.d("FuckingError", "FuckingError: 14");
                                                                        CutWaveState = 1;
                                                                        break;
                                                                    }

                                                                }
                                                                else if(TempCC < CorrelationCoefficientThreshold)
                                                                {
                                                                    //log.d("FuckingError", "FuckingError: 15");
                                                                    CutWaveState = 1;
                                                                    break;
                                                                }
                                                            }
                                                            else if(((DTFSP.Index - PastSP.Index)>=(AccDH.WindowSize[2]+AdditionalWindowSizes)))
                                                            {

                                                                int temp = 0;
                                                                temp = ZStartPoint.get(AccDH.TheFirstSPIndex+1).Index;
                                                                for(int i = 0;i<temp;i++)
                                                                {
                                                                    ZLinearInterpolationAccelerationData.removeFirst();
                                                                    SPIndex[2] = SPIndex[2] - 1;
                                                                }
                                                                for(int i = 0;i < ZStartPoint.size();i++)
                                                                {
                                                                    ZStartPoint.get(i).Index = ZStartPoint.get(i).Index - temp ;
                                                                }
                                                                DTFSP.Index = DTFSP.Index - temp;

                                                                for(int i = 0;i <= AccDH.TheFirstSPIndex;i++)
                                                                {
                                                                    ZStartPoint.removeFirst();
                                                                }

                                                                CutWaveState = 7;
                                                            }
                                                        }
                                                        if(CutWaveState != 7)
                                                        {
                                                            break;
                                                        }



                                                        case 7:
                                                        {

                                                            DebugState7 = 0;
                                                            for(int i = 0;i < ZStartPoint.size();i++)
                                                            {
                                                                DebugState7 = 1;
                                                                for(int j = (i+1);j < ZStartPoint.size();j++)
                                                                {
                                                                    DebugState7 = 2;
                                                                    DataTypeForStartPoint PastSP = new DataTypeForStartPoint();
                                                                    DataTypeForStartPoint NowSP = new DataTypeForStartPoint();

                                                                    float TempCC = 0;
                                                                    int MidIndex = 0;

                                                                    PastSP = ZStartPoint.get(i);
                                                                    NowSP = ZStartPoint.get(j);
                                                                    DebugState7 = 3;
                                                                    if(((NowSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound800ms)&&(NowSP.UpOrDown == PastSP.UpOrDown))
                                                                    {
                                                                        DebugState7 = 4;
                                                                        if((NowSP.Index - PastSP.Index) % 2 == 0)
                                                                        {
                                                                            MidIndex = PastSP.Index + (NowSP.Index - PastSP.Index)/2;
                                                                        }else
                                                                        {
                                                                            MidIndex = PastSP.Index + (NowSP.Index - PastSP.Index -1)/2;
                                                                        }
                                                                        DebugState7 = 5;

                                                                        iii = PastSP.Index;
                                                                        jjj = NowSP.Index;
                                                                        TempCC = CC.CorrelationCoefficient(ZLinearInterpolationAccelerationData,PastSP.Index,MidIndex);
                                                                        DebugState7 = 6;

                                                                        DebugState7 = 7;
                                                                        if(TempCC >= CorrelationCoefficientThreshold)
                                                                        {
                                                                            DebugState7 = 8;
                                                                            C7FindTheWave = true;

                                                                            ZFirstFindSimilarWave.clear();
                                                                            for(int k = PastSP.Index;k<=NowSP.Index;k++)
                                                                            {
                                                                                AccelerationData TempData = new AccelerationData();
                                                                                TempData = ZLinearInterpolationAccelerationData.get(k);
                                                                                ZFirstFindSimilarWave.add(TempData);
                                                                            }
                                                                            DebugState7 = 9;
                                                                            //log.d("wtf", "wtf: " + String.valueOf(DebugState7));
                                                                            AccDH.TheFirstSPIndex = i;
                                                                            AccDH.TheSecondSPIndex = j;
                                                                            AccDH.WindowSize[2] = NowSP.Index - PastSP.Index;
                                                                            break;
                                                                        }
                                                                        else if(TempCC < CorrelationCoefficientThreshold)
                                                                        {
                                                                            DebugState7 = 10;
                                                                            CutWaveState = 7;
                                                                        }
                                                                        DebugState7 = 11;
                                                                    }
                                                                    DebugState7 = 12;
                                                                }
                                                                if(C7FindTheWave)
                                                                {
                                                                    DebugState7 = 13;
                                                                    break;
                                                                }
                                                            }
                                                            DebugState7 = 14;
                                                            if(C7FindTheWave)
                                                            {
                                                                DebugState7 = 15;
                                                                C7FindTheWave = false;
                                                                DebugState7 = 16;
                                                                for(int j = AccDH.TheSecondSPIndex + 1 ;j<ZStartPoint.size();j++)
                                                                {
                                                                    DataTypeForStartPoint PastSP = new DataTypeForStartPoint();
                                                                    DataTypeForStartPoint NowSP = new DataTypeForStartPoint();
                                                                    DebugState7 = 17;
                                                                    float TempCC = 0;
                                                                    int MidIndex = 0;

                                                                    PastSP = ZStartPoint.get(AccDH.TheSecondSPIndex);
                                                                    NowSP = ZStartPoint.get(j);
                                                                    DebugState7 = 18;
                                                                    if(((NowSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound800ms)&&(NowSP.UpOrDown == PastSP.UpOrDown)&&((NowSP.Index - PastSP.Index)>=(AccDH.WindowSize[2]-AdditionalWindowSizes))&&((NowSP.Index - PastSP.Index)<=(AccDH.WindowSize[2]+AdditionalWindowSizes)))
                                                                    {
                                                                        DebugState7 = 19;
                                                                        if((NowSP.Index - PastSP.Index) % 2 == 0)
                                                                        {
                                                                            MidIndex = PastSP.Index + (NowSP.Index - PastSP.Index)/2;
                                                                        }else
                                                                        {
                                                                            MidIndex = PastSP.Index + (NowSP.Index - PastSP.Index -1)/2;
                                                                        }
                                                                        DebugState7 = 20;
                                                                        TempCC = CC.CorrelationCoefficient(ZLinearInterpolationAccelerationData,PastSP.Index,MidIndex);
                                                                        DebugState7 = 21;
                                                                        if(TempCC >= CorrelationCoefficientThreshold)
                                                                        {
                                                                            DebugState7 = 22;
                                                                            ZTempForSecondAccelerationData.clear();
                                                                            for(int i = 0;i<ZFirstFindSimilarWave.size();i++)
                                                                            {
                                                                                AccelerationData TempData = new AccelerationData();
                                                                                TempData = ZFirstFindSimilarWave.get(i);
                                                                                ZTempForSecondAccelerationData.add(TempData);
                                                                            }
                                                                            DebugState7 = 23;
                                                                            for(int i = PastSP.Index + 1;i<=NowSP.Index;i++)
                                                                            {
                                                                                AccelerationData TempData = new AccelerationData();
                                                                                TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                                ZTempForSecondAccelerationData.add(TempData);
                                                                            }
                                                                            DebugState7 = 24;
                                                                            if((ZTempForSecondAccelerationData.size()-1) % 2 == 0)
                                                                            {
                                                                                MidIndex = 0 + (ZTempForSecondAccelerationData.size()-1)/2;
                                                                            }else
                                                                            {
                                                                                MidIndex = 0 + (ZTempForSecondAccelerationData.size()-2)/2;
                                                                            }
                                                                            DebugState7 = 25;
                                                                            float TempCCWithFirstCut = 0;
                                                                            TempCCWithFirstCut = CC.CorrelationCoefficient(ZTempForSecondAccelerationData,0,MidIndex);
                                                                            DebugState7 = 26;
                                                                            if(TempCCWithFirstCut >= CorrelationCoefficientThreshold3)
                                                                            {
                                                                                DebugState7 = 27;
                                                                                int TempWindowSize = NowSP.Index - PastSP.Index;
                                                                                AccDH.WindowSize[2] = (AccDH.WindowSize[2] + TempWindowSize)/2;

                                                                                if(Need_To_Calculate_The_Steps_For_Unstable_Wave[1] == true)
                                                                                {
                                                                                    Unstable_Wave_End_Time[1] = ZTempForSecondAccelerationData.getFirst().TimeAccelerationData;
                                                                                    Window_Size_For_Unstable_Wave[1] = AccDH.WindowSize[2]+1;
                                                                                    CalculateTheUnstableSteps(1);

                                                                                    if(Check_Compensation(Check_Compensation_DataZ,Unstable_Wave_End_Time[1]) == -1)
                                                                                    {
                                                                                        Unstable_Wave_Steps[1] = 0;
                                                                                        Check_Compensation_DataZ.clear();
                                                                                    }
                                                                                }

                                                                                ZToState2SimilarWave.clear();
                                                                                for(int i = PastSP.Index;i<=NowSP.Index;i++)
                                                                                {
                                                                                    AccelerationData TempData = new AccelerationData();
                                                                                    TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                                    ZToState2SimilarWave.add(TempData);
                                                                                }
                                                                                ToState2WindowSize[1] = NowSP.Index - PastSP.Index;
                                                                                DebugState7 = 28;
                                                                                int TempIndex = 0;

                                                                                TempIndex = NowSP.Index;
                                                                                DebugState7 = 29;
                                                                                for(int i = 0;i<TempIndex;i++)
                                                                                {
                                                                                    ZLinearInterpolationAccelerationData.removeFirst();
                                                                                    SPIndex[2] = SPIndex[2] - 1;
                                                                                }
                                                                                DebugState7 = 30;
                                                                                for(int i = 0;i < ZStartPoint.size();i++)
                                                                                {
                                                                                    ZStartPoint.get(i).Index = ZStartPoint.get(i).Index - TempIndex;
                                                                                }
                                                                                DebugState7 = 31;
                                                                                DTFSP.Index = DTFSP.Index - TempIndex;

                                                                                for(int i = 0;i < j;i++)
                                                                                {
                                                                                    ZStartPoint.removeFirst();
                                                                                }
                                                                                DebugState7 = 32;
                                                                                AccDH.TheFirstSPIndex = 0;
                                                                                AccDH.TheSecondSPIndex = 0;
                                                                                DebugState7 = 28;

                                                                                CutWaveState = 2;
                                                                                if(Need_To_Calculate_The_Steps_For_Unstable_Wave[1] == true)
                                                                                {
                                                                                    if(Unstable_Wave_Steps[1] != 0)
                                                                                    {
                                                                                        CalculateTheStepsFromTwoAxisDataType PastSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                                        PastSteps.Steps = Unstable_Wave_Steps[1];
                                                                                        PastSteps.StartTime = Unstable_Wave_Start_Time[1];
                                                                                        PastSteps.EndTime = Unstable_Wave_End_Time[1];
                                                                                        CalculateTheStepsFromTwoAxis(2,PastSteps);
                                                                                    }
                                                                                    Unstable_Wave_Steps[1] = 0;
                                                                                    Unstable_Wave_Start_Time[1] = 0;
                                                                                    Unstable_Wave_End_Time[1] = 0;
                                                                                    Need_To_Calculate_The_Steps_For_Unstable_Wave[1] = false;
                                                                                }

                                                                                WalkingStep[2] = WalkingStep[2] + 4;
                                                                                CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                                NowSteps.Steps = 4;
                                                                                NowSteps.StartTime = ZTempForSecondAccelerationData.getFirst().TimeAccelerationData;
                                                                                NowSteps.EndTime = ZTempForSecondAccelerationData.getLast().TimeAccelerationData;
                                                                                CalculateTheStepsFromTwoAxis(2,NowSteps);

                                                                                DebugState7 = 33;
                                                                                break;
                                                                            }
                                                                            else
                                                                            {
                                                                                DebugState7 = 29;
                                                                            }
                                                                            DebugState7 = 30;
                                                                        }
                                                                        else if(TempCC < CorrelationCoefficientThreshold)
                                                                        {
                                                                            DebugState7 = 31;
                                                                        }
                                                                        DebugState7 = 32;
                                                                    }
                                                                    else if(((NowSP.Index - PastSP.Index)>=(AccDH.WindowSize[2]+AdditionalWindowSizes)))
                                                                    {
                                                                        DebugState7 = 33;
                                                                        int TempIndex = 0;
                                                                        TempIndex = ZStartPoint.get(AccDH.TheFirstSPIndex+1).Index;
                                                                        for(int i = 0;i<TempIndex;i++)
                                                                        {
                                                                            ZLinearInterpolationAccelerationData.removeFirst();
                                                                            SPIndex[2] = SPIndex[2] - 1;
                                                                        }
                                                                        for(int i = 0;i < ZStartPoint.size();i++)
                                                                        {
                                                                            ZStartPoint.get(i).Index = ZStartPoint.get(i).Index - TempIndex;
                                                                        }

                                                                        DTFSP.Index = DTFSP.Index - TempIndex;


                                                                        for(int i = 0;i <= AccDH.TheFirstSPIndex;i++)
                                                                        {
                                                                            ZStartPoint.removeFirst();
                                                                        }

                                                                        DebugState7 = 34;
                                                                        CutWaveState = 7;
                                                                        RecordTheS7SecondWave = false;
                                                                        break;
                                                                    }
                                                                    DebugState7 = 35;
                                                                    if((NowSP.Index - PastSP.Index)<=(AccDH.WindowSize[2]+AdditionalWindowSizes))
                                                                    {
                                                                        DebugState7 = 36;
                                                                        RecordTheS7SecondWave = true;
                                                                    }
                                                                }

                                                                DebugState7 = 37;
                                                                if((RecordTheS7SecondWave == true)&&(CutWaveState != 2))
                                                                {
                                                                    DebugState7 = 38;
                                                                    RecordTheS7SecondWave = false;
                                                                    CutWaveState = 1;
                                                                    C7FindTheWave = false;
                                                                }
                                                                DebugState7 = 39;
                                                            }
                                                            else
                                                            {
                                                                DebugState7 = 40;
                                                                CutWaveState = 0;
                                                                C7FindTheWave = false;
                                                            }
                                                        }
                                                        break;

                                                        case 2:
                                                        {

                                                            DataTypeForStartPoint PastSP = new DataTypeForStartPoint();
                                                            float TempCC = 0;
                                                            int MidIndex = 0;
                                                            PastSP = ZStartPoint.getFirst();

                                                            if(State2FirstRun)
                                                            {
                                                                State2SomeBug = 1;

                                                                if(((DTFSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound800ms)&&(DTFSP.UpOrDown == PastSP.UpOrDown)&&((DTFSP.Index - PastSP.Index)<=(ToState2WindowSize[1] + AdditionalWindowSizes))&&((DTFSP.Index - PastSP.Index)>=(ToState2WindowSize[1] - AdditionalWindowSizes)))
                                                                {
                                                                    if((DTFSP.Index - PastSP.Index) % 2 == 0)
                                                                    {
                                                                        MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index)/2;
                                                                    }else
                                                                    {
                                                                        MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index -1)/2;
                                                                    }

                                                                    TempCC = CC.CorrelationCoefficient(ZLinearInterpolationAccelerationData,PastSP.Index,MidIndex);
                                                                    if(ZTMCC1 <= TempCC)
                                                                    {
                                                                        ZTMCC1 = TempCC;
                                                                        ZTMCCI1 = ZStartPoint.size();
                                                                    }
                                                                    DebugState7 = 26;
                                                                    if((TempCC >= CorrelationCoefficientThreshold))

                                                                    {
                                                                        ZTMCC1 = -1;
                                                                        ZTMCCI1 = 0;
                                                                        WalkingStep[2] = WalkingStep[2]+2;

                                                                        CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                        NowSteps.Steps = 2;
                                                                        NowSteps.StartTime = PastSP.ST;
                                                                        NowSteps.EndTime = DTFSP.ST;
                                                                        CalculateTheStepsFromTwoAxis(2,NowSteps);

                                                                        CutWaveState = 2;

                                                                        //Window size
                                                                        TheState2WindowSize[1] = DTFSP.Index - PastSP.Index;

                                                                        State2FirstRun = false;

                                                                        State2AccelerationData.clear();
                                                                        for(int i = PastSP.Index;i<=DTFSP.Index;i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                            State2AccelerationData.add(TempData);
                                                                        }

                                                                        ZStartPoint.clear();
                                                                        ZLinearInterpolationAccelerationData.clear();
                                                                        ZLinearInterpolationAccelerationData.add(ZAD);
                                                                        SPIndex[2] = 1;
                                                                        DTFSP.Index = SPIndex[2]-1;
                                                                    }
                                                                    else if(TempCC < CorrelationCoefficientThreshold)
                                                                    {
                                                                        CutWaveState = 2;
                                                                    }
                                                                }else if((DTFSP.Index - PastSP.Index)>=(ToState2WindowSize[1] + AdditionalWindowSizes))
                                                                {
                                                                    int MidIndexT = 0;
                                                                    float TempSCCT = 0;
                                                                    State21AccelerationDataZ.clear();
                                                                    if(ZTMCCI1 < ZStartPoint.size())
                                                                    {
                                                                        for(int i = PastSP.Index;i<=ZStartPoint.get(ZTMCCI1).Index;i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                            State21AccelerationDataZ.add(TempData);
                                                                        }

                                                                        if((State21AccelerationDataZ.size()-1) % 2 == 0)
                                                                        {
                                                                            MidIndexT = 0 + (State21AccelerationDataZ.size()-1)/2;
                                                                        }else
                                                                        {
                                                                            MidIndexT = 0 + (State21AccelerationDataZ.size()-2)/2;
                                                                        }
                                                                        TempSCCT = CC.CorrelationCoefficient(State21AccelerationDataZ,0,MidIndexT);

                                                                        if(TempSCCT >= CorrelationCoefficientThreshold)
                                                                        {
                                                                            Unstable_Wave_Start_Time[1] = ZStartPoint.get(ZTMCCI1).ST;
                                                                            Need_To_Calculate_The_Steps_For_Unstable_Wave[1] = true;

                                                                            ZTMCC1 = -1;
                                                                            ZTMCCI1 = 0;


                                                                            int TempIndex = 0;
                                                                            TempIndex = ZStartPoint.get(ZTMCCI1).Index;
                                                                            for(int i = 0;i<TempIndex;i++)
                                                                            {
                                                                                ZLinearInterpolationAccelerationData.removeFirst();
                                                                                SPIndex[2] = SPIndex[2] - 1;
                                                                            }
                                                                            for(int i = 0;i < ZStartPoint.size();i++)
                                                                            {
                                                                                ZStartPoint.get(i).Index = ZStartPoint.get(i).Index - TempIndex ;
                                                                            }
                                                                            DTFSP.Index = DTFSP.Index - TempIndex;
                                                                            for(int i = 0;i < ZTMCCI1;i++)
                                                                            {
                                                                                ZStartPoint.removeFirst();
                                                                            }

                                                                            CutWaveState = 0;

                                                                            WalkingStep[2] = WalkingStep[2]+2;

                                                                            CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                            NowSteps.Steps = 2;
                                                                            NowSteps.StartTime = State21AccelerationDataZ.getFirst().TimeAccelerationData;
                                                                            NowSteps.EndTime = State21AccelerationDataZ.getLast().TimeAccelerationData;
                                                                            CalculateTheStepsFromTwoAxis(2,NowSteps);

                                                                            State2FirstRun = true;
                                                                            TheMaxCCIndex = 0;
                                                                            TheMaxCC = -1;
                                                                            ToState2WindowSize[1] = 0;
                                                                            Check_Compensation_DataZ.clear();
                                                                            for(int i = 0;i<ZLinearInterpolationAccelerationData.size();i++)
                                                                            {
                                                                                Check_Compensation_DataZ.add(ZLinearInterpolationAccelerationData.get(i));
                                                                            }
                                                                            break;
                                                                        }
                                                                        else
                                                                        {
                                                                            ZTMCC1 = -1;
                                                                            ZTMCCI1 = 0;
                                                                            Unstable_Wave_Start_Time[1] = PastSP.ST;
                                                                            Need_To_Calculate_The_Steps_For_Unstable_Wave[1] = true;
                                                                            CutWaveState = 0;

                                                                            ZStartPoint.clear();
                                                                            ZLinearInterpolationAccelerationData.clear();
                                                                            ZLinearInterpolationAccelerationData.add(ZAD);
                                                                            SPIndex[2] = 1;
                                                                            DTFSP.Index = SPIndex[2]-1;
                                                                            State2FirstRun = true;
                                                                            TheMaxCCIndex = 0;
                                                                            TheMaxCC = -1;
                                                                            ToState2WindowSize[1] = 0;
                                                                            Check_Compensation_DataZ.clear();
                                                                            break;
                                                                        }
                                                                    }else
                                                                    {
                                                                        for(int i = PastSP.Index;i<ZLinearInterpolationAccelerationData.size();i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                            State21AccelerationDataZ.add(TempData);
                                                                        }

                                                                        if((State21AccelerationDataZ.size()-1) % 2 == 0)
                                                                        {
                                                                            MidIndexT = 0 + (State21AccelerationDataZ.size()-1)/2;
                                                                        }else
                                                                        {
                                                                            MidIndexT = 0 + (State21AccelerationDataZ.size()-2)/2;
                                                                        }
                                                                        TempSCCT = CC.CorrelationCoefficient(State21AccelerationDataZ,0,MidIndexT);

                                                                        if(TempSCCT >= CorrelationCoefficientThreshold)
                                                                        {

                                                                            ZTMCC1 = -1;
                                                                            ZTMCCI1 = 0;

                                                                            Need_To_Calculate_The_Steps_For_Unstable_Wave[1] = true;
                                                                            Unstable_Wave_Start_Time[1] = DTFSP.ST;

                                                                            CutWaveState = 0;

                                                                            WalkingStep[2] = WalkingStep[2]+2;
                                                                            CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                            NowSteps.Steps = 2;
                                                                            NowSteps.StartTime = State21AccelerationDataZ.getFirst().TimeAccelerationData;
                                                                            NowSteps.EndTime = State21AccelerationDataZ.getLast().TimeAccelerationData;
                                                                            CalculateTheStepsFromTwoAxis(2,NowSteps);

                                                                            ZStartPoint.clear();
                                                                            ZLinearInterpolationAccelerationData.clear();
                                                                            ZLinearInterpolationAccelerationData.add(ZAD);
                                                                            SPIndex[2] = 1;
                                                                            DTFSP.Index = SPIndex[2]-1;
                                                                            State2FirstRun = true;
                                                                            TheMaxCCIndex = 0;
                                                                            TheMaxCC = -1;
                                                                            ToState2WindowSize[1] = 0;
                                                                            Check_Compensation_DataZ.clear();
                                                                            break;
                                                                        }
                                                                        else
                                                                        {
                                                                            ZTMCC1 = -1;
                                                                            ZTMCCI1 = 0;
                                                                            Unstable_Wave_Start_Time[1] = PastSP.ST;
                                                                            Need_To_Calculate_The_Steps_For_Unstable_Wave[1] = true;
                                                                            CutWaveState = 0;

                                                                            ZStartPoint.clear();
                                                                            ZLinearInterpolationAccelerationData.clear();
                                                                            ZLinearInterpolationAccelerationData.add(ZAD);
                                                                            SPIndex[2] = 1;
                                                                            DTFSP.Index = SPIndex[2]-1;
                                                                            State2FirstRun = true;
                                                                            TheMaxCCIndex = 0;
                                                                            TheMaxCC = -1;
                                                                            ToState2WindowSize[1] = 0;
                                                                            Check_Compensation_DataZ.clear();
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            else
                                                            {
                                                                State2SomeBug = 2;
                                                                DebugState = 2;
                                                                if(((DTFSP.ST - PastSP.ST)>=Walking2StepTimeLowerBound800ms)&&(DTFSP.UpOrDown == PastSP.UpOrDown)&&((DTFSP.Index - PastSP.Index)>=(TheState2WindowSize[1]-AdditionalWindowSizes))&&((DTFSP.Index - PastSP.Index)<=(TheState2WindowSize[1]+AdditionalWindowSizes)))
                                                                {
                                                                    if((DTFSP.Index - PastSP.Index) % 2 == 0)
                                                                    {
                                                                        MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index)/2;
                                                                    }else
                                                                    {
                                                                        MidIndex = PastSP.Index + (DTFSP.Index - PastSP.Index -1)/2;
                                                                    }

                                                                    TempCC = CC.CorrelationCoefficient(ZLinearInterpolationAccelerationData,PastSP.Index,MidIndex);

                                                                    if(TheMaxCC <= TempCC)
                                                                    {
                                                                        TheMaxCC = TempCC;

                                                                        TheMaxCCIndex = ZStartPoint.size();
                                                                    }

                                                                    if((TempCC >= CorrelationCoefficientThreshold))
                                                                    {
                                                                        State2TempAccelerationData.clear();
                                                                        for(int i = 0;i<State2AccelerationData.size();i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = State2AccelerationData.get(i);
                                                                            State2TempAccelerationData.add(TempData);
                                                                        }
                                                                        for(int i = PastSP.Index+1;i<=DTFSP.Index;i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                            State2TempAccelerationData.add(TempData);
                                                                        }
                                                                        int MidI = 0;

                                                                        if((State2TempAccelerationData.size()-1) % 2 == 0)
                                                                        {
                                                                            MidI = 0 + (State2TempAccelerationData.size()-1)/2;
                                                                        }else
                                                                        {
                                                                            MidI = 0 + (State2TempAccelerationData.size()-2)/2;
                                                                        }

                                                                        float SecondCheckCC = 0;
                                                                        SecondCheckCC = CC.CorrelationCoefficient(State2TempAccelerationData,0,MidI);

                                                                        if( SecondCheckCC >= CorrelationCoefficientThreshold )
                                                                        {
                                                                            WalkingStep[2] = WalkingStep[2]+2;
                                                                            CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                            NowSteps.Steps = 2;
                                                                            NowSteps.StartTime = PastSP.ST;
                                                                            NowSteps.EndTime = DTFSP.ST;
                                                                            CalculateTheStepsFromTwoAxis(2,NowSteps);

                                                                            CutWaveState = 2;

                                                                            //Window size
                                                                            TheState2WindowSize[1] = DTFSP.Index - PastSP.Index;

                                                                            //clear list
                                                                            State2AccelerationData.clear();
                                                                            State2TempAccelerationData.clear();

                                                                            TheMaxCC = -1;
                                                                            TheMaxCCIndex = 0;

                                                                            for(int i = PastSP.Index;i<=DTFSP.Index;i++)
                                                                            {
                                                                                AccelerationData TempData = new AccelerationData();
                                                                                TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                                State2AccelerationData.add(TempData);
                                                                            }

                                                                            ZStartPoint.clear();
                                                                            ZLinearInterpolationAccelerationData.clear();
                                                                            ZLinearInterpolationAccelerationData.add(ZAD);
                                                                            SPIndex[2] = 1;
                                                                            DTFSP.Index = SPIndex[2]-1;
                                                                            break;
                                                                        }
                                                                        else
                                                                        {
                                                                            CutWaveState = 2;
                                                                        }
                                                                    }
                                                                    else if(TempCC < CorrelationCoefficientThreshold)
                                                                    {
                                                                        CutWaveState = 2;
                                                                    }
                                                                }else if(((DTFSP.Index - PastSP.Index)>(TheState2WindowSize[1]+AdditionalWindowSizes)))
                                                                {
                                                                    DebugState = 3;
                                                                    SearchTheSimilarWaveFromPast[1] = true;
                                                                }
                                                                DebugState = 5;
                                                                if(SearchTheSimilarWaveFromPast[1])
                                                                {
                                                                    SearchTheSimilarWaveFromPast[1] = false;

                                                                    if(TheMaxCCIndex < ZStartPoint.size())
                                                                    {
                                                                        State2AccelerationData.clear();
                                                                        for(int i = PastSP.Index;i<=ZStartPoint.get(TheMaxCCIndex).Index;i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                            State2AccelerationData.add(TempData);
                                                                        }

                                                                        if((State2AccelerationData.size()-1) % 2 == 0)
                                                                        {
                                                                            MidIndex = 0 + (State2AccelerationData.size()-1)/2;
                                                                        }else
                                                                        {
                                                                            MidIndex = 0 + (State2AccelerationData.size()-2)/2;
                                                                        }

                                                                        float TempSCC = 0;
                                                                        TempSCC = CC.CorrelationCoefficient(State2AccelerationData,0,MidIndex);

                                                                        if(TempSCC >= CorrelationCoefficientThreshold)
                                                                        {
                                                                            DebugState = 6;
                                                                            WalkingStep[2] = WalkingStep[2]+2;

                                                                            CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                            NowSteps.Steps = 2;
                                                                            NowSteps.StartTime = State2AccelerationData.getFirst().TimeAccelerationData;
                                                                            NowSteps.EndTime = State2AccelerationData.getLast().TimeAccelerationData;
                                                                            CalculateTheStepsFromTwoAxis(2,NowSteps);

                                                                            Unstable_Wave_Start_Time[1] = ZStartPoint.get(TheMaxCCIndex).ST;
                                                                            Need_To_Calculate_The_Steps_For_Unstable_Wave[1] = true;
                                                                            CutWaveState = 0;

                                                                            int TempIndex = 0;
                                                                            TempIndex = ZStartPoint.get(TheMaxCCIndex).Index;
                                                                            for(int i = 0;i<TempIndex;i++)
                                                                            {
                                                                                ZLinearInterpolationAccelerationData.removeFirst();
                                                                                SPIndex[2] = SPIndex[2] - 1;
                                                                            }
                                                                            for(int i = 0;i < ZStartPoint.size();i++)
                                                                            {
                                                                                ZStartPoint.get(i).Index = ZStartPoint.get(i).Index - TempIndex ;
                                                                            }
                                                                            DTFSP.Index = DTFSP.Index - TempIndex;
                                                                            for(int i = 0;i < TheMaxCCIndex;i++)
                                                                            {
                                                                                ZStartPoint.removeFirst();
                                                                            }

                                                                            Check_Compensation_DataZ.clear();
                                                                            for(int i = 0;i<ZLinearInterpolationAccelerationData.size();i++)
                                                                            {
                                                                                Check_Compensation_DataZ.add(ZLinearInterpolationAccelerationData.get(i));
                                                                            }
                                                                            State2FirstRun = true;
                                                                            TheMaxCCIndex = 0;
                                                                            TheMaxCC = -1;
                                                                            break;
                                                                        }
                                                                        else
                                                                        {
                                                                            Check_Compensation_DataZ.clear();
                                                                            for(int i = 0;i<ZLinearInterpolationAccelerationData.size();i++)
                                                                            {
                                                                                Check_Compensation_DataZ.add(ZLinearInterpolationAccelerationData.get(i));
                                                                            }
                                                                            DebugState = 7;
                                                                            Unstable_Wave_Start_Time[1] = PastSP.ST;
                                                                            Need_To_Calculate_The_Steps_For_Unstable_Wave[1] = true;
                                                                            CutWaveState = 0;

                                                                            ZStartPoint.clear();
                                                                            ZLinearInterpolationAccelerationData.clear();
                                                                            ZLinearInterpolationAccelerationData.add(ZAD);
                                                                            SPIndex[2] = 1;
                                                                            DTFSP.Index = SPIndex[2]-1;
                                                                            State2FirstRun = true;
                                                                            TheMaxCCIndex = 0;
                                                                            TheMaxCC = -1;
                                                                            break;
                                                                        }

                                                                    }
                                                                    else
                                                                    {
                                                                        State2AccelerationData.clear();
                                                                        for(int i = PastSP.Index;i<ZLinearInterpolationAccelerationData.size();i++)
                                                                        {
                                                                            AccelerationData TempData = new AccelerationData();
                                                                            TempData = ZLinearInterpolationAccelerationData.get(i);
                                                                            State2AccelerationData.add(TempData);
                                                                        }

                                                                        if((State2AccelerationData.size()-1) % 2 == 0)
                                                                        {
                                                                            MidIndex = 0 + (State2AccelerationData.size()-1)/2;
                                                                        }else
                                                                        {
                                                                            MidIndex = 0 + (State2AccelerationData.size()-2)/2;
                                                                        }

                                                                        float TempSCC = 0;
                                                                        TempSCC = CC.CorrelationCoefficient(State2AccelerationData,0,MidIndex);

                                                                        if(TempSCC >= CorrelationCoefficientThreshold)
                                                                        {
                                                                            DebugState = 6;
                                                                            WalkingStep[2] = WalkingStep[2]+2;
                                                                            CalculateTheStepsFromTwoAxisDataType NowSteps = new CalculateTheStepsFromTwoAxisDataType();
                                                                            NowSteps.Steps = 2;
                                                                            NowSteps.StartTime = State2AccelerationData.getFirst().TimeAccelerationData;
                                                                            NowSteps.EndTime = State2AccelerationData.getLast().TimeAccelerationData;
                                                                            CalculateTheStepsFromTwoAxis(2,NowSteps);
                                                                            Unstable_Wave_Start_Time[1] = DTFSP.ST;
                                                                            Need_To_Calculate_The_Steps_For_Unstable_Wave[1] = true;
                                                                            CutWaveState = 0;

                                                                            Check_Compensation_DataZ.clear();
                                                                            for(int i = 0;i<ZLinearInterpolationAccelerationData.size();i++)
                                                                            {
                                                                                if(ZLinearInterpolationAccelerationData.get(i).TimeAccelerationData>=State2AccelerationData.getLast().TimeAccelerationData)
                                                                                {
                                                                                    Check_Compensation_DataZ.add(ZLinearInterpolationAccelerationData.get(i));
                                                                                }
                                                                            }
                                                                            ZStartPoint.clear();
                                                                            ZLinearInterpolationAccelerationData.clear();
                                                                            ZLinearInterpolationAccelerationData.add(ZAD);
                                                                            SPIndex[2] = 1;
                                                                            DTFSP.Index = SPIndex[2]-1;
                                                                            State2FirstRun = true;
                                                                            TheMaxCCIndex = 0;
                                                                            TheMaxCC = -1;
                                                                            break;
                                                                        }
                                                                        else
                                                                        {
                                                                            Check_Compensation_DataZ.clear();
                                                                            for(int i = 0;i<ZLinearInterpolationAccelerationData.size();i++)
                                                                            {
                                                                                {
                                                                                    Check_Compensation_DataZ.add(ZLinearInterpolationAccelerationData.get(i));
                                                                                }
                                                                            }
                                                                            DebugState = 7;
                                                                            CutWaveState = 0;
                                                                            Unstable_Wave_Start_Time[1] = PastSP.ST;
                                                                            Need_To_Calculate_The_Steps_For_Unstable_Wave[1] = true;
                                                                            ZStartPoint.clear();
                                                                            ZLinearInterpolationAccelerationData.clear();
                                                                            ZLinearInterpolationAccelerationData.add(ZAD);
                                                                            SPIndex[2] = 1;
                                                                            DTFSP.Index = SPIndex[2]-1;
                                                                            State2FirstRun = true;
                                                                            TheMaxCCIndex = 0;
                                                                            TheMaxCC = -1;
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    PutStartPointDataToTheLinkedList(ZStartPoint,DTFSP);
                                                }
                                                else
                                                {
                                                    PutStartPointDataToTheLinkedList(ZStartPoint,DTFSP);
                                                }
                                            }
                                        }
                                    }catch( Exception ex)
                                    {
                                        if(CutWaveState == 7)
                                        {
                                        }
                                        else if(CutWaveState == 0)
                                        {
                                        }
                                        else
                                        {
                                        }
                                    }
                                LinearInterpolationTime = LinearInterpolationTime + AverageSensingTimeInterval20ms;
                                Acc_last_LIAccData[0] = XAD.AccelerationData;
                                Acc_last_LIAccData[1] = YAD.AccelerationData;
                                Acc_last_LIAccData[2] = ZAD.AccelerationData;

                                for(int x = 0;x<3;x++)
                                {
                                    if(FirstDoLI[x] == false)
                                    {
                                        FirstDoLI[x] = true;
                                    }
                                }
                            }
                        }
                        else
                        {
                            AngleDataList.clear();
                            AccelerationDataLinkedList.clear();
                        }
                    }
                    if(GlobalStepsList.size() > 0  && GlobalStepsList.getLast().Steps != currentstep)
                    {
                        checkOrein();
                        System.out.println(currentstep);
                        currentstep = GlobalStepsList.getLast().Steps;
                        LastAddPath = GlobalStepsList.getLast();
                    }
                }


            }

            catch (Exception e){
                Log.d("Exception",e.getMessage());
            }



        }
    };


    static class MHandler extends Handler {
        WeakReference<MainActivity> outerClass;

        MHandler(MainActivity activity) {
            outerClass = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            MainActivity theClass = outerClass.get();
            switch (msg.what){
                case Constants.Handler.SensorEvent: {
//                    theClass.checkOrein();
                    Bundle bundle = msg.getData();

                    theClass.freeDraw.addPath(bundle.getInt("Step"), bundle.getFloat("Orien"),bundle.getInt("PathID"));
                    double distance = Math.sqrt(Math.pow(theClass.x,2) + Math.pow(theClass.y,2));
                    double distance2 = Math.sqrt(Math.pow(theClass.x,2) + Math.pow((theClass.y-21),2));
                    theClass.TV_Step_Count.setText("Step Count: " + theClass.GlobalStepsList.getLast().Steps);
                    theClass.TV_Orien.setText("Orientation: " + theClass.mDecimalFormat(theClass.data_gyroscope.Orein + theClass.resetOrien));
                    theClass.TV_Dist.setText("Distance to destination: " + theClass.mDecimalFormat(distance) + " m");
                    theClass.TV_Start.setText("Distance to starting point: " + theClass.mDecimalFormat(distance2)  + " m");

                    String last_dis = theClass.mDecimalFormat(theClass.last_distance);
                    String sg_dis = theClass.mDecimalFormat(theClass.sg_distance);
                    if (! last_dis.equals(sg_dis)) {
                        String dataText = "orien: " + theClass.mDecimalFormat(theClass.last_orien) + ", distance: " + sg_dis + "\n";
                        theClass.TV_step.setText(theClass.TV_step.getText() + dataText);
                    }

                    break;
                }
                case Constants.Handler.GyroEvent:{
//                    theClass.TV_Start.setText(msg.getData().getFloat("START") + "," + msg.getData().getFloat("ALLINZ"));
//                    System.out.println( msg.getData().getFloat("START") + "," + msg.getData().getFloat("ALLINZ"));
                }
            }
        }
    }

    static class PDRinfo {
        public int step;
        public double orein;
        public PDRinfo(int s,double o){
            this.step = s;
            this.orein = o;
        }
    }
}
