package intertech.com.nougatnotifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import java.util.LinkedList;
import java.util.List;

public class NotificationUtil {
    private final static String GROUP_KEY_BUNDLED = "group_key_bundled";

    public static final String KEY_INLINE_REPLY = "KEY_INLINE_REPLY";

    public static final int NOTIFICATION_INLINE_ACTION = 1;
    private static final int NOTIFICATION_BUNDLED_BASE_ID = 1000;

    //simple way to keep track of the number of bundled notifications
    private static int numberOfBundled = 0;
    //Simple way to track text for notifications that have already been issued
    private static List<CharSequence> issuedMessages = new LinkedList<>();

    public static void inlineReplyNotification(String message) {
        inlineReplyNotification(message, null);
    }

    public static void inlineReplyNotification(String message, CharSequence[] history) {
        Context context = NougatNotificationApplication.context();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = inlineReplyBuilder();
        builder.setContentText(message);
        if(history != null) {
            builder.setRemoteInputHistory(history);
        }
        notificationManager.notify(NOTIFICATION_INLINE_ACTION, builder.build());
    }

    private static NotificationCompat.Builder inlineReplyBuilder() {
        Context context = NougatNotificationApplication.context();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_flag_notification)
                        .setContentTitle("Inline Action");


        Intent respondIntent;
        PendingIntent respondPendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //we are running Nougat
            //the intent should start a service, so that the response can be handled in the background
            respondIntent = new Intent(context, NotificationIntentService.class);
            respondPendingIntent = PendingIntent.getService(context, 0, respondIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            //we are running a version prior to Nougat
            //There will be no response included with intent, so we want to start an Activity to allow
            //the user to enter input. This demo app does not have that capability, but in the real world
            //our Activity ought to allow that
            respondIntent = new Intent(context, MainActivity.class);
            respondPendingIntent = PendingIntent.getActivity(context, 0, respondIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        respondIntent.setAction(NotificationIntentService.ACTION_RESPOND);

        //Build the Action for an inline response, which includes a RemoteInput
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_INLINE_REPLY)
                .setLabel("Your inline response")
                .build();

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_input_get,
                        "Respond", respondPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        builder.addAction(action);

        //Build the Action for "Done", so the IntentService can take appropriate action (e.g. dismiss
        //the notification, clear the history, etc.
        Intent doneIntent = new Intent(context, NotificationIntentService.class);
        doneIntent.setAction(NotificationIntentService.ACTION_DONE);
        PendingIntent donePendingIntent = PendingIntent.getService(context, 0, doneIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action doneAction =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_close_clear_cancel,
                        "Close", donePendingIntent)
                        .build();

        builder.addAction(doneAction);

        return builder;
    }

    public static void bundledNotification() {
        Context context = NougatNotificationApplication.context();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        String message = "This is message # " + ++numberOfBundled
                + ". This text is generally too long to fit on a single line in a notification";
        issuedMessages.add(message);

        //Build and issue the group summary. Use inbox style so that all messages are displayed
        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Group Summary")
                .setContentText("This is the group summary")
                .setSmallIcon(R.drawable.ic_flag_notification)
                .setGroupSummary(true)
                .setGroup(GROUP_KEY_BUNDLED);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Messages:");
        for (CharSequence cs : issuedMessages) {
            inboxStyle.addLine(cs);
        }
        summaryBuilder.setStyle(inboxStyle);

        notificationManager.notify(NOTIFICATION_BUNDLED_BASE_ID, summaryBuilder.build());



        //issue the Bundled notification. Since there is a summary notification, this will only display
        //on systems with Nougat or later
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Bundled Notification " + numberOfBundled)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_flag_notification)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setGroup(GROUP_KEY_BUNDLED);

        //Add an action that simply starts the main activity. This is not very useful it is mainly for demonstration
        Intent intent = new Intent(context, MainActivity.class);
        //Each notification needs a unique request code, so that each pending intent is unique. It does not matter
        //in this simple case, but is important if we need to take action on a specific notification, such as
        //deleting a message
        int requestCode = NOTIFICATION_BUNDLED_BASE_ID + numberOfBundled;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_input_get,
                        "Do Something", pendingIntent)
                        .build();
        builder.addAction(action);

        notificationManager.notify(NOTIFICATION_BUNDLED_BASE_ID + numberOfBundled, builder.build());


    }

}
