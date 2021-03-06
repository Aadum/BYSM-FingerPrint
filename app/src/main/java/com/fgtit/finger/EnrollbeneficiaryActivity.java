package com.fgtit.finger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.fpi.MtGpio;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.SoundPool;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fgtit.app.ActivityList;
import com.fgtit.data.GlobalData;
import com.fgtit.data.UserItem;
import com.fgtit.fpcore.FPMatch;
import com.fgtit.model.Beneficiary;
import com.fgtit.model.Item;
import com.fgtit.utils.ExtApi;
import com.fgtit.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.AsyncFingerprint;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortManager;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

import static com.fgtit.data.Constants.REALM_BASE_URL;

public class EnrollbeneficiaryActivity extends AppCompatActivity {


    private EditText firstname, lastname, enroldate, phone, comment, editTextfingerprint2, editTextfingerprint1, editText8, editText9, editTextLastName, editTextGravity, editTextParity, editTextId;
    private TextView text1, text2, text3;
    private ImageView imgPhoto, imgFinger1, imgFinger2;
    private EditText datefiled;

    private String selectedLga, selectedward;


    private byte[] jpgbytes = null;

    private byte[] model1 = new byte[512];
    private byte[] model2 = new byte[512];
    private boolean isenrol1 = false;
    private boolean isenrol2 = false;
    private int savecount = 0;
    private int mDeviceType = 0;

    private ImageView fpImage;
    private TextView tvFpStatus;
    private AsyncFingerprint vFingerprint;
    private Dialog fpDialog = null;
    private int iFinger = 0;
    private boolean bIsUpImage = true;
    private int count;
    private boolean bcheck = false;


    //Barcode
    private SerialPort mSerialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private EnrollbeneficiaryActivity.ReadThread mReadThread;
    private byte[] databuf = new byte[1024];
    private int datasize = 0;
    private int soundIda;
    private SoundPool soundPool;

    private Timer TimerBarcode = null;
    private TimerTask TaskBarcode = null;
    private Handler HandlerBarcode;

    //RFID
    private Timer TimerCard = null;
    private TimerTask TaskCard = null;
    private Handler HandlerCard;
    private int rfidtype = 0;

    //NFC
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;

    UserItem person = new UserItem();
    public String CardSN = "";

    private Spinner spin1, spin2, spinLga, spinWard;

    private boolean bIsCancel = false;
    private boolean bCapture = false;

    private Realm realm;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollbeneficiary);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstname = (EditText) findViewById(R.id.editTextFirstname);
        lastname = (EditText) findViewById(R.id.editTextLastName);
        editTextId = (EditText) findViewById(R.id.editTextId);
        phone = (EditText) findViewById(R.id.editTextPhone);
        comment = (EditText) findViewById(R.id.editTextComment);

        editTextfingerprint1 = (EditText) findViewById(R.id.editTextfingerprint1);
        editTextfingerprint2 = (EditText) findViewById(R.id.editTextFingerprint2);

//        editTextGravity = (EditText) findViewById(R.id.editTextGravity);
//        editTextParity = (EditText) findViewById(R.id.editTextParity);
//        editTextGravity = (EditText) findViewById(R.id.editTextGravity);
        ((EditText) findViewById(R.id.datefield)).setText(ExtApi.getStringDate());


        text1 = (TextView) findViewById(R.id.textView3);
//        text2 = (TextView) findViewById(R.id.textView4);
        text3 = (TextView) findViewById(R.id.textView5);

        imgPhoto = (ImageView) findViewById(R.id.imageView1);
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bCapture = true;
                Intent intent = new Intent(EnrollbeneficiaryActivity.this, CameraExActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", "1");
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        imgFinger1 = (ImageView) findViewById(R.id.imageView2);
        imgFinger1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                FPDialog(1);
            }
        });

        imgFinger2 = (ImageView) findViewById(R.id.imageView3);
        imgFinger2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FPDialog(2);
            }
        });

//        final ImageView imgBardcode1d = (ImageView) findViewById(R.id.imageView4);
//        imgBardcode1d.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                //	ToastUtil.showToastTop(EnrollActivity.this,"Please sweep Barcode...");
//                //	BarcodeOpen();
//            }
//        });
//
//        final ImageView imgBardcode2d = (ImageView) findViewById(R.id.imageView5);
//        imgBardcode2d.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                bCapture = true;
//                Intent intent = new Intent(EnrollActivity.this, CaptureActivity.class);
//                startActivityForResult(intent, 0);
//            }
//        });

//        final ImageView imgCard = (ImageView) findViewById(R.id.imageView6);
//        imgCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                ToastUtil.showToastTop(EnrollbeneficiaryActivity.this, "Please put the card...");
//                ReadCardSn();
//            }
//        });


//Spinner to generate LGA the actual list is in the string resource

        spinLga = (Spinner) findViewById(R.id.spinnerLga);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.lga_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLga.setAdapter(adapter3);
        spinLga.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {

                selectedLga = parent.getItemAtPosition(pos).toString();
                //person.ident=pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spinLga.setSelection(0);


//Spinner to generate Ward the actual list is in the string resource

        spinWard = (Spinner) findViewById(R.id.spinnerWard);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this, R.array.ward_array, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinWard.setAdapter(adapter4);
        spinWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
                //person.ident=pos;

                selectedward = parent.getItemAtPosition(pos).toString();
            }


            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spinWard.setSelection(0);


        soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
        soundIda = soundPool.load(this, R.raw.dong, 1);

        //Card
        InitReadCard();
        //Barcode
        //  openSerialPort();
        try {
//Todo: uncomment        vFingerprint = SerialPortManager.getInstance().getNewAsyncFingerprint();
            FPInit();
        } catch (Exception e) {
            // This will catch any exception, because they are all descended from Exception
            System.out.println("Error " + e.getMessage());

        }

    }

    private void workExit() {
        if (SerialPortManager.getInstance().isOpen()) {
            bIsCancel = true;
            SerialPortManager.getInstance().closeSerialPort();
            CloseReadCard();
            //	BarcodeClose();

            //if(fpDialog.isShowing()){
            //	fpDialog.cancel();
            //}

            this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1: {
                bCapture = false;
                Bundle bl = data.getExtras();
                String barcode = bl.getString("barcode");
//                editText9.setText(barcode);
            }
            break;
            case 2:
                break;
            case 3: {
                bCapture = false;
                Bundle bl = data.getExtras();
                String id = bl.getString("id");
                Toast.makeText(EnrollbeneficiaryActivity.this, "Pictures Finish", Toast.LENGTH_SHORT).show();
                byte[] photo = bl.getByteArray("photo");
                if (photo != null) {
                    try {
                        Matrix matrix = new Matrix();
                        Bitmap bm = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                        matrix.preRotate(-90);
                        Bitmap nbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);


                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        nbm.compress(Bitmap.CompressFormat.JPEG, 80, out);
                        jpgbytes = out.toByteArray();

                        Bitmap bitmap = BitmapFactory.decodeByteArray(jpgbytes, 0, jpgbytes.length);
                        imgPhoto.setImageBitmap(bitmap);
                    } catch (Exception e) {
                    }
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enroll, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Back");
            builder.setMessage("Data not saved, back?");
            //builder.setCancelable(false);
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    workExit();
                }
            });
            builder.create().show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_save) {

            Log.i("check1", "before check");


            realm.executeTransactionAsync(realm -> {
                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setFirstname(firstname.getText().toString());
                beneficiary.setLastname(lastname.getText().toString());
                beneficiary.setWard("my ward");
                beneficiary.setLga("my lga");
                beneficiary.setPhone("089887877");
                beneficiary.setComment("no comment");

                realm.insert(beneficiary);

                Log.i("check1", beneficiary.getFirstname());
            });

            RealmResults<Beneficiary> beneficiaries = setUpRealm();

//
//                    if (isenrol1) {
//                        person.template1 = ExtApi.BytesToBase64(model1, model1.length);
//                        person.bytes1 = new byte[model1.length];
//                        System.arraycopy(model1, 0, person.bytes1, 0, model1.length);
//                    }
//                    if (isenrol2) {
//                        person.template2 = ExtApi.BytesToBase64(model2, model2.length);
//                        person.bytes2 = new byte[model2.length];
//                        System.arraycopy(model2, 0, person.bytes2, 0, model2.length);
//                    }
//                    //if(jpgbytes!=null)
//                    //	person.photo=ExtApi.BytesToBase64(jpgbytes,jpgbytes.length);
//                    if (CardSN.length() > 4)
//                        person.cardsn = (CardSN);
//                    else
//                        person.cardsn = ("null");
//
//                    GlobalData.getInstance().userList.add(person);
//                    //GlobalData.getInstance().SaveUsersList();
//                    GlobalData.getInstance().SaveUserByID(person, jpgbytes);


            Toast.makeText(EnrollbeneficiaryActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
//                    CloseReadCard();
// Todo: uncomment                   SerialPortManager.getInstance().closeSerialPort();
//            finish();

        }

        return super.onOptionsItemSelected(item);
}


    private boolean CheckInputData(int type) {
        int len = lastname.getText().toString().length();
        if (len <= 0) {
            Toast.makeText(EnrollbeneficiaryActivity.this, "ID cannot be null", Toast.LENGTH_SHORT).show();
            return false;
        }
        len = firstname.getText().toString().length();
        if (len <= 0) {
            Toast.makeText(EnrollbeneficiaryActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return false;
        }
		/*
		if(mRefSize1<=0){
			Toast.makeText(EnrollActivity.this, "Please Input Template One", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(mRefSize2<=0){
			Toast.makeText(EnrollActivity.this, "Please Input Template Two", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!iscap){
			Toast.makeText(EnrollActivity.this, "Please Take Photo", Toast.LENGTH_SHORT).show();
			return false;
		}
		*/
        if (type == 1) {
            if (GlobalData.getInstance().IsHaveUserItem(editTextId.getText().toString())) {
                Toast.makeText(EnrollbeneficiaryActivity.this, "ID Exists", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    //?????
    private void FPDialog(int i) {
        iFinger = i;
        AlertDialog.Builder builder = new AlertDialog.Builder(EnrollbeneficiaryActivity.this);
        builder.setTitle("Register fingerprint");
        final LayoutInflater inflater = LayoutInflater.from(EnrollbeneficiaryActivity.this);
        View vl = inflater.inflate(R.layout.dialog_enrolfinger, null);
        fpImage = (ImageView) vl.findViewById(R.id.imageView1);
        tvFpStatus = (TextView) vl.findViewById(R.id.textview1);
        builder.setView(vl);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //SerialPortManager.getInstance().closeSerialPort();
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //SerialPortManager.getInstance().closeSerialPort();
                dialog.dismiss();
            }
        });

        fpDialog = builder.create();
        fpDialog.setCanceledOnTouchOutside(false);
        fpDialog.show();

        FPProcess();
    }

    private void FPInit() {
        vFingerprint.setOnGetImageListener(new AsyncFingerprint.OnGetImageListener() {
            @Override
            public void onGetImageSuccess() {
                if (!bIsCancel) {
                    if (bcheck) {
                        vFingerprint.FP_GetImage();
                    } else {
                        if (bIsUpImage) {
                            vFingerprint.FP_UpImage();
                            tvFpStatus.setText(getString(R.string.txt_fpdisplay));
                        } else {
                            tvFpStatus.setText(getString(R.string.txt_fpprocess));
                            vFingerprint.FP_GenChar(count);
                        }
                    }
                }
            }

            @Override
            public void onGetImageFail() {
                if (!bIsCancel) {
                    if (bcheck) {
                        bcheck = false;
                        tvFpStatus.setText(getString(R.string.txt_fpplace));
                        vFingerprint.FP_GetImage();
                        count++;
                    } else {
                        vFingerprint.FP_GetImage();
                    }
                }
            }
        });

        vFingerprint.setOnUpImageListener(new AsyncFingerprint.OnUpImageListener() {
            @Override
            public void onUpImageSuccess(byte[] data) {
                Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                fpImage.setImageBitmap(image);
                //fpImage.setBackgroundDrawable(new BitmapDrawable(image));
                vFingerprint.FP_GenChar(count);
                tvFpStatus.setText(getString(R.string.txt_fpprocess));
            }

            @Override
            public void onUpImageFail() {
            }
        });

        vFingerprint.setOnGenCharListener(new AsyncFingerprint.OnGenCharListener() {
            @Override
            public void onGenCharSuccess(int bufferId) {
                if (bufferId == 1) {
                    bcheck = true;
                    tvFpStatus.setText(getString(R.string.txt_fplift));
                    vFingerprint.FP_GetImage();
                } else if (bufferId == 2) {
                    vFingerprint.FP_RegModel();
                }
            }

            @Override
            public void onGenCharFail() {
                tvFpStatus.setText(getString(R.string.txt_fpfail));
            }
        });

        vFingerprint.setOnRegModelListener(new AsyncFingerprint.OnRegModelListener() {

            @Override
            public void onRegModelSuccess() {
                vFingerprint.FP_UpChar();
                //tvFpStatus.setText(getString(R.string.txt_fpenrolok));
            }

            @Override
            public void onRegModelFail() {
                tvFpStatus.setText(getString(R.string.txt_fpenrolfail));
            }
        });

        vFingerprint.setOnUpCharListener(new AsyncFingerprint.OnUpCharListener() {

            @Override
            public void onUpCharSuccess(byte[] model) {

                for (int i = 0; i < GlobalData.getInstance().userList.size(); i++) {
                    if (GlobalData.getInstance().userList.get(i).bytes1 != null) {
                        if (FPMatch.getInstance().MatchTemplate(model, GlobalData.getInstance().userList.get(i).bytes1) > 60) {
                            tvFpStatus.setText(getString(R.string.txt_fpduplicate));
                            return;
                        }
                    }
                    if (GlobalData.getInstance().userList.get(i).bytes2 != null) {
                        if (FPMatch.getInstance().MatchTemplate(model, GlobalData.getInstance().userList.get(i).bytes2) > 60) {
                            tvFpStatus.setText(getString(R.string.txt_fpduplicate));
                            return;
                        }
                    }
                }

                if (iFinger == 1) {
                    editTextfingerprint1.setText(getString(R.string.txt_fpenrolok));
                    System.arraycopy(model, 0, EnrollbeneficiaryActivity.this.model1, 0, 512);
                    isenrol1 = true;
                } else {
                    editTextfingerprint2.setText(getString(R.string.txt_fpenrolok));
                    System.arraycopy(model, 0, EnrollbeneficiaryActivity.this.model2, 0, 512);
                    isenrol2 = true;
                }
                tvFpStatus.setText(getString(R.string.txt_fpenrolok));
                fpDialog.cancel();
            }

            @Override
            public void onUpCharFail() {
                tvFpStatus.setText(getString(R.string.txt_fpenrolfail));
            }
        });

    }

    private void FPProcess() {
        count = 1;
        tvFpStatus.setText(getString(R.string.txt_fpplace));
        try {
            Thread.currentThread();
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vFingerprint.FP_GetImage();
    }

    public void BarcodeOpen() {
        if (mDeviceType == 0) {
            MtGpio mt = new MtGpio();
            mt.BCPowerSwitch(true);
            mt.BCReadSwitch(true);
            try {
                Thread.currentThread();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            datasize = 0;
            mt.BCReadSwitch(false);
        } else {
            byte[] cmd = new byte[2];
            cmd[0] = (0x1b);
            cmd[1] = (0x31);
            try {
                mOutputStream.write(cmd);
            } catch (IOException e) {
            }
        }
    }

    public void BarcodeClose() {
        if (mReadThread != null)
            mReadThread.interrupt();
        closeSerialPort();
        mSerialPort = null;
        if (mDeviceType == 0) {
            MtGpio mt = new MtGpio();
            mt.BCReadSwitch(true);
            mt.BCPowerSwitch(false);
        } else {

        }
    }

    public void openSerialPort() {
        try {
            mSerialPort = getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            /* Create a receiving thread */
            mReadThread = new EnrollbeneficiaryActivity.ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
        } catch (IOException e) {
        } catch (InvalidParameterException e) {
        }
    }

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            String path = "/dev/ttyMT1";
            int baudrate = 9600;    //1D
            //	int baudrate = 115200;	//2D
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }
            mSerialPort = new SerialPort();
            if (mSerialPort.getmodel().equals("FP07")) {
                path = "/dev/ttyMT2";
                mDeviceType = 1;
                baudrate = 9600;
            } else {
                path = "/dev/ttyMT1";
                mDeviceType = 0;
            }
            mSerialPort.OpenDevice(new File(path), baudrate, 0, SerialPort.DEVTYPE_UART);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()/*true*/) {
                int size;
                try {
                    byte[] buffer = new byte[256];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EnrollbeneficiaryActivity.this, "Read barcodes fail", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                System.arraycopy(buffer, 0, databuf, datasize, size);
                datasize = datasize + size;
                if (TimerBarcode == null) {
                    TimerBarcodeStart();
                }
            }
        });
    }

    public void TimerBarcodeStart() {
        TimerBarcode = new Timer();
        HandlerBarcode = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TimerBarcodeStop();
                if (datasize > 0) {
                    byte tp[] = new byte[datasize];
                    System.arraycopy(databuf, 0, tp, 0, datasize);
                    editText8.setText(new String(tp));
                    soundPool.play(soundIda, 1.0f, 0.5f, 1, 0, 1.0f);
                    datasize = 0;
                }
                super.handleMessage(msg);
            }
        };
        TaskBarcode = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                HandlerBarcode.sendMessage(message);
            }
        };
        TimerBarcode.schedule(TaskBarcode, 1000, 1000);
    }

    public void TimerBarcodeStop() {
        if (TimerBarcode != null) {
            TimerBarcode.cancel();
            TimerBarcode = null;
            TaskBarcode.cancel();
            TaskBarcode = null;
        }
    }

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

    // THIS SECTION IS FOR NFC

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private RealmResults<Beneficiary> setUpRealm() {
        SyncConfiguration configuration = SyncUser.current()
                .createConfiguration(REALM_BASE_URL + "/cct")
                .build();
        realm = Realm.getInstance(configuration);

        return realm
                .where(Beneficiary.class)
                .sort("firstname", Sort.DESCENDING)
                .findAllAsync();
    }

    private void processIntent(Intent intent) {
        byte[] sn = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String cardstr =/*Integer.toString(count)+":"+*/
                Integer.toHexString(sn[0] & 0xFF).toUpperCase() +
                        Integer.toHexString(sn[1] & 0xFF).toUpperCase() +
                        Integer.toHexString(sn[2] & 0xFF).toUpperCase() +
                        Integer.toHexString(sn[3] & 0xFF).toUpperCase();

        for (int i = 0; i < GlobalData.getInstance().userList.size(); i++) {
            if (GlobalData.getInstance().userList.get(i).cardsn.indexOf(cardstr) >= 0) {
                Toast.makeText(EnrollbeneficiaryActivity.this, "Failed,Duplicate registration!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
//        editText10.setText(cardstr);
        CardSN = cardstr;
        //soundPool.play(soundIda, 1.0f, 0.5f, 1, 0, 1.0f);
    }

    @Override
    public void onPause() {
        if (ActivityList.getInstance().IsUseNFC) {
            if (nfcAdapter != null)
                nfcAdapter.disableForegroundDispatch(this);
        }
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            if (!bCapture) {
                workExit();
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityList.getInstance().IsUseNFC) {
            if (nfcAdapter != null)
                nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, null);
        }
    }
}