package cn.source.system.mapper;

import cn.source.system.domain.MallCommunity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 社区主体表 Mapper 接口
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
@Mapper
public interface MallCommunityMapper {

    int insertCommunity(MallCommunity mallCommunity);

    List<MallCommunity> list(MallCommunity mallCommunity);

}
