package kryword.recuperalo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import kryword.recuperalo.Modelos.ObjetoEncontrado;

public class MarkerActivity extends AppCompatActivity {

    final static int REQUEST_CODE_MODIFY = 100;

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
        if (objeto.getName()!=null && !objeto.getUid().equals(FirebaseAuth.getInstance().getUid())) {
            button.setText("Contactar con " + objeto.getName());
        }else if (objeto.getName() != null && objeto.getUid().equals(FirebaseAuth.getInstance().getUid())){
            button.setText("Modificar objeto encontrado");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), FoundActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", objTitle);
                    bundle.putString("description", objDescription);
                    bundle.putDouble("lat", position.getLatitude());
                    bundle.putDouble("long", position.getLongitude());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE_MODIFY);
                }
            });
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MODIFY && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            ParseGeoPoint point = new ParseGeoPoint(bundle.getDouble("lat"), bundle.getDouble("long"));
            final String title = bundle.getString("title");
            final String description = bundle.getString("description");
            ParseQuery<ObjetoEncontrado> query = ParseQuery.getQuery("ObjetoEncontrado");
            // Obtengo el punto más cercano a la posición del punto que voy a modificar.
            query.whereWithinKilometers("position", point, 0.0001);
            Toast.makeText(this, "Estamos modificando el objeto", Toast.LENGTH_SHORT).show();
            query.findInBackground(new FindCallback<ObjetoEncontrado>() {
                @Override
                public void done(List<ObjetoEncontrado> objects, ParseException e) {
                    if (e == null && objects.size() == 1){
                        ObjetoEncontrado objeto = objects.get(0);
                        if (objeto.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                            if (!TextUtils.isEmpty(title)) {
                                objeto.setTitle(title);
                                ((TextView) findViewById(R.id.title)).setText(title);
                            }
                            if (!TextUtils.isEmpty(description)){
                                objeto.setDescription(description);
                                ((TextView) findViewById(R.id.description)).setText(description);
                            }
                            objeto.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null){
                                        Toast.makeText(getApplicationContext(), "Se ha actualizado correctamente el objeto", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Hubo un error a la hora de guardar el objeto", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "No se pudo modificar el objeto", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}
