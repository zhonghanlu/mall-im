package cn.source.system.service;

import java.util.List;
import cn.source.system.domain.MallSchool;

/**
 * 校区Service接口
 * 
 * @author sourcebyte.vip
 * @date 2025-03-09
 */
public interface IMallSchoolService 
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
     * 批量删除校区
     * 
     * @param ids 需要删除的校区主键集合
     * @return 结果
     */
    public int deleteMallSchoolByIds(Long[] ids);

    /**
     * 删除校区信息
     * 
     * @param id 校区主键
     * @return 结果
     */
    public int deleteMallSchoolById(Long id);
}
