package com.yeling.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName DeleteProductRequest
 * @Date 2025/10/8 15:55
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteProductRequest {

    @ToolParam(description = "要删除的商品名称 (可选)", required = false)
    private String productName;

    @ToolParam(description = "要删除的商品品牌 (可选，用于精确查找同名商品)", required = false)
    private String brand;

}
