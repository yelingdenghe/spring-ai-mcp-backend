package com.yeling.mcp.tool;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yeling.entity.Product;
import com.yeling.entity.request.CreatProductRequest;
import com.yeling.entity.request.DeleteProductRequest;
import com.yeling.entity.request.ModifyProductRequest;
import com.yeling.entity.request.QueryProductRequest;
import com.yeling.mapper.ProductMapper;
import jakarta.annotation.Resource;
import jdk.jfr.TransitionFrom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName ProductTool
 * @Date 2025/10/8 15:20
 * @Version 1.0
 */
@Component
@Slf4j
public class ProductTool {

    @Resource
    private ProductMapper productMapper;

    // 创建一个ObjectMapper实例用于序列化
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 注册JavaTimeModule以正确处理LocalDateTime等Java 8时间类型
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Transactional
    @Tool(description = "删除一个或多个产品。通过名称和品牌精确删除。")
    public String deleteProduct(DeleteProductRequest request) {
        log.info("====== 调用MCP工具：deleteProduct() ======");
        log.info("====== 删除产品参数，productName: {} ======", request);

        // 安全检查：防止没有任何条件时误删全表数据
        if (!StringUtils.hasText(request.getProductName()) && !StringUtils.hasText(request.getBrand())) {
            return "操作失败：删除条件不能为空，请至少提供一个删除条件（如产品名称或品牌）。";
        }

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        StringJoiner description = new StringJoiner("，"); // 用于拼接删除条件的描述

        // 动态构建查询条件
        if (StringUtils.hasText(request.getProductName())) {
            queryWrapper.eq("product_name", request.getProductName());
            description.add("名称为 '" + request.getProductName() + "'");
        }

        if (StringUtils.hasText(request.getBrand())) {
            queryWrapper.eq("brand", request.getBrand());
            description.add("品牌为 '" + request.getBrand() + "'");
        }
        int deletedRows = productMapper.delete(queryWrapper);

        if (deletedRows > 0) {
            String successMsg = String.format("操作成功, 共删除了 %d 个符合条件 [%s] 的产品。", deletedRows, description);
            log.info(successMsg);
            return successMsg;
        } else {
            String failureMsg = String.format("操作失败, 未找到任何符合条件 [%s] 的产品。", description);
            log.warn(failureMsg);
            return failureMsg;
        }
    }

    @Tool(description = "创建/新增产品")
    public String createProduct(CreatProductRequest request) {
        log.info("====== 调用MCP工具：createProduct() ======");
        log.info("====== 创建/新增产品参数：{} ======", request);

        Product product = new Product();

        // 随机生成id
        product.setProductId(String.valueOf(new Random().nextInt(100000000)));

        BeanUtils.copyProperties(request, product);

        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);

        return "创建/新增产品成功";
    }

    @Tool(description = "查询商品信息。可以根据编号、名称、品牌、价格范围和状态进行筛选，并支持排序和分页。")
    public String queryProduct(QueryProductRequest request) {
        log.info("====== 调用MCP工具：queryProduct() ======");
        log.info("====== 查询产品参数：{} ======", request);

        // 1. 设置分页
        Page<Product> page = new Page<>(request.getPageNumber(), request.getPageSize());

        // 2. 构建动态查询条件
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(request.getProductId()), "product_id", request.getProductId());
        queryWrapper.like(StringUtils.hasText(request.getProductName()), "product_name", request.getProductName());
        queryWrapper.eq(StringUtils.hasText(request.getBrand()), "brand", request.getBrand());
        queryWrapper.ge(request.getMinPrice() != null, "price", request.getMinPrice());
        queryWrapper.le(request.getMaxPrice() != null, "price", request.getMaxPrice());
        queryWrapper.eq(request.getStatus() != null, "status", request.getStatus());

        // 3. 构建动态排序条件
        if (StringUtils.hasText(request.getSortBy())) {
            String dbColumn = null;
            // 将前端友好的排序字段名映射到数据库列名，防止SQL注入风险
            if ("price".equalsIgnoreCase(request.getSortBy())) {
                dbColumn = "price";
            } else if ("createTime".equalsIgnoreCase(request.getSortBy())) {
                dbColumn = "create_time";
            }

            if (dbColumn != null) {
                // 如果sortEnum为空，String.valueOf会返回"null"，equalsIgnoreCase("DESC")为false，isAsc为true，默认升序。
                boolean isAsc = !"DESC".equalsIgnoreCase(String.valueOf(request.getSortOrder()));
                // 是否应用该排序，使用的排序方向和字段名
                queryWrapper.orderBy(true, isAsc, dbColumn);
            }
        }

        // 4. 执行查询
        Page<Product> productPage = productMapper.selectPage(page, queryWrapper);
        List<Product> products = productPage.getRecords();

        // 5. 处理并返回结果
        if (products.isEmpty()) {
            return "未找到符合条件的商品。";
        }

        try {
            // 将结果列表序列化为JSON字符串返回
            return objectMapper.writeValueAsString(products);
        } catch (JsonProcessingException e) {
            log.error("商品列表JSON序列化失败", e);
            return "查询成功，但结果格式化失败。";
        }
    }

    @Transactional
    @Tool(description = "根据商品编号/商品名称+商品品牌来修改商品信息。")
    public String modifyProduct(ModifyProductRequest request) {
        log.info("====== 调用MCP工具：modifyProduct() ======");
        log.info("====== 修改商品参数：{} ======", request);

        // 1. 验证定位条件是否充足
        boolean hasId = StringUtils.hasText(request.getFindProductId());
        boolean hasNameAndBrand = StringUtils.hasText(request.getFindProductName()) && StringUtils.hasText(request.getFindBrand());
        if (!hasId && !hasNameAndBrand) {
            return "操作失败：必须提供商品编号，或同时提供商品名称和品牌来定位要修改的商品。";
        }

        // 2. 验证是否提供了任何要修改的数据
        if (request.getProductName() == null && request.getBrand() == null && request.getPrice() == null &&
                request.getStock() == null && request.getDescription() == null && request.getStatus() == null) {
            return "操作失败：没有提供任何需要修改的商品信息。";
        }

        // 3. 构建查询/定位条件 (WHERE子句)
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        String findConditionDesc;

        if (hasId) {
            queryWrapper.eq("product_id", request.getFindProductId());
            findConditionDesc = "编号为 '" + request.getFindProductId() + "'";
        } else {
            queryWrapper
                    .eq("product_name", request.getFindProductName())
                    .eq("brand", request.getFindBrand());
            findConditionDesc = "名称为 '" + request.getFindProductName() + "'，且品牌为 '" + request.getFindBrand() + "'";
        }

        // 安全检查：在更新前，先确认商品是否存在且唯一
        List<Product> productsToUpdate = productMapper.selectList(queryWrapper);
        if (productsToUpdate.isEmpty()) {
            return "操作失败：未找到符合条件 [" + findConditionDesc + "] 的商品。";
        }
        if (productsToUpdate.size() > 1) {
            return "操作失败：找到多个符合条件的商品，请使用唯一的商品编号进行修改以避免歧义。";
        }

        // 4. 构建更新数据 (SET子句)
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        // 动态设置要更新的字段，只有非null的字段才会被更新
        updateWrapper.set(request.getProductName() != null, "product_name", request.getProductName());
        updateWrapper.set(request.getBrand() != null, "brand", request.getBrand());
        updateWrapper.set(request.getPrice() != null, "price", request.getPrice());
        updateWrapper.set(request.getStock() != null, "stock", request.getStock());
        updateWrapper.set(request.getDescription() != null, "description", request.getDescription());
        updateWrapper.set(request.getStatus() != null, "status", request.getStatus());
        updateWrapper.set("update_time", LocalDateTime.now()); // 自动更新修改时间

        // 直接在 UpdateWrapper 上构建 WHERE 条件
        if (hasId) {
            updateWrapper.eq("product_id", request.getFindProductId());
        } else {
            updateWrapper.eq("product_name", request.getFindProductName())
                    .eq("brand", request.getFindBrand());
        }

        // 5. 执行更新
        int updatedRows = productMapper.update(null, updateWrapper);

        // 6. 返回准确的结果
        if (updatedRows > 0) {
            String successMsg = "操作成功：已更新 " + updatedRows + " 个商品的信息。";
            log.info(successMsg);
            return successMsg;
        } else {
            return "操作失败：更新未生效，请检查商品状态或联系管理员。";
        }
    }
}
