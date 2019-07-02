package app.m.reportes.Retrofit;

import java.io.File;

import app.m.reportes.Reportes;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

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
                                     @Field("productImage") File productImage,
                                     @Field("usuario_id") String usuario_id);

    @GET("reports/{reportId}")
    @FormUrlEncoded
    Observable<Reportes> coleccion_reportes(@Field("reportId") String reportId);

}
