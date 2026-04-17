package com.aistudio.service.service;

import com.aistudio.service.dto.request.MenuRequest;
import com.aistudio.service.dto.response.MenuTreeVO;
import com.aistudio.service.dto.response.RoleMenuTreeVO;
import com.aistudio.service.entity.SysMenu;

import java.util.List;

public interface MenuService {
    List<MenuTreeVO> getMenuTree(Long userId);
    List<RoleMenuTreeVO> getRoleMenuTree(Long roleId);
    void updateRoleMenus(Long roleId, List<Long> menuIds);

    List<SysMenu> listAllMenus();
    SysMenu getMenuById(Long id);
    Long createMenu(MenuRequest request);
    void updateMenu(Long id, MenuRequest request);
    void deleteMenu(Long id);
}
