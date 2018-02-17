package kryword.recuperalo;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;

import java.io.InputStream;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class MapLostActivity extends AppCompatActivity {

    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_lost);
        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_map);
        mapView = (MapView)findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                    mapboxMap.addImage(
                            "my-marker-image",
                            BitmapFactory.decodeResource(MapLostActivity.this.getResources(),
                                    R.drawable.mapbox_marker_icon_default)
                    );
                    mapboxMap.addSource(new GeoJsonSource("marker-source", loadGeoJsonFromAsset()));

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

                    // Show
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.81098654188819, -1.6432714462280273), 14));
            }
        });
    }

    @Override
    public void onBackPressed() {
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
