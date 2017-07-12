package police2.com.crimewatchers.app;

import android.app.Application;
import android.preference.PreferenceManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Abhishek on 19-Jan-17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("firebase", false).apply();
    }
}
