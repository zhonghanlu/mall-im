package cn.source.system.controller;


import cn.source.common.core.domain.AjaxResult;
import cn.source.system.domain.ImRelationships;
import cn.source.system.domain.request.OptApplyRequest;
import cn.source.system.service.IImRelationshipsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhl
 * @since 2025-03-09
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/im-relationships")
public class ImRelationshipsController {

    private final IImRelationshipsService imRelationshipsService;

    @PostMapping("/add")
    public AjaxResult addFriend(@RequestBody ImRelationships relationships) {
        imRelationshipsService.add(relationships);
        return AjaxResult.success();
    }

    @GetMapping("/friend-list")
    public AjaxResult friendList() {
        List<ImRelationships> imRelationshipsList = imRelationshipsService.friendList();
        return AjaxResult.success(imRelationshipsList);
    }

    @GetMapping("/friend-apply-list")
    public AjaxResult friendApplyList() {
        List<ImRelationships> imRelationshipsList = imRelationshipsService.friendApplyList();
        return AjaxResult.success(imRelationshipsList);
    }

    @PostMapping("/opt-apply")
    public AjaxResult optApply(@RequestBody OptApplyRequest request) {
        imRelationshipsService.optApply(request);
        return AjaxResult.success();
    }

}
