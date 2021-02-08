package com.example.gezijoke;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.alibaba.fastjson.JSONObject;
import com.example.gezijoke.model.Destination;
import com.example.gezijoke.model.User;
import com.example.gezijoke.ui.login.UserManager;
import com.example.gezijoke.utils.AppConfig;
import com.example.gezijoke.utils.NavGraphBuilder;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.GetRequest;
import com.example.libnetwork.JsonCallBack;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private NavController navController = null;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(fragment);

        navView.setOnNavigationItemSelectedListener(this);

        NavGraphBuilder.build(navController, this,
                getSupportFragmentManager(), fragment.getId());


        GetRequest<JSONObject> request = new GetRequest<>("www.mooc.com");

        request.execute();

        request.execute(new JsonCallBack<JSONObject>() {
            @Override
            public void onSuccess(ApiResponse<JSONObject> response) {
                super.onSuccess(response);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        HashMap<String, Destination> destConfig = AppConfig.getsDestConfig();
        Iterator<Map.Entry<String, Destination>> iterator = destConfig.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Destination> entry = iterator.next();
            Destination value = entry.getValue();
            if (null != value
                    && !UserManager.get().isLogin()
                    && value.isNeedLogin()
                    && value.getId() == item.getItemId()
            ) {
                UserManager.get().login(this).observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        navView.setSelectedItemId(item.getItemId());
                    }
                });
                return false;
            }
        }
        navController.navigate(item.getItemId());
        return !TextUtils.isEmpty(item.getTitle());
    }

}