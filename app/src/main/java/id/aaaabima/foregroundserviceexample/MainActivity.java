package id.aaaabima.foregroundserviceexample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements ForegroundServiceExample.Callback {
    private ForegroundServiceExample foregroundService;
    private boolean isBound = false;
    private TextView dataTextView;

    private static final int REQUEST_OVERLAY_PERMISSION = 1;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ForegroundServiceExample.LocalBinder binder = (ForegroundServiceExample.LocalBinder) iBinder;
            foregroundService = binder.getService();
            foregroundService.setCallback(MainActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button startServiceButton = findViewById(R.id.btnStartService);
        dataTextView = findViewById(R.id.tvData);

        dataTextView.setText(getString(R.string.data_d, -1));

        startServiceButton.setOnClickListener(v -> {
            if (!PermissionUtils.canDrawOverlays(this)) {
                PermissionUtils.requestOverlayPermission(this);
            } else {
                startAndBindService();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (PermissionUtils.canDrawOverlays(this)) {
                startAndBindService();
            }
            else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startAndBindService() {
        Intent intent = new Intent(this, ForegroundServiceExample.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDataGenerated(int number) {
        runOnUiThread(() -> dataTextView.setText(getString(R.string.data_d, number)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}