package org.jeecg.modules.chess.pair.controller;

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
import org.jeecg.modules.chess.pair.entity.ChessFriendPair;
import org.jeecg.modules.chess.pair.service.IChessFriendPairService;
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
 * @Description: 对弈关系
 * @Author: jeecg-boot
 * @Date:   2025-04-25
 * @Version: V1.0
 */
@Slf4j
@Tag(name="对弈关系")
@RestController
@RequestMapping("/pair/chessFriendPair")
public class ChessFriendPairController extends JeecgController<ChessFriendPair, IChessFriendPairService> {
	@Autowired
	private IChessFriendPairService chessFriendPairService;
	
	/**
	 * 分页列表查询
	 *
	 * @param chessFriendPair
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "对弈关系-分页列表查询")
	@Operation(summary = "对弈关系-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ChessFriendPair chessFriendPair,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ChessFriendPair> queryWrapper = QueryGenerator.initQueryWrapper(chessFriendPair, req.getParameterMap());
		Page<ChessFriendPair> page = new Page<ChessFriendPair>(pageNo, pageSize);
		IPage<ChessFriendPair> pageList = chessFriendPairService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param chessFriendPair
	 * @return
	 */
	@AutoLog(value = "对弈关系-添加")
	@Operation(summary = "对弈关系-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ChessFriendPair chessFriendPair) {
		chessFriendPairService.save(chessFriendPair);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param chessFriendPair
	 * @return
	 */
	@AutoLog(value = "对弈关系-编辑")
	@Operation(summary = "对弈关系-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody ChessFriendPair chessFriendPair) {
		chessFriendPairService.updateById(chessFriendPair);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "对弈关系-通过id删除")
	@Operation(summary = "对弈关系-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		chessFriendPairService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "对弈关系-批量删除")
	@Operation(summary = "对弈关系-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.chessFriendPairService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "对弈关系-通过id查询")
	@Operation(summary = "对弈关系-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ChessFriendPair chessFriendPair = chessFriendPairService.getById(id);
		return Result.OK(chessFriendPair);
	}




  /**
   * 导出excel
   *
   * @param request
   * @param chessFriendPair
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, ChessFriendPair chessFriendPair) {
      return super.exportXls(request, chessFriendPair, ChessFriendPair.class, "对弈关系");
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
      return super.importExcel(request, response, ChessFriendPair.class);
  }

}
