package org.example.springboot_hd.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserImportExportMapper {
    /**
     * 检查手机号是否存在（用于导入验证）
     */
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * 检查身份证号是否存在（用于导入验证）
     */
    boolean existsByIdCard(@Param("idCard") String idCard);

    /**
     * 检查账号是否存在（用于导入验证）
     */
    boolean existsByAccount(@Param("account") String account);
}
