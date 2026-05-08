package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.MenuRequest;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private static final String APP_CODE_CONSOLE = "CONSOLE";

    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<MenuTreeVO> getMenuTree(Long userId) {
        List<SysMenu> menus = menuMapper.selectByUserId(userId);
        List<MenuTreeVO> vos = dedupeMenus(menus).stream().map(m -> {
            MenuTreeVO vo = new MenuTreeVO();
            BeanUtils.copyProperties(m, vo);
            return vo;
        }).collect(Collectors.toList());
        return buildTree(vos, 0L);
    }

    @Override
    public List<RoleMenuTreeVO> getRoleMenuTree(Long roleId) {
        // 获取所有菜单
        List<SysMenu> allMenus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getAppCode, APP_CODE_CONSOLE)
                .orderByAsc(SysMenu::getSort));
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

    @Override
    public List<SysMenu> listAllMenus() {
        return menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getAppCode, APP_CODE_CONSOLE)
                .orderByAsc(SysMenu::getSort));
    }

    @Override
    public SysMenu getMenuById(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(404, "菜单不存在");
        }
        return menu;
    }

    @Override
    public Long createMenu(MenuRequest request) {
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(request, menu);
        menu.setAppCode(APP_CODE_CONSOLE);
        menuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    public void updateMenu(Long id, MenuRequest request) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(404, "菜单不存在");
        }
        BeanUtils.copyProperties(request, menu);
        menu.setId(id);
        menu.setAppCode(APP_CODE_CONSOLE);
        menuMapper.updateById(menu);
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        // 检查是否有子菜单
        long childCount = menuMapper.selectCount(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException(400, "请先删除子菜单");
        }
        // 检查是否被角色使用
        long roleCount = roleMenuMapper.selectCount(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getMenuId, id));
        if (roleCount > 0) {
            throw new BusinessException(400, "该菜单已被角色使用，无法删除");
        }
        menuMapper.deleteById(id);
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

    private List<SysMenu> dedupeMenus(List<SysMenu> menus) {
        Map<String, SysMenu> map = new LinkedHashMap<>();
        for (SysMenu menu : menus) {
            String key = (menu.getAppCode() == null ? "" : menu.getAppCode()) + "|"
                    + (menu.getPath() == null || menu.getPath().isBlank() ? String.valueOf(menu.getId()) : menu.getPath());
            map.putIfAbsent(key, menu);
        }
        return new ArrayList<>(map.values());
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
