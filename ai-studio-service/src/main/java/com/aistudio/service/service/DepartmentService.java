package com.aistudio.service.service;

import com.aistudio.service.entity.SysDepartment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DepartmentService extends IService<SysDepartment> {
    List<SysDepartment> listActiveDepartments();
}
