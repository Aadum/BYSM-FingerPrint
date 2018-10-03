package com.fgtit.finger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.fgtit.app.ActivityList;
import com.fgtit.app.UpdateApp;
import com.fgtit.data.GlobalData;
import com.fgtit.fpcore.FPMatch;
import com.fgtit.utils.ExtApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialPort;

public class MainActivity extends AppCompatActivity {

    private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    private static final int RE_WORK0 = 0;
    private static final int RE_WORK1 = 1;
    private static final int RE_WORK2 = 2;

    private String btAddress = "";

    private Menu mainMenu;
    private TextView txtView;
    private Button btn01, btn02, btn03;
    private long exitTime = 0;
    private WakeLock wakeLock;

    private Timer startTimer;
    private TimerTask startTask;
    Handler startHandler;

    private ProgressDialog progressDialog;

    private SoundPool soundPool;
    private int soundIda, soundIdb;
    private boolean soundflag = false;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private BitmapDescriptor mCurrentMarker;
    private LocationMode mCurrentMode;

    public LocationClient mLocationClient = null;
//    public BDLocationListener myListener = new MyLocationListener();
    private ListView listView;
    private List<Map<String, Object>> mData;



    private Runnable runnableUserItemListGetNet;

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
//
//        try {
//

//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }


            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            listView=(ListView) findViewById(R.id.listView1);
            SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.listview_menuitem,
                    new String[]{"title","info","img"},
                    new int[]{R.id.title,R.id.info,R.id.img});
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new ListView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    //Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);
                    switch(pos){
                        case 0:{
                            Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        }
                        break;
                        case 1:{
                            Intent intent = new Intent(MainActivity.this, EmployeesActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        }
                        break;
//                        case 2:{
//                            Intent intent = new Intent(MainActivity.this, UtilitiesActivity.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                        }
//                        break;
//                        case 3:{
//                            Intent intent = new Intent(MainActivity.this, SystemActivity.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                        }
//                        break;
                        case 2:{
                            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        }
                        break;
                        case 3:{

                            Intent intent = new Intent(MainActivity.this, SignOnActivity.class);
                            startActivity(intent);
                            //	startActivityForResult(intent,RE_WORK1);
                            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                            //     new Thread(runnableUserItemListGetNet).start();
                        }
                        break;
                    }
                }
            });





        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ActivityList.getInstance().IsUseNFC = ExtApi.IsSupportNFC(this);
        SerialPort sp = new SerialPort();
        if (sp.getmodel().equals("b82")) {
            ActivityList.getInstance().IsPad = true;
        } else {
            ActivityList.getInstance().IsPad = false;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());



        txtView = (TextView) findViewById(R.id.textView1);

        //MtGpio.getInstance().FPPowerSwitch(true);

        //
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(15000);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);

//        mLocationClient = new LocationClient(getApplicationContext());
//        mLocationClient.registerLocationListener(myListener);
//        mLocationClient.setLocOption(option);
//        mLocationClient.start();
        //mLocationClient.requestLocation();


//        mMapView = (MapView) findViewById(R.id.bmapView);
//        mBaiduMap = mMapView.getMap();


//        mBaiduMap.setMyLocationEnabled(true);

//        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
//        //mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
//        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
//        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
//        mBaiduMap.setMyLocationConfigeration(config);
        btn01 = (Button) findViewById(R.id.button1);
        btn01.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignOnActivity.class);
                startActivity(intent);
                //	startActivityForResult(intent,RE_WORK1);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                //     new Thread(runnableUserItemListGetNet).start();
            }
        });

//        btn02 = (Button) findViewById(R.id.button2);
//        btn02.setOnClickListener(new View.OnClickListener() {
//            //@Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SignOffActivity.class);
//                startActivity(intent);
//                //	startActivityForResult(intent,RE_WORK2);
//                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//            }
//        });

		/*
		btn03=(Button)findViewById(R.id.button3);
		btn03.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {		
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		*/

        //LocationInit();


        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "sc");
        wakeLock.acquire();

        ActivityList.getInstance().setMainContext(this);
        ActivityList.getInstance().LoadConfig();

        GlobalData.getInstance().SetContext(this);
        GlobalData.getInstance().CreateDir();
        GlobalData.getInstance().LoadFileList();
        //GlobalData.getInstance().LoadUsersList();
        GlobalData.getInstance().LoadConfig();
        //GlobalData.getInstance().LoadRecordsList();
        GlobalData.getInstance().LoadWorkList();
        GlobalData.getInstance().LoadLineList();
        GlobalData.getInstance().LoadDeptList();

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundIda = soundPool.load(this, R.raw.start, 1);
        soundIdb = soundPool.load(this, R.raw.stop, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundflag = true;    //  ��ʾ�������
            }
        });

        if (FPMatch.getInstance().InitMatch() == 0) {
            Toast.makeText(getApplicationContext(), "Init Matcher Fail!", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getApplicationContext(), "Init Matcher OK!", Toast.LENGTH_SHORT).show();
        }

//        LatLng ll = new LatLng(ActivityList.getInstance().MapLat, ActivityList.getInstance().MapLng);
//        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, ActivityList.getInstance().MapZoom);
//        mBaiduMap.animateMapStatus(u);

        UpdateApp.getInstance().setAppContext(this);
        LoadUserListThread();

        setFpIoState(true);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;
        switch (densityDpi) {
            case 210: {    //7 inch
                LinearLayout mapLayout = (LinearLayout) findViewById(R.id.mapLayout);
                mapLayout.getLayoutParams().height = 920;
            }
            break;
            case 240: {    //5 inch-
            }
            break;
        }
    }

    private void setFpIoState(boolean isOn) {
        int state = 0;
        if (isOn) {
            state = 1;
        } else {
            state = 0;
        }
        Intent i = new Intent("ismart.intent.action.fingerPrint_control");
        i.putExtra("state", state);
        sendBroadcast(i);
    }

    @SuppressLint("HandlerLeak")
    public Handler mLoadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        //progressDialog.setTitle("Please Wait");
                        progressDialog.setMessage("Please Wait,Load Count : " + String.valueOf(msg.arg1) + " ...");
                        //progressDialog.setIcon(android.R.drawable.btn_star);
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "User Count:" + String.valueOf(GlobalData.getInstance().userList.size()), Toast.LENGTH_SHORT).show();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Please Wait,Load Count : " + String.valueOf(msg.arg1) + " ...", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    void LoadUserListThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int count = GlobalData.getInstance().GetUsersCount();
                if (count > 200) {
                    Message message = new Message();
               /* 	message.what=1;  
                	message.arg1=count;
                    mLoadHandler.sendMessage(message);*/

                    GlobalData.getInstance().LoadUsersList();

                    message.what = 2;
                    mLoadHandler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = 3;
                    message.arg1 = count;
                    mLoadHandler.sendMessage(message);
                    GlobalData.getInstance().LoadUsersList();
                }
            }
        });
        thread.start();
    }

//    public class MyLocationListener implements BDLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            if (location == null)
//                return;
//			/*
//			StringBuffer sb = new StringBuffer(256);
//			sb.append("time : ");
//			sb.append(location.getTime());
//			sb.append("\nerror code : ");
//			sb.append(location.getLocType());
//			sb.append("\nlatitude : ");
//			sb.append(location.getLatitude());
//			sb.append("\nlontitude : ");
//			sb.append(location.getLongitude());
//			sb.append("\nradius : ");
//			sb.append(location.getRadius());
//			if (location.getLocType() == BDLocation.TypeGpsLocation){
//				sb.append("\nspeed : ");
//				sb.append(location.getSpeed());
//				sb.append("\nsatellite : ");
//				sb.append(location.getSatelliteNumber());
//			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//				sb.append("\naddr : ");
//				sb.append(location.getAddrStr());
//			}
//
//			txtView.setText(sb.toString());
//			*/
//
//            StringBuffer sb = new StringBuffer(256);
//            switch (location.getLocType()) {
//                case 61:
//                    sb.append("Satellite positioning");
//                    GlobalData.getInstance().glocal = true;
//                    break;
//                case 66:
//                    sb.append("Offline positioning");
//                    GlobalData.getInstance().glocal = true;
//                    break;
//                case 161:
//                    sb.append("Network positioning");
//                    GlobalData.getInstance().glocal = true;
//                    break;
//                default:
//                    sb.append("Positioning failure");
//                    GlobalData.getInstance().glocal = false;
//                    break;
//            }
//
//            sb.append("  Time: ");
//            sb.append(location.getTime());
//            sb.append("\nLatitude: ");
//            sb.append(location.getLatitude());
//            sb.append("  Longitude: ");
//            sb.append(location.getLongitude());
//
//            txtView.setText(sb.toString());
//
////            GlobalData.getInstance().glat = location.getLatitude();
////            GlobalData.getInstance().glng = location.getLongitude();
////            ActivityList.getInstance().MapLat = location.getLatitude();
////            ActivityList.getInstance().MapLng = location.getLongitude();
////
////            //������λͼ��
////            //mBaiduMap.setMyLocationEnabled(true);
////            //���춨λ����  ,�˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
////            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(location.getDirection()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
////            //���ö�λ����
////            mBaiduMap.setMyLocationData(locData);
////            //���ö�λͼ������ã���λģʽ���Ƿ���������Ϣ���û��Զ��嶨λͼ�꣩
////            //mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
////            //mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
////            //mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
////            //MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
////            //mBaiduMap.setMyLocationConfigeration(config);
////
////            if (Math.abs(ActivityList.getInstance().MapZoom - mBaiduMap.getMapStatus().zoom) >= 1.0f) {
////                ActivityList.getInstance().MapZoom = mBaiduMap.getMapStatus().zoom;
////                ActivityList.getInstance().SetConfigByVal("MapZoom", ActivityList.getInstance().MapZoom);
////            }
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
        TimerStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        TimeStop();

        wakeLock.release(); //������ֻ���
        soundPool.release();
        soundPool = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mMapView.onPause();
        wakeLock.release();//������ֻ���
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mMapView.onResume();
        wakeLock.acquire(); //���ñ��ֻ���
    }

    public void TimerStart() {
        startTimer = new Timer();
        startHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                //mLocationClient.requestLocation();
                super.handleMessage(msg);
            }
        };
        startTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                startHandler.sendMessage(message);
            }
        };
        startTimer.schedule(startTask, 15000, 15000);
    }

    public void TimeStop() {
        if (startTimer != null) {
            startTimer.cancel();
            startTimer = null;
            startTask.cancel();
            startTask = null;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            exitApplication();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exitApplication() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), getString(R.string.txt_exitinfo), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {

//            ActivityList.getInstance().SetConfigByVal("MapLat", String.valueOf(ActivityList.getInstance().MapLat));
//            ActivityList.getInstance().SetConfigByVal("MapLng", String.valueOf(ActivityList.getInstance().MapLng));

            finish();
            System.exit(0);
            //AppList.getInstance().exit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mainMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                exitApplication();
                return true;
            case R.id.action_refresh:
                //if(GlobalData.getInstance().glocal){
                //	txtView.setText("Net Location:"+String.valueOf(GlobalData.getInstance().glat)+","+String.valueOf(GlobalData.getInstance().glng));
                //}
                mLocationClient.requestLocation();
                return true;
            case R.id.action_manage: {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                //btReader.SetMessageHandler(btHandler);
                //Intent serverIntent = new Intent(this, DeviceListActivity.class);
                //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RE_WORK0:
                break;
            case RE_WORK1:
                break;
            case RE_WORK2:
                break;
        }
    }

    private void LocationInit() {
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS Location", Toast.LENGTH_SHORT).show();
            GpsLocationInit();
            return;
        } else {
            Toast.makeText(this, "Net Location", Toast.LENGTH_SHORT).show();
            NetLocationInit();
        }
    }

    private void GetLocationInfo(Location location) {
        if (location != null) {
            GlobalData.getInstance().glocal = true;
            GlobalData.getInstance().glat = location.getLatitude();
            GlobalData.getInstance().glng = location.getLongitude();
            //SetMapCenter(location.getLatitude(),location.getLongitude());
            //SetMapMaker(location.getLatitude(),location.getLongitude());
            txtView.setText("Net Location :  " + String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
        } else {
            GlobalData.getInstance().glocal = false;
            txtView.setText("No location found");
        }

    }

    private void NetLocationInit() {
        // ��ȡ��LocationManager����
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // ����һ��Criteria����
        Criteria criteria = new Criteria();
        // ���ô��Ծ�ȷ��
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        // �����Ƿ���Ҫ���غ�����Ϣ
        criteria.setAltitudeRequired(false);
        // �����Ƿ���Ҫ���ط�λ��Ϣ
        criteria.setBearingRequired(false);
        // �����Ƿ������ѷ���
        criteria.setCostAllowed(true);
        // ���õ������ĵȼ�
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        // �����Ƿ���Ҫ�����ٶ���Ϣ
        criteria.setSpeedRequired(false);
        // �������õ�Criteria���󣬻�ȡ����ϴ˱�׼��provider���� 41
        String currentProvider = locationManager.getBestProvider(criteria, true);

        // ���ݵ�ǰprovider�����ȡ���һ��λ����Ϣ 44
        Location currentLocation = locationManager.getLastKnownLocation(currentProvider);
        // ���λ����ϢΪnull�����������λ����Ϣ 46
        if (currentLocation == null) {
            locationManager.requestLocationUpdates(currentProvider, 5000, 0, netlocationListener);
        } else {
            GetLocationInfo(currentLocation);
            locationManager.requestLocationUpdates(currentProvider, 5000, 0, netlocationListener);    //LocationManager.GPS_PROVIDER
        }
    }

    private LocationListener netlocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            GetLocationInfo(location);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    private void GpsLocationInit() {
        LocationManager gpslocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // ���ҵ�������Ϣ
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // �߾���
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // �͹���
        String currentProvider = gpslocationManager.getBestProvider(criteria, true);
        Location gpsLocation = gpslocationManager.getLastKnownLocation(currentProvider);
        //String gpsProvider = gpslocationManager.getProvider(LocationManager.GPS_PROVIDER).getName();
        //Location gpsLocation = gpslocationManager.getLastKnownLocation(gpsProvider);

        // ���λ����ϢΪnull�����������λ����Ϣ
        if (gpsLocation == null) {
            //gpslocationManager.requestLocationUpdates(gpsProvider, 0, 0,gpslocationListener);
            gpslocationManager.requestLocationUpdates(currentProvider, 0, 0, gpslocationListener);
        } else {
            GetLocationInfo(gpsLocation);
            gpslocationManager.requestLocationUpdates(currentProvider, 0, 0, gpslocationListener);
        }
        gpslocationManager.addGpsStatusListener(gpsListener);
    }

    private LocationListener gpslocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            GetLocationInfo(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        // ״̬�ı�ʱ����107
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
        // GPS״̬�����仯ʱ����
        @Override
        public void onGpsStatusChanged(int event) {
            // ��ȡ��ǰ״̬
            switch (event) {
                // ��һ�ζ�λʱ���¼�
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    break;
                // ��ʼ��λ���¼�
                case GpsStatus.GPS_EVENT_STARTED:
                    break;
                // ����GPS����״̬�¼�
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    //Toast.makeText(MainMapActivity.this, "GPS_EVENT_SATELLITE_STATUS",Toast.LENGTH_SHORT).show();
                    break;
                // ֹͣ��λ�¼�
                case GpsStatus.GPS_EVENT_STOPPED:
                    break;
            }
        }
    };

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();

        map = new HashMap<String, Object>();
        map.put("title", getString(R.string.txt_title_01));
        map.put("info", getString(R.string.txt_info_01));
        map.put("img", R.drawable.view_details);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getString(R.string.txt_title_02));
        map.put("info", getString(R.string.txt_info_02));
        map.put("img", R.drawable.group);
        list.add(map);

//        map = new HashMap<String, Object>();
//        map.put("title", getString(R.string.txt_title_03));
//        map.put("info", getString(R.string.txt_info_03));
//        map.put("img", R.drawable.reload);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("title", getString(R.string.txt_title_04));
//        map.put("info", getString(R.string.txt_info_04));
//        map.put("img", R.drawable.engineering);
//        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", getString(R.string.txt_title_05));
        map.put("info", getString(R.string.txt_info_05));
        map.put("img", R.drawable.about);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "Make Payment");
        map.put("info", "Pay beneficiaries");
        map.put("img", R.drawable.view_details);
        list.add(map);

        mData=list;
        return list;
    }



//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        switch(id){
//            case android.R.id.home:
//                this.setResult(1);
//                this.finish();
//                return true;
//            case R.id.action_settings:
//                Intent intent = new Intent(this, SettingsActivity.class);
//                startActivity(intent);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
//            this.setResult(1);
//            this.finish();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}
