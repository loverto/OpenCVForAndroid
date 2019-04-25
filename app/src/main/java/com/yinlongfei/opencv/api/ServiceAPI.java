package com.yinlongfei.opencv.api;

import com.yinlongfei.opencv.entity.LoginVM;
import com.yinlongfei.opencv.entity.Photo;
import com.yinlongfei.opencv.entity.Token;
import com.yinlongfei.opencv.entity.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceAPI {

    String ENDPOINT = "http://192.168.3.108:8080/"; //http://119.23.13.228";

    @GET("/api/account")
    Observable<User> getAccount();

    @POST("/api/authenticate")
    Observable<Token> login(@Body LoginVM loginVM);

    @GET("/api/photos")
    Observable<List<Photo>> getPhoto(@Query("page") int page, @Query("size") int size);


}
