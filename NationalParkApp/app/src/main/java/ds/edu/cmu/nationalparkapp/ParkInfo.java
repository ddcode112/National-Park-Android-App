package ds.edu.cmu.nationalparkapp;
/**
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Apr 6, 2023
 *
 * This class stores information of a park.
 */

import android.graphics.Bitmap;

public class ParkInfo {
    // name of park
    public String fullName;
    // image of park
    public Bitmap img;
    // description of park
    public String description;
    // minimum entrance fee
    public float min;
    // maximum entrance fee
    public float max;
    // url of park
    public String url;

    public String getFullName() {
        return fullName;
    }

    public Bitmap getImg() {
        return img;
    }

    public String getDescription() {
        return description;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public String getUrl() {
        return url;
    }
}
