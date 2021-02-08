package com.example.libnetwork;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;

public class JsonConvert implements Convert {
    @Override
    public Object convert(String response, Type type) {
        JSONObject object = JSON.parseObject(response);
        JSONObject data = object.getJSONObject("data");
        if (null!=data){
            Object data1=data.get("data");
            return  JSON.parseObject(data1.toString(),type);
        }

        return null;
    }

    @Override
    public Object convert(String response, Class claz) {
        JSONObject object = JSON.parseObject(response);
        JSONObject data = object.getJSONObject("data");
        if (null!=data){
            Object data1=data.get("data");
            return  JSON.parseObject(data1.toString(),claz);
        }


        return null;
    }
}
