package kryword.recuperalo;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import kryword.recuperalo.Modelos.ObjetoEncontrado;

/**
 * Created by Kryword on 11/03/2018.
 */

public class MainApplication extends Application {

    public List<ObjetoEncontrado> list = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(ObjetoEncontrado.class);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("myAppId") //si no has cambiado APP_ID, sino pon el valor de APP_ID
                .clientKey("empty")
                .server("https://recuperalopp.herokuapp.com/parse/")
                .build());
    }
}
