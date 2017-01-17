package charlyn23.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;


public class MainActivity extends AppCompatActivity implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SensorEventListener    {

    Button profile;
    Button leaderboard;
    Button accomplishments;
    Button newSession;
    TextView stepsTextView;
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    long steps;
    public static int stepsSaved;
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    private static PendingResult<Status> pendingResult;
    SensorRequest sensorRequest;
    SensorEventListener listener;
    public int stepsTaken;
    public boolean authenticated;
    public static SharedPreferences stepsSharedPreferences;
    public static SharedPreferences.Editor userEditor;
    public static SharedPreferences.Editor stepsEditor;
    public static boolean choseGoogle;
    public static SharedPreferences userDataSharedPrefs;
    public boolean sessionInProgess;
    private long lastTime = 0;
    private float lastX, lastY, lastZ;
    private static final int THRESHOLD = 600; //used to see whether a shake gesture has been detected or not.


    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(listener, mAccelerometer);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         final boolean createdAccount = MakeAccountActivity.createdAccount;

        profile = (Button)findViewById(R.id.profile);
        leaderboard = (Button)findViewById(R.id.leaderboard);
        accomplishments = (Button)findViewById(R.id.accomplishments);
        newSession = (Button)findViewById(R.id.sneaker);
        stepsTextView = (TextView)findViewById(R.id.stepsTextView);


        //Make SensorManager and use it to get accelerometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Shared Preferences and editors
        userDataSharedPrefs  = getApplicationContext().getSharedPreferences("userDataSharedPrefs", Context.MODE_PRIVATE);
        userEditor = userDataSharedPrefs.edit();
        stepsSharedPreferences = getApplicationContext().getSharedPreferences("stepsSharedPrefs", MODE_PRIVATE);
        stepsEditor = stepsSharedPreferences.edit();


        if (stepsSharedPreferences.contains("newStep") ) {
            stepsTextView.setText(String.valueOf(stepsSharedPreferences.getInt("newStep", 0)));
            ///TODO: Straighten out steps variables (steps and stepsSaved).
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                if (!userLoggedIn() && !choseGoogle){
                    ProfileAlert profileAlert = new ProfileAlert();
                    profileAlert.show(getFragmentManager(), "profile");
                }
                else if (userLoggedIn()){
                    Intent intent = new Intent(getBaseContext(), MakeAccountActivity.class);
                    startActivity(intent);
                }
                else if (choseGoogle){
                    Toast.makeText(getBaseContext(), "User chose Google", Toast.LENGTH_SHORT).show();
                }


            }
        });

        newSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewSessionOnClick();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                if (!userLoggedIn() && !choseGoogle){
                    ProfileAlert profileAlert = new ProfileAlert();
                    profileAlert.show(getFragmentManager(), "profile");
                }
                else if (userLoggedIn()){
                    Intent intent = new Intent(getBaseContext(), MakeAccountActivity.class);
                    startActivity(intent);
                }
                else if (choseGoogle){
                    Toast.makeText(getBaseContext(), "User chose Google", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
//        final boolean createdAccount = MakeAccountActivity.createdAccount;
//        Toast.makeText(getBaseContext(), "onResume: " + createdAccount, Toast.LENGTH_SHORT).show();
//            profile.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ProfileAlert profileAlert = new ProfileAlert();
//                    profileAlert.show(getFragmentManager(), "profile");
//                    if(createdAccount){
//                        Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
//                        startActivity(intent);
//                    }
//                    else if (choseGoogle){
//                        Toast.makeText(getBaseContext(), "User chose Google", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Intent intent = new Intent(getBaseContext(), MakeAccountActivity.class);
//                        startActivity(intent);
//                    }
//                }
//            });
//
//
//        if (createdAccount && stepsSharedPreferences.contains("steps")) {
//            stepsTextView.setText(String.valueOf(stepsSharedPreferences.getInt("steps", 0)));
//            Toast.makeText(getBaseContext(), "Account was created", Toast.LENGTH_SHORT).show();
//
//        }


//    }


    public void setNewSessionOnClick(){
        newSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register listener, start new session, update UI
                mSensorManager.registerListener(listener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                if (stepsSharedPreferences.contains("steps")) {
                    stepsTextView.setText(String.valueOf(stepsSharedPreferences.getInt("steps", 0)));
                }


            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            Log.i("MainActivity", "buildClient");

        }

    }

    public void buildClient(){
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.GOALS_API)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.PROFILE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i("buildCLient", "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Subscribe to some data sources!
//                                subscribe();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.w("onConnectionSuspended", "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.w("onConnectionSuspended", "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        })
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.w("onConnectionFailed", "Google Play services connection failed. Cause: " +
                                result.toString());

                        Snackbar.make(
                                MainActivity.this.findViewById(R.id.activity_profile),
                                "Exception while connecting to Google Play services: " +
                                        result.getErrorMessage(),
                                Snackbar.LENGTH_INDEFINITE).show();
                    }
                })

                .addOnConnectionFailedListener(this)
                .build();
    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Invoke APIs here
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if( !authInProgress ) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult( MainActivity.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {

            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
        }
    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {
        for( Field field : dataPoint.getDataType().getFields()){
            Value val = dataPoint.getValue(field);
            Log.i("onDataPoint", val.toString());
            stepsTaken+= val.asInt();
            Log.i("stepsTaken" , String.valueOf(stepsTaken));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Pressing Google on the Profile alert begins GoogleFit user flow
    public void doPositiveClick(){
        buildClient();
        Log.i("FragmentAlertDialog", "Positive click!");
        Fitness.SensorsApi.add( mApiClient, sensorRequest, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e( "GoogleFit", "SensorApi successfully added" );
                        }
                    }
                });
    }

    //Avg step length is 70cm for women, 78cm for men
    public float getDistanceRunFemale(long steps){
        float distance = (float)(steps*70)/(float)100000;
        return distance;
    }

    public float getDistanceRunMale(long steps){
        float distance = (float)(steps*78)/(float)100000;
        return distance;
    }

    //Part of this logic is from https://www.sitepoint.com/using-android-sensors-application/ author Paula Green
    //The device I was using did not have step counter sensor, so I used accelerometer. Stats seem to be more
    //accurate when using Google Fit.
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //During testing, 2.5 seemed to be a reasonable threshold to determine a step.
            float threshold = (float) 2.5;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            long currentTime = System.currentTimeMillis();
            //500milsec = 0.5 sec
            if ((currentTime - lastTime) > 500) {
                long diffTime = (currentTime - lastTime);
                lastTime = currentTime;
                float deltaXYZ = Math.abs(x + y + z - lastX - lastY - lastZ);
                float speed = deltaXYZ / diffTime * 2000;
                double mph = speed * 2.23694;
                if (speed > THRESHOLD) {
                }
                lastX = x;
                lastY = y;
                lastZ = z;
                //Checks if there are steps saved from previous session to keep step count up-to-date with each new session
                if (stepsSharedPreferences.getInt("newSteps", 0) > 0) {
                    steps = stepsSharedPreferences.getInt("newSteps", 0);
                }
                if (deltaXYZ > threshold) {
                    steps += 1;
                    stepsEditor.putInt("newStep", (int) steps);
                    stepsEditor.commit();
                    System.out.println(String.valueOf(steps));
                    stepsTextView.setText(String.valueOf(steps));

                }

            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public boolean userLoggedIn() {
        return userDataSharedPrefs.contains("currentUser");
    }
}

/**If user chooses to create their own FitnessApp account, all data will be saved locally. If they use GoogleFit,
 * they can take advantage of added features, like leaderboard.
 */
