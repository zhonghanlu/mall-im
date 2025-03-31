package cn.source.system.service.impl;

import cn.source.common.utils.uuid.IDGenerator;
import cn.source.system.domain.MallSchool;
import cn.source.system.mapper.MallSchoolMapper;
import cn.source.system.service.IMallSchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 校区Service业务层处理
 *
 * @author sourcebyte.vip
 * @date 2025-03-09
 */
@Service
public class MallSchoolServiceImpl implements IMallSchoolService {
    @Autowired
    private MallSchoolMapper mallSchoolMapper;

    /**
     * 查询校区
     *
     * @param id 校区主键
     * @return 校区
     */
    @Override
    public MallSchool selectMallSchoolById(Long id) {
        return mallSchoolMapper.selectMallSchoolById(id);
    }

    /**
     * 查询校区列表
     *
     * @param mallSchool 校区
     * @return 校区
     */
    @Override
    public List<MallSchool> selectMallSchoolList(MallSchool mallSchool) {
        return mallSchoolMapper.selectMallSchoolList(mallSchool);
    }

    /**
     * 新增校区
     *
     * @param mallSchool 校区
     * @return 结果
     */
    @Override
    public int insertMallSchool(MallSchool mallSchool) {
        mallSchool.setId(IDGenerator.next());
        return mallSchoolMapper.insertMallSchool(mallSchool);
    }

    /**
     * 修改校区
     *
     * @param mallSchool 校区
     * @return 结果
     */
    @Override
    public int updateMallSchool(MallSchool mallSchool) {
        return mallSchoolMapper.updateMallSchool(mallSchool);
    }

    /**
     * 批量删除校区
     *
     * @param ids 需要删除的校区主键
     * @return 结果
     */
    @Override
    public int deleteMallSchoolByIds(Long[] ids) {
        return mallSchoolMapper.deleteMallSchoolByIds(ids);
    }

    /**
     * 删除校区信息
     *
     * @param id 校区主键
     * @return 结果
     */
    @Override
    public int deleteMallSchoolById(Long id) {
        return mallSchoolMapper.deleteMallSchoolById(id);
    }
}
