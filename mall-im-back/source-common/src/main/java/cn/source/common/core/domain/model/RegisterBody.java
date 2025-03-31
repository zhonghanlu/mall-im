package cn.source.common.core.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户注册对象
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterBody extends LoginBody {
    /**
     * 校区 id
     */
    private Long schoolId;

    /**
     * 昵称
     */
    private String nickName;


}
