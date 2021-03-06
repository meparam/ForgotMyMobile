package com.forgotMyMobile.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.forgotMyMobile.helpers.PreferenceHelper;
import com.forgotMyMobile.listeners.SmsReceiver;
import com.mymobile.forgotmymobile.R;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
	public static final String STOP_AUTO_FWD = "stopAutoFwd";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		Log.d(TAG,"On create start");
        EditText passCodeView = (EditText) findViewById(R.id.passcode);
		passCodeView.setText(PreferenceHelper.getPassCode(getApplicationContext()));

        CheckBox autoFwdCheckBox = (CheckBox) findViewById(R.id.autoForward);
		autoFwdCheckBox.setChecked(PreferenceHelper.isAutoForwardEnabled(getApplicationContext()));
		
		Button saveButton = (Button) findViewById(R.id.savePreferences);
		saveButton.setOnClickListener(setSaveButtonListener());
		Log.d(TAG,"On create completed");
	}


    private OnClickListener setSaveButtonListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
			EditText passCodeView = (EditText) MainActivity.this.findViewById(R.id.passcode);
			String passcode = passCodeView.getText().toString();
			
			CheckBox autoFwdCheckBox = (CheckBox) findViewById(R.id.autoForward);
			boolean needAutoFwd = autoFwdCheckBox.isChecked();
			
				if( passcode != null && !passcode.trim().isEmpty()) {
                    PreferenceHelper.saveSettings(passcode, needAutoFwd, MainActivity.this);
					showSuccessMessage();
					Log.i("MainActivity", "Preferences saved successfully!");
				} else {
					Toast.makeText(MainActivity.this, "Please set a valid Pass code.", Toast.LENGTH_LONG).show();
				}
			}
			
		};
	}

    protected void showSuccessMessage() {
		final String passCode = PreferenceHelper.getPassCode(MainActivity.this);
		
		new AlertDialog.Builder(this)
	    .setTitle("All set! you can relax now..")
	    .setMessage("Your pass code has been saved.\n" +
	    		"To retrieve unread sms' and missed calls, " +
	    		"just sms: " + Html.fromHtml("<b>"+passCode+"</b>") + " from any mobile.")
	    .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            dialog.cancel();
	        }
	     })	    
	     .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            MainActivity.this.finish();
		        }
		     })
	    .setIcon(R.drawable.relax_chair)
	     .show();
	}

	@Override
	public void onStart(){
		super.onStart();
		startService(new Intent(this,SmsReceiver.class));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Intent i = getIntent();
		if (i.hasExtra(STOP_AUTO_FWD)){
			i.removeExtra(STOP_AUTO_FWD);
			PreferenceHelper.setAutoFwd(getApplicationContext(), false);
			new AlertDialog.Builder(this)
            .setTitle("Auto Forward Stopped!")
            .setMessage("Auto forward of missed call details and new messages has been STOPPED!.\n" +
            		"Please change your passcode for security reasons.")
            .setCancelable(true)
            .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .setIcon(R.drawable.alert)
            .show();
		}
	}
	
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.info:
                showInfo();
                return true;
            case R.id.help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showHelp() {
        new AlertDialog.Builder(this)
                .setTitle("Help")
                .setMessage("What is this app? \n" +
                        "This app is like a helper, if you ever forget your mobile, " +
                                "and want to find out who called you or messaged you, " +
                                "simply send an sms with your pass code and app will respond with the details. \n\n"+

                        "How to use this app?" + "\n" +
                "Once installed, set the pass code and save, the app will be listening to incoming messages. " +
                        "If it receives a message with the set pass code, It will respond with the details.  \n")
                .setCancelable(true)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.help)
                .show();

    }

    private void showInfo() {
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG,"VersionName not set");
        }

        new AlertDialog.Builder(this)
                .setTitle("About Us")
                .setMessage("Current Version: " + versionName + "\n\n" +
                        "Contact: "+getResources().getString(R.string.email_id))
                .setCancelable(true)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.info)
                .show();

    }

}
