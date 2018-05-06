package kryword.recuperalo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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

import kryword.recuperalo.Modelos.ObjetoEncontrado;

public class MapLostActivity extends AppCompatActivity {

    /**
     * Estas constantes permiten cambiar los datos de forma más fácil y visible
     */
    private final double ZOOM = 14f;
    private final double LATITUDE_PAMPLONA = 42.8157961;
    private final double LONGITUDE_PAMPLONA = -1.6675312;

    private MapView mapView;
    private MainApplication ma;
    private boolean newPos; // Esto sirve para mantener el conocimiento de si hay que actualizar posición de cámara o no
    private LatLng position;

    private List<Marker> markers;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_lost);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Posición por defecto, es el centro de Pamplona.
        position = new LatLng(LATITUDE_PAMPLONA, LONGITUDE_PAMPLONA);
        newPos = true;

        ma = (MainApplication) this.getApplicationContext();
        getAndModifyList();
        Mapbox.getInstance(this, getString(R.string.access_token));
        mapView = findViewById(R.id.mapView);
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
                        bundle.putDouble("lat", marker.getPosition().getLatitude());
                        bundle.putDouble("long", marker.getPosition().getLongitude());
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

    private void getAndModifyList() {
        ParseQuery<ObjetoEncontrado> query = ParseQuery.getQuery("ObjetoEncontrado");
        query.findInBackground(new FindCallback<ObjetoEncontrado>() {
            public void done(List<ObjetoEncontrado> objects, ParseException e) {
                if (e == null) {
                    ma.list = objects;
                    Log.v("query OK ", "getAndModifyList()");
                    updateMap();
                } else {
                    Log.v("error query, reason: " + e.getMessage(), "getAndModifyList()");
                }
            }
        });
    }

    private void updateMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                markers = mapboxMap.getMarkers();
                for (Marker marker : markers) {
                    mapboxMap.removeMarker(marker);
                }
                Toast.makeText(getBaseContext(), "Actualizado el mapa", Toast.LENGTH_SHORT).show();
                for (ObjetoEncontrado objeto : ma.list) {
                    mapboxMap.addMarker(new MarkerOptions()
                            .position(objeto.getLatLngPosition())
                            .title(objeto.getTitle())
                            .setSnippet(objeto.getDescription()));
                }
                if (newPos) {
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
                }
            }
        });
    }

    public void updatePosition(View view) {
        // Ejecuto esto en caso de que no se hayan dado los permisos a la aplicación,
        // la aplicación los pedirá y la próxima vez que se pulse el botón este funcionará.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 50);
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.v("LOCATION", "Encontrada localización: " + location);
                            position.setLatitude(location.getLatitude());
                            position.setLongitude(location.getLongitude());
                            newPos = true;
                            updateMap();
                        }
                    }
                }
        );
    }

    public void filterResults(View view){
        Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
        startActivityForResult(intent, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK){
            updateMap();
        }
    }
}
