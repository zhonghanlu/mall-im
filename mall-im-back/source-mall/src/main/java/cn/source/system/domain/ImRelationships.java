package cn.source.system.domain;

import cn.source.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class ImRelationships extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long relatedId;

    /**
     * 0 申请 1 通过 2拒绝
     */
    private Integer status;

    private String applyContent;

    private Long applyId;

    /// /////////////////////////////

    private Long friendId;

    private Long schoolId;

    private String userName;

    private String schoolName;

    private String avatar;

    private LocalDateTime lastTime;
}
