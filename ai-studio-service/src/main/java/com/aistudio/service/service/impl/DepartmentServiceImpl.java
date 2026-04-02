package com.aistudio.service.service.impl;

import com.aistudio.service.entity.SysDepartment;
import com.aistudio.service.mapper.SysDepartmentMapper;
import com.aistudio.service.service.DepartmentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment> implements DepartmentService {

    @Override
    public List<SysDepartment> listActiveDepartments() {
        return baseMapper.selectList(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getStatus, 1)
                .orderByAsc(SysDepartment::getSort));
    }
}
