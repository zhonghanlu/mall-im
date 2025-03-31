package cn.source.system.service;

import cn.source.system.domain.ImRelationships;
import cn.source.system.domain.request.OptApplyRequest;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
public interface IImRelationshipsService {

    void add(ImRelationships relationships);

    List<ImRelationships> friendList();

    List<ImRelationships> friendApplyList();

    void optApply(OptApplyRequest request);
}
