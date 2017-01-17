package charlyn23.fitnessapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by charlynbuchanan on 12/30/16.
 */

public class ProfileAlert extends DialogFragment {
    public AlertDialog.Builder builder;
    public static boolean choseGoogle;

    public static ProfileAlert newInstance(boolean choseGoogle) {
        ProfileAlert frag = new ProfileAlert();
        Bundle args = new Bundle();
        args.putBoolean("choseGoogle", choseGoogle);
        frag.setArguments(args);
        return frag;
    }



    final static int GOOGLE_REQUEST_CODE = 1;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity mainActivity = new MainActivity();
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.your_fitness)
                .setMessage(R.string.profile_alert_message)
                .setPositiveButton(R.string.create_profile, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent goToProfileIntent = new Intent(getActivity(), MakeAccountActivity.class);
                        startActivity(goToProfileIntent);
                    }
                })
                .setNegativeButton(R.string.google_signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choseGoogle = true;
                        ((MainActivity)getActivity()).doPositiveClick();
                        Log.i("Google", "selected, choseGoogle = " + choseGoogle);


                    }
                });
        return builder.create();
    }
}
