package com.pirate.arena.app.services;

import com.pirate.arena.app.request.RequestUnlock;
import com.pirate.arena.app.request.RequestUpdateLocker;

public interface IServiceLocker {



    String updateLocker(RequestUpdateLocker requestUpdateLocker, Class clazz);


    String unlockLocker(RequestUnlock requestUnlock , Class clazz);




}
