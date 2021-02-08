package com.example.gezijoke.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gezijoke.R;
import com.example.gezijoke.model.User;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.ApiService;
import com.example.libnetwork.JsonCallBack;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActiivty extends AppCompatActivity implements View.OnClickListener {
    private View actionColse;
    private View actionLogin;
    private Tencent tencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_actiivty);


        actionColse = findViewById(R.id.action_close);
        actionLogin = findViewById(R.id.action_login);


        actionColse.setOnClickListener(this);
        actionLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_close) {
            finish();
        } else if (v.getId() == R.id.action_login) {
            login();
        }
    }

    private void login() {

        tencent = Tencent.createInstance("101935588",
                getApplicationContext());

        tencent.login(this, "all", new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject response = (JSONObject) o;

                try {
                    String openid = response.getString("openid");
                    String access_token = response.getString("access_token");
                    String expires_in = response.getString("expires_in");
                    long expires_time = response.getLong("expires_time");

                    tencent.setAccessToken(access_token, expires_in);
                    tencent.setOpenId(openid);

                    QQToken qqToken = tencent.getQQToken();

                    getUserInfo(qqToken, expires_time, openid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(getApplicationContext(), "登录失败" + uiError.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "登录取消", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getUserInfo(QQToken qqToken, long expires_time, String openid) {
        UserInfo userInfo = new UserInfo(getApplicationContext(), qqToken);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                //     ret	返回码
                //     msg	如果ret<0，会有相应的错误信息提示，返回数据全部用UTF-8编码。
                //     nickname	用户在QQ空间的昵称。
                //     figureurl	大小为30×30像素的QQ空间头像URL。
                //     figureurl_1	大小为50×50像素的QQ空间头像URL。
                //     figureurl_2	大小为100×100像素的QQ空间头像URL。
                //     figureurl_qq_1	大小为40×40像素的QQ头像URL。
                //     figureurl_qq_2	大小为100×100像素的QQ头像URL。需要注意，不是所有的用户都拥有QQ的100x100的头像，但40x40像素则是一定会有。
                //     gender	性别。 如果获取不到则默认返回"男"


                JSONObject response = (JSONObject) o;
                try {
                    String nickname = response.getString("nickname");
                    String figureurl_2 = response.getString("figureurl_2");

                    save(nickname, figureurl_2, openid, expires_time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(getApplicationContext(), "登录失败" + uiError.toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "登录取消", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void save(String nickname, String figureurl_2, String openid, long expires_time) {

        ApiService.get("/user/insert")
                .addParam("name", nickname)
                .addParam("qqOpenId", openid)
                .addParam("expires_time", expires_time)
                .addParam("avatar", figureurl_2)
                .execute(new JsonCallBack<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        if (response.body != null) {
                            UserManager.get().save(response.body);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "登录失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ApiResponse<User> response) {
                        Toast.makeText(getApplicationContext(), "登录失败" + response.message, Toast.LENGTH_SHORT).show();


                    }
                });

    }
}