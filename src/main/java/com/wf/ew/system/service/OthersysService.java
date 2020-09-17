package com.wf.ew.system.service;

import com.wf.ew.system.model.User;

public interface OthersysService {

    public void addAllUser(User user);

    public void updateAllUser(User user);

    public void updateAllPwd(User user, String oldPassword, String newPassword);

    public void deleteAllUser(User user);

}
