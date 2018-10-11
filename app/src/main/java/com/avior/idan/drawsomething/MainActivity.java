package com.avior.idan.drawsomething;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * MainActivity - manages the BottomNavigationBar.
 */

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, new LoginFragment()).addToBackStack(null).commit();
                    return true;
                case R.id.navigation_dashboard:
                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new DrawingFragment()).addToBackStack(null).commit();
                    else
                        Toast.makeText(getApplicationContext(), "You must login/register first!", Toast.LENGTH_LONG).show();
                    return true;
                case R.id.navigation_my_drawings:
                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new MyDrawingsFragment()).addToBackStack(null).commit();
                    else
                        Toast.makeText(getApplicationContext(), "You must login/register first!", Toast.LENGTH_LONG).show();
                    return true;
                case R.id.navigation_all_drawings:
                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new AllDrawingsFragment()).addToBackStack(null).commit();
                    else
                        Toast.makeText(getApplicationContext(), "You must login/register first!", Toast.LENGTH_LONG).show();
                    return true;
                case R.id.navigation_notifications:
                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new InvitationsFragment()).addToBackStack(null).commit();
                    else
                        Toast.makeText(getApplicationContext(), "You must login/register first!", Toast.LENGTH_LONG).show();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        navigation.setSelectedItemId(R.id.navigation_home);
        if (user != null)
            Toast.makeText(this, "Welcome " + user.getEmail(), Toast.LENGTH_LONG).show();
    }


    /*
    Gets a String representing an email address and returns the standard email address format used in the app.
     */
    public static String getModifiedEmailAddress(String email){
        return email.replace(".", "_").replace("@", "_at_");
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
