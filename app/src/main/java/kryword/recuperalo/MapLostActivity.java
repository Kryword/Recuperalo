package kryword.recuperalo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import kryword.recuperalo.Modelos.Punto;

public class MapLostActivity extends AppCompatActivity {

    private final double ZOOM = 14f;

    MapView mapView;
    MainApplication ma;
    boolean newPos; // Esto me sirve para mantener el conocimiento de si hay que actualizar posición de cámara o no
    LatLng position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_lost);

        // Posición por defecto, es el centro de Pamplona.
        position = new LatLng(42.8157961, -1.6675312);
        newPos = true;

        ma = (MainApplication) this.getApplicationContext();
        getList();

        Mapbox.getInstance(this, getString(R.string.access_token));
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                mapboxMap.addImage(
                        "my-marker-image",
                        BitmapFactory.decodeResource(MapLostActivity.this.getResources(),
                                R.drawable.mapbox_marker_icon_default)
                );
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

        // Actualizo mapa
        updateMap();
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
                    Log.v("query OK ", "getList()");
                    updateMap();
                } else {
                    Log.v("error query, reason: " + e.getMessage(), "getList()");
                }
            }
        });
    }

    private void updateMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                Toast.makeText(getBaseContext(), "Actualizado el mapa", Toast.LENGTH_SHORT).show();
                for (Punto punto : ma.list) {
                    mapboxMap.addMarker(new MarkerOptions().position(punto.getPos()).title(punto.getData().getTitle()).setSnippet(punto.getData().getDescription()));
                }
                if (newPos) {
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
                }
            }
        });
    }

    public void updatePosition(View view){
        Location location = getLastKnownLocation();
        if (location != null) {
            Log.v("LOCATION", "Encontrada localización: " + location);
            position.setLatitude(location.getLatitude());
            position.setLongitude(location.getLongitude());
            newPos = true;
            updateMap();
        }
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            // Habría que modificar este SupressLint por otra cosa para mantener la estabilidad.
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Encontrada la mejor localización
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
