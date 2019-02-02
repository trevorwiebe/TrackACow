package com.trevorwiebe.trackacow.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.trevorwiebe.trackacow.db.dao.CowDao;
import com.trevorwiebe.trackacow.db.dao.DrugDao;
import com.trevorwiebe.trackacow.db.dao.DrugsGivenDao;
import com.trevorwiebe.trackacow.db.dao.PenDao;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;

@Database(entities = {PenEntity.class, CowEntity.class, DrugsGivenEntity.class, DrugEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract PenDao penDao();
    public abstract CowDao cowDao();
    public abstract DrugsGivenDao drugsGivenDao();
    public abstract DrugDao drugDao();

    public static AppDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class,
                    "track_a_cow_db")
                    .build();
        }
        return INSTANCE;
    }
}
