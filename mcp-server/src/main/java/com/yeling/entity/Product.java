package com.yeling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName Product
 * @Date 2025/10/8 10:24
 * @Version 1.0
 */
@Data
@TableName("product")
public class Product {
    @TableId("product_id")
    private String productId;

    @TableField("product_name")
    private String productName;

    private String brand;
    private Integer price;
    private Integer stock;
    private String description;
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

}
