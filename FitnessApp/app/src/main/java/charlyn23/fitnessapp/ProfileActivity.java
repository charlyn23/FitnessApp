package charlyn23.fitnessapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;


public class ProfileActivity extends AppCompatActivity {

    SensorManager sManager;
    Sensor stepSensor;
    long steps;
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    private static PendingResult<Status> pendingResult;
    SensorRequest sensorRequest;
    OnDataPointListener listener;
    public int stepsTaken;
    public static SharedPreferences stepsSharedPreferences;
    public static SharedPreferences.Editor editor;
    public static boolean choseGoogle;
    public static SharedPreferences userDataSharedPrefs;
    SharedPreferences.Editor userDataEditor;

    public PendingResult<Status> getPendingResult() {


        return pendingResult;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        choseGoogle = false;
        userDataSharedPrefs  = getApplicationContext().getSharedPreferences("userDataSharedPrefs", Context.MODE_PRIVATE);
        if (MakeAccountActivity.createdAccount = true) {

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);

    }


    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.RecordingApi.subscribe(mApiClient, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i("subscribe()", "Existing subscription for activity detected.");
                            } else {
                                Log.i("subscribe()", "Successfully subscribed!");
                            }
                        } else {
                            Log.w("subscribe()", "There was a problem subscribing.");
                        }
                    }
                });
    }

}
//TODO: fetch data from fit profile and populate app's profile activity

