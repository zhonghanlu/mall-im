package cn.source.system.domain.vo;

import cn.source.system.domain.MallGoods;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author : zhl
 * @Date: 2025/3/9 17:01
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MallGoodsVo {

    /**
     * 主体内容商品
     */
    private List<MallGoods> contentList;

    /**
     * 热销商品
     */
    private List<MallGoods> hotList;

}
