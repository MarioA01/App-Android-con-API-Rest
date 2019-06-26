package app.m.reportes.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MyService {
    @POST("register/")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("email") String email,
                                    @Field("name") String name,
                                    @Field("password") String password);

    @POST("login/")
    @FormUrlEncoded
    Observable<Boolean> loginUser(@Field("email") String email,
                                 @Field("password")String password);
}
