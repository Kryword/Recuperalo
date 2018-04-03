package kryword.recuperalo.Modelos;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("ObjetoEncontrado")
public class ObjetoEncontrado extends ParseObject{
    private String title;
    private String description;
    private ParseGeoPoint position;

    public void setDescription(String description) {
        put("description", description);
    }

    public void setPosition(double latitude, double longitude) {
        put("position", new ParseGeoPoint(latitude, longitude));
    }

    public void setPosition(LatLng position) {
        put("position", new ParseGeoPoint(position.getLatitude(), position.getLongitude()));
    }

    public void setPosition(ParseGeoPoint position) {
        put("position", position);
    }

    public void setTitle(String title){
        put("title", title);
    }

    public String getTitle() {
        return getString("title");
    }

    public String getDescription() {
        return getString("description");
    }

    public ParseGeoPoint getPosition() {
        return getParseGeoPoint("position");
    }

    public LatLng getLatLngPosition(){
        ParseGeoPoint parsePoint = getPosition();
        return new LatLng(parsePoint.getLatitude(), parsePoint.getLongitude());
    }
}
