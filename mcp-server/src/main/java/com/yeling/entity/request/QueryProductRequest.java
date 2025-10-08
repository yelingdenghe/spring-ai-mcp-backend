package com.yeling.entity.request;

import com.yeling.enums.ListSortEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName QueryProductRequest
 * @Date 2025/10/8 16:06
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QueryProductRequest {

    @ToolParam(description = "商品编号", required = false)
    private String productId;

    @ToolParam(description = "商品名称 (支持模糊查询)", required = false)
    private String productName;

    @ToolParam(description = "商品品牌", required = false)
    private String brand;

    @ToolParam(description = "最低价格", required = false)
    private Integer minPrice;

    @ToolParam(description = "最高价格", required = false)
    private Integer maxPrice;

    @ToolParam(description = "商品状态(下架为0，上架为1，预售为2)", required = false)
    private Integer status;

    // --- 排序条件 ---
    @ToolParam(description = "排序字段 (可选值: 'price', 'createTime')", required = false)
    private String sortBy;

    @ToolParam(description = "排序顺序 (可选值: 'ASC' for 升序, 'DESC' for 降序)", required = false)
    private ListSortEnum sortOrder;

    // --- 分页参数 ---
    @ToolParam(description = "页码，从1开始", required = false)
    private Integer pageNumber = 1; // 默认第一页

    @ToolParam(description = "每页大小，默认10", required = false)
    private Integer pageSize = 10; // 默认每页10条

}
