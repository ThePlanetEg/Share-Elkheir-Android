package eg.com.theplanet.akram.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by georgenaiem on 5/22/16.
 */
public class User {

    @SerializedName("id")
    public String userID;

    @SerializedName("fb_id")
    public String facebookID;

    @SerializedName("name")
    public String username;

    @SerializedName("image")
    public String imageURL;

    @SerializedName("email")
    public String emailAddress;

    @SerializedName("password")
    public String password;
}
