package cn.source.system.mapper;


import cn.source.system.domain.MallCommunityPic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
@Mapper
public interface MallCommunityPicMapper {

    int batchInsert(@Param("list") List<MallCommunityPic> list);

}
