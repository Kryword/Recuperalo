package kryword.recuperalo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
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
