package com.practica2.widgetimgPractica1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    Button btn_cambiar,btn_crear,btn_buscar,btn_guardar,btn_back,btn_next;
    ImageView img_galery;
    TextView txt_count,txt_path;


    public static Bitmap result;

    public static int num_imag = 0;
    public static int num_imag_pref = 0;

    static int REQUEST_IMG = 100;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_cambiar = findViewById(R.id.btn_cambiar);
        btn_crear = findViewById(R.id.btn_crear);
        btn_buscar =findViewById(R.id.btn_buscar);
        btn_guardar = findViewById(R.id.btn_guardar);
        btn_back = findViewById(R.id.btn_back);
        btn_next = findViewById(R.id.btn_next);

        txt_count = findViewById(R.id.txt_count);
        txt_path = findViewById(R.id.txt_path);

        img_galery =findViewById(R.id.img_galeria);

        txt_count.setText("num_img_pref: "+ num_imag_pref);


        btn_next.setOnClickListener(v -> {
            if(num_imag_pref<=4){
                SharedPreferences sharedPreferences = getSharedPreferences("imagenes",Context.MODE_PRIVATE);
                String data = sharedPreferences.getString("img"+num_imag_pref,"");
                txt_path.setText("path: "+data);
                num_imag_pref++;
                txt_count.setText("num_img_pref: "+ num_imag_pref);

            }else{
                num_imag_pref = 0;
                txt_count.setText("num_img_pref: "+ num_imag_pref);
            }
        });
        btn_back.setOnClickListener(v -> {
            if(num_imag_pref>=0){
                SharedPreferences sharedPreferences = getSharedPreferences("imagenes",Context.MODE_PRIVATE);
                String data = sharedPreferences.getString("img"+num_imag_pref,"");
                txt_path.setText("path: "+data);
                num_imag_pref--;
                txt_count.setText("num_img_pref: "+ num_imag_pref);
            }else{
                num_imag_pref = 4;
                txt_count.setText("num_img_pref: "+ num_imag_pref);
            }
        });



        btn_guardar.setOnClickListener(v -> {

            SharedPreferences sharedPreferences = getSharedPreferences("imagenes",Context.MODE_PRIVATE);
            String data = sharedPreferences.getString("img"+num_imag_pref,"");
            Bitmap bitmap = BitmapFactory.decodeFile(data);


            img_galery.setImageBitmap(bitmap);


            num_imag = 4;

            AppWidgetManager widgetManager = getApplicationContext().getSystemService(AppWidgetManager.class);
            Intent intent = new Intent (getApplication(),miwidget.class);
            intent.setAction(widgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids =widgetManager.getAppWidgetIds(new ComponentName(getApplication(),miwidget.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            sendBroadcast(intent);

        });

        btn_cambiar.setOnClickListener(v -> {
            num_imag =(++num_imag)%3;
            AppWidgetManager widgetManager = getApplicationContext().getSystemService(AppWidgetManager.class);
            Intent intent = new Intent (getApplication(),miwidget.class);
            intent.setAction(widgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids =widgetManager.getAppWidgetIds(new ComponentName(getApplication(),miwidget.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            sendBroadcast(intent);
        });

        btn_buscar.setOnClickListener(v -> {
            //abrimos el explorador de rutas para escoger la imagen

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent,REQUEST_IMG);
        });


        btn_crear.setOnClickListener(v -> {
            AppWidgetManager widgetManager = getApplicationContext().getSystemService(AppWidgetManager.class);
            ComponentName myprovider = new ComponentName(getApplicationContext(),miwidget.class);
            if(widgetManager.isRequestPinAppWidgetSupported()){
                widgetManager.requestPinAppWidget(myprovider,null,null);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_IMG){
            Uri uri = data.getData();
            String path_absolute = getRealPathFromURI(uri);


            if(num_imag_pref<=4){
                SharedPreferences sharedPreferences = getSharedPreferences("imagenes",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("img" +  num_imag_pref , path_absolute);
                editor.commit();

                Toast.makeText(this,num_imag_pref+" "+path_absolute,Toast.LENGTH_SHORT).show();
                num_imag_pref++;

            }else{
                num_imag_pref = 0;
            }



        }
        if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
        }

    }//onActivityResult
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else
            {
                cursor.moveToFirst(); int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); result = cursor.getString(idx); cursor.close();
            }
        return result;
    }



}