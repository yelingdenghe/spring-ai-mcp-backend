package com.yeling.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName ModifyProductRequest
 * @Date 2025/10/8 17:20
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ModifyProductRequest {

    // --- 定位条件 (用于WHERE子句) ---
    @ToolParam(description = "要修改的商品的【确切】编号。如果提供，将优先使用此条件定位商品。", required = false)
    private String findProductId;

    @ToolParam(description = "要修改的商品的【原】名称。当商品编号未知时，与商品品牌组合使用来定位商品。", required = false)
    private String findProductName;

    @ToolParam(description = "要修改的商品的【原】品牌。当商品编号未知时，与商品名称组合使用来定位商品。", required = false)
    private String findBrand;

    // --- 待更新的数据 (用于SET子句) ---
    // 注意：这些字段名与Product实体类完全对应
    @ToolParam(description = "要更新成的【新】商品名称", required = false)
    private String productName;

    @ToolParam(description = "要更新成的【新】商品品牌", required = false)
    private String brand;

    @ToolParam(description = "要更新成的【新】商品价格", required = false)
    private Integer price;

    @ToolParam(description = "要更新成的【新】商品库存", required = false)
    private Integer stock;

    @ToolParam(description = "要更新成的【新】商品描述", required = false)
    private String description;

    @ToolParam(description = "要更新成的【新】商品状态(下架为0，上架为1，预售为2)", required = false)
    private Integer status;

}
