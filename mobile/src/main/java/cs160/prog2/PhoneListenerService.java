package cs160.prog2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class PhoneListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        if (value.equals("UPDATE")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("LOC", value);
            Log.d("T", "about to reload " + value);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DetailedView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String[] repData = value.split("~");
            intent.putExtra("REP", repData);
            Log.d("T", "about to start mobile DetailedView with " + value);
            startActivity(intent);
        }
    }
}
