package police2.com.crimewatchers.api;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface {

    //http://35.154.87.55/api/setStatus?auth=KLASDG987GHSMPO34908234OIRJG&id=03mVEN9lTDXD8glnRaEv0VEQr8g1&status=offline
    @GET("setStatus")
    Call<ResponseBody> setStatus(@Query("auth") String apikey, @Query("id") String id, @Query("status") String status);



    //http://35.154.87.55/api/reassignTask?auth=KLASDG987GHSMPO34908234OIRJG&taskid=rtb24012017051636pm&policeid=Yoqr5emHcMPDkxMCrvz72bqcSvt2
    @GET("reassignTask")
    Call<ResponseBody> reassignTask(@Query("auth") String apikey, @Query("taskid") String taskid, @Query("policeid") String policeid);

}