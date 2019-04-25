package com.yinlongfei.opencv.uitls;

import android.content.SharedPreferences;

import com.yinlongfei.opencv.App;
import com.yinlongfei.opencv.entity.User;

public class AccountManager {
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    public static AccountManager create() {
        AccountManager accountManager = new AccountManager();
        accountManager.sp = App.getAppContext().getSharedPreferences("account", 0);
        accountManager.editor = sp.edit();
        return accountManager;
    }

    public void setToken(String token) {
        editor.putString("token", token);
        editor.commit();
    }

    public String getToken() {
        String token = sp.getString("token", "");
        return token;
    }

    public void setAccount(User account) {
        editor.putString("userId", account.getLogin());
        editor.putString("userEmail", account.getEmail());
        editor.putString("userName", account.getFirstName());
        editor.commit();
    }

    public User getAccount() {
        User account = new User();
//        account = sp.getString("token", "");
        account.setLogin(sp.getString("login", ""));
        account.setEmail(sp.getString("userEmail", ""));
        account.setFirstName(sp.getString("userName", ""));
        return account;
    }

    public void clearAccount() {
        editor.putString("token", "");
        editor.putString("login", "");
        editor.putString("userEmail", "");
        editor.putString("userName", "");
        editor.commit();
    }

    public void setUser(User user) {
        editor.putString("login", user.getLogin());
        editor.putString("userEmail", user.getEmail());
        editor.putString("userName", user.getFirstName());
        editor.commit();
    }
}
