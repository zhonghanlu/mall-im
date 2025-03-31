package cn.source.system.service.impl;

import cn.source.common.core.domain.model.LoginUser;
import cn.source.common.exception.ServiceException;
import cn.source.common.utils.DateUtils;
import cn.source.common.utils.SecurityUtils;
import cn.source.common.utils.uuid.IDGenerator;
import cn.source.system.domain.MallCommunity;
import cn.source.system.domain.MallCommunityPic;
import cn.source.system.mapper.MallCommunityMapper;
import cn.source.system.mapper.MallCommunityPicMapper;
import cn.source.system.service.IMallCommunityService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 社区主体表 服务实现类
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
@Service
@RequiredArgsConstructor
public class MallCommunityServiceImpl implements IMallCommunityService {

    private final MallCommunityMapper mallCommunityMapper;

    private final MallCommunityPicMapper mallCommunityPicMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertCommunity(MallCommunity mallCommunity) {
        List<MallCommunityPic> picUrlList = new ArrayList<>();

        LoginUser loginUser = SecurityUtils.getLoginUser();
        mallCommunity.setId(IDGenerator.next());
        mallCommunity.setPushName(loginUser.getUsername());
        mallCommunity.setPushId(loginUser.getUserId());
        mallCommunity.setPushSchool(loginUser.getSchoolName());
        mallCommunity.setStart(0);
        mallCommunity.setRecommend(0);
        mallCommunity.setCreateBy(loginUser.getUsername());
        mallCommunity.setCreateTime(DateUtils.getNowDate());
        int i = mallCommunityMapper.insertCommunity(mallCommunity);
        if (i <= 0) {
            throw new ServiceException("新增失败");
        }

        List<String> picUrl = mallCommunity.getPicUrl();
        if (CollectionUtils.isNotEmpty(picUrl)) {
            picUrl.forEach(item -> {
                MallCommunityPic communityPic = new MallCommunityPic();
                communityPic.setId(IDGenerator.next());
                communityPic.setCommunityId(mallCommunity.getId());
                communityPic.setPicUrl(item);
                picUrlList.add(communityPic);
            });
        }

        int i1 = mallCommunityPicMapper.batchInsert(picUrlList);

        if (i1 <= 0) {
            throw new ServiceException("新增失败");
        }
    }

    @Override
    public List<MallCommunity> list(MallCommunity mallCommunity) {
        return mallCommunityMapper.list(mallCommunity);
    }
}
