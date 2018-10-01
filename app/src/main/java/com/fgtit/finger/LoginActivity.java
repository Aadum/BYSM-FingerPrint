package com.fgtit.finger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fgtit.app.ActivityList;
import com.fgtit.data.GlobalData;
import com.fgtit.utils.ToastUtil;

public class LoginActivity extends AppCompatActivity {

	private EditText editText1,editText2;
	private long exitTime = 0;
	private PowerManager.WakeLock wakeLock;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logon);

		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());


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

        
		Button btn01=(Button)findViewById(R.id.button1);
		btn01.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				if(editText1.getText().toString().equals("admin")){
					if(editText2.getText().toString().equals(ActivityList.getInstance().PassWord)){
						ToastUtil.showToastTop(LoginActivity.this,"Logon ...");
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						startActivityForResult(intent,0);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}else{
						ToastUtil.showToastTop(LoginActivity.this, "Wrong password!!");
					}
				}else{
					ToastUtil.showToastTop(LoginActivity.this, "Administrator error");
				}
			}
		});
		
		editText1=(EditText)findViewById(R.id.editText1);
		editText2=(EditText)findViewById(R.id.editText2);
		editText1.setText("admin");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
		case R.id.action_return:
		case android.R.id.home:
			this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
	    	this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	    	return true;  
	    }
	    return super.onKeyDown(keyCode, event);  
	} 
	
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (resultCode){
		case 0:{
				this.setResult(0);
				this.finish();
			}
			break;
		case 1:{
				this.setResult(1);
				this.finish();
			}
			break;
		case 2:
			break;
		default:
			break;
		}
	}
    
}
