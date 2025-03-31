package cn.source.system.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MallCommunityPic implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long communityId;

    private String picUrl;
}
