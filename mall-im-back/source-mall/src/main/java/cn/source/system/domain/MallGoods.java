package cn.source.system.domain;

import cn.source.common.annotation.Excel;
import cn.source.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品管理对象 mall_goods
 *
 * @author zhl
 * @date 2025-03-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MallGoods extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 商品分类
     */
    @Excel(name = "商品分类")
    private String goodsClassify;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称")
    private String goodsName;

    /**
     * 卖点
     */
    @Excel(name = "卖点")
    private String goodsFeature;

    /**
     * 销售价
     */
    @Excel(name = "销售价")
    private BigDecimal newPrice;

    /**
     * 划线价
     */
    @Excel(name = "划线价")
    private BigDecimal oldPrice;

    /**
     * 库存
     */
    @Excel(name = "库存")
    private Integer goodsStock;

    /**
     * 主图
     */
    @Excel(name = "主图")
    private String goodsFaceUrl;

    /**
     * 轮播图
     */
    private String goodsItemUrl;

    /**
     * 图文详情
     */
    private String articleContent;

    /**
     * 排序
     */
    private Integer sortNo;

    /**
     * 是否上架
     */
    @Excel(name = "是否上架")
    private String status;

    /**
     * 是否上架
     */
    @Excel(name = "是否热点")
    private String hotStatus;

    /**
     * 浏览量
     */
    private Integer goodsView;

    /**
     * 商品规格信息
     */
    private List<MallGoodsSpec> mallGoodsSpecList;

    /**
     * 商品评价信息
     */
    private List<MallGoodReview> mallGoodReviewList;


    /// //////////////////////////////////////////

    /**
     * 搜索标识
     */
    private Integer searchFlag;

}
