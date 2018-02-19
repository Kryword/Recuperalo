package kryword.recuperalo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.Point;

import java.io.InputStream;
import java.util.List;

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
        mapView = (MapView)findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                    mapboxMap.addImage(
                            "my-marker-image",
                            BitmapFactory.decodeResource(MapLostActivity.this.getResources(),
                                    R.drawable.mapbox_marker_icon_default)
                    );
                    GeoJsonSource source = new GeoJsonSource("marker-source", UtilsForJson.loadGeoJsonFromAsset(MapLostActivity.this));

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

                    // Show
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.81098654188819, -1.6432714462280273), 14));

                    mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng point) {
                            List<Feature> in = mapboxMap.queryRenderedFeatures(mapboxMap.getProjection().toScreenLocation(point), "marker-layer");
                            if(in.size()>0) {
                                Feature feature = in.get(0);
                                Point punto = (Point) feature.getGeometry();
                                Intent intent = new Intent(getApplicationContext(), MarkerActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("title", feature.getStringProperty("title"));
                                bundle.putString("description", feature.getStringProperty("description"));
                                bundle.putDouble("long", punto.getCoordinates().getLongitude());
                                bundle.putDouble("lat", punto.getCoordinates().getLatitude());
                                intent.putExtras(bundle);
                                startActivity(intent);

                                Log.i("INFO", punto.getCoordinates().toString());
                            }
                        }
                    });
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
