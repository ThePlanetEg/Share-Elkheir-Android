package eg.com.theplanet.akram.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by georgenaiem on 5/22/16.
 */
public class Contribution {

    @SerializedName("id")
    public String contributionID;

    @SerializedName("user_id")
    public String userID;

    @SerializedName("type_of_contribution")
    public String type;

    @SerializedName("latitude")
    public double latitude;

    @SerializedName("longitude")
    public double longitude;

    @SerializedName("area")
    public String area;

    @SerializedName("quantity")
    public String quantity;

    @SerializedName("access")
    public String accessibility;

    @SerializedName("covered")
    public int isCovered;

    @SerializedName("user")
    public User user;

    @SerializedName("created_at")
    public String timestamp;

}
