package kryword.recuperalo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class FoundActivity extends AppCompatActivity {
    private Double lat, lon;
    private String title, description;
    private EditText titleText, descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        lat = bundle.getDouble("lat");
        lon = bundle.getDouble("long");
        title = bundle.getString("title");
        description = bundle.getString("description");

        titleText = findViewById(R.id.title);
        descriptionText = findViewById(R.id.description);
        if (title != null){
            titleText.setText(title);
        }
        if (description != null){
            descriptionText.setText(description);
        }
    }

    public void publicar(View view){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("long", lon);
        title = titleText.getText().toString();
        description = descriptionText.getText().toString();
        bundle.putString("title", title);
        bundle.putString("description", description);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancelar(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
