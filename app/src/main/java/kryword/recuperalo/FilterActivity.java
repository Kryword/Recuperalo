package kryword.recuperalo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import kryword.recuperalo.Modelos.ObjetoEncontrado;

public class FilterActivity extends AppCompatActivity {
    private MainApplication ma;
    private FilterAdapter filterAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ma = (MainApplication) getApplicationContext();
        list = findViewById(R.id.filterList);
        final List<ObjetoEncontrado> lista = new ArrayList<>(ma.list);
        filterAdapter = new FilterAdapter(this, lista);
        list.setAdapter(filterAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), MarkerActivity.class);
                Bundle bundle = new Bundle();
                LatLng pos = ((ObjetoEncontrado)filterAdapter.getItem(position)).getLatLngPosition();
                bundle.putDouble("lat", pos.getLatitude());
                bundle.putDouble("long", pos.getLongitude());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void filterMyObjects(View view){
        String myUid = FirebaseAuth.getInstance().getUid();
        List<ObjetoEncontrado> objetos = new ArrayList<>();
        filterAdapter = new FilterAdapter(getApplicationContext(), new ArrayList<>(ma.list));
        for (ObjetoEncontrado objetoEncontrado : ma.list) {
            if (objetoEncontrado.getUid() == null || !objetoEncontrado.getUid().equals(myUid)){
                objetos.add(objetoEncontrado);
            }
        }
        for (ObjetoEncontrado objeto : objetos) {
            filterAdapter.remove(objeto);
        }
        list.setAdapter(filterAdapter);
    }

    public void filterObjects(View view){
        EditText editText = findViewById(R.id.filterText);
        String word = editText.getText().toString();
        if (!TextUtils.isEmpty(word)) {
            List<ObjetoEncontrado> objetos = new ArrayList<>();
            filterAdapter = new FilterAdapter(getApplicationContext(), new ArrayList<>(ma.list));
            for (ObjetoEncontrado objetoEncontrado : ma.list) {
                if (!objetoEncontrado.getTitle().toLowerCase().contains(word.toLowerCase())){
                    objetos.add(objetoEncontrado);
                }
            }
            for (ObjetoEncontrado objeto : objetos) {
                filterAdapter.remove(objeto);
            }
        }else{
            ParseQuery<ObjetoEncontrado> query = ParseQuery.getQuery("ObjetoEncontrado");
            query.findInBackground(new FindCallback<ObjetoEncontrado>() {
                @Override
                public void done(List<ObjetoEncontrado> objects, ParseException e) {
                    if(e == null){
                        Log.d("Actualización","Actualizada la lista de elementos");
                        ma.list = objects;
                        filterAdapter = new FilterAdapter(getApplicationContext(), new ArrayList<>(ma.list));
                    }else{
                        Toast.makeText(ma, "Error a la hora de pedir los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        list.setAdapter(filterAdapter);
    }

    public void backToMap(View view){
        ma.list = filterAdapter.getData();
        setResult(RESULT_OK);
        finish();
    }
}
