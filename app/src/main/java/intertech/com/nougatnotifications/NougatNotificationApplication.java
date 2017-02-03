package intertech.com.nougatnotifications;

import android.app.Application;
import android.content.Context;

/**
 * Extension of {@link Application}, which exists primarily to provide easy access to a
 * {@link Context} from anywhere in the app.
 */
public class NougatNotificationApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context context() {
        return context;
    }

}
