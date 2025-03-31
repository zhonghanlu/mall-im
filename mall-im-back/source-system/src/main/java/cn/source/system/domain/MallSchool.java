package cn.source.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.source.common.annotation.Excel;
import cn.source.common.core.domain.BaseEntity;

/**
 * 校区对象 mall_school
 * 
 * @author zhl
 * @date 2025-03-09
 */
public class MallSchool extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 id */
    private Long id;

    /** 校区图片 */
    @Excel(name = "校区图片")
    private String picUrl;

    /** 学校名称 */
    @Excel(name = "学校名称")
    private String schoolName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPicUrl(String picUrl) 
    {
        this.picUrl = picUrl;
    }

    public String getPicUrl() 
    {
        return picUrl;
    }
    public void setSchoolName(String schoolName) 
    {
        this.schoolName = schoolName;
    }

    public String getSchoolName() 
    {
        return schoolName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("picUrl", getPicUrl())
            .append("schoolName", getSchoolName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
