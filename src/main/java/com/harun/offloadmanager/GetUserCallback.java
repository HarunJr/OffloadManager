package com.harun.offloadmanager;

import com.harun.offloadmanager.models.User;

/**
 * Created by HARUN on 10/12/2016.
 */

public interface GetUserCallback {

    public abstract void done(User returnedUser);
}
