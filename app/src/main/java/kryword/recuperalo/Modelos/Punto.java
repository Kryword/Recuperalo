package kryword.recuperalo.Modelos;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Kryword on 11/03/2018.
 */
@ParseClassName("Punto")
public class Punto extends ParseObject{
    public void setPos(LatLng pos) {
        put("latitud", pos.getLatitude());
        put("longitud", pos.getLongitude());
    }

    public void setData(PointData data) {
        put("title", data.getTitle());
        put("description", data.getDescription());
    }

    public LatLng getPos() {
        return new LatLng(getDouble("latitud"), getDouble("longitud"));
    }

    public PointData getData() {
        return new PointData(getString("title"), getString("description"));
    }
}
