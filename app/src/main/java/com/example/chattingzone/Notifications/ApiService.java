package com.example.chattingzone.Notifications;

import com.example.chattingzone.Notifications.MyResponse;
import com.example.chattingzone.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA9xq0aUk:APA91bE6cAdGcWbgycu_JqN3ZWJj0xdnZUGAr3hRDV85W0qyL8Lyw3vKvXrev2HpiikI4Qy6Kq5Ch6dS1L4x29rbuQGWKqqhuUDwQo4enjGMiXZlTob5tyNh2PVZR0NaU3B25rvqt2YS"})

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);


}
