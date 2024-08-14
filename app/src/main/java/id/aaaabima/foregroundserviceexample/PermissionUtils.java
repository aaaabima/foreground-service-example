package id.aaaabima.foregroundserviceexample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

public class PermissionUtils {

    public static boolean canDrawOverlays(Context context) {
        return Settings.canDrawOverlays(context);
    }

    public static void requestOverlayPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
