package kryword.recuperalo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.InputStream;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class MarkerActivity extends AppCompatActivity {

    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        title.setText(bundle.getString("title"));
        description.setText(bundle.getString("description"));
        final LatLng position = new LatLng(bundle.getDouble("lat"), bundle.getDouble("long"));
        Mapbox.getInstance(this, getString(R.string.access_token));

        mapView = (MapView)findViewById(R.id.viewMap);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                mapboxMap.addImage(
                        "my-marker-image",
                        BitmapFactory.decodeResource(MarkerActivity.this.getResources(),
                                R.drawable.mapbox_marker_icon_default)
                );
                GeoJsonSource source = new GeoJsonSource("marker-source", loadGeoJsonFromAsset());
                mapboxMap.addSource(source);
                // Add the symbol-layer
                mapboxMap.addLayer(
                        new SymbolLayer("marker-layer", "marker-source")
                                .withProperties(
                                        iconImage("my-marker-image"),
                                        iconAllowOverlap(true),
                                        textField("{title}"),
                                        textColor(Color.RED),
                                        textSize(10f)
                                )
                );
                mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            }
        });
    }

    @Override
    public void onBackPressed() {
        mapView.onDestroy();
        finish();
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
