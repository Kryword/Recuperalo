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
            fileOut.write(loadGeoJsonFromAsset().getBytes());
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

    private String loadGeoJsonFromAsset() {

        try {
            // Load GeoJSON file
            InputStream is = getResources().openRawResource(R.raw.points);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");

        } catch (Exception exception) {
            Log.e("StyleLineActivity", "Exception Loading GeoJSON: " + exception.toString());
            exception.printStackTrace();
            return null;
        }

    }
}
