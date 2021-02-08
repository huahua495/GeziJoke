package com.example.libnetwork.cache;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.libcommon.AppGlobals;

/**
 * exportSchema = true
 *  生成的json文件，描述
 */
@Database(entities = {Cache.class},version = 1,exportSchema = true)
public abstract class CacheDatabase extends RoomDatabase {
    private static final CacheDatabase ppjoke_cache;
    static {
        //创建内存数据库  但是这种数据库的数据只存在内存中,进程被杀后，数据随之丢失
//        Room.inMemoryDatabaseBuilder()

         ppjoke_cache = Room.databaseBuilder(
                AppGlobals.getsApplication(),
                CacheDatabase.class,
                "ppjoke_cache"
        )
                //是否允许在主线程进行查询
                .allowMainThreadQueries()
                //数据库创建和打开后的回调
//        .addCallback()
                //设置查询的线程池
//        .setQueryExecutor()
                //
//        .openHelperFactory()
                //room 的日志模式
//        .setJournalMode()

                //数据库升级异常之后的回滚
//                .fallbackToDestructiveMigration()
                //数据库升级异常后，回滚到指定的版本
//                .fallbackToDestructiveMigrationFrom()
//                .addMigrations(CacheDatabase.sMigration)
                .build();

    }

    public abstract CacheDao getCache();

    public static CacheDatabase get() {
        return ppjoke_cache;
    }

    /*    static Migration sMigration = new Migration(1, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("alter table teacher rename to student");

            database.execSQL("alter table teacher add column teacher_age INTEGER NOT NULL default 0");
        }
    };*/
}
