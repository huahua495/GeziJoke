package com.example.libnetwork.cache;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "cache")
public class Cache implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @NotNull
    public String key;
    public byte[] data;


}
