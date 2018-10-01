package com.fgtit.finger;

//import android.content.DialogInterface;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fgtit.data.GlobalData;
import com.fgtit.data.SQLiteDatabaseHandler;
import com.fgtit.data.Transaction;
import com.fgtit.data.PaymentCursorAdapter;
import com.fgtit.data.UserItem;
import com.fgtit.utils.ExtApi;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private ListView listView1,listView2;
    private List<Map<String, Object>> mData1,mData2, mData3;
    private EditText editTextAmount;
    private Integer amount;
    private Spinner spinnerPregnancyStatus;
    private String pregnancy_status;
    private String payment_date;
    private ArrayAdapter adapter2;


    private ImageView photoImage;
    private SQLiteDatabaseHandler db;

    private int 	pos=0;
    private UserItem person=null;
    private Transaction mytransactions = null;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

//Enable the back button
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new SQLiteDatabaseHandler(this);


        editTextAmount = (EditText) findViewById(R.id.editTextAmount);

        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        pos = bundle.getInt("pos");
        //int pos=this.getIntent().getIntExtra("pos",0);
        if(pos< GlobalData.getInstance().userList.size())
            person=GlobalData.getInstance().userList.get(pos);

        photoImage=(ImageView)findViewById(R.id.imageView1);
        if(person.photo.length()>1000){
            photoImage.setImageBitmap(Bytes2Bimap(ExtApi.Base64ToBytes(person.photo)));
        }else{
            byte[] photo=GlobalData.getInstance().LoadPhotoByID(person.id);
            if(photo!=null)
                photoImage.setImageBitmap(Bytes2Bimap(photo));
            else
                photoImage.setImageBitmap(ExtApi.LoadBitmap(getResources(),R.drawable.guest));
        }


        listView1=(ListView) findViewById(R.id.list);
        SimpleAdapter adapter1 = new SimpleAdapter(this,getData1(),R.layout.listview_empitem,
                new String[]{"title","info" },
                new int[]{R.id.title,R.id.info });
        listView1.setAdapter(adapter1);

//        listView2=(ListView) findViewById(R.id.listView2);
//        SimpleAdapter adapter2 = new SimpleAdapter(this,getData2(),R.layout.listview_empitem,
//                new String[]{"title","info" },
//                new int[]{R.id.title,R.id.info});
//        listView2.setAdapter(adapter2);




//
//        new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(), "You've changed your mind to delete all records", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        builder.show();




        final Button payButton = (Button) findViewById(R.id.buttonPay);
        payButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                new AlertDialog.Builder(PaymentActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure ?")
                        .setMessage("Are you sure you want to make this Payment")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                amount =  Integer.parseInt(editTextAmount.getText().toString());
                                spinnerPregnancyStatus = (Spinner) findViewById(R.id.spinnerPregnancyStatus);

                                pregnancy_status = spinnerPregnancyStatus.getSelectedItem().toString();


                                payment_date = ExtApi.getStringDate();
                                // create some players
                                Transaction transaction1  = new Transaction(1, Integer.parseInt(person.id), amount, "Loking good", pregnancy_status, payment_date);

                                // add them
                                db.addTransaction(transaction1);
                                Toast.makeText(getApplicationContext(), "Payment has been made", Toast.LENGTH_SHORT).show();
                                adapter2.notifyDataSetChanged();
                                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);// New activity
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish(); // Call once you redirect to another activity
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


            }
        });




        // list all Transactions belonging to specified user
        List<Transaction> mytransactions = db.allUserTransaction(Integer.parseInt(person.id));

        if (mytransactions != null) {

            String[] itemsNames = new String[mytransactions.size()];

            for (int i = 0; i < mytransactions.size(); i++) {
                itemsNames[i] = mytransactions.get(i).toString();

                Log.i("userid-" + i, String.valueOf(itemsNames[i]));
            }

            // display like string instances
            ListView list = (ListView) findViewById(R.id.listView1);

            adapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames);

            list.setAdapter(adapter2);

//            list.setAdapter(new ArrayAdapter<String>(this,
//                    android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames));

////             display like string instances
//            ListView list = (ListView) findViewById(R.id.listView1);
//            PaymentCursorAdapter todoAdapter = new PaymentCursorAdapter(this, db.allUsersTransaction(Integer.parseInt(person.id)));
//            list.setAdapter(todoAdapter);


        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.employee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_clear:
                db.deleteTransactionData();
                this.finish();
                return true;
            case R.id.action_delete:
                if(pos>=0){
                    GlobalData.getInstance().DeleteUserByID(person.id);
                    GlobalData.getInstance().userList.remove(pos);
                    //GlobalData.getInstance().SaveUsersList();
                    Intent resultIntent = new Intent();
                    //resultIntent.putExtra("reload", 1);
                    setResult(0, resultIntent);
                    this.finish();
                }

                //this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Map<String, Object>> getData1() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();

        if(person!=null){
            map = new HashMap<String, Object>();
            map.put("title", getString(R.string.txt_username)+":");
            map.put("info", person.name);
            list.add(map);

            map = new HashMap<String, Object>();
            map.put("title", "Last name:");
            map.put("info", person.lastname);
            list.add(map);



        }

        mData1=list;
        return list;
    }

    private List<Map<String, Object>> getData2() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if(mytransactions!=null){

            Map<String, Object> map = new HashMap<String, Object>();
			/*
			map = new HashMap<String, Object>();
			map.put("title", getString(R.string.txt_statu)+":");
			map.put("info", "Normal");
			list.add(map);
			*/
            map = new HashMap<String, Object>();
            map.put("title", "Amount");
            map.put("info", mytransactions.getAmount());
            list.add(map);

            map = new HashMap<String, Object>();
            map.put("title",  "Date");
            map.put("info", mytransactions.getPaymentDate());
            list.add(map);

            map = new HashMap<String, Object>();
            map.put("title",  "Ward:");
            map.put("info", person.ward);
            list.add(map);


            map = new HashMap<String, Object>();
            map.put("title", getString(R.string.txt_gender)+":");
            switch(person.gender){
                case 0:	map.put("info", getString(R.string.txt_male));	break;
                case 1:	map.put("info", getString(R.string.txt_female));	break;
            }
            list.add(map);

            map = new HashMap<String, Object>();
            map.put("title", getString(R.string.txt_phone)+":");
            map.put("info", person.phone);
            list.add(map);

            map = new HashMap<String, Object>();
            map.put("title", getString(R.string.txt_remark)+":");
            map.put("info", "");
            list.add(map);

        }
        mData2=list;
        return list;
    }




    private Bitmap Bytes2Bimap(byte[] b){
        if(b.length!=0){
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        else {
            return null;
        }
    }






}
