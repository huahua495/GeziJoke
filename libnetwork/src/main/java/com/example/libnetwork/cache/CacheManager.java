package com.example.libnetwork.cache;

import android.animation.TypeConverter;
import android.system.Os;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CacheManager {
    public static <T> void save(String key, T body) {

        Cache cache = new Cache();
        cache.key = key;
        cache.data = toByteArray(body);
        CacheDatabase.get().getCache().save(cache);
    }

    public static Object getCache(String key) {
        Cache cache = CacheDatabase.get().getCache().getCache(key);
        if (null != cache && cache.data != null) {
            return toObject(cache.data);
        }
        return null;
    }

    private static Object toObject(byte[] data) {
        ByteArrayInputStream bais=null;
        ObjectInputStream ois=null;

        try {
            bais=new ByteArrayInputStream(data);
            ois=new ObjectInputStream(bais);
            return ois.readObject();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (null != ois) {
                    ois.close();
                }
                if (null != bais) {
                    bais.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private static <T> byte[] toByteArray(T body) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(body);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != baos) {
                    baos.close();
                }
                if (null != oos) {
                    oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }
}
