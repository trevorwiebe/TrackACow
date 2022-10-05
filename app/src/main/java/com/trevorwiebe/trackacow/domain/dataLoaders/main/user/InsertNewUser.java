package com.trevorwiebe.trackacow.domain.dataLoaders.main.user;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.UserEntity;

@Deprecated(since="Use use-cases instead")
public class InsertNewUser extends AsyncTask<Context, Void, Void> {

    private UserEntity userEntity;

    public InsertNewUser(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).userDao().insertUser(userEntity);
        return null;
    }
}
