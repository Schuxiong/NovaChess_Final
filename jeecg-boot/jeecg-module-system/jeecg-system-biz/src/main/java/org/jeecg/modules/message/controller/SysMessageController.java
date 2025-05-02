package org.jeecg.modules.message.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.WebsocketConst;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.message.entity.SysMessage;
import org.jeecg.modules.message.service.ISysMessageService;
import org.jeecg.modules.message.websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 消息
 * @author: jeecg-boot
 * @date: 2019-04-09
 * @version: V1.0
 */
@Slf4j
@Tag(name = "消息管理", description = "消息及聊天相关接口")
@RestController
@RequestMapping("/sys/message/sysMessage")
public class SysMessageController extends JeecgController<SysMessage, ISysMessageService> {
	@Autowired
	private ISysMessageService sysMessageService;
	
	@Autowired
	private WebSocket webSocket;
	
	@Autowired
	private ISysBaseAPI sysBaseApi;

	/**
	 * 分页列表查询
	 * 
	 * @param sysMessage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@Operation(summary = "分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysMessage sysMessage, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<SysMessage> queryWrapper = QueryGenerator.initQueryWrapper(sysMessage, req.getParameterMap());
		Page<SysMessage> page = new Page<SysMessage>(pageNo, pageSize);
		IPage<SysMessage> pageList = sysMessageService.page(page, queryWrapper);
        return Result.ok(pageList);
	}

	/**
	 * 添加
	 * 
	 * @param sysMessage
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysMessage sysMessage) {
		sysMessageService.save(sysMessage);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 * 
	 * @param sysMessage
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysMessage sysMessage) {	
		sysMessageService.updateById(sysMessage);
        return Result.ok("修改成功!");

	}

	/**
	 * 通过id删除
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		sysMessageService.removeById(id);
        return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {

		this.sysMessageService.removeByIds(Arrays.asList(ids.split(",")));
	    return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		SysMessage sysMessage = sysMessageService.getById(id);
		return Result.ok(sysMessage);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 */
	@GetMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysMessage sysMessage) {
		return super.exportXls(request,sysMessage,SysMessage.class, "推送消息模板");
	}

	/**
	 * excel导入
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping(value = "/importExcel")
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, SysMessage.class);
	}
	
	/**
	 * 获取聊天记录
	 * 
	 * @param senderId 发送者ID
	 * @param receiverId 接收者ID
	 * @param pageNo 页码
	 * @param pageSize 每页记录数
	 * @return
	 */
	@Operation(summary = "获取聊天会话列表")
	@GetMapping("/getChatSessions")
	public Result<?> getChatSessions(
			@RequestParam(name = "userId", required = true) String userId,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		QueryWrapper<SysMessage> queryWrapper = new QueryWrapper<>();
		// 查询当前用户参与的所有聊天会话
		queryWrapper.and(wrapper -> wrapper
				.eq("es_sender_id", userId)
				.or()
				.eq("es_receiver_id", userId));
		// 消息类型为聊天消息
		queryWrapper.eq("es_category", "1");
		// 按时间排序
		queryWrapper.orderByDesc("es_send_time");
		Page<SysMessage> page = new Page<>(pageNo, pageSize);
		IPage<SysMessage> pageList = sysMessageService.page(page, queryWrapper);
		
		// 添加头像信息
		pageList.getRecords().forEach(message -> {
			String senderId = message.getEsSenderId();
			String receiverId = message.getEsReceiverId();
			LoginUser sender = sysBaseApi.getUserById(senderId);
			LoginUser receiver = sysBaseApi.getUserById(receiverId);
			if(sender != null) {
				message.setEsSenderAvatar(sender.getAvatar());
			}
			if(receiver != null) {
				message.setEsReceiverAvatar(receiver.getAvatar());
			}
		});
		return Result.ok(pageList);
	}
	
	@Operation(summary = "获取聊天记录")
	@GetMapping("/getChatHistory")
	public Result<?> getChatHistory(
			@RequestParam(name = "senderId", required = true) String senderId,
			@RequestParam(name = "receiverId", required = true) String receiverId,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
		QueryWrapper<SysMessage> queryWrapper = new QueryWrapper<>();
		// 查询条件：(发送者=senderId AND 接收者=receiverId) OR (发送者=receiverId AND 接收者=senderId)
		queryWrapper.and(wrapper -> wrapper
				.eq("es_sender_id", senderId).eq("es_receiver_id", receiverId)
				.or()
				.eq("es_sender_id", receiverId).eq("es_receiver_id", senderId));
		// 消息类型为聊天消息
		queryWrapper.eq("es_category", "1");
		// 按时间排序
		queryWrapper.orderByDesc("es_send_time");
		Page<SysMessage> page = new Page<>(pageNo, pageSize);
		IPage<SysMessage> pageList = sysMessageService.page(page, queryWrapper);
		
		// 添加头像信息
		LoginUser sender = sysBaseApi.getUserById(senderId);
		LoginUser receiver = sysBaseApi.getUserById(receiverId);
		String senderAvatar = sender != null ? sender.getAvatar() : null;
		String receiverAvatar = receiver != null ? receiver.getAvatar() : null;
		pageList.getRecords().forEach(message -> {
			message.setEsSenderAvatar(senderAvatar);
			message.setEsReceiverAvatar(receiverAvatar);
		});
		return Result.ok(pageList);
	}
	
	/**
	 * 发送私聊消息
	 * 
	 * @param sysMessage 消息对象
	 * @param request HTTP请求
	 * @return
	 */
	@Operation(summary = "发送私聊消息")
	@PostMapping("/sendChatMessage")
	public Result<?> sendChatMessage(@RequestBody SysMessage sysMessage, HttpServletRequest request) {
		try {
			// 获取当前登录用户
			LoginUser sysUser = (LoginUser) request.getAttribute("loginUser");
			if (sysUser == null) {
				sysUser = JwtUtil.UserInfo(request);
			}
			if (sysUser == null) {
				return Result.error("用户未登录");
			}
			// 根据username查询接收者ID
			if (sysMessage.getEsReceiverName() != null) {
				String username = sysMessage.getEsReceiverName();
				LoginUser receiver = sysBaseApi.getUserByName(username);
				if (receiver == null) {
					return Result.error("接收用户不存在");
				}
				sysMessage.setEsReceiverId(receiver.getId());
			} else {
				return Result.error("必须指定接收者用户名");
			}
			// 设置发送者信息
			sysMessage.setEsSenderId(sysMessage.getEsSenderId() != null ? sysMessage.getEsSenderId() : sysUser.getId());
			sysMessage.setEsSenderName(sysMessage.getEsSenderName() != null ? sysMessage.getEsSenderName() : sysUser.getRealname());
			// 设置消息类型为聊天消息
			sysMessage.setEsCategory("1");
			// 设置消息状态为已发送
			sysMessage.setEsSendStatus("1");
			// 设置发送时间
			sysMessage.setEsSendTime(new Date());
			// 保存消息
			sysMessageService.save(sysMessage);
			// 通过WebSocket推送消息给接收者
			JSONObject obj = new JSONObject();
			obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_CHAT);
			obj.put(WebsocketConst.MSG_ID, sysMessage.getId());
			obj.put(WebsocketConst.MSG_TXT, sysMessage.getEsContent());
			obj.put("senderId", sysMessage.getEsSenderId());
			obj.put("senderName", sysMessage.getEsSenderName());
			obj.put("time", sysMessage.getEsSendTime());
			webSocket.sendMessage(sysMessage.getEsReceiverId(), obj.toJSONString());
			return Result.ok("消息发送成功");
		} catch (Exception e) {
			log.error("发送聊天消息失败", e);
			return Result.error("消息发送失败: " + e.getMessage());
		}
	}

}
