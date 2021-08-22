package com.example.wincc_alarme;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolder {
    @GET("api/Alarm")
    Call<List<AlarmModel>> getAlarme();
}
