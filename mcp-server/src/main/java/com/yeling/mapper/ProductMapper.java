package com.yeling.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeling.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName ProductMapper
 * @Date 2025/10/8 10:31
 * @Version 1.0
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
