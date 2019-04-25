package com.yinlongfei.opencv.uitls;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class TokenAuthenticator implements Authenticator {
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (response.code() == 401) {
            //Call<Void> refreshCall = refereshAccessToken(refereshToken);


            //make it as retrofit synchronous call
//            Response<Void> refreshResponse = refreshCall.execute();
//            if (refreshResponse != null && refreshResponse.code() == 200) {
//                //read new JWT value from response body or interceptor depending upon your JWT availability logic
//                newCookieValue = readNewJwtValue();
//                return response.request().newBuilder()
//                        .header("basic-auth", newCookieValue)
//                        .build();
//            } else {
//                return null;
//            }
        }
        return null;
    }
}