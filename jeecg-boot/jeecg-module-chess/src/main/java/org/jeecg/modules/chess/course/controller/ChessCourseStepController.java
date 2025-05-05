package org.jeecg.modules.chess.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.chess.course.entity.ChessCourseStep;
import org.jeecg.modules.chess.course.service.IChessCourseStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.jeecg.common.system.query.QueryGenerator;

@Slf4j
@Tag(name="国际象棋课程步骤")
@RestController
@RequestMapping("/course/chessCourseStep")
public class ChessCourseStepController extends JeecgController<ChessCourseStep, IChessCourseStepService> {
	@Autowired
	private IChessCourseStepService chessCourseStepService;
	
	/**
	 * 分页列表查询
	 *
	 * @param chessCourseStep
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "国际象棋课程步骤-分页列表查询")
	@Operation(summary = "国际象棋课程步骤-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ChessCourseStep chessCourseStep,
								@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								HttpServletRequest req) {
		QueryWrapper<ChessCourseStep> queryWrapper = QueryGenerator.initQueryWrapper(chessCourseStep, req.getParameterMap());
		Page<ChessCourseStep> page = new Page<ChessCourseStep>(pageNo, pageSize);
		IPage<ChessCourseStep> pageList = chessCourseStepService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param chessCourseStep
	 * @return
	 */
	@AutoLog(value = "国际象棋课程步骤-添加")
	@Operation(summary = "国际象棋课程步骤-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ChessCourseStep chessCourseStep) {
		chessCourseStepService.save(chessCourseStep);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param chessCourseStep
	 * @return
	 */
	@AutoLog(value = "国际象棋课程步骤-编辑")
	@Operation(summary = "国际象棋课程步骤-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody ChessCourseStep chessCourseStep) {
		chessCourseStepService.updateById(chessCourseStep);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "国际象棋课程步骤-通过id删除")
	@Operation(summary = "国际象棋课程步骤-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		chessCourseStepService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "国际象棋课程步骤-批量删除")
	@Operation(summary = "国际象棋课程步骤-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.chessCourseStepService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "国际象棋课程步骤-通过id查询")
	@Operation(summary = "国际象棋课程步骤-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ChessCourseStep chessCourseStep = chessCourseStepService.getById(id);
		return Result.OK(chessCourseStep);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param chessCourseStep
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, ChessCourseStep chessCourseStep) {
      return super.exportXls(request, chessCourseStep, ChessCourseStep.class, "国际象棋课程步骤");
  }

  /**
   * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      return super.importExcel(request, response, ChessCourseStep.class);
  }
}