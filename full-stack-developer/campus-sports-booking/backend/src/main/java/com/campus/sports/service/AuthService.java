package com.campus.sports.service;

import com.campus.sports.common.result.Result;
import com.campus.sports.entity.system.user.SystemUser;

public interface AuthService {
    
    Result<String> login(String username, String password);
    
    Result<Void> logout();
    
    Result<SystemUser> getCurrentUser();
}
