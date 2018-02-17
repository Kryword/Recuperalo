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

import java.net.MalformedURLException;
import java.net.URL;

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
                try{
                    URL geoJsonUrl = new URL("https://d2ad6b4ur7yvpq.cloudfront.net/naturalearth-3.3.0/ne_50m_urban_areas.geojson");
                    GeoJsonSource source = new GeoJsonSource("urban-areas", geoJsonUrl);
                    mapboxMap.addSource(source);
                    mapboxMap.addImage(
                            "my-marker-image",
                            BitmapFactory.decodeResource(MapLostActivity.this.getResources(),
                                    R.drawable.mapbox_marker_icon_default)
                    );

                    double p1[] = {4.91638, 52.35673};
                    double p2[] = {4.91638, 52.34673};
                    FeatureCollection markers = FeatureCollection.fromFeatures(new Feature[] {
                            Feature.fromGeometry(Point.fromCoordinates(p1)),
                            Feature.fromGeometry(Point.fromCoordinates(p2))
                    });
                    Log.i("FEATURES: ", markers.toJson());
                    mapboxMap.addSource(new GeoJsonSource("marker-source", markers));

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
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.35273, 4.91638), 14));
                }catch (MalformedURLException ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
