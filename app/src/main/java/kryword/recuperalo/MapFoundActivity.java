package kryword.recuperalo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import kryword.recuperalo.Modelos.ObjetoEncontrado;

public class MapFoundActivity extends AppCompatActivity {

    private final double ZOOM = 14f;
    private final double LATITUDE_PAMPLONA = 42.8157961;
    private final double LONGITUDE_PAMPLONA = -1.6675312;
    private final int REQUEST_CODE = 1;

    private MapView mapView;
    private MainApplication ma;

    private LatLng position;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_found);
        Mapbox.getInstance(this, getString(R.string.access_token));
        // Above is layout

        ma = (MainApplication) this.getApplicationContext();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Posición por defecto, es el centro de Pamplona.
        position = new LatLng(LATITUDE_PAMPLONA, LONGITUDE_PAMPLONA);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
                mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng point) {
                        Intent intent = new Intent(getApplicationContext(), FoundActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("lat", point.getLatitude());
                        bundle.putDouble("long", point.getLongitude());
                        intent.putExtras(bundle);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Info", Integer.toString(resultCode));
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            final LatLng pos = new LatLng(bundle.getDouble("lat"), bundle.getDouble("long"));
            final String title = bundle.getString("title");
            final String description = bundle.getString("description");
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final MapboxMap mapboxMap) {
                    mapboxMap.addMarker(new MarkerOptions().title(title).position(pos));
                }
            });
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String uid = mAuth.getCurrentUser().getUid();
            String name = mAuth.getCurrentUser().getDisplayName();
            addNewPoint(title, description, pos, uid, name);
        }
    }

    private void addNewPoint(String title, String description, LatLng pos, String uid, String name){
        final ObjetoEncontrado objeto = new ObjetoEncontrado();
        objeto.setTitle(title);
        objeto.setDescription(description);
        objeto.setPosition(pos);
        objeto.setUid(uid);
        objeto.setName(name);
        objeto.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    ma.list.add(objeto);
                }else{
                    Log.e("ParseServer", "Error trying to add new object in background: " + e.getMessage());
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
                            mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(final MapboxMap mapboxMap) {
                                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
                                }
                            });
                        }
                    }
                }
        );
    }
}
