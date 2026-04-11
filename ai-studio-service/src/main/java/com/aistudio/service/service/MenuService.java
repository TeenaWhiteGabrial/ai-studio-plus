package com.aistudio.service.service;

import com.aistudio.service.dto.response.MenuTreeVO;
import com.aistudio.service.dto.response.RoleMenuTreeVO;

import java.util.List;

public interface MenuService {
    List<MenuTreeVO> getMenuTree(Long userId);
    List<RoleMenuTreeVO> getRoleMenuTree(Long roleId);
    void updateRoleMenus(Long roleId, List<Long> menuIds);
}
