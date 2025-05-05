package org.jeecg.modules.chess.score.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.chess.score.entity.ChessPlayerScore;
import org.jeecg.modules.chess.score.service.IChessPlayerScoreService;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

 /**
 * @Description: 游戏选手积分
 * @Author: jeecg-boot
 * @Date:   2025-05-02
 * @Version: V1.0
 */
@Slf4j
@Tag(name="游戏选手积分")
@RestController
@RequestMapping("/score/chessPlayerScore")
public class ChessPlayerScoreController extends JeecgController<ChessPlayerScore, IChessPlayerScoreService> {
	@Autowired
	private IChessPlayerScoreService chessPlayerScoreService;
	
	/**
	 * 分页列表查询
	 *
	 * @param chessPlayerScore
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "游戏选手积分-分页列表查询")
	@Operation(summary = "游戏选手积分-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ChessPlayerScore chessPlayerScore,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ChessPlayerScore> queryWrapper = QueryGenerator.initQueryWrapper(chessPlayerScore, req.getParameterMap());
		Page<ChessPlayerScore> page = new Page<ChessPlayerScore>(pageNo, pageSize);
		IPage<ChessPlayerScore> pageList = chessPlayerScoreService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param chessPlayerScore
	 * @return
	 */
	@AutoLog(value = "游戏选手积分-添加")
	@Operation(summary = "游戏选手积分-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ChessPlayerScore chessPlayerScore) {
		chessPlayerScoreService.save(chessPlayerScore);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param chessPlayerScore
	 * @return
	 */
	@AutoLog(value = "游戏选手积分-编辑")
	@Operation(summary = "游戏选手积分-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody ChessPlayerScore chessPlayerScore) {
		chessPlayerScoreService.updateById(chessPlayerScore);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "游戏选手积分-通过id删除")
	@Operation(summary = "游戏选手积分-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		chessPlayerScoreService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "游戏选手积分-批量删除")
	@Operation(summary = "游戏选手积分-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.chessPlayerScoreService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "游戏选手积分-通过id查询")
	@Operation(summary = "游戏选手积分-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ChessPlayerScore chessPlayerScore = chessPlayerScoreService.getById(id);
		return Result.OK(chessPlayerScore);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param chessPlayerScore
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, ChessPlayerScore chessPlayerScore) {
      return super.exportXls(request, chessPlayerScore, ChessPlayerScore.class, "游戏选手积分");
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
      return super.importExcel(request, response, ChessPlayerScore.class);
  }

}
