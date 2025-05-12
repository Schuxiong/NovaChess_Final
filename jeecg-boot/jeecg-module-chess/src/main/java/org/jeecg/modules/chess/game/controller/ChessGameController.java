package org.jeecg.modules.chess.game.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
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
import org.jeecg.modules.chess.move.entity.ChessMove;
import org.jeecg.modules.chess.move.service.IChessMoveService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.chess.game.vo.ChessGameBatchVO;
import org.jeecg.modules.chess.game.vo.ChessGameVO;
import org.jeecg.modules.chess.game.vo.PlayerPairVO;
import org.jeecg.modules.chess.score.entity.ChessPlayerScore;
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
 * @Date: 2025-04-27
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "游戏")
@RestController
@RequestMapping("/game/chessGame")
public class ChessGameController extends JeecgController<ChessGame, IChessGameService> {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private IChessGameService chessGameService;
	@Autowired
	private IChessMoveService chessMoveService;

	/**
	 * 游戏初始化
	 *
	 * @param invitingPlayerInfoFromRequest
	 *
	 * @return
	 */
	@AutoLog(value = "游戏初始化")
	@Operation(summary = "游戏初始化")
	@PostMapping(value = "/init")
	public Result<?> gameInit(@RequestBody PlayerPairVO invitingPlayerInfoFromRequest) {
		LoginUser currentUser = (LoginUser) SecurityUtils.getSubject().getPrincipal(); // 这是接受邀请的人

		PlayerPairVO acceptingPlayerPairVO = new PlayerPairVO();
		acceptingPlayerPairVO.setUserId(currentUser.getId());
		acceptingPlayerPairVO.setUserAccount(currentUser.getUsername());

		// invitingPlayerInfoFromRequest 应包含最初邀请者的信息和 sourceInviteId
		// 以及最初邀请者选择的执棋，或接受者选择的执棋（取决于前端如何设计）

		// 关键：确保 sourceInviteId 是从 invitingPlayerInfoFromRequest 获取并设置给发起游戏的玩家
		// 同时，也需要确定谁设置了执棋方 (holdChess)
		// 假设: invitingPlayerInfoFromRequest.getHoldChess() 是 *邀请者* 当时选择的自己要执的棋
		// 那么接受者执相反的棋
		if (invitingPlayerInfoFromRequest.getHoldChess() == 1) { // 邀请者选黑
			acceptingPlayerPairVO.setHoldChess(2); // 接受者执白
		} else { // 邀请者选白 (或未指定，则默认为白，接受者执黑)
			acceptingPlayerPairVO.setHoldChess(1); // 接受者执黑
		}

		// sourceInviteId 应该由 invitingPlayerInfoFromRequest 提供，并传递给服务层
		// 服务层会将这个 sourceInviteId 存到 ChessGame 表中
		// 并且，服务层 init 方法的第一个参数是"当前操作者/接受者"，第二个参数是"对手/邀请者"

		// 我们需要将邀请者的数据 (invitingPlayerInfoFromRequest) 作为"对手"传给 service.init
		// 将接受者的数据 (acceptingPlayerPairVO) 作为"当前玩家"传给 service.init

		// 为了符合 service.init(source, target) 的语义，其中 source 是当前操作者，target 是对手：
		// sourcePlayerPariVO 应该是 acceptingPlayerPairVO
		// targetPlayerPairVO 应该是 invitingPlayerInfoFromRequest

		PlayerPairVO serviceSourceArg = acceptingPlayerPairVO; // 接受者 (当前用户)
		PlayerPairVO serviceTargetArg = invitingPlayerInfoFromRequest; // 邀请者 (对手)

		// 确保 serviceTargetArg (邀请者一方) 也携带了 sourceInviteId，如果它是由邀请方信息带来的
		// 如果 sourceInviteId 是顶层传入，且不属于 invitingPlayerInfoFromRequest 的一部分，则需要显式设置
		// 但通常设计是 invitingPlayerInfoFromRequest (作为对手方/邀请方数据载体)应该包含它自己的相关信息，包括它发起的邀请ID
		if (invitingPlayerInfoFromRequest.getSourceInviteId() != null
				&& !invitingPlayerInfoFromRequest.getSourceInviteId().isEmpty()) {
			// 如果邀请方信息中已包含sourceInviteId, 确保serviceSourceArg(接受者)也知晓此ID以供服务层统一处理
			// 服务层init方法会从第一个参数获取sourceInviteId
			serviceSourceArg.setSourceInviteId(invitingPlayerInfoFromRequest.getSourceInviteId());
		}

		// log.info("Calling chessGameService.init with: ");
		// log.info(" Accepting Player (Service Source Arg): userId={}, account={},
		// holdChess={}, sourceInviteId={}",
		// serviceSourceArg.getUserId(), serviceSourceArg.getUserAccount(),
		// serviceSourceArg.getHoldChess(), serviceSourceArg.getSourceInviteId());
		// log.info(" Inviting Player (Service Target Arg): userId={}, account={},
		// holdChess={}, sourceInviteId={}",
		// serviceTargetArg.getUserId(), serviceTargetArg.getUserAccount(),
		// serviceTargetArg.getHoldChess(), serviceTargetArg.getSourceInviteId());

		ChessGameVO objChessGameVO = chessGameService.init(serviceSourceArg, serviceTargetArg);

		if (objChessGameVO.getErrorMessage() != null) {
			return Result.error("游戏初始化失败: " + objChessGameVO.getErrorMessage());
		}
		return Result.OK("成功", objChessGameVO);
	}

	/**
	 * 游戏进入
	 *
	 * @return
	 */
	@AutoLog(value = "当前登录用户进入游戏")
	@Operation(summary = "当前登录用户进入游戏")
	@PostMapping(value = "/enter")
	public Result<?> gameEnter(@RequestParam(required = false) String inviteId) {
		// 获取当前登录用户
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		// log.info("用户 [账号:" + sysUser.getUsername() + ", ID:" + sysUser.getId() + "]
		// 尝试进入游戏. 提供的邀请ID: " + inviteId);

		List<ChessGameBatchVO> lstResult;
		if (inviteId != null && !inviteId.isEmpty()) {
			// log.info("尝试通过邀请ID: " + inviteId + " 进入游戏");
			QueryWrapper<ChessGame> directQuery = new QueryWrapper<>();
			directQuery.eq("source_invite_id", inviteId);
			directQuery.eq("game_state", 1); // 游戏进行中
			directQuery.eq("del_flag", 0); // 未删除
			List<ChessGame> games = chessGameService.list(directQuery);

			if (games != null && !games.isEmpty()) {
				// log.info("通过邀请ID: " + inviteId + " 找到 " + games.size() + " 个游戏");
				lstResult = new ArrayList<>();
				for (ChessGame game : games) {
					ChessGameBatchVO vo = new ChessGameBatchVO();
					vo.setGameId(game.getId());
					lstResult.add(vo);
				}
			} else {
				// log.info("未通过邀请ID: " + inviteId + " 找到游戏，尝试通过用户ID: " + sysUser.getId() + "
				// 进入");
				lstResult = chessGameService.enter(sysUser.getId());
			}
		} else {
			// log.info("未提供邀请ID，尝试通过用户ID: " + sysUser.getId() + " 进入游戏");
			lstResult = chessGameService.enter(sysUser.getId());
		}

		if (lstResult == null || lstResult.isEmpty()) {
			// log.warn("用户 [账号:" + sysUser.getUsername() + ", ID:" + sysUser.getId() + "]
			// 未能进入任何游戏. InviteId: " + inviteId);
			return Result.error("未能找到您可以进入的游戏。");
		}

		// log.info("用户 [账号:" + sysUser.getUsername() + ", ID:" + sysUser.getId() + "]
		// 成功进入游戏，找到 " + lstResult.size() + " 个可进入的游戏局.");

		// 发送WebSocket通知
		for (ChessGameBatchVO gameVO : lstResult) {
			String gameId = gameVO.getGameId();
			if (gameId != null && !gameId.isEmpty()) {
				Map<String, Object> messagePayload = new HashMap<>();
				messagePayload.put("type", "PLAYER_JOINED");
				Map<String, String> payloadData = new HashMap<>();
				payloadData.put("userId", sysUser.getId());
				payloadData.put("username", sysUser.getUsername());
				payloadData.put("gameId", gameId);
				messagePayload.put("payload", payloadData);
				String destination = "/topic/game/" + gameId;
				messagingTemplate.convertAndSend(destination, messagePayload);
				log.info("Sent PLAYER_JOINED notification to {} for game {} and user {}", destination, gameId,
						sysUser.getUsername());
			}
		}

		return Result.OK("成功", lstResult);
	}

	/**
	 * 通过sourceInviteId获取gameId
	 *
	 * @param sourceInviteId
	 * @return
	 */
	@AutoLog(value = "通过sourceInviteId获取gameId")
	@Operation(summary = "通过sourceInviteId获取gameId")
	@GetMapping(value = "/getGameIdByInviteId")
	public Result<?> getGameIdByInviteId(@RequestParam String sourceInviteId) {
		QueryWrapper<ChessGame> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("source_invite_id", sourceInviteId);
		// 移除game_state和del_flag的严格限制，只根据source_invite_id查询
		List<ChessGame> games = chessGameService.list(queryWrapper);

		if (games == null || games.isEmpty()) {
			return Result.error("未找到对应的游戏");
		}

		return Result.OK(games.get(0).getId());
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
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
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
	@RequestMapping(value = "/edit", method = { RequestMethod.PUT, RequestMethod.POST })
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
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
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
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
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
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
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

	/**
	 * 获取对局回放记录
	 *
	 * @param gameId 对局ID
	 * @return 包含按时间顺序排列的行棋记录列表
	 */
	@AutoLog(value = "游戏-获取对局回放记录")
	@Operation(summary = "游戏-获取对局回放记录")
	@GetMapping(value = "/replay/{gameId}")
	public Result<?> getGameReplayMoves(@PathVariable("gameId") String gameId) {
		if (oConvertUtils.isEmpty(gameId)) {
			return Result.error("对局ID不能为空");
		}

		// 校验gameId对应的游戏是否存在
		ChessGame game = chessGameService.getById(gameId);
		if (game == null) {
			return Result.error("指定的对局不存在或已被删除");
		}

		// 根据gameId查询所有相关的行棋记录
		QueryWrapper<ChessMove> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("chess_game_id", gameId);
		queryWrapper.eq("del_flag", 0); // 只查询未删除的记录
		queryWrapper.orderByAsc("create_time"); // 确保按创建时间（即行棋顺序）升序排列

		List<ChessMove> moves = chessMoveService.list(queryWrapper);

		if (moves == null || moves.isEmpty()) {
			return Result.OK("该对局没有行棋记录", new ArrayList<>());
		}

		return Result.OK("成功获取行棋记录", moves);
	}

}
