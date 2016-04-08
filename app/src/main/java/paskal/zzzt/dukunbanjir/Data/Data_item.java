package paskal.zzzt.dukunbanjir.Data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TERAPAN2-03 on 22/03/2016.
 */
public class Data_item {
    public String ID;
    public static String Status;
    public static String Tinggi;


    public Data_item(JSONObject object) throws JSONException {

        ID = object.getString("id");
        Status = object.getString("status");
        Tinggi = object.getString("tinggi_air");


    }

    public static void add(Data_item content) {
    }
}
