package com.aistudio.service.service;

import com.aistudio.service.dto.request.BatchUserImportRequest;
import com.aistudio.service.dto.request.ChangePasswordRequest;
import com.aistudio.service.dto.request.UpdateProfileRequest;
import com.aistudio.service.dto.request.UserCreateRequest;
import com.aistudio.service.dto.request.UserUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.UserImportResult;
import com.aistudio.service.entity.SysUser;

import java.util.List;

public interface UserService {
    PageResult<SysUser> listUsers(int page, int size, String keyword, String department);
    Long createUser(UserCreateRequest request);
    void updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    void assignRoles(Long userId, List<Long> roleIds);
    List<UserImportResult> batchImport(List<BatchUserImportRequest> requests);
    SysUser getById(Long id);
    void updateUserStatus(Long id, Integer status);
    void changePassword(Long userId, ChangePasswordRequest request);
    void updateProfile(Long userId, UpdateProfileRequest request);
}
