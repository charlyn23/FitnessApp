package charlyn23.fitnessapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class NewSession extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);

//        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_ACTIVITY_SAMPLE)
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        if (status.isSuccess()) {
//                            if (status.getStatusCode()
//                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
//                                Log.i(TAG, "Existing subscription for activity detected.");
//                            } else {
//                                Log.i(TAG, "Successfully subscribed!");
//                            }
//                        } else {
//                            Log.i(TAG, "There was a problem subscribing.");
//                        }
//                    }
//                });
    }
}
