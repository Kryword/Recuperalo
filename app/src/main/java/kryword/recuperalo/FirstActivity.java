package kryword.recuperalo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        FileOutputStream fileOut;
        try {
            fileOut = openFileOutput("points.json", Context.MODE_PRIVATE);
            fileOut.write(UtilsForJson.loadGeoJsonFromAsset(this).getBytes());
            fileOut.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void encontrado(View view){
        Intent intent = new Intent(getApplicationContext(), MapFoundActivity.class);
        startActivity(intent);
    }

    public void perdido(View view){
        Intent intent = new Intent(getApplicationContext(), MapLostActivity.class);
        startActivity(intent);
    }
}
