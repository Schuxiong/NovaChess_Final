package org.jeecg.modules.chess.game.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.chess.game.entity.ChessGame;
import org.jeecg.modules.chess.game.entity.ChessPieces;
import org.jeecg.modules.chess.game.service.IChessGameService;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.chess.game.vo.ChessGameVO;
import org.jeecg.modules.chess.game.vo.PlayerPairVO;
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
 * @Description: 游戏
 * @Author: jeecg-boot
 * @Date:   2025-04-27
 * @Version: V1.0
 */
@Slf4j
@Tag(name="游戏")
@RestController
@RequestMapping("/game/chessGame")
public class ChessGameController extends JeecgController<ChessGame, IChessGameService> {
	@Autowired
	private IChessGameService chessGameService;


	 /**
	  * 游戏初始化
	  *
	  * @param targetPlayerPairVO
	  *
	  * @return
	  */
	 @AutoLog(value = "游戏初始化")
	 @Operation(summary = "游戏初始化")
	 @PostMapping(value = "/init")
	 public Result<?> gameInit(@RequestBody PlayerPairVO targetPlayerPairVO){
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();//获取登录用户信息
		 PlayerPairVO objSourcePlayerPairVO = new PlayerPairVO();
		 objSourcePlayerPairVO.setUserId(sysUser.getId());
		 objSourcePlayerPairVO.setUserAccount(sysUser.getUsername());//账号
		 if(targetPlayerPairVO.getHoldChess() == 1){
			 objSourcePlayerPairVO.setHoldChess(2);
		 }else{
			 objSourcePlayerPairVO.setHoldChess(1);
		 }
		 ChessGameVO objChessGameVO = chessGameService.init(objSourcePlayerPairVO,targetPlayerPairVO);
		 return Result.OK("成功",objChessGameVO);
	 }

	/**
	 * 分页列表查询
	 *
	 * @param chessGame
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "游戏-分页列表查询")
	@Operation(summary = "游戏-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ChessGame chessGame,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ChessGame> queryWrapper = QueryGenerator.initQueryWrapper(chessGame, req.getParameterMap());
		Page<ChessGame> page = new Page<ChessGame>(pageNo, pageSize);
		IPage<ChessGame> pageList = chessGameService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param chessGame
	 * @return
	 */
	@AutoLog(value = "游戏-添加")
	@Operation(summary = "游戏-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ChessGame chessGame) {
		chessGameService.save(chessGame);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param chessGame
	 * @return
	 */
	@AutoLog(value = "游戏-编辑")
	@Operation(summary = "游戏-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody ChessGame chessGame) {
		chessGameService.updateById(chessGame);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "游戏-通过id删除")
	@Operation(summary = "游戏-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		chessGameService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "游戏-批量删除")
	@Operation(summary = "游戏-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.chessGameService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "游戏-通过id查询")
	@Operation(summary = "游戏-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ChessGame chessGame = chessGameService.getById(id);
		return Result.OK(chessGame);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param chessGame
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, ChessGame chessGame) {
      return super.exportXls(request, chessGame, ChessGame.class, "游戏");
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
      return super.importExcel(request, response, ChessGame.class);
  }

}
