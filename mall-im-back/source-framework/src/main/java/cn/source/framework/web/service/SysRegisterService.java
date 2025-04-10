package cn.source.framework.web.service;

import cn.source.common.constant.Constants;
import cn.source.common.constant.UserConstants;
import cn.source.common.core.domain.entity.SysUser;
import cn.source.common.core.domain.model.RegisterBody;
import cn.source.common.core.redis.RedisCache;
import cn.source.common.exception.user.CaptchaException;
import cn.source.common.exception.user.CaptchaExpireException;
import cn.source.common.utils.MessageUtils;
import cn.source.common.utils.SecurityUtils;
import cn.source.common.utils.StringUtils;
import cn.source.framework.manager.AsyncManager;
import cn.source.framework.manager.factory.AsyncFactory;
import cn.source.system.domain.MallSchool;
import cn.source.system.service.IMallSchoolService;
import cn.source.system.service.ISysConfigService;
import cn.source.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 注册校验方法
 *
 * @author ruoyi
 */
@Component
public class SysRegisterService {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    @Resource
    private IMallSchoolService mallSchoolService;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody) {
        Long schoolId = registerBody.getSchoolId();
        String nickName = registerBody.getNickName();

        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();

        boolean captchaOnOff = configService.selectCaptchaOnOff();
        // 验证码开关
        if (captchaOnOff) {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username)) {
            msg = "用户名不能为空";
        } else if (StringUtils.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(username))) {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        } else {
            SysUser sysUser = new SysUser();
            if (Objects.nonNull(schoolId)) {
                MallSchool mallSchool = mallSchoolService.selectMallSchoolById(schoolId);
                sysUser.setSchoolId(schoolId);
                sysUser.setSchoolName(mallSchool.getSchoolName());
            }
            sysUser.setUserName(username);
            sysUser.setNickName(StringUtils.isNotBlank(nickName) ? nickName : username);
            sysUser.setPassword(SecurityUtils.encryptPassword(registerBody.getPassword()));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag) {
                msg = "注册失败,请联系系统管理人员";
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER,
                        MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException();
        }
    }
}
