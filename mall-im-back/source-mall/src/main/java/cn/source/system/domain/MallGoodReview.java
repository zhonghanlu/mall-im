package cn.source.system.domain;

import cn.source.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class MallGoodReview extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long goodId;

    private Long reviewUserId;

    private String reviewUserUrl;

    private String reviewUserName;

    private Integer score;

    private LocalDateTime reviewDate;

    private String content;
}
