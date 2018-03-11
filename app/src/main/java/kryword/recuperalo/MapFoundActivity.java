package kryword.recuperalo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.Point;
import com.parse.ParseException;
import com.parse.SaveCallback;

import kryword.recuperalo.Modelos.PointData;
import kryword.recuperalo.Modelos.Punto;

public class MapFoundActivity extends AppCompatActivity {

    MapView mapView;
    MainApplication ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_found);
        Mapbox.getInstance(this, getString(R.string.access_token));
        // Above is layout

        ma = (MainApplication) this.getApplicationContext();


        mapView = (MapView)findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng point) {
                        Intent intent = new Intent(getApplicationContext(), FoundActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("lat", point.getLatitude());
                        bundle.putDouble("long", point.getLongitude());
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 1);
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
        if (resultCode == RESULT_OK) {
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
            addNewPoint(title, description, pos);
        }
    }

    private void addNewPoint(String title, String description, LatLng pos){
        final Punto punto = new Punto();
        punto.setData(new PointData(title, description));
        punto.setPos(pos);
        punto.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    ma.list.add(punto);
                }else{
                    Log.e("ParseServer", "Error trying to add new point in background: " + e.getMessage());
                }
            }
        });
    }
}
