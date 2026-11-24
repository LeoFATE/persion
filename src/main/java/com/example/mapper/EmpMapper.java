package com.example.mapper;

import com.example.entity.Emp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmpMapper {

    /**
     * 插入员工记录，返回受影响行数
     */
    int insertEmp(Emp emp);

    /**
     * 查询所有员工（不分页）
     */
    List<Emp> selectAll();

    /**
     * 分页查询：offset 从 0 开始
     */
    List<Emp> selectPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计总数
     */
    int countAll();

    /**
     * 更新员工
     */
    int updateEmp(Emp emp);
}
