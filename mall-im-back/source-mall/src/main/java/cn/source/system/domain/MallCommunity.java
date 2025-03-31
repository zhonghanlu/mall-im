package cn.source.system.domain;

import cn.source.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 社区主体表
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MallCommunity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long pushId;

    private String avatar;

    private Long goodsId;

    private String pushName;

    private String pushSchool;

    private String pushContent;

    private Integer start;

    private Integer recommend;

    /// ///////////////////////////////////////

    private Long schoolId;

    private List<String> picUrl;

    private List<MallCommunityPic> mallCommunityPicList;
}
