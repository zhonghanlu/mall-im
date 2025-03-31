package cn.source.system.service.impl;

import cn.source.common.exception.ServiceException;
import cn.source.common.utils.SecurityUtils;
import cn.source.system.domain.ImRelationships;
import cn.source.system.domain.request.OptApplyRequest;
import cn.source.system.mapper.ImRelationshipsMapper;
import cn.source.system.service.IImRelationshipsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
@Service
@RequiredArgsConstructor
public class ImRelationshipsServiceImpl implements IImRelationshipsService {

    private final ImRelationshipsMapper imRelationshipsMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ImRelationships relationships) {
        // 申请
        relationships.setStatus(0);

        // userId < relatedId 存储im关系
        Long userId = SecurityUtils.getUserId();
        Long relatedId = relationships.getRelatedId();

        ImRelationships imRelationships = imRelationshipsMapper.selectByUserIdAndApplyId(userId, relatedId);

        if (Objects.nonNull(imRelationships)) {
            if (0 == imRelationships.getStatus()) {
                throw new ServiceException("已存在好友申请关系，请查看好友申请列表");
            }
            if (1 == imRelationships.getStatus()) {
                throw new ServiceException("已存在好友关系，请查看好友列表");
            }
            imRelationships.setStatus(0);
            imRelationships.setApplyContent(relationships.getApplyContent());
            imRelationships.setApplyId(relationships.getRelatedId());
            imRelationshipsMapper.updateImRelationships(imRelationships);
            return;
        }

        if (userId > relatedId) {
            Long temp = userId;
            userId = relatedId;
            relatedId = temp;
        }
        // 对方接受，对方 id
        relationships.setApplyId(relationships.getRelatedId());
        relationships.setUserId(userId);
        relationships.setRelatedId(relatedId);
        relationships.setCreateBy(String.valueOf(userId));
        relationships.setCreateTime(new Date());
        int a = imRelationshipsMapper.insert(relationships);

        if (a <= 0) {
            throw new ServiceException("申请失败");
        }
    }

    @Override
    public List<ImRelationships> friendList() {
        List<ImRelationships> relationshipsList = new ArrayList<>();
        Long userId = SecurityUtils.getUserId();
        if (Objects.nonNull(userId)) {
            ImRelationships imRelationships = new ImRelationships();
            imRelationships.setUserId(userId);
            imRelationships.setStatus(1);
            relationshipsList = imRelationshipsMapper.selectList(imRelationships);
        }

        // 关联用户信息
        if (CollectionUtils.isNotEmpty(relationshipsList)) {
            Set<Long> userIdList = relationshipsList.stream().map(ImRelationships::getRelatedId).collect(Collectors.toSet());
            relationshipsList = imRelationshipsMapper.selectInfoList(userIdList);
        }

        return relationshipsList;
    }

    @Override
    public List<ImRelationships> friendApplyList() {
        List<ImRelationships> relationshipsList = new ArrayList<>();
        Long userId = SecurityUtils.getUserId();
        if (Objects.nonNull(userId)) {
            ImRelationships imRelationships = new ImRelationships();
            imRelationships.setUserId(userId);
            imRelationships.setStatus(0);
            relationshipsList = imRelationshipsMapper.selectApplyList(imRelationships);
        }

        if (CollectionUtils.isNotEmpty(relationshipsList)) {
            Set<Long> userIdList = relationshipsList.stream().map(ImRelationships::getRelatedId).collect(Collectors.toSet());
            List<ImRelationships> imRelationshipsList = imRelationshipsMapper.selectInfoList(userIdList);
            Map<Long, ImRelationships> relationshipsMap = imRelationshipsList
                    .stream()
                    .collect(Collectors.toMap(ImRelationships::getFriendId, Function.identity()));

            relationshipsList.forEach(relationships -> {
                ImRelationships imRelationships = relationshipsMap.get(relationships.getRelatedId());
                if (Objects.nonNull(imRelationships)) {
                    relationships.setAvatar(imRelationships.getAvatar());
                    relationships.setSchoolName(imRelationships.getSchoolName());
                    relationships.setUserName(imRelationships.getUserName());
                }
            });
        }
        return relationshipsList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void optApply(OptApplyRequest request) {
        // 当前用户 id
        Long userId = SecurityUtils.getUserId();

        Long applyId = request.getApplyId();

        // 比较
        if (userId > applyId) {
            Long temp = userId;
            userId = applyId;
            applyId = temp;
        }

        // 需校验数据是否存在
        ImRelationships imRelationships = imRelationshipsMapper.selectByUserIdAndApplyId(userId, applyId);
        if (Objects.isNull(imRelationships)) {
            throw new ServiceException("申请关系不存在");
        }

        int a = imRelationshipsMapper.updateStatus(userId, applyId, request.getFlag());

        if (a <= 0) {
            throw new ServiceException("操作失败");
        }
    }
}
