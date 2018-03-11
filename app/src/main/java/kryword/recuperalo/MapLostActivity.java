package kryword.recuperalo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.Point;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.InputStream;
import java.util.List;

import kryword.recuperalo.Modelos.Punto;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class MapLostActivity extends AppCompatActivity {

    MapView mapView;
    MainApplication ma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_lost);
        ma = (MainApplication) this.getApplicationContext();
        getList();
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
                    for (Punto punto : ma.list) {
                        mapboxMap.addMarker(new MarkerOptions().position(punto.getPos()).title(punto.getData().getTitle()).setSnippet(punto.getData().getDescription()));
                    }

                    // Move camera over points
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.81098654188819, -1.6432714462280273), 14));

                    mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Intent intent = new Intent(getApplicationContext(), MarkerActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("title", marker.getTitle());
                            bundle.putString("description", marker.getSnippet());
                            bundle.putDouble("long", marker.getPosition().getLongitude());
                            bundle.putDouble("lat", marker.getPosition().getLatitude());
                            intent.putExtras(bundle);
                            startActivity(intent);
                            return true;
                        }
                    });
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getList() {
        ParseQuery<Punto> query = ParseQuery.getQuery("Punto");
        query.findInBackground(new FindCallback<Punto>() {
            public void done(List<Punto> objects, ParseException e) {
                if (e == null) {
                    ma.list = objects;
                    Log.v("query OK ", "getServerList()");
                } else {
                    Log.v("error query, reason: " + e.getMessage(), "getServerList()");
                    Toast.makeText(
                            getBaseContext(),
                            "getServerList(): error  query, reason: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
