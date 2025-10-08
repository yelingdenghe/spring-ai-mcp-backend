package com.yeling.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName CreatProductRequest
 * @Date 2025/10/8 15:17
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreatProductRequest {

    @ToolParam(description = "商品名称")
    private String productName;

    @ToolParam(description = "商品品牌")
    private String brand;

    @ToolParam(description = "商品价格")
    private Integer price;

    @ToolParam(description = "商品库存")
    private Integer stock;

    @ToolParam(description = "商品描述(可为空)", required = false)
    private String description;

    @ToolParam(description = "商品状态(下架为0，上架为1，预售为2)")
    private Integer status;

}
