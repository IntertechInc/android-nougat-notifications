package intertech.com.nougatnotifications;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button inlineButton = (Button) findViewById(R.id.inlineResponse);
        inlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NotificationUtil.inlineReplyNotification("This Notification includes inline actions");
            }
        });

        Button bundledButton = (Button) findViewById(R.id.bundled);
        bundledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NotificationUtil.bundledNotification();
            }
        });

    }
}
