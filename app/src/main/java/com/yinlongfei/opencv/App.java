package com.yinlongfei.opencv;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.yinlongfei.opencv.api.ServiceAPI;
import com.yinlongfei.opencv.entity.LoginVM;
import com.yinlongfei.opencv.entity.Token;
import com.yinlongfei.opencv.entity.User;
import com.yinlongfei.opencv.uitls.AccountManager;

import java.io.IOException;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class App extends Application {

    private static ServiceAPI serverAPI;
    private static Context context;

    private String token;
    private static AccountManager accountManager;


    @Override public void onCreate(){
        super.onCreate();

        context = getApplicationContext();

        accountManager = AccountManager.create();

        token = accountManager.getToken();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 添加头部拦截器 并设定验证刷新 *******
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder request = chain.request().newBuilder();
                //添加Token请求头 这里的token应当是从本地数据库中读取的 **********
                request.addHeader("Authorization", token);
                Response proceed = chain.proceed(request.build());
                //如果token过期 再去重新请求token 然后设置token的请求头 重新发起请求 用户无感
                if (isTokenExpired(proceed)){
                    String newHeaderToken = getNewToken();
                    //使用新的Token，创建新的请求
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", newHeaderToken)
                            .build();
                    accountManager.setToken(newHeaderToken);
                    token = newHeaderToken;
                    return chain.proceed(newRequest);
                }
                return proceed;
            }
        });

        OkHttpClient httpClient = builder.addInterceptor(logging).build();

        serverAPI = new Retrofit.Builder()
                .baseUrl(ServiceAPI.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
                .create(ServiceAPI.class);


        Observable<User> account = serverAPI.getAccount();
        account.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        Log.d("aa", "获取帐号信息成功"+user.toString());
                        App.getAccountManager().setAccount(user);
                        System.out.println(user);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("aa", "accept: 失败：" + throwable+"\n");
                    }
                });

        //WechatApi = WXAPIFactory.createWXAPI(this, APP_ID);
    }


    /**
     * 根据Response，判断Token是否失效
     * 401表示token过期
     * @param response
     * @return
     */
    private boolean isTokenExpired(Response response) {
        Log.e("状态码",response.code()+"---------------------");
        if (response.code() == 401) {
            return true;
        }
        return false;
    }

    /**
     * 这里可以考虑让后台提供一个接口,通过用户名直接返回一个token-----------------
     * @return
     * @throws IOException
     */
    @SuppressLint("CheckResult")
    private String getNewToken() throws IOException {


        LoginVM loginRequest = new LoginVM();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin");
        Observable<Token> login = serverAPI.login(loginRequest);
        login.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Token>() {
                    @Override
                    public void accept(Token token1) throws Exception {
                        token = "Bearer "+token1.getIdToken();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("test",throwable.getMessage());
                    }
                });
//
//        OkHttpClient okHttpClient = new OkHttpClient();
//        Gson gson = new Gson();
//        String json = gson.toJson(loginRequest);
//        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
//                , json);
//        Request request = new Request.Builder().url(ServiceAPI.ENDPOINT+"authenticate").post(requestBody).build();
//        Call call = okHttpClient.newCall(request);
//        String string = Objects.requireNonNull(call.execute().body()).string();
//        Token token1 = gson.fromJson(string, Token.class);

        Log.e("token刷新结果",token);
        return token;
    }

    public static ServiceAPI getServerAPI() {
        return serverAPI;
    }

    public static Context getAppContext() {
        return context;
    }

    public static AccountManager getAccountManager(){
        return accountManager;
    }

}