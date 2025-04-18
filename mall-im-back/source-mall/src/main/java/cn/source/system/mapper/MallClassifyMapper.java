package cn.source.system.mapper;

import cn.source.system.domain.MallClassify;

import java.util.List;

/**
 * 分类管理Mapper接口
 *
 * @author zhl
 * @date 2025-03-04
 */
public interface MallClassifyMapper {
    /**
     * 查询分类管理
     *
     * @param id 分类管理主键
     * @return 分类管理
     */
    public MallClassify selectMallClassifyById(Long id);

    /**
     * 查询分类管理列表
     *
     * @param mallClassify 分类管理
     * @return 分类管理集合
     */
    public List<MallClassify> selectMallClassifyList(MallClassify mallClassify);

    /**
     * 新增分类管理
     *
     * @param mallClassify 分类管理
     * @return 结果
     */
    public int insertMallClassify(MallClassify mallClassify);

    /**
     * 修改分类管理
     *
     * @param mallClassify 分类管理
     * @return 结果
     */
    public int updateMallClassify(MallClassify mallClassify);

    /**
     * 删除分类管理
     *
     * @param id 分类管理主键
     * @return 结果
     */
    public int deleteMallClassifyById(Long id);

    /**
     * 批量删除分类管理
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMallClassifyByIds(Long[] ids);
}
