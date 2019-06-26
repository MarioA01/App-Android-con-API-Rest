package app.m.reportes;

import app.m.reportes.Retrofit.MyService;
import app.m.reportes.Retrofit.RetrofitClient;

public class Connection {

    private Connection(){}

    public static final String API_URL = "http://192.168.0.30:3000/API/"; //Aqui pones tu ip de tu compu

    public static MyService getServiceRemote(){
        return RetrofitClient.getClient(API_URL).create(MyService.class);
    }
}
