package intertech.com.nougatnotifications;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import static intertech.com.nougatnotifications.NotificationUtil.NOTIFICATION_INLINE_ACTION;

/**
 * {@link IntentService} for handling {@link RemoteInput} from Notifications.
 *
 */
public class NotificationIntentService extends IntentService {

    private static final String TAG = NotificationIntentService.class.getSimpleName();

    public static final String ACTION_DONE = "action_done";
    public static final String ACTION_RESPOND = "action_respond";

    //Simple way to track the user's response history from the notification.
    //This is for demonstration purposes only; it is not recommended.
    private static List<CharSequence> responseHistory = new LinkedList<>();

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if(ACTION_DONE.equals(intent.getAction())) {
            responseHistory.clear();
            notificationManager.cancel(NOTIFICATION_INLINE_ACTION);
            return;
        }

        CharSequence reply = null;
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            reply = remoteInput.getCharSequence(NotificationUtil.KEY_INLINE_REPLY);
            responseHistory.add(0, reply);
            Log.i(TAG, reply.toString());
        } else {
            Intent activityIntent = new Intent(this, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //cancel the notification since we starting an Activity
            notificationManager.cancel(NOTIFICATION_INLINE_ACTION);
            startActivity(activityIntent);
            return;
        }


        //Do one of the following to stop the progress spinner in the Notification:

        //Option 1: Cancel the notification
        //notificationManager.cancel(NOTIFICATION_INLINE_ACTION);

        //Option 2: Issue a new Notification with the same ID
        if(!responseHistory.isEmpty()) {
            CharSequence[] history = new CharSequence[responseHistory.size()];
            NotificationUtil.inlineReplyNotification("Response has been processed", responseHistory.toArray(history));
        } else {
            NotificationUtil.inlineReplyNotification("Response has been processed");
        }

    }
}
