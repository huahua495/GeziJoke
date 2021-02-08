package com.example.libnetwork.cache;

import androidx.room.TypeConverter;

import java.net.PortUnreachableException;
import java.util.Date;

public class DataConverter {

    @TypeConverter
    public static Long data2Long(Date date){
        return date.getTime();
    }

    @TypeConverter
    public static Date long2Date(Long date){
        return new Date(date);
    }
}

