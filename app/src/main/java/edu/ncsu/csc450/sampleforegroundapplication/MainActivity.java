package edu.ncsu.csc450.sampleforegroundapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.ncsu.csc450.contextmiddleware.IContextCallback;
import edu.ncsu.csc450.contextmiddleware.IContextInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    protected Button bLocation, bNewYork, bJack;
    protected TextView mSum, locationText, nycText, jackText;
    protected IContextInterface contextService;
    protected ServiceConnection addServiceConnection, contextServiceConnection;
    private boolean mContextIsBound;
    private IContextCallback.Stub mContextCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSum = (TextView) findViewById(R.id.sum);
        locationText = (TextView) findViewById(R.id.locationText);
        nycText = (TextView) findViewById(R.id.newYorkText);
        jackText = (TextView) findViewById(R.id.jackText);
        bLocation = (Button) findViewById(R.id.locButton);
        bNewYork = (Button) findViewById(R.id.nyButton);
        bJack = (Button) findViewById(R.id.jackStatusButton);

        initConnection();

        bLocation.setOnClickListener(this);
        bNewYork.setOnClickListener(this);
        bJack.setOnClickListener(this);
    }

    /*
     * Initialize connection with the service. Implement all Callback methods
     */
    void initConnection() {

        mContextCallback = new IContextCallback.Stub() {

            @Override
            public void locationUpdateCallback(final String latitude, final String longitude) throws RemoteException {
                Log.d("Location Update",latitude+", "+longitude);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locationText.setText("Location: "+ latitude + ", " + longitude);
                    }
                });
            }

            @Override
            public void batteryStatusCallback(final boolean batteryStatus) throws RemoteException {
                Log.d("Battery Update"," ");
            }

          /*  @Override
            public void batteryStatusCallback(final boolean batteryStatus) throws RemoteException {
                Log.d("Battery Update", String.valueOf(batteryStatus));
                /*
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        jackText.setText("Battery: " + String.valueOf(batteryStatus) );
                    }
                });
                */
            //}

        };


        contextServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                contextService = IContextInterface.Stub.asInterface(service);
                Toast.makeText(getApplicationContext(),
                        "Context Service Connected", Toast.LENGTH_SHORT)
                        .show();
                Log.d("IApp", "Binding is done - Context Service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub
                contextService = null;
                Toast.makeText(getApplicationContext(), "Service Disconnected",
                        Toast.LENGTH_SHORT).show();
                Log.d("IRemote", "Binding - Location Service disconnected");
            }
        };
        if (contextService == null) {
            Intent contextIntent = new Intent();
            contextIntent.setPackage("edu.ncsu.csc450.contextmiddleware");
            contextIntent.setAction("service.contextFinder");
            bindService(contextIntent, contextServiceConnection, Context.BIND_AUTO_CREATE);
            mContextIsBound = true;
            if(contextService==null)
                Log.d("locService","NULL");
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        if(addServiceConnection != null)
            unbindService(addServiceConnection);
        if(contextServiceConnection != null)
            unbindService(contextServiceConnection);
    };

    /*
     * Calls to interface methods.
     * The service has to be bound before any of the interface methods can be invoked.
     */
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Log.d("Context is bound",Boolean.toString(mContextIsBound));
        switch (v.getId()) {

            case R.id.locButton: {
                try {
                    if(mContextIsBound) {
                        // Async call to service function. Response handled in callback function locationUpdateCallback() in initConnection()
                        contextService.registerForLocationUpdates(mContextCallback);
                    }
                } catch (RemoteException e){
                    e.printStackTrace();
                }
            }
            break;
            case R.id.nyButton: {


            }
            break;
            case R.id.jackStatusButton: {
                try {
                    if(mContextIsBound) {
                        // Sync call to service funtion. Response handled immediately.
                        if(contextService.isJackPluggedIn())
                            jackText.setText("Headset Status: Plugged");
                        else
                            jackText.setText("Headset Status: Unplugged");
                    }
                } catch (RemoteException e){
                    e.printStackTrace();
                }
            }
            break;
        }
    }
}
