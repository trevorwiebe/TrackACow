package com.trevorwiebe.trackacow.domain.dataLoaders.main.user;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.UserEntity;

@Deprecated(since="Use use-cases instead")
public class QueryUserByUid extends AsyncTask<Context, Void, UserEntity> {

    private String uid;
    private OnUserByUidLoaded onUserByUidLoaded;

    public QueryUserByUid(String uid, OnUserByUidLoaded onUserByUidLoaded) {
        this.uid = uid;
        this.onUserByUidLoaded = onUserByUidLoaded;
    }

    public interface OnUserByUidLoaded {
        void onUserByUidLoaded(UserEntity userEntity);
    }

    @Override
    protected UserEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).userDao().getUserByUid(uid);
    }

    @Override
    protected void onPostExecute(UserEntity userEntity) {
        super.onPostExecute(userEntity);
        onUserByUidLoaded.onUserByUidLoaded(userEntity);
    }
}
