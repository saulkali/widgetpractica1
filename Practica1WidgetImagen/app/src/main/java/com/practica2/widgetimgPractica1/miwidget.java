package com.practica2.widgetimgPractica1;

import android.appwidget.AppWidgetProvider;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class miwidget extends AppWidgetProvider {

    public static String action_next="action_next";
    public static String action_back ="action_back";
    private static int count=0;
    private static int update=0;



    public void onUpdate(Context context, AppWidgetManager widgetManager, int[] appWidgetIds){
        final int arraySize = appWidgetIds.length;
        for(int i = 0; i<arraySize; i++){
            int appWidgetID = appWidgetIds[i];



            //nuestro primer intent para abrir nuestra aplicacion movil
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.my_widget);
            views.setOnClickPendingIntent(R.id.btn_abrir_app,pendingIntent);

            //nuestro segundo intent para actualizar imagen del widget y avanazarlo
            Intent intent_next = new Intent(context,miwidget.class);
            intent_next.setAction(action_next);
            PendingIntent pendingIntent_next = PendingIntent.getBroadcast(context,0,intent_next,0);
            views.setOnClickPendingIntent(R.id.btn_next_widget,pendingIntent_next);


            //nuestro segundo intent para actualizar imagen del widget y elegir la de atras
            Intent intent_back = new Intent(context,miwidget.class);
            intent_next.setAction(action_back);
            PendingIntent pendingIntent_back = PendingIntent.getBroadcast(context,0,intent_next,0);
            views.setOnClickPendingIntent(R.id.btn_back_widget,pendingIntent_back);


            SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
            String strdate = mdformat.format(Calendar.getInstance().getTime());
            views.setTextViewText(R.id.txt_ultimaActualizacion, strdate);
            if(update==1){
                SharedPreferences sharedPreferences = context.getSharedPreferences("imagenes",Context.MODE_PRIVATE);
                String data = sharedPreferences.getString("img"+String.valueOf(count),"");

                update=0;
                Bitmap bitmap = dimencion(data);

                views.setImageViewBitmap(R.id.img_widget,bitmap);
            }else{
                if(MainActivity.num_imag == 0){
                    views.setImageViewResource(R.id.img_widget,R.drawable.image1);
                }else{
                    if(MainActivity.num_imag == 1){
                        views.setImageViewResource(R.id.img_widget,R.drawable.image2);
                    }else{
                        if(MainActivity.num_imag == 2){
                            views.setImageViewResource(R.id.img_widget,R.drawable.image3);
                        }else{
                            if(MainActivity.num_imag == 4){

                                SharedPreferences sharedPreferences = context.getSharedPreferences("imagenes",Context.MODE_PRIVATE);
                                String data = sharedPreferences.getString("img"+String.valueOf(MainActivity.num_imag_pref),"");

                                Bitmap bitmap = dimencion(data);

                                views.setImageViewBitmap(R.id.img_widget,bitmap);

                            }
                        }
                    }
                }
            }


            widgetManager.updateAppWidget(appWidgetID,views);

        }
    }
    private void onUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(),getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        update=1;
        if(count<=4){
            count++;
        }else{
            count=0;
        }
        onUpdate(context, appWidgetManager, appWidgetIds);
    }
    private void onUpdate2(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(),getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        update=1;
        if(count>=0){
            count--;
        }else{
            count=4;
        }

        onUpdate(context, appWidgetManager, appWidgetIds);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (action_next.equals(intent.getAction())) {
            onUpdate(context);
        }else{
            if(action_back.equals(intent.getAction())){
                onUpdate2(context);
            }
        }
    }




    public Bitmap dimencion(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        int img_width=options.outWidth;
        int img_height = options.outHeight;
        int ratio;
        options.inJustDecodeBounds = false;
        if(img_width>img_height){
            ratio = img_width/250;
        }else{
            ratio = img_height/250;
        }
        options.inSampleSize = ratio;
        return BitmapFactory.decodeFile(path,options);
    }

}
