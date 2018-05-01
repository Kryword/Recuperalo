package kryword.recuperalo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.function.Consumer;

import kryword.recuperalo.Modelos.ObjetoEncontrado;

public class FilterActivity extends AppCompatActivity {
    MainApplication ma;
    MyAdapter myAdapter;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ma = (MainApplication) getApplicationContext();
        list = (ListView) findViewById(R.id.list_view);
        final List<ObjetoEncontrado> lista = new ArrayList<>(ma.list);
        myAdapter = new MyAdapter(this, lista);
        list.setAdapter(myAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), MarkerActivity.class);
                Bundle bundle = new Bundle();
                LatLng pos = lista.get(position).getLatLngPosition();
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
        myAdapter = new MyAdapter(getApplicationContext(), new ArrayList<ObjetoEncontrado>(ma.list));
        for (ObjetoEncontrado objetoEncontrado : ma.list) {
            if (objetoEncontrado.getUid() == null || !objetoEncontrado.getUid().equals(myUid)){
                objetos.add(objetoEncontrado);
            }
        }
        for (ObjetoEncontrado objeto : objetos) {
            myAdapter.remove(objeto);
        }
        list.setAdapter(myAdapter);
    }

    public void filterObjects(View view){
        EditText editText = (EditText) findViewById(R.id.editText);
        String word = editText.getText().toString();
        if (!TextUtils.isEmpty(word)) {
            List<ObjetoEncontrado> objetos = new ArrayList<>();
            myAdapter = new MyAdapter(getApplicationContext(), new ArrayList<ObjetoEncontrado>(ma.list));
            for (ObjetoEncontrado objetoEncontrado : ma.list) {
                if (objetoEncontrado.getTitle().toLowerCase().indexOf(word.toLowerCase()) == -1){
                    objetos.add(objetoEncontrado);
                }
            }
            for (ObjetoEncontrado objeto : objetos) {
                myAdapter.remove(objeto);
            }
        }else{
            ParseQuery<ObjetoEncontrado> query = ParseQuery.getQuery("ObjetoEncontrado");
            query.findInBackground(new FindCallback<ObjetoEncontrado>() {
                @Override
                public void done(List<ObjetoEncontrado> objects, ParseException e) {
                    if(e == null){
                        Log.d("Actualización","Actualizada la lista de elementos");
                        ma.list = objects;
                        myAdapter = new MyAdapter(getApplicationContext(), new ArrayList<ObjetoEncontrado>(ma.list));
                    }else{
                        Toast.makeText(ma, "Error a la hora de pedir los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        list.setAdapter(myAdapter);
    }

    public void backToMap(View view){
        ma.list = myAdapter.data;
        setResult(RESULT_OK);
        finish();
    }
}
