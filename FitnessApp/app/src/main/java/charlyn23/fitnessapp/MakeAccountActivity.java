package charlyn23.fitnessapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MakeAccountActivity extends AppCompatActivity {

    String username;
    String password;
    String derivedPassword;
    String confirmPassword;
    public  static  boolean createdAccount;
    public SharedPreferences userDataSharedPrefs;
    public SharedPreferences.Editor editor;
    public boolean femaleUser;
    public boolean maleUser;
    ArrayList<String> currentUserDataArrayList;
    public static SortedSet<String> currentUserSet;
    public static SortedSet<String> permanentUserSet;
    public static User currentUser;
    public static ArrayList<User> users;
    public static String userSex;
    public static boolean enteredPassword;
    public  static boolean passwordIsHidden;

    public EditText userNameEditText;
    Button saveButton;
    Button why;
    RadioGroup sexes;
    RadioButton female;
    RadioButton male;
    EditText passwordEditText;
    TextView passwordToggle;
    TextView confirmPasswordToggle;
    EditText confirmPasswordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_account);

        passwordIsHidden = true;
        userDataSharedPrefs  = getApplicationContext().getSharedPreferences("userDataSharedPrefs", Context.MODE_PRIVATE);
        editor = userDataSharedPrefs.edit();
        users = new ArrayList<>();

        saveButton = (Button)findViewById(R.id.save);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        passwordToggle = (TextView)findViewById(R.id.passwordToggle);
        why = (Button)findViewById(R.id.why);
        sexes = (RadioGroup)findViewById(R.id.sexes);
        female = (RadioButton)findViewById(R.id.female);
        male = (RadioButton)findViewById(R.id.male);


         if (passwordEditText.getText().length() == 0) {
             passwordToggle.setVisibility(View.GONE);
             enteredPassword = false;
         }
        if (confirmPasswordEditText.getText().length() == 0) {
            confirmPasswordToggle.setVisibility(View.GONE);
            enteredPassword = false;
        }
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordEditText.getText().length() > 0) {
                    passwordToggle.setVisibility(View.VISIBLE);
                    enteredPassword = true;
                    Log.i("onTextChanged" , "something happens");

                }
                else {
                    passwordToggle.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                password = String.valueOf(passwordEditText.getText());
                Log.i("afterTextChanged ", password);


            }
        });
        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = String.valueOf(passwordEditText.getText());
                if (!passwordIsHidden) {
                   hidePassword();
                }


                 else {
                    showPassword();

                }

            }
        });
        if (!userDataSharedPrefs.contains("currentUser")) {
            saveButton.setText("Save/Login");
        }

        why.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(MakeAccountActivity.this).create();
                alertDialog.setTitle("Why do we need this?");
                alertDialog.setMessage("This allows us to provide you with the most accurate data. It is optional and will default to female unless specified.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });


        //populate fields if an account was created
        /*This is a hack. SharedPreferences only takes primitives or Sets, not my custom User objects. In order to
        fetch a user name from sharedprefs, I saved both a currentUser and a permanentUser. currentUser will be eliminated
        when a user logs out but the permanentUser will persist. currentUser's dataset will provide each session's info during OnCreate.
        I didn't want to use an iterator just to fetch one element from the set, so I converted it to an ArrayList, but it didn't populate
         as expected. Set isn't guaranteed order. I ended up having to loop anyway and evaluate the value of each index.
         */
        if (userDataSharedPrefs.contains("currentUser")) {
            Set<String> currentUserDataSet =  userDataSharedPrefs.getStringSet("currentUser", null);
            currentUserDataArrayList = new ArrayList<>(currentUserDataSet);
            for (int i = 0; i < currentUserDataArrayList.size(); i++) {
                if (currentUserDataArrayList.get(i).equals("male") | currentUserDataArrayList.get(i).equals("female")) {
                    userSex = currentUserDataArrayList.get(i);
                }
                else if (userDataSharedPrefs.contains(currentUserDataArrayList.get(i))) {
                    username = currentUserDataArrayList.get(i);
                }
                else {
                    derivedPassword = currentUserDataArrayList.get(i);

                }

            }
            userNameEditText.setText(username);
            passwordEditText.setText(derivedPassword);
            confirmPasswordEditText.setText(derivedPassword);
//            confirmPasswordTextInput.setText(derivedPassword);
            if (userSex.equals("female")){
                sexes.check(R.id.female);
            }
            else{
                sexes.check(R.id.male);
            }
            saveButton.setText("Logout");
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userNameEditText.getText().length() == 0 | passwordEditText.getText().length() == 0 | confirmPasswordEditText.getText().length() == 0) {
                    Toast.makeText(getBaseContext(), "Please enter all info", Toast.LENGTH_SHORT).show();
                }
                else if (!passwordsMatch()) {
                    Toast.makeText(getBaseContext(), "Passwords must match. " + passwordEditText.getText() + " != " + confirmPasswordEditText.getText(), Toast.LENGTH_SHORT).show();
                }
                //passwords match, new user made
                else if (passwordsMatch() &&  !userDataSharedPrefs.contains("currentUser") && !userDataSharedPrefs.contains(String.valueOf(userNameEditText.getText()))) {
                    makeNewUser();
                    Log.i("Save ", "passwords match, new user made");
                    finish();
                }
                //previous user logs in again
                else if (userDataSharedPrefs.contains(String.valueOf(userNameEditText.getText())) && !userDataSharedPrefs.contains("currentUser")
                        && userDataSharedPrefs.getStringSet(String.valueOf(userNameEditText.getText()), null).contains(String.valueOf(passwordEditText.getText()))){
                    Toast.makeText(getBaseContext(), "Welcome back " + String.valueOf(userNameEditText.getText()), Toast.LENGTH_SHORT).show();
                    populateProfile();
                    Log.i("Save", "previous user logged in");
                    finish();
                }
                //user logs out
                else if (userDataSharedPrefs.contains(String.valueOf(userNameEditText.getText())) && userDataSharedPrefs.contains("currentUser")) {
                    logOut();
                    Log.i("Save", "Logout");
                }
            }
        });


    }

    public User makeNewUser() {
        //Create new users based on submitted info: A temporary currentUser to refer to within a session,
        // and a permanent User to refer to between sessions.

        int selectedID = sexes.getCheckedRadioButtonId();
        String selectedSex = "";
        if (selectedID == R.id.female) {
            selectedSex = "female";
            femaleUser = true;
        }
        if (selectedID == R.id.male) {
            selectedSex = "male";
            maleUser = true;
        }
            currentUser = new User(String.valueOf(userNameEditText.getText()), String.valueOf(passwordEditText.getText()), selectedSex);
            User permanentUser = new User(String.valueOf(userNameEditText.getText()), String.valueOf(passwordEditText.getText()), selectedSex);


        Log.i("currentUser", currentUser.userName);
            Toast.makeText(getBaseContext(), "currentUser: " + currentUser.userName, Toast.LENGTH_SHORT).show();

            //Each user has its own set
            currentUserSet = new TreeSet<>();
            currentUserSet.add(currentUser.userName);
            currentUserSet.add(currentUser.password);
            currentUserSet.add(currentUser.sex);


            permanentUserSet = new TreeSet<>();
            permanentUserSet.add(permanentUser.userName);
            permanentUserSet.add(permanentUser.password);
            permanentUserSet.add(permanentUser.sex);

            //add users to sharedprefs. the permanent user will be used for populating the profile activity session-to-session
            editor.putStringSet("currentUser", currentUserSet);
            editor.putStringSet(permanentUser.userName, permanentUserSet);
            editor.commit();
            users.add(currentUser);

            createdAccount = true;
            Toast.makeText(getBaseContext(), "Account Created: " + currentUser.userName + ", " + userDataSharedPrefs.getString("sex", ""), Toast.LENGTH_SHORT).show();
            saveButton.setText("Logout");

        return  currentUser;
    }

    public void logOut() {
        userNameEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        createdAccount = false;
        saveButton.setText("Save");
        sexes.clearCheck();
        editor.remove("currentUser");
        editor.commit();
        //permanentUser is still saved if user logs in again
    }

    public void populateProfile() {
        //recreate currentUser and get user from userSharedPrefs (permanentUser)
        username = String.valueOf(userNameEditText.getText());
        userNameEditText.setText(username);

        Set<String> currentUserDataSet =  userDataSharedPrefs.getStringSet(username, null);
        currentUserDataArrayList = new ArrayList<>(currentUserDataSet);
        for (int i = 0; i < currentUserDataArrayList.size(); i++) {
            if (currentUserDataArrayList.get(i).equals("male") | currentUserDataArrayList.get(i).equals("female")) {
                userSex = currentUserDataArrayList.get(i);
            }
            if (userDataSharedPrefs.contains(currentUserDataArrayList.get(i))) {
                username = currentUserDataArrayList.get(i);
            }
            else {
                password = currentUserDataArrayList.get(i);
            }

        }
        userNameEditText.setText(username);
        passwordEditText.setText(password);
        confirmPasswordEditText.setText(password);
        if (userSex.equals("female")){
            sexes.check(R.id.female);
        }
        else{
            sexes.check(R.id.male);
        }
        saveButton.setText("Logout");
        createdAccount = true;

        editor.putStringSet("currentUser", currentUserDataSet);
        editor.commit();
    }

    public boolean passwordsMatch() {
        return String.valueOf(passwordEditText.getText()).equals(String.valueOf(confirmPasswordEditText.getText()));
    }

    public void showPassword() {
        passwordToggle.setText("SHOW");
        passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        passwordEditText.setSelection(passwordEditText.length());
        enteredPassword = false;
        passwordIsHidden = false;
    }

    public void hidePassword() {
        passwordToggle.setText("HIDE");
        passwordEditText.setSelection(passwordEditText.length());
        passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordEditText.setText(password);

        enteredPassword = true;
        passwordIsHidden = true;
    }

}
