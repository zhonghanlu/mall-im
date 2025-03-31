package cn.source.common.constant;

/**
 * @author zhl
 * @create 2024/7/17 11:01
 */
public final class RedisConstant {

    private RedisConstant() {
    }

    public static final String PLACEHOLDER = ":";

    /**
     * 验证码相关
     */
    public static final String CAPTCHA_CODE_KEY = "captcha:code:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    ///////////////////////////////////////参数redisKey//////////////////////////////////////////////
    /**
     * 系统参数redis key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    ///////////////////////////////////////加密redisKey//////////////////////////////////////////////
    /**
     * sm解密参数redis key
     */
    public static final String SM2_CACHE_KEY_PREFIX = "sm2-decrypt-cache:";


    /// /////////////////////////////////////业务-机构管理-学生管理//////////////////////////////////////////////
    public static final String SCHOOL_OR_GRADE = "school_or_grade:";

    /**
     * 默认初始密码
     */
    public static final String SYS_CONFIG_INIT_PASSWORD = "init_password";

    ////////////////////////////////////////业务-课程管理-待分班管理//////////////////////////////////////////////

    /**
     * 确认分班机构id
     */
    public static final String CONFIRMED_CLASS_ORG_ID = "confirmed_class_org_id";

    ////////////////////////////////////////业务-机构管理-课程类型//////////////////////////////////////////////

    /**
     * 课程比例缓存前缀
     */
    public static final String COURSE_TYPE_RATIO = "course_type_ratio";

    ////////////////////////////////////////业务-OJ-AI//////////////////////////////////////////////

    /**
     * 多轮对话缓存MAP前缀
     */
    public static final String AI_CHAT_HISTORY = "ai_chat_history:";

    /**
     * 多轮对话缓存轮数前缀
     */
    public static final String AI_CHAT_ROUND_HISTORY = "ai_chat_round_history:";
}
