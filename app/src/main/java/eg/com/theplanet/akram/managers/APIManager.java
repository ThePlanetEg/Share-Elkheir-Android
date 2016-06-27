package eg.com.theplanet.akram.managers;

import java.util.Map;

import eg.com.theplanet.akram.models.responses.AddContributionResponse;
import eg.com.theplanet.akram.models.responses.ContributionsResponse;
import eg.com.theplanet.akram.models.responses.LatestContributionResponse;
import eg.com.theplanet.akram.models.responses.LoginResponse;
import eg.com.theplanet.akram.models.responses.RegisterResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by georgenaiem on 5/25/16.
 */
public interface APIManager {

    @Multipart
    @POST("signup")
    Call<RegisterResponse> register(@PartMap Map<String, RequestBody> map);

    @POST("login-fb")
    Call<LoginResponse> loginWithFacebook(@Query("access_token") String facebookToken);

    @POST("login")
    Call<LoginResponse> loginWithEmail(@Query("email") String email,
                                       @Query("password") String password);

    @POST("refresh-token")
    Call<LoginResponse> refreshToken(@Query("token") String token);

    @POST("add-contribution")
    Call<AddContributionResponse> addContribution(@Query("token") String token,
                                                  @Query("user_id") String userID,
                                                  @Query("type_of_contribution") String type,
                                                  @Query("quantity") String quantity,
                                                  @Query("access") String safety,
                                                  @Query("covered") String isCovered,
                                                  @Query("latitude") double latitude,
                                                  @Query("longitude") double longitude,
                                                  @Query("area") String area,
                                                  @Query("feedback") String feedback);


    @POST("get-contributions")
    Call<ContributionsResponse> getContributions(@Query("token") String token,
                                                 @Query("lat1") String northeastLatitude,
                                                 @Query("lng1") String northeastLongitude,
                                                 @Query("lat2") String southwestLatitude,
                                                 @Query("lng2") String southwestLongitude,
                                                 @Query("zoom_level") String zoomLevel);

    @POST("get-access")
    Call<ContributionsResponse> getAccessibility(@Query("token") String token,
                                                 @Query("lat1") String northeastLatitude,
                                                 @Query("lng1") String northeastLongitude,
                                                 @Query("lat2") String southwestLatitude,
                                                 @Query("lng2") String southwestLongitude,
                                                 @Query("zoom_level") String zoomLevel);

    @POST("get-latest-contribution")
    Call<LatestContributionResponse> getLatestContribution(@Query("token") String token,
                                                           @Query("lat1") String northeastLatitude,
                                                           @Query("lng1") String northeastLongitude,
                                                           @Query("lat2") String southwestLatitude,
                                                           @Query("lng2") String southwestLongitude,
                                                           @Query("zoom_level") String zoomLevel);


    @POST("get-user-contributions")
    Call<ContributionsResponse> getUserContributions(@Query("token") String token,
                                                     @Query("user_id") String userID,
                                                     @Query("offset") int offset);
}


