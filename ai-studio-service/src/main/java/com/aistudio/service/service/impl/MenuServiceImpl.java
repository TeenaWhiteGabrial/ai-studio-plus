package com.aistudio.service.service.impl;

import com.aistudio.service.dto.response.MenuTreeVO;
import com.aistudio.service.dto.response.RoleMenuTreeVO;
import com.aistudio.service.entity.SysMenu;
import com.aistudio.service.entity.SysRoleMenu;
import com.aistudio.service.mapper.SysMenuMapper;
import com.aistudio.service.mapper.SysRoleMenuMapper;
import com.aistudio.service.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<MenuTreeVO> getMenuTree(Long userId) {
        List<SysMenu> menus = menuMapper.selectByUserId(userId);
        List<MenuTreeVO> vos = menus.stream().map(m -> {
            MenuTreeVO vo = new MenuTreeVO();
            BeanUtils.copyProperties(m, vo);
            return vo;
        }).collect(Collectors.toList());
        return buildTree(vos, 0L);
    }

    @Override
    public List<RoleMenuTreeVO> getRoleMenuTree(Long roleId) {
        // 获取所有菜单
        List<SysMenu> allMenus = menuMapper.selectList(null);
        // 获取角色已分配的菜单ID
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        Set<Long> roleMenuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toSet());

        // 构建带选中状态的菜单树
        List<RoleMenuTreeVO> vos = allMenus.stream().map(m -> {
            RoleMenuTreeVO vo = new RoleMenuTreeVO();
            BeanUtils.copyProperties(m, vo);
            vo.setChecked(roleMenuIds.contains(m.getId()));
            return vo;
        }).collect(Collectors.toList());

        return buildRoleMenuTree(vos, 0L);
    }

    @Override
    @Transactional
    public void updateRoleMenus(Long roleId, List<Long> menuIds) {
        // 删除原有菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));
        // 插入新的菜单关联
        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    private List<MenuTreeVO> buildTree(List<MenuTreeVO> all, Long parentId) {
        List<MenuTreeVO> result = new ArrayList<>();
        for (MenuTreeVO vo : all) {
            if (parentId.equals(vo.getParentId())) {
                vo.setChildren(buildTree(all, vo.getId()));
                result.add(vo);
            }
        }
        return result;
    }

    private List<RoleMenuTreeVO> buildRoleMenuTree(List<RoleMenuTreeVO> all, Long parentId) {
        List<RoleMenuTreeVO> result = new ArrayList<>();
        for (RoleMenuTreeVO vo : all) {
            if (parentId.equals(vo.getParentId())) {
                vo.setChildren(buildRoleMenuTree(all, vo.getId()));
                result.add(vo);
            }
        }
        return result;
    }
}
