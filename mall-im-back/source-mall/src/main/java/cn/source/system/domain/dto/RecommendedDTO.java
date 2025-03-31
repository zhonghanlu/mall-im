package cn.source.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author : zhl
 * @Date: 2025/3/15 17:33
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedDTO {

    private Long userId;

    private Long goodsId;

    private Long quantity;

}
