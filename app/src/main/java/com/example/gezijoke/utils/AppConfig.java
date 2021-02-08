package com.example.gezijoke.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.gezijoke.model.BottomBar;
import com.example.gezijoke.model.Destination;
import com.example.libcommon.AppGlobals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class AppConfig {

    private static HashMap<String, Destination> sDestConfig;

    private static BottomBar sBottomBar;

    public static HashMap<String, Destination> getsDestConfig() {
        if (null == sDestConfig) {
            String content = parseFile("destination.json");
            sDestConfig = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
            }.getType());
        }
        return sDestConfig;
    }

    /**
     * 解析json文件
     *
     * @param fileName
     * @return
     */
    private static String parseFile(String fileName) {
        AssetManager assets = AppGlobals.getsApplication().getResources().getAssets();

        InputStream stream = null;
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            stream = assets.open(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(stream));

            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != stream) {
                    stream.close();
                }
                if (null != bufferedReader) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return builder.toString();
    }


    public static BottomBar getBottomBar() {
        if (null == sBottomBar) {
            String content = parseFile("main_tabs_config.json");
            sBottomBar = JSON.parseObject(content, BottomBar.class);
        }

        return sBottomBar;
    }
}
