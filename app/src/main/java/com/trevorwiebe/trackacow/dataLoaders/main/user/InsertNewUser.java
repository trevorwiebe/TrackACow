package com.trevorwiebe.trackacow.dataLoaders.main.user;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.ui.auth.data.model.User;
import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.UserEntity;

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
