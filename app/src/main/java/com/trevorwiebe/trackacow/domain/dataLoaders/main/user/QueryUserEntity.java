package com.trevorwiebe.trackacow.domain.dataLoaders.main.user;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.entities.UserEntity;

public class QueryUserEntity extends AsyncTask<Context, Void, UserEntity> {

    private OnUserLoaded onUserLoaded;
    private String uid;

    public QueryUserEntity(String uid, OnUserLoaded onUserLoaded) {
        this.uid = uid;
        this.onUserLoaded = onUserLoaded;
    }

    public interface OnUserLoaded {
        void onUserLoaded(UserEntity userEntity);
    }

    @Override
    protected UserEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).userDao().getUserByUid(uid);
    }

    @Override
    protected void onPostExecute(UserEntity userEntity) {
        super.onPostExecute(userEntity);
        onUserLoaded.onUserLoaded(userEntity);
    }
}
