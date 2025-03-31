package cn.source.web.controller.system;

import cn.source.common.annotation.Log;
import cn.source.common.core.controller.BaseController;
import cn.source.common.core.domain.AjaxResult;
import cn.source.common.core.page.TableDataInfo;
import cn.source.common.enums.BusinessType;
import cn.source.common.utils.poi.ExcelUtil;
import cn.source.system.domain.MallSchool;
import cn.source.system.service.IMallSchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 校区Controller
 *
 * @author zhl
 * @date 2025-03-09
 */
@RestController
@RequestMapping("/system/school")
public class MallSchoolController extends BaseController {
    @Autowired
    private IMallSchoolService mallSchoolService;

    /**
     * 查询校区列表
     */
    @PreAuthorize("@ss.hasPermi('system:school:list')")
    @GetMapping("/list")
    public TableDataInfo list(MallSchool mallSchool) {
        startPage();
        List<MallSchool> list = mallSchoolService.selectMallSchoolList(mallSchool);
        return getDataTable(list);
    }

    @GetMapping("/list-all")
    public AjaxResult listAll() {
        MallSchool mallSchool = new MallSchool();
        List<MallSchool> list = mallSchoolService.selectMallSchoolList(mallSchool);
        return AjaxResult.success(list);
    }

    @GetMapping("/index")
    public AjaxResult schoolIndex() {
        MallSchool mallSchool = new MallSchool();
        List<MallSchool> list = mallSchoolService.selectMallSchoolList(mallSchool);
        return AjaxResult.success(list);
    }

    /**
     * 获取校区详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(mallSchoolService.selectMallSchoolById(id));
    }

    /**
     * 新增校区
     */
    @PreAuthorize("@ss.hasPermi('system:school:add')")
    @Log(title = "校区", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MallSchool mallSchool) {
        return toAjax(mallSchoolService.insertMallSchool(mallSchool));
    }

    /**
     * 修改校区
     */
    @PreAuthorize("@ss.hasPermi('system:school:edit')")
    @Log(title = "校区", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MallSchool mallSchool) {
        return toAjax(mallSchoolService.updateMallSchool(mallSchool));
    }

    /**
     * 删除校区
     */
    @PreAuthorize("@ss.hasPermi('system:school:remove')")
    @Log(title = "校区", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(mallSchoolService.deleteMallSchoolByIds(ids));
    }

    /**
     * 导出校区列表
     */
    @PreAuthorize("@ss.hasPermi('system:school:export')")
    @Log(title = "校区", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MallSchool mallSchool) {
        List<MallSchool> list = mallSchoolService.selectMallSchoolList(mallSchool);
        ExcelUtil<MallSchool> util = new ExcelUtil<MallSchool>(MallSchool.class);
        util.exportExcel(response, list, "校区数据");
    }
}
