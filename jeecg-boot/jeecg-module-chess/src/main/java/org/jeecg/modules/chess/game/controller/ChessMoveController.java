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
import org.jeecg.modules.chess.game.entity.ChessMove;
import org.jeecg.modules.chess.game.service.IChessMoveService;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.chess.game.vo.ChessPiecesVO;
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
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.chess.game.entity.ChessGame;
import org.jeecg.modules.chess.game.service.IChessGameService;
import org.jeecg.modules.chess.game.vo.ChessGameVO;

/**
 * @Description: 行棋
 * @Author: jeecg-boot
 * @Date: 2025-04-27
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "行棋")
@RestController
@RequestMapping("/game/chessMove")
public class ChessMoveController extends JeecgController<ChessMove, IChessMoveService> {
	@Autowired
	private IChessMoveService chessMoveService;
	@Autowired
	private IChessGameService chessGameService;

	/**
	 * 下棋
	 *
	 * @param chessMove
	 * @param request
	 * @return
	 */
	@AutoLog(value = "行棋-下棋")
	@Operation(summary = "行棋-下棋")
	@PostMapping(value = "/move")
	public Result<?> movePieces(@RequestBody ChessMove chessMove, HttpServletRequest request) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		chessMove.setUserId(sysUser.getId());

		// 检查游戏是否存在
		ChessGame game = chessGameService.getById(chessMove.getChessGameId());
		if (game == null) {
			return Result.error("游戏不存在");
		}

		ChessPiecesVO obj = chessMoveService.movePieces(chessMove);
		if (obj.getErrorMessage() != null && !obj.getErrorMessage().isEmpty()) {
			return Result.error(obj.getErrorMessage());
		}

		// 返回最新游戏状态
		ChessGameVO currentGameState = chessGameService.getChessGameChessPieces(
				chessMove.getChessGameId(),
				sysUser.getId());

		// 将当前游戏状态添加到响应中
		obj.setCurrentGameState(currentGameState);

		return Result.ok(obj);
	}

	/**
	 * 分页列表查询
	 *
	 * @param chessMove
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "行棋-分页列表查询")
	@Operation(summary = "行棋-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ChessMove chessMove,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<ChessMove> queryWrapper = QueryGenerator.initQueryWrapper(chessMove, req.getParameterMap());
		Page<ChessMove> page = new Page<ChessMove>(pageNo, pageSize);
		IPage<ChessMove> pageList = chessMoveService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 * 添加
	 *
	 * @param chessMove
	 * @return
	 */
	@AutoLog(value = "行棋-添加")
	@Operation(summary = "行棋-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ChessMove chessMove) {
		chessMoveService.save(chessMove);
		return Result.OK("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param chessMove
	 * @return
	 */
	@AutoLog(value = "行棋-编辑")
	@Operation(summary = "行棋-编辑")
	@RequestMapping(value = "/edit", method = { RequestMethod.PUT, RequestMethod.POST })
	public Result<?> edit(@RequestBody ChessMove chessMove) {
		chessMoveService.updateById(chessMove);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "行棋-通过id删除")
	@Operation(summary = "行棋-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		chessMoveService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "行棋-批量删除")
	@Operation(summary = "行棋-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.chessMoveService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "行棋-通过id查询")
	@Operation(summary = "行棋-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		ChessMove chessMove = chessMoveService.getById(id);
		return Result.OK(chessMove);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param chessMove
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, ChessMove chessMove) {
		return super.exportXls(request, chessMove, ChessMove.class, "行棋");
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
		return super.importExcel(request, response, ChessMove.class);
	}

}
