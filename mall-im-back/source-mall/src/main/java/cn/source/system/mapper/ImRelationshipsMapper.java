package cn.source.system.mapper;

import cn.source.system.domain.ImRelationships;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
public interface ImRelationshipsMapper {

    int insert(ImRelationships imRelationships);

    List<ImRelationships> selectApplyList(ImRelationships imRelationships);

    List<ImRelationships> selectList(ImRelationships imRelationships);

    List<ImRelationships> selectInfoList(@Param("userIdList") Set<Long> userIdList);

    int updateStatus(@Param("userId") Long userId, @Param("applyId") Long applyId, @Param("flag") Integer flag);

    ImRelationships selectByUserIdAndApplyId(@Param("userId") Long userId, @Param("applyId") Long applyId);

    void updateImRelationships(ImRelationships imRelationships);

}
