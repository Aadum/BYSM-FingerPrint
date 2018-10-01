package com.fgtit.finger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fgtit.app.ActivityList;
import com.fgtit.data.GlobalData;
import com.fgtit.data.ImageSimpleAdapter;
import com.fgtit.data.RecordItem;
import com.fgtit.data.UserItem;
import com.fgtit.fpcore.FPMatch;
import com.fgtit.utils.ExtApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.AsyncFingerprint;
import android_serialport_api.AsyncFingerprint.OnGenCharListener;
import android_serialport_api.AsyncFingerprint.OnGetImageListener;
import android_serialport_api.AsyncFingerprint.OnUpCharListener;
import android_serialport_api.AsyncFingerprint.OnUpImageListener;
import android_serialport_api.SerialPortManager;

public class SignOnActivity extends AppCompatActivity {

    private ListView listView1;
    private ArrayList<HashMap<String, Object>> mData1;
    private SimpleAdapter adapter1;

    private TextView tvFpStatus;
    private ImageView fpImage;

    private AsyncFingerprint vFingerprint;

    private boolean bIsCancel = false;
    private boolean bfpWork = false;

    private Timer startTimer;
    private TimerTask startTask;
    private Handler startHandler;

    private int SelectedID = 0;

    //RFID    
    private int rfidtype = 0;
    private Timer xTimer = null;
    private TimerTask xTask = null;
    private Handler xHandler;

    //NFC
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_local);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView1 = (ListView) findViewById(R.id.listView1);
        mData1 = new ArrayList<HashMap<String, Object>>();
        adapter1 = new ImageSimpleAdapter(this, mData1, R.layout.listview_signitem,
                new String[]{"title", "info", "dts", "img"},
                new int[]{R.id.title, R.id.info, R.id.dts, R.id.img});
        listView1.setAdapter(adapter1);

        tvFpStatus = (TextView) findViewById(R.id.textView1);
        tvFpStatus.setText("");
        fpImage = (ImageView) findViewById(R.id.imageView1);
        fpImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bIsCancel = false;
                tvFpStatus.setText(getString(R.string.txt_fpplace));
                vFingerprint.FP_GetImage();
                bfpWork = true;

            }
        });

        //Card
        InitReadCard();
        ReadCardSn();


        //Fingerprint
/*        vFingerprint = SerialPortManager.getInstance().getNewAsyncFingerprint();
           FPInit();
   		FPProcess();*/
        listView1.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                //Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);

                if (GlobalData.getInstance().userList.size() > 0) {

                    if(SelectedID > 0) {
                        Intent intent = new Intent(SignOnActivity.this, PaymentActivity.class);
                        Bundle bundle = new Bundle();
//                    bundle.putInt("pos",pos);
                        bundle.putInt("pos", SelectedID);
                        intent.putExtras(bundle);

                        startActivityForResult(intent, 0);
                    }
                }

            }
        });









    }

    private void workExit() {
        if (SerialPortManager.getInstance().isOpen()) {
            bIsCancel = true;
            SerialPortManager.getInstance().closeSerialPort();
            CloseReadCard();
            this.finish();
        }
    }

    private void AddPersonItem(UserItem person) {

        RecordItem rs = new RecordItem();
        rs.id = person.id;
        rs.name = person.name;
        rs.datetime = ExtApi.getStringDate();
//        if (GlobalData.getInstance().glocal) {
//            rs.lat = String.valueOf(GlobalData.getInstance().glat);
//            rs.lng = String.valueOf(GlobalData.getInstance().glng);
//        }
        rs.type = "0";
        rs.worktype = person.worktype;
        rs.linetype = person.linetype;
        rs.depttype = person.depttype;

        GlobalData.getInstance().AppendRecord(rs);
        GlobalData.getInstance().recordList.add(rs);

        //GlobalData.getInstance().AppendLocalRecord(person, 1);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("title", rs.name);
        map.put("info", rs.id);
        map.put("dts", rs.datetime);
        if (person.photo.length() > 1000) {
            map.put("img", ExtApi.Bytes2Bimap(ExtApi.Base64ToBytes(person.photo)));
        } else {
            byte[] photo = GlobalData.getInstance().LoadPhotoByID(person.id);
            if (photo != null)
                map.put("img", ExtApi.Bytes2Bimap(photo));
            else
                map.put("img", ExtApi.LoadBitmap(getResources(), R.drawable.guest));
        }
        SelectedID = Integer.parseInt(rs.id);
        Log.i("My ID: ", String.valueOf(SelectedID));
        mData1.add(map);
        adapter1.notifyDataSetChanged();
        ScrollListViewToBottom();
    }

    private void ScrollListViewToBottom() {
        listView1.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView1.setSelection(adapter1.getCount() - 1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_off, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Toast.makeText(SignOnActivity.this, "Cancel...", Toast.LENGTH_SHORT).show();
                workExit();
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                return true;
            case R.id.action_screen:
                mData1.clear();
                adapter1.notifyDataSetChanged();
                break;
            case R.id.action_sign:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Toast.makeText(SignOnActivity.this, "Cancel...", Toast.LENGTH_SHORT).show();
            workExit();
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void FPProcess() {
        if (!bfpWork) {
            try {
                Thread.currentThread();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tvFpStatus.setText(getString(R.string.txt_fpplace));
            vFingerprint.FP_GetImage();
            bfpWork = true;
        }
    }

    private void FPInit() {
        //ָ�ƴ���
        vFingerprint.setOnGetImageListener(new OnGetImageListener() {
            @Override
            public void onGetImageSuccess() {
                if (ActivityList.getInstance().IsUpImage) {
                    vFingerprint.FP_UpImage();
                    tvFpStatus.setText(getString(R.string.txt_fpdisplay));
                } else {
                    tvFpStatus.setText(getString(R.string.txt_fpprocess));
                    vFingerprint.FP_GenChar(1);
                }
            }

            @Override
            public void onGetImageFail() {
                if (!bIsCancel) {
                    vFingerprint.FP_GetImage();
                    //SignLocalActivity.this.AddStatus("Error");
                } else {
                    Toast.makeText(SignOnActivity.this, "Cancel OK", Toast.LENGTH_SHORT).show();
                }
            }
        });

        vFingerprint.setOnUpImageListener(new OnUpImageListener() {
            @Override
            public void onUpImageSuccess(byte[] data) {
                Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                fpImage.setImageBitmap(image);
                //fpImage.setBackgroundDrawable(new BitmapDrawable(image));
                tvFpStatus.setText(getString(R.string.txt_fpprocess));
                vFingerprint.FP_GenChar(1);
            }

            @Override
            public void onUpImageFail() {
                bfpWork = false;
                TimerStart();
            }
        });

        vFingerprint.setOnGenCharListener(new OnGenCharListener() {
            @Override
            public void onGenCharSuccess(int bufferId) {
                tvFpStatus.setText(getString(R.string.txt_fpidentify));
                vFingerprint.FP_UpChar();
            }

            @Override
            public void onGenCharFail() {
                tvFpStatus.setText(getString(R.string.txt_fpfail));
            }
        });

        vFingerprint.setOnUpCharListener(new OnUpCharListener() {

            @Override
            public void onUpCharSuccess(byte[] model) {

                for (int i = 0; i < GlobalData.getInstance().userList.size(); i++) {
                    byte[] tmp = new byte[256];
                    if (GlobalData.getInstance().userList.get(i).bytes1 != null) {
                        System.arraycopy(GlobalData.getInstance().userList.get(i).bytes1, 0, tmp, 0, 256);
                        if (FPMatch.getInstance().MatchTemplate(model, tmp) > 60) {
								/*if(	GlobalData.getInstance().FindUserItemByUserItem(GlobalData.getInstance().userList.get(i)))
							      {*/
                            AddPersonItem(GlobalData.getInstance().userList.get(i));
                            tvFpStatus.setText(getString(R.string.txt_fpmatchok));
                            break;
/*									}else{
										Toast.makeText(SignOnActivity.this, "Please wait 120s seconds until next attendance", Toast.LENGTH_SHORT).show();	
										break;
									}*/
                        }
                        System.arraycopy(GlobalData.getInstance().userList.get(i).bytes1, 256, tmp, 0, 256);
                        if (FPMatch.getInstance().MatchTemplate(model, tmp) > 60) {
/*								if(	GlobalData.getInstance().FindUserItemByUserItem(GlobalData.getInstance().userList.get(i)))
							      {*/
                            AddPersonItem(GlobalData.getInstance().userList.get(i));
                            tvFpStatus.setText(getString(R.string.txt_fpmatchok));
                            break;
/*									}else{
										Toast.makeText(SignOnActivity.this, "Please wait 120s seconds until next attendance", Toast.LENGTH_SHORT).show();	
										break;
									}*/
                        }
                    }
                    if (GlobalData.getInstance().userList.get(i).bytes2 != null) {
                        System.arraycopy(GlobalData.getInstance().userList.get(i).bytes2, 0, tmp, 0, 256);
                        if (FPMatch.getInstance().MatchTemplate(model, tmp) > 60) {
							/*	if(	GlobalData.getInstance().FindUserItemByUserItem(GlobalData.getInstance().userList.get(i)))
							      {*/
                            AddPersonItem(GlobalData.getInstance().userList.get(i));
                            tvFpStatus.setText(getString(R.string.txt_fpmatchok));
                            break;
/*									}else{
										Toast.makeText(SignOnActivity.this, "Please wait 120s seconds until next attendance", Toast.LENGTH_SHORT).show();	
										break;
									}*/
                        }
                        System.arraycopy(GlobalData.getInstance().userList.get(i).bytes2, 256, tmp, 0, 256);
                        if (FPMatch.getInstance().MatchTemplate(model, tmp) > 60) {
							/*	if(	GlobalData.getInstance().FindUserItemByUserItem(GlobalData.getInstance().userList.get(i)))
							      {*/
                            AddPersonItem(GlobalData.getInstance().userList.get(i));
                            tvFpStatus.setText(getString(R.string.txt_fpmatchok));
                            break;
								/*	}else{
										Toast.makeText(SignOnActivity.this, "Please wait 120s seconds until next attendance", Toast.LENGTH_SHORT).show();	
										break;
									}*/
                        }
                    }
                }

                bfpWork = false;
                TimerStart();
            }

            @Override
            public void onUpCharFail() {
                tvFpStatus.setText(getString(R.string.txt_fpmatchfail) + ":-1");
                bfpWork = false;
                TimerStart();
            }
        });

    }

    @SuppressLint("HandlerLeak")
    public void TimerStart() {
        if (bIsCancel)
            return;
        if (startTimer == null) {
            startTimer = new Timer();
            startHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    TimeStop();
                    //FPProcess();
                    if (!bfpWork) {
                      /*  try {
                            Thread.currentThread();
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                        tvFpStatus.setText(getString(R.string.txt_fpplace));
                        vFingerprint.FP_GetImage();
                        bfpWork = true;
                    }
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
            startTimer.schedule(startTask, 1000, 1000);
        }
    }

    public void TimeStop() {
        if (startTimer != null) {
            startTimer.cancel();
            startTimer = null;
            startTask.cancel();
            startTask = null;
        }
    }

    //Card
    public void InitReadCard() {
        if (ActivityList.getInstance().IsUseNFC) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter == null) {
                Toast.makeText(this, "Device does not support NFC!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(this, "Enable the NFC function in the system settings!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            mFilters = new IntentFilter[]{
                    new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                    new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                    new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};
        }
    }

    public void CloseReadCard() {
        if (ActivityList.getInstance().IsUseNFC) {
        } else {
        }
    }

    public void ReadCardSn() {
        if (ActivityList.getInstance().IsUseNFC) {
        } else {
        }
    }


    //NFC
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        byte[] sn = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String cardsn =
                Integer.toHexString(sn[0] & 0xFF).toUpperCase() +
                        Integer.toHexString(sn[1] & 0xFF).toUpperCase() +
                        Integer.toHexString(sn[2] & 0xFF).toUpperCase() +
                        Integer.toHexString(sn[3] & 0xFF).toUpperCase();
        for (int i = 0; i < GlobalData.getInstance().userList.size(); i++) {
            if (GlobalData.getInstance().userList.get(i).cardsn.indexOf(cardsn) >= 0) {
                AddPersonItem(GlobalData.getInstance().userList.get(i));
            }
        }
    }

    @Override
    public void onPause() {
        if (ActivityList.getInstance().IsUseNFC) {
            if (nfcAdapter != null)
                nfcAdapter.disableForegroundDispatch(this);
        }
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            if (SerialPortManager.getInstance().isOpen()) {
                bIsCancel = true;
                SerialPortManager.getInstance().closeSerialPort();
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (ActivityList.getInstance().IsUseNFC) {
            if (nfcAdapter != null)
                nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, null);
        }
        vFingerprint = SerialPortManager.getInstance().getNewAsyncFingerprint();
        FPInit();
        FPProcess();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        workExit();
        super.onDestroy();
    }
}