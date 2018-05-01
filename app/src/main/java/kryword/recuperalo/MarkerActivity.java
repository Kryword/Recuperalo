package kryword.recuperalo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import kryword.recuperalo.Modelos.ObjetoEncontrado;

public class MarkerActivity extends AppCompatActivity {

    MapView mapView;
    MainApplication ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

        ((TextView)findViewById(R.id.description)).setMovementMethod(new ScrollingMovementMethod());

        ma = (MainApplication)getApplicationContext();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        Button button = (Button) findViewById(R.id.contact);

        // Este es el id del marcador dentro de la lista de puntos, puedo usar esto para obtener todos los datos
        LatLng markerPos = new LatLng(bundle.getDouble("lat"), bundle.getDouble("long"));
        // Objeto dentro de la lista correspondiente al marcador
        ObjetoEncontrado objeto = findObject(markerPos);
        final String objTitle = objeto.getTitle();
        final String objDescription = objeto.getDescription();
        final LatLng position = objeto.getLatLngPosition();
        if (objeto.getName()!=null) {
            button.setText("Contactar con " + objeto.getName());
        }
        // Relleno los campos del título y la descripción con los datos obtenidos del objeto
        title.setText(objTitle);
        description.setText(objDescription);
        Log.i("Datos del objeto:", position + "; " + objTitle + "; " + objDescription);

        Mapbox.getInstance(this, getString(R.string.access_token));
        mapView = (MapView)findViewById(R.id.viewMap);

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final MapboxMap mapboxMap) {
                    mapboxMap.addImage(
                            "my-marker-image",
                            BitmapFactory.decodeResource(MarkerActivity.this.getResources(),
                                    R.drawable.mapbox_marker_icon_default)
                    );
                    mapboxMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(objTitle)
                            .setSnippet(objDescription));
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        finish();
    }

    private ObjetoEncontrado findObject(LatLng position){
        for (ObjetoEncontrado objetoEncontrado: ma.list){
            if(objetoEncontrado.getPosition().getLatitude() == position.getLatitude() && objetoEncontrado.getPosition().getLongitude() == position.getLongitude()){
                return objetoEncontrado;
            }
        }
        return null;
    }
}
