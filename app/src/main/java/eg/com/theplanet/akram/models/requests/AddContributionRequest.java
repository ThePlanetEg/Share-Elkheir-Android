package eg.com.theplanet.akram.models.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by georgenaiem on 5/26/16.
 */
public class AddContributionRequest {
    @SerializedName("token")
    public String token;

    @SerializedName("user_id")
    public String userID;

    @SerializedName("type_of_contribution")
    public String type;

    @SerializedName("quantity")
    public String quantity;

    @SerializedName("access")
    public String access;

    @SerializedName("covered")
    public String isCovered;

    @SerializedName("latitude")
    public double latitude;

    @SerializedName("longitude")
    public double longitude;

    @SerializedName("area")
    public String area;

    @SerializedName("feedback")
    public String feedback;
}
