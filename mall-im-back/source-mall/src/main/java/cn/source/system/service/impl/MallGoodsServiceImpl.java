package cn.source.system.service.impl;

import cn.source.common.exception.ServiceException;
import cn.source.common.utils.DateUtils;
import cn.source.common.utils.SecurityUtils;
import cn.source.common.utils.StringUtils;
import cn.source.common.utils.UserBasedCollaborativeFiltering;
import cn.source.common.utils.uuid.IDGenerator;
import cn.source.system.domain.MallClassify;
import cn.source.system.domain.MallGoodReview;
import cn.source.system.domain.MallGoods;
import cn.source.system.domain.MallGoodsSpec;
import cn.source.system.domain.dto.RecommendedDTO;
import cn.source.system.mapper.MallClassifyMapper;
import cn.source.system.mapper.MallGoodReviewMapper;
import cn.source.system.mapper.MallGoodsMapper;
import cn.source.system.service.IMallGoodsService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品管理Service业务层处理
 *
 * @author zhl
 * @date 2025-03-04
 */
@Service
public class MallGoodsServiceImpl implements IMallGoodsService {
    @Autowired
    private MallGoodsMapper mallGoodsMapper;

    @Resource
    private MallClassifyMapper mallClassifyMapper;

    @Resource
    private MallGoodReviewMapper mallGoodReviewMapper;

    /**
     * 查询商品管理
     *
     * @param id 商品管理主键
     * @return 商品管理
     */
    @Override
    public MallGoods selectMallGoodsById(Long id) {
        return mallGoodsMapper.selectMallGoodsById(id);
    }

    /**
     * 查询商品管理列表
     *
     * @param mallGoods 商品管理
     * @return 商品管理
     */
    @Override
    public List<MallGoods> selectMallGoodsList(MallGoods mallGoods) {
        return mallGoodsMapper.selectMallGoodsList(mallGoods);
    }

    /**
     * 新增商品管理
     *
     * @param mallGoods 商品管理
     * @return 结果
     */
    @Transactional
    @Override
    public int insertMallGoods(MallGoods mallGoods) {
        mallGoods.setCreateTime(DateUtils.getNowDate());
        int rows = mallGoodsMapper.insertMallGoods(mallGoods);
        insertMallGoodsSpec(mallGoods);
        return rows;
    }

    /**
     * 修改商品管理
     *
     * @param mallGoods 商品管理
     * @return 结果
     */
    @Transactional
    @Override
    public int updateMallGoods(MallGoods mallGoods) {
        mallGoods.setUpdateTime(DateUtils.getNowDate());
        mallGoodsMapper.deleteMallGoodsSpecByParentId(mallGoods.getId());
        insertMallGoodsSpec(mallGoods);
        return mallGoodsMapper.updateMallGoods(mallGoods);
    }

    /**
     * 批量删除商品管理
     *
     * @param ids 需要删除的商品管理主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteMallGoodsByIds(Long[] ids) {
        mallGoodsMapper.deleteMallGoodsSpecByParentIds(ids);
        return mallGoodsMapper.deleteMallGoodsByIds(ids);
    }

    /**
     * 删除商品管理信息
     *
     * @param id 商品管理主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteMallGoodsById(Long id) {
        mallGoodsMapper.deleteMallGoodsSpecByParentId(id);
        return mallGoodsMapper.deleteMallGoodsById(id);
    }

    /**
     * 新增商品规格信息
     *
     * @param mallGoods 商品管理对象
     */
    public void insertMallGoodsSpec(MallGoods mallGoods) {
        List<MallGoodsSpec> mallGoodsSpecList = mallGoods.getMallGoodsSpecList();
        Long id = mallGoods.getId();
        if (StringUtils.isNotNull(mallGoodsSpecList)) {
            List<MallGoodsSpec> list = new ArrayList<MallGoodsSpec>();
            for (MallGoodsSpec mallGoodsSpec : mallGoodsSpecList) {
                mallGoodsSpec.setParentId(id);
                list.add(mallGoodsSpec);
            }
            if (list.size() > 0) {
                mallGoodsMapper.batchMallGoodsSpec(list);
            }
        }
    }

    /**
     * 修改商品管理
     *
     * @param mallGoods 商品管理
     * @return 结果
     */
    @Transactional
    @Override
    public int changeStatus(MallGoods mallGoods) {
        mallGoods.setUpdateTime(DateUtils.getNowDate());
        return mallGoodsMapper.changeStatus(mallGoods);
    }

    @Override
    public List<MallGoods> pcList(MallGoods mallGoods) {

        String goodsClassify = mallGoods.getGoodsClassify();
        if (StringUtils.isNotEmpty(goodsClassify)) {
            MallClassify mallClassify = new MallClassify();
            mallClassify.setClassifyName(goodsClassify);
            List<MallClassify> mallClassifyList = mallClassifyMapper.selectMallClassifyList(mallClassify);
            // TODO
        }

        List<MallGoods> mallGoodsList = new ArrayList<>();
        switch (Objects.isNull(mallGoods.getSearchFlag()) ? 1 : mallGoods.getSearchFlag()) {
            case 2:
                mallGoodsList = mallGoodsMapper.selectMallGoodsList(mallGoods);
                break;
            case 3:
                mallGoods.setHotStatus("0");
                mallGoodsList = mallGoodsMapper.selectMallGoodsList(mallGoods);
                break;
            default:
                //默认为 1 走推荐
                mallGoodsList = mallGoodsMapper.selectMallGoodsList(mallGoods);
        }
        return mallGoodsList;
    }

    @Override
    public void addView(MallGoodReview mallGoodReview) {
        mallGoodReview.setId(IDGenerator.next());
        mallGoodReview.setReviewUserId(SecurityUtils.getUserId());
        mallGoodReview.setReviewUserName(SecurityUtils.getUsername());
        mallGoodReview.setReviewDate(LocalDateTime.now());
        mallGoodReview.setCreateBy(String.valueOf(SecurityUtils.getUserId()));
        mallGoodReview.setCreateTime(new Date());
        int i = mallGoodReviewMapper.insert(mallGoodReview);
        if (i <= 0) {
            throw new ServiceException("评价失败");
        }
    }

    @Override
    public List<MallGoods> recommended() {
        List<MallGoods> mallGoodsList;
        Map<Long, Map<Long, Long>> recommendedMap = new HashMap<>();

        List<RecommendedDTO> recommendedDTOList = mallGoodsMapper.recommendedBaseData();
        // 过滤null 数据
        recommendedDTOList = recommendedDTOList.stream().filter(recommendedDTO ->
                recommendedDTO.getUserId() != null
                        && recommendedDTO.getGoodsId() != null
                        && recommendedDTO.getQuantity() != null).collect(Collectors.toList());

        // 根据用户维度整理数据
        Map<Long, List<RecommendedDTO>> userMap = recommendedDTOList
                .stream().collect(Collectors.groupingBy(RecommendedDTO::getUserId));

        userMap.forEach((key, value) -> {
            // 商品对应的购买个数
            Map<Long, Long> goodsMap = value.stream()
                    .collect(Collectors.toMap(RecommendedDTO::getGoodsId,
                            RecommendedDTO::getQuantity, (k1, k2) -> k2));
            recommendedMap.put(key, goodsMap);
        });

        Long userId = SecurityUtils.getUserId();

        Map<Long, Long> userSelfMap = recommendedMap.get(userId);
        if (Objects.isNull(userSelfMap) || userSelfMap.isEmpty()) {
            List<MallGoods> mallGoodsList1 = mallGoodsMapper.selectMallGoodsList(new MallGoods());
            return mallGoodsList1.stream().limit(5).collect(Collectors.toList());
        }

        List<Long> goodsIdList = UserBasedCollaborativeFiltering.recommendItems(recommendedMap, userId);

        if (CollectionUtils.isNotEmpty(goodsIdList)) {
            mallGoodsList = mallGoodsMapper.selectMallGoodsListByIdList(goodsIdList);
            return mallGoodsList;
        }

        return Collections.emptyList();
    }
}
