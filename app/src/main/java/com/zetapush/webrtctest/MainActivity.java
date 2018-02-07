package com.zetapush.webrtctest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zetapush.library.SmartClient;
import com.zetapush.library.ZetaPushService;
import com.zetapush.webrtc.ZetapushConnectActivity;

import org.appspot.apprtc.SettingsActivity;

public class MainActivity extends Activity {

    // ZetaPush Service
    private SmartClient client;
    private ZetaPushConnectionReceiver      zetaPushReceiver = new ZetaPushConnectionReceiver();

    private final String                    SANDBOX_ID       = "x4ekRxc5";
    private final String                    LOGIN            = "android";
    private final String                    PASSWORD         = "password";

    private Button btnLaunch;
    private Button btnSettings;
    private Button btnConnect;
    private Button btnDisconnect;

    private EditText etSandboxId;
    private EditText etLogin;
    private EditText etPassword;

    private Toast logToast;
    private static final String TAG = MainActivity.class.getSimpleName();

    // List of mandatory application permissions.
    private static final String[] MANDATORY_PERMISSIONS = {"android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO", "android.permission.INTERNET", "android.permission.CAMERA"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLaunch = (Button) findViewById(R.id.buttonLaunch);
        btnLaunch.setEnabled(false);
        btnSettings = (Button) findViewById(R.id.buttonSettings);
        btnConnect = (Button) findViewById(R.id.buttonConnect);
        btnDisconnect = (Button) findViewById(R.id.buttonDisconnect);
        btnDisconnect.setEnabled(false);

        etSandboxId = (EditText) findViewById(R.id.editTextSandboxId);
        etLogin = (EditText) findViewById(R.id.editTextLogin);
        etPassword = (EditText) findViewById(R.id.editTextPassword);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSettingsActivity();
            }
        });
        btnLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchConnectActivity();
            }
        });
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchConnection();
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
        checkPermissions();

        // Create client
        client = new SmartClient(this);

        // Get saved account from previous launch
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        etSandboxId.setText(sharedPref.getString(getString(R.string.saved_sandboxId), ""));
        etLogin.setText(sharedPref.getString(getString(R.string.saved_login), ""));
        etPassword.setText(sharedPref.getString(getString(R.string.saved_password), ""));

    }


    private void launchConnection() {

        if (etSandboxId.getText().toString().matches("") || etLogin.getText().toString().matches("") || etPassword.getText().toString().matches("")) {
            return;
        }

        // Save account for futur launch
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_sandboxId), etSandboxId.getText().toString());
        editor.putString(getString(R.string.saved_login), etLogin.getText().toString());
        editor.putString(getString(R.string.saved_password), etPassword.getText().toString());
        editor.commit();

        client.connect(etSandboxId.getText().toString(), etLogin.getText().toString(), etPassword.getText().toString());
    }

    private void disconnect(){
        client.disconnect();
    }

    /** Called when the user taps the Send button */
    public void launchConnectActivity() {
        // Connect ZetaPush Client
        //client.connect(SANDBOX_ID, LOGIN, PASSWORD);

        Intent intent = new Intent(this, ZetapushConnectActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Send button */
    public void launchSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void checkPermissions(){
        // Check for mandatory permissions.
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                //logAndToast("Permission " + permission + " is not granted");
                Log.d("Permission ", "Permission " + permission + " is not granted");
                //setResult(RESULT_CANCELED);
                //finish();
                //return;

                ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
            } else {
                Log.d("Permission ", "Permission " + permission + " is Granted");
            }
        }
    }

    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(zetaPushReceiver, new IntentFilter(ZetaPushService.FLAG_ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        unregisterReceiver(zetaPushReceiver);
        Log.d(TAG, "onPause after unregister");
    }

    @Override
    public void onDestroy(){
        client.release();
        super.onDestroy();
    }

    // BroadcastReceiver for the connection status
    private class ZetaPushConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getStringExtra(ZetaPushService.FLAG_STATE_CONNECTION)) {

                case ZetaPushService.FLAG_CONNECTION_ESTABLISHED:
                    Log.d("ConnectionReceiver", "Connection established");
                    btnLaunch.setEnabled(true);
                    btnConnect.setEnabled(false);
                    btnDisconnect.setEnabled(true);
                    break;
                case ZetaPushService.FLAG_CONNECTION_CLOSED:
                    Log.d("ConnectionReceiver", "Connection closed");
                    btnLaunch.setEnabled(false);
                    btnConnect.setEnabled(true);
                    btnDisconnect.setEnabled(false);
                    break;
            }
        }
    }

}
