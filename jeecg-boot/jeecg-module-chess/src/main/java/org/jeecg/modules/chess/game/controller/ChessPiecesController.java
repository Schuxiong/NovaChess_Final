package org.jeecg.modules.chess.game.controller;

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
import org.jeecg.modules.chess.game.entity.ChessPieces;
import org.jeecg.modules.chess.game.service.IChessPiecesService;
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
 * @Description: 棋子位置
 * @Author: jeecg-boot
 * @Date:   2025-04-27
 * @Version: V1.0
 */
@Slf4j
@Tag(name="棋子位置")
@RestController
@RequestMapping("/game/chessPieces")
public class ChessPiecesController extends JeecgController<ChessPieces, IChessPiecesService> {
	@Autowired
	private IChessPiecesService chessPiecesService;
	
	/**
	 * 分页列表查询
	 *
	 * @param chessPieces
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "棋子位置-分页列表查询")
	@Operation(summary = "棋子位置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ChessPieces chessPieces,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ChessPieces> queryWrapper = QueryGenerator.initQueryWrapper(chessPieces, req.getParameterMap());
		Page<ChessPieces> page = new Page<ChessPieces>(pageNo, pageSize);
		IPage<ChessPieces> pageList = chessPiecesService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param chessPieces
	 * @return
	 */
	@AutoLog(value = "棋子位置-添加")
	@Operation(summary = "棋子位置-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ChessPieces chessPieces) {
		chessPiecesService.save(chessPieces);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param chessPieces
	 * @return
	 */
	@AutoLog(value = "棋子位置-编辑")
	@Operation(summary = "棋子位置-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody ChessPieces chessPieces) {
		chessPiecesService.updateById(chessPieces);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "棋子位置-通过id删除")
	@Operation(summary = "棋子位置-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		chessPiecesService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "棋子位置-批量删除")
	@Operation(summary = "棋子位置-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.chessPiecesService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "棋子位置-通过id查询")
	@Operation(summary = "棋子位置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ChessPieces chessPieces = chessPiecesService.getById(id);
		return Result.OK(chessPieces);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param chessPieces
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, ChessPieces chessPieces) {
      return super.exportXls(request, chessPieces, ChessPieces.class, "棋子位置");
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
      return super.importExcel(request, response, ChessPieces.class);
  }

}
