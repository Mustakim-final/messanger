package com.example.messenger.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA_ZzMrVw:APA91bHF2qT3reOEg0EDzsOXgzgRzecE6yXqv8x6V6G5fXyjXpNl7-rYIZeqrd1XkczbXO87j3Xgh8243eJ9xzr0hvNHbv9CZ8RpGL-6uIy1EWTqVWv21l2c2-mZUlS03J4E9FB-oV3i"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
