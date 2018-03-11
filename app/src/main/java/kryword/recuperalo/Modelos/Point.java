package kryword.recuperalo.Modelos;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by Kryword on 11/03/2018.
 */

public class Point {
    private LatLng pos;
    private PointData data;

    Point(){};

    public void setPos(LatLng pos) {
        this.pos = pos;
    }

    public void setData(PointData data) {
        this.data = data;
    }

    public LatLng getPos() {
        return pos;
    }

    public PointData getData() {
        return data;
    }
}
