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
import org.jeecg.modules.chess.course.entity.ChessBoardSetup;
import org.jeecg.modules.chess.course.service.IChessBoardSetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Tag(name="棋盘初始设置")
@RestController
@RequestMapping("/course/chessBoardSetup")
public class ChessBoardSetupController extends JeecgController<ChessBoardSetup, IChessBoardSetupService> {
	@Autowired
	private IChessBoardSetupService chessBoardSetupService;
	
	/**
	 * 分页列表查询
	 *
	 * @param chessBoardSetup
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "棋盘初始设置-分页列表查询")
	@Operation(summary = "棋盘初始设置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ChessBoardSetup chessBoardSetup,
								@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								HttpServletRequest req) {
		QueryWrapper<ChessBoardSetup> queryWrapper = new QueryWrapper<>(chessBoardSetup);
		Page<ChessBoardSetup> page = new Page<ChessBoardSetup>(pageNo, pageSize);
		IPage<ChessBoardSetup> pageList = chessBoardSetupService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param chessBoardSetup
	 * @return
	 */
	@AutoLog(value = "棋盘初始设置-添加")
	@Operation(summary = "棋盘初始设置-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ChessBoardSetup chessBoardSetup) {
		chessBoardSetupService.save(chessBoardSetup);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param chessBoardSetup
	 * @return
	 */
	@AutoLog(value = "棋盘初始设置-编辑")
	@Operation(summary = "棋盘初始设置-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody ChessBoardSetup chessBoardSetup) {
		chessBoardSetupService.updateById(chessBoardSetup);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "棋盘初始设置-通过id删除")
	@Operation(summary = "棋盘初始设置-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		chessBoardSetupService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "棋盘初始设置-批量删除")
	@Operation(summary = "棋盘初始设置-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.chessBoardSetupService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "棋盘初始设置-通过id查询")
	@Operation(summary = "棋盘初始设置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ChessBoardSetup chessBoardSetup = chessBoardSetupService.getById(id);
		return Result.OK(chessBoardSetup);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param chessBoardSetup
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, ChessBoardSetup chessBoardSetup) {
      return super.exportXls(request, chessBoardSetup, ChessBoardSetup.class, "棋盘初始设置");
  }

  /**
   * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
      return super.importExcel(request, response, ChessBoardSetup.class);
  }
}