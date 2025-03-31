package cn.source.system.service;

import cn.source.system.domain.MallCommunity;

import java.util.List;

/**
 * <p>
 * 社区主体表 服务类
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
public interface IMallCommunityService {

    void insertCommunity(MallCommunity mallCommunity);

    List<MallCommunity> list(MallCommunity mallCommunity);
}
