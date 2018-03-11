package kryword.recuperalo.Modelos;

/**
 * Created by Kryword on 11/03/2018.
 */

public class PointData {
    private String title;
    private String description;
    public PointData(){}

    public PointData(String title, String description){
        this.title = title;
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
