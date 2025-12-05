package org.example.springboot_hd.mapper;

import org.example.springboot_hd.entity.ExaminationPackage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExaminationPackageMapper {

    /**
     * 根据套餐ID查询
     */
    ExaminationPackage selectById(@Param("packageId") Long packageId);

    /**
     * 查询所有套餐
     */
    List<ExaminationPackage> selectAll();

    /**
     * 新增套餐
     */
    int insert(ExaminationPackage examinationPackage);

    /**
     * 根据ID更新套餐
     */
    int updateById(ExaminationPackage examinationPackage);

    /**
     * 根据ID删除套餐
     */
    int deleteById(@Param("packageId") Long packageId);
    // 分页查询方法（配合MyBatis分页插件）
    List<ExaminationPackage> selectPage(
            @Param("packageName") String packageName,
            @Param("packageStatus") String packageStatus,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("pageNum") int pageNum,
            @Param("pageSize") int pageSize);

    //查询总条数（配合分页）
    int selectTotal(
            @Param("packageName") String packageName,
            @Param("packageStatus") String packageStatus,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);
}