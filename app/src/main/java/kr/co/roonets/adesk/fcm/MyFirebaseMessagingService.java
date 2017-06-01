package kr.co.roonets.adesk.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import kr.co.roonets.adesk.MainActivity;
import kr.co.roonets.adesk.R;

import static android.R.attr.id;


/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 6/13/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyGcmListenerService";
    Bitmap bigPicture;

    @Override
    public void onMessageReceived(RemoteMessage message) {  // 앱이 fore-ground 상태에서만 실행되는 콜백

        Log.d(TAG, "From: " + message.getFrom());   // FCM Project No : 497384876689

        // Check if message contains a data payload.
        if (message.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + message.getData());
            /*
            int id = 0;
            Object obj = message.getData().get("id");
            if (obj != null) {
                id = Integer.valueOf(obj.toString());
            }
            */
        }
        // Check if message contains a notification payload.
        if (message.getNotification() != null) {
            String image = message.getNotification().getIcon();
            String title = message.getNotification().getTitle();
            String text = message.getNotification().getBody();
            String sound = message.getNotification().getSound();
            String bigTitle = message.getData().get("bigTitle");
            String bigBodyMessage = message.getData().get("bigBodyMessage");
            String bigImage = message.getData().get("bigImage");

            this.sendNotification(new NotificationData(image, id, title, text, sound, bigTitle, bigBodyMessage, bigImage));
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param notificationData GCM message received.
     */
    private void sendNotification(NotificationData notificationData) {

        /* 메세지가 클릭되었을 때 이동하는 클래스 및 세팅 Value */
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra(NotificationData.TEXT, notificationData.getTextMessage());
        intent.putExtra("message", notificationData.getTextMessage());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder notificationBuilder = null;
        try {
            notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setSmallIcon(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_a : R.drawable.ic_a); // 앱이 foreground에 있을 때 적용되는 이미지
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            notificationBuilder.setColor(0xffffaec9);
            notificationBuilder.setContentTitle(URLDecoder.decode(notificationData.getTitle(), "UTF-8"));
            notificationBuilder.setContentText(URLDecoder.decode(notificationData.getTextMessage(), "UTF-8"));
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            notificationBuilder.setVibrate(new long[] {100, 0, 100, 0});
            notificationBuilder.setLights(000000255,500,2000);
            if (notificationData.getBigTitle() != null && notificationData.getBigTitle() != "") {
                if (notificationData.getBigImage() != null && notificationData.getBigImage() != "") {
                    try {
                        Log.e(TAG, "sendNotification: b");
                        URL url = new URL(notificationData.getBigImage());
                        bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bigPicture)
                            .setBigContentTitle(URLDecoder.decode(notificationData.getBigTitle(), "UTF-8"))
                            .setSummaryText(URLDecoder.decode(notificationData.getBigBodyMessage(), "UTF-8")));
                } else {
                    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(URLDecoder.decode(notificationData.getBigTitle(), "UTF-8"))
                            .bigText(URLDecoder.decode(notificationData.getBigBodyMessage(), "UTF-8")));
                }
            }
            notificationBuilder.setContentIntent(pendingIntent);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (notificationBuilder != null) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationData.getId(), notificationBuilder.build());
        } else {
            Log.d(TAG, "notificationBuilder 개체를 만들 수 없습니다");
        }
    }
}