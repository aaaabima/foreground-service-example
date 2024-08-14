package id.aaaabima.foregroundserviceexample;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Random;

public class ForegroundServiceExample extends Service {
    private final IBinder binder = new LocalBinder();
    private Button overlayButton;
    private Callback callback;
    private WindowManager windowManager;

    public interface Callback {
        void onDataGenerated(int number);
    }

    public class LocalBinder extends Binder {
        ForegroundServiceExample getService() {
            return ForegroundServiceExample.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Add button on top of MainActivity
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlayButton = new Button(this);
        overlayButton.setText("Generate");
        overlayButton.setOnClickListener(v -> {
            // Generate random data
            int randomData = new Random().nextInt(100);
            if (callback != null) {
                Log.d("DataGenerated", String.valueOf(randomData));
                callback.onDataGenerated(randomData);
            }
            removeOverlayButton();
            stopSelf();
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.CENTER;
        windowManager.addView(overlayButton, params);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeOverlayButton();
    }

    private void removeOverlayButton() {
        if (windowManager != null && overlayButton != null) {
            windowManager.removeView(overlayButton);
            overlayButton = null; // Avoid memory leaks
        }
    }
}
