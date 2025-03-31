package cn.source.system.controller;


import cn.source.common.core.controller.BaseController;
import cn.source.common.core.domain.AjaxResult;
import cn.source.common.core.page.TableDataInfo;
import cn.source.common.utils.PageUtils;
import cn.source.system.domain.MallCommunity;
import cn.source.system.service.IMallCommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 社区主体表 前端控制器
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/mall-community")
public class MallCommunityController extends BaseController {

    private final IMallCommunityService mallCommunityService;

    @PostMapping("/add")
    public AjaxResult insertCommunity(@RequestBody MallCommunity mallCommunity) {
        mallCommunityService.insertCommunity(mallCommunity);
        return AjaxResult.success();
    }

    @GetMapping("/list")
    public TableDataInfo list(MallCommunity mallCommunity) {
        PageUtils.startPage();
        List<MallCommunity> mallCommunityList = mallCommunityService.list(mallCommunity);
        return getDataTable(mallCommunityList);
    }
}
