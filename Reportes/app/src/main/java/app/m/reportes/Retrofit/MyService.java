package app.m.reportes.Retrofit;

import android.graphics.Bitmap;

import java.io.File;

import app.m.reportes.Reportes;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MyService {
    @POST("register/")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("email") String email,
                                    @Field("name") String name,
                                    @Field("password") String password);

    @POST("login/")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("email") String email,
                                 @Field("password")String password);

    @POST("reports/")
    @FormUrlEncoded
    Observable<String> createReports(@Field("title") String title,
                                     @Field("descripcion") String descripcion,
                                     @Field("productImage") String productImage,
                                     @Field("usuarioid") String usuarioid);

    @GET("reports/{reportId}")
    @FormUrlEncoded
    Observable<String> coleccion_reportes(@Path("reportId") String reportId);

}
