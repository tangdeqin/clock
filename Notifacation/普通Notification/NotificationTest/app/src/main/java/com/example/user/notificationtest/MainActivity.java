package com.example.user.notificationtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button button;
    public static NotificationManager notificationManager;
    private static final String CHANNEL1 = "channel1";
    private static final String CHANNEL2 = "channel2";
    private static final String CHANNEL3 = "channel3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.send);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initNotification(v.getContext(),CHANNEL3);
            }
        });
    }

    public static void initNotification(Context context,String channelId){ //创建多个通知渠道
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL1, "通知渠道1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("通知渠道的描述1");
            channel1.enableLights(true);//是否在桌面icon右上角展示小红点
            channel1.setLightColor(Color.WHITE);//红点颜色
            channel1.enableVibration(true);

            NotificationChannel channel2 = new NotificationChannel(CHANNEL2, "通知渠道2", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("通知渠道的描述2");
            channel1.enableLights(true);
            channel1.setLightColor(Color.RED);

            NotificationChannel channel3 = new NotificationChannel(CHANNEL3, "通知渠道3", NotificationManager.IMPORTANCE_HIGH);
            channel3.setDescription("通知渠道的描述3");
            channel1.enableLights(true);
            channel1.setLightColor(Color.YELLOW);


            List<NotificationChannel> channels = new ArrayList<>();
            channels.add(channel1);
            channels.add(channel2);
            channels.add(channel3);
            notificationManager.createNotificationChannels(channels);

            Notification.Builder builder = new Notification.Builder(context,channelId);
            Intent intent = new Intent(context,SecondActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
            builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("标题1")
                    .setContentText(channelId)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            Notification notification = builder.build();
            notificationManager.notify(1, notification);
        }

    }

}
