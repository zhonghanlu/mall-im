package cn.source.system.service.impl;

import cn.source.common.annotation.DataScope;
import cn.source.common.exception.ServiceException;
import cn.source.common.utils.DateUtils;
import cn.source.common.utils.SecurityUtils;
import cn.source.common.utils.StringUtils;
import cn.source.common.utils.uuid.CodeUtil;
import cn.source.common.utils.uuid.IDGenerator;
import cn.source.system.domain.MallGoods;
import cn.source.system.domain.MallOrder;
import cn.source.system.domain.MallOrderGoods;
import cn.source.system.mapper.MallGoodsMapper;
import cn.source.system.mapper.MallOrderMapper;
import cn.source.system.service.IMallOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 订单信息Service业务层处理
 *
 * @author zhl
 * @date 2025-03-04
 */
@Service
public class MallOrderServiceImpl implements IMallOrderService {
    @Autowired
    private MallOrderMapper mallOrderMapper;

    @Resource
    private MallGoodsMapper mallGoodsMapper;

    /**
     * 查询订单信息
     *
     * @param id 订单信息主键
     * @return 订单信息
     */
    @Override
    public MallOrder selectMallOrderById(Long id) {
        return mallOrderMapper.selectMallOrderById(id);
    }

    /**
     * 查询订单信息列表
     *
     * @param mallOrder 订单信息
     * @return 订单信息
     */
    @Override
    @DataScope(userAlias = "mall_order")
    public List<MallOrder> selectMallOrderList(MallOrder mallOrder) {
        return mallOrderMapper.selectMallOrderList(mallOrder);
    }

    @Override
    public List<MallOrder> selectMallOwnOrderList(MallOrder mallOrder) {
        mallOrder.setUserId(SecurityUtils.getUserId());
        return mallOrderMapper.selectMallOwnOrderList(mallOrder);
    }

    /**
     * 新增订单信息
     *
     * @param mallOrder 订单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long insertMallOrder(MallOrder mallOrder) {
        mallOrder.setId(IDGenerator.next());
        if (StringUtils.isNull(mallOrder.getUserId())) {
            mallOrder.setUserId(SecurityUtils.getUserId());
        }
        mallOrder.setOrderCode(CodeUtil.getCode());
        mallOrder.setOrderCreateTime(DateUtils.getNowDate());
        mallOrder.setPayStatus("1");//代付款
        mallOrder.setOrderStatus("1");//代付款
        mallOrder.setCreateTime(DateUtils.getNowDate());
        mallOrder.setSortNo(0);
        // 保存订单
        mallOrderMapper.insertMallOrder(mallOrder);
        List<MallOrderGoods> mallOrderGoodsList = mallOrder.getMallOrderGoodsList();
        for (MallOrderGoods mallOrderGoods : mallOrderGoodsList) {
            mallOrderGoods.setId(IDGenerator.next());
            // 设置订单id
            mallOrderGoods.setOrderId(mallOrder.getId());

            // 商品库存扣减
            MallGoods mallGoods = mallGoodsMapper.selectMallGoodsById(mallOrderGoods.getGoodsId());
            if (mallGoods.getGoodsStock() - mallOrderGoods.getValue() < 0) {
                throw new ServiceException("库存不足");
            }
            mallGoods.setGoodsStock(mallGoods.getGoodsStock() - mallOrderGoods.getValue());
            mallGoodsMapper.updateMallGoods(mallGoods);
        }

        // 保存订单商品信息
        mallOrderMapper.insertMallOrderGoods(mallOrderGoodsList);
        return mallOrder.getId();
    }

    /**
     * 修改订单信息
     *
     * @param mallOrder 订单信息
     * @return 结果
     */
    @Override
    public int updateMallOrder(MallOrder mallOrder) {
        mallOrder.setUpdateTime(DateUtils.getNowDate());
        return mallOrderMapper.updateMallOrder(mallOrder);
    }

    /**
     * 批量删除订单信息
     *
     * @param ids 需要删除的订单信息主键
     * @return 结果
     */
    @Override
    public int deleteMallOrderByIds(Long[] ids) {
        return mallOrderMapper.deleteMallOrderByIds(ids);
    }

    /**
     * 删除订单信息信息
     *
     * @param id 订单信息主键
     * @return 结果
     */
    @Override
    public int deleteMallOrderById(Long id) {
        return mallOrderMapper.deleteMallOrderById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deliver(Long id) {
        MallOrder mallOrder = mallOrderMapper.selectMallOrderById(id);
        if (Objects.isNull(mallOrder)) {
            throw new ServiceException("订单不存在");
        }

        if (!"2".equals(mallOrder.getOrderStatus())) {
            throw new ServiceException("订单已发货");
        }

        mallOrder.setOrderStatus("5");

        int i = mallOrderMapper.updateMallOrder(mallOrder);

        if (i <= 0) {
            throw new ServiceException("发货失败");
        }

    }
}
