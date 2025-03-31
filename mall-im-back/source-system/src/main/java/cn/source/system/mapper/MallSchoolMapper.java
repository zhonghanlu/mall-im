package cn.source.system.mapper;

import java.util.List;
import cn.source.system.domain.MallSchool;

/**
 * 校区Mapper接口
 * 
 * @author sourcebyte.vip
 * @date 2025-03-09
 */
public interface MallSchoolMapper 
{
    /**
     * 查询校区
     * 
     * @param id 校区主键
     * @return 校区
     */
    public MallSchool selectMallSchoolById(Long id);

    /**
     * 查询校区列表
     * 
     * @param mallSchool 校区
     * @return 校区集合
     */
    public List<MallSchool> selectMallSchoolList(MallSchool mallSchool);

    /**
     * 新增校区
     * 
     * @param mallSchool 校区
     * @return 结果
     */
    public int insertMallSchool(MallSchool mallSchool);

    /**
     * 修改校区
     * 
     * @param mallSchool 校区
     * @return 结果
     */
    public int updateMallSchool(MallSchool mallSchool);

    /**
     * 删除校区
     * 
     * @param id 校区主键
     * @return 结果
     */
    public int deleteMallSchoolById(Long id);

    /**
     * 批量删除校区
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMallSchoolByIds(Long[] ids);
}
