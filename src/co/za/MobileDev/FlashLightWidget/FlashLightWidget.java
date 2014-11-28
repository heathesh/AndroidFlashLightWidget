package co.za.MobileDev.FlashLightWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.widget.RemoteViews;

/**
 * Created by Heathesh Bhandari on 2014/11/24.
 */
public class FlashLightWidget extends AppWidgetProvider {

    private static final String FLASH_BUTTON_CLICKED = "flashLightWidgetButtonClick";

    private static boolean _status = true;

    private static Camera _camera = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews;
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
        watchWidget = new ComponentName(context, FlashLightWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.imageButtonFlash, getPendingSelfIntent(context, FLASH_BUTTON_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (FLASH_BUTTON_CLICKED.equals(intent.getAction())) {

            RemoteViews remoteViews = new RemoteViews( context.getPackageName(), R.layout.main );

            if (_status) {
                try {
                    _camera = Camera.open();
                    Camera.Parameters parameters = _camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    _camera.setParameters(parameters);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                remoteViews.setImageViewResource(R.id.imageButtonFlash, R.drawable.torchon);
                _status = false;
            }  else {
                try {
                    Camera.Parameters parameters = _camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    _camera.setParameters(parameters);

                    _camera.release();
                    _camera = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                remoteViews.setImageViewResource(R.id.imageButtonFlash, R.drawable.torchoff);
                _status = true;
            }

            ComponentName watchWidget = new ComponentName( context, FlashLightWidget.class );
            (AppWidgetManager.getInstance(context)).updateAppWidget( watchWidget, remoteViews );
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
