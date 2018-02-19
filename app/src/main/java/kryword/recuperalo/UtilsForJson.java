package kryword.recuperalo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kryword on 18/02/2018.
 */

public class UtilsForJson {

    public static void addNewFeatureToJson(Feature feature, Activity activity){
        try {
            JSONObject collect = new JSONObject(loadGeoJsonFromAsset(activity));
            JSONArray js = collect.getJSONArray("features");
            JSONObject completeFeature = new JSONObject(feature.toJson());
            js.put(completeFeature);
            FileOutputStream fileOut = activity.openFileOutput("points.json", Context.MODE_PRIVATE);
            fileOut.write(collect.toString().getBytes());
            fileOut.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadGeoJsonFromAsset(Activity activity) {
        try {
            // Load GeoJSON file
            InputStream is = activity.openFileInput("points.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");

        } catch (Exception exception) {
            Log.e("StyleLineActivity", "Exception Loading GeoJSON: " + exception.toString());
            exception.printStackTrace();
            return null;
        }
    }
}
