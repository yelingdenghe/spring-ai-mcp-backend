package com.yeling.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName OperatorSummary
 * @Date 2025/10/23 10:14
 * @Version 1.0
 */
public record OperatorSummary(
        @JsonPropertyDescription("干员的代号, 例如 '阿米娅'")
        @JsonProperty("name")
        String name,

        @JsonPropertyDescription("干员的基础档案信息，必须从工具返回的数据中提取")
        @JsonProperty("base_info")
        BaseInfo baseInfo,

        @JsonPropertyDescription("干员的客观履历，即干员的经历和背景故事。可以从工具返回的 '档案资料' 字段中提取和总结。")
        @JsonProperty("resume")
        String resume,

        @JsonPropertyDescription("干员的综合体检测试结果，必须从工具返回的数据中提取")
        @JsonProperty("physical_exam")
        PhysicalExam physicalExam,

        @JsonPropertyDescription("干员的矿石病感染情况和临床诊断分析。必须从 '档案4文本'、'矿石病感染情况'、'体细胞与源石融合率' 等字段中提取和总结。")
        @JsonProperty("infection_info")
        InfectionInfo infectionInfo
) {

    // 嵌套 Record，使结构更清晰

    public record BaseInfo(
            @JsonPropertyDescription("性别")
            String gender,
            @JsonPropertyDescription("种族")
            String race,
            @JsonPropertyDescription("身高")
            String height,
            @JsonPropertyDescription("出身地")
            String birthplace,
            @JsonPropertyDescription("生日")
            String birthday,
            @JsonPropertyDescription("战斗经验")
            @JsonProperty("combat_experience")
            String combatExperience
    ) {}

    public record PhysicalExam(
            @JsonPropertyDescription("物理强度")
            @JsonProperty("physical_strength")
            String physicalStrength,
            @JsonPropertyDescription("战场机动")
            String mobility,
            @JsonPropertyDescription("生理耐受")
            @JsonProperty("physiological_endurance")
            String physiologicalEndurance,
            @JsonPropertyDescription("战术规划")
            @JsonProperty("tactical_planning")
            String tacticalPlanning,
            @JsonPropertyDescription("战斗技巧")
            @JsonProperty("combat_skill")
            String combatSkill,
            @JsonPropertyDescription("源石技艺适应性")
            @JsonProperty("arts_adaptability")
            String artsAdaptability
    ) {}

    public record InfectionInfo(
            @JsonPropertyDescription("矿石病感染情况的文字描述, 例如 '体表有源石结晶分布...'")
            @JsonProperty("status_desc")
            String statusDesc,
            @JsonPropertyDescription("体细胞与源石融合率, 例如 '19%'")
            @JsonProperty("fusion_rate")
            String fusionRate,
            @JsonPropertyDescription("血液源石结晶密度, 例如 '0.27u/L'")
            @JsonProperty("crystal_density")
            String crystalDensity,
            @JsonPropertyDescription("临床诊断分析的总结, 例如 '造影检测结果显示...'")
            @JsonProperty("analysis_summary")
            String analysisSummary
    ) {}
}