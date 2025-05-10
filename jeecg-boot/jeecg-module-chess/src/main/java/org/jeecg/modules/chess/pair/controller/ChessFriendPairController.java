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

import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;

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
		// 检查是否已存在相同用户的待处理邀请
		QueryWrapper<ChessFriendPair> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("source_user_id", chessFriendPair.getSourceUserId());
		queryWrapper.eq("accept_user_id", chessFriendPair.getAcceptUserId());
		queryWrapper.eq("invite_status", 0);
		List<ChessFriendPair> existingList = chessFriendPairService.list(queryWrapper);
		
		if (!existingList.isEmpty()) {
			if(existingList.size() > 1) {
				// 清理重复数据
				for(int i=1; i<existingList.size(); i++) {
					chessFriendPairService.removeById(existingList.get(i).getId());
				}
			}
			// 更新现有邀请
			ChessFriendPair existing = existingList.get(0);
			chessFriendPair.setId(existing.getId());
			chessFriendPairService.updateById(chessFriendPair);
			return Result.OK(Map.of("status", "updated", "message", "邀请已更新！", "id", chessFriendPair.getId()));
		}
		
		// 设置邀请状态为待接受(0)
		chessFriendPair.setInviteStatus(0);
		chessFriendPairService.save(chessFriendPair);
		return Result.OK(Map.of("status", "created", "message", "添加成功！", "id", chessFriendPair.getId()));
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
  
  /**
   * 清除用户所有待处理的邀请
   * @param userId 用户ID
   * @return
   */
  @AutoLog(value = "对弈关系-清除待处理邀请")
  @Operation(summary = "对弈关系-清除待处理邀请")
  @PostMapping(value = "/clearPendingInvitations")
  public Result<?> clearPendingInvitations(@RequestBody Map<String, String> params) {
      String userId = params.get("userId");
      QueryWrapper<ChessFriendPair> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("accept_user_id", userId);
      queryWrapper.eq("invite_status", 0);
      chessFriendPairService.remove(queryWrapper);
      return Result.OK("清除成功！");
  }
  
  /**
   * 查询待接受的邀请
   *
   * @param userId 当前用户ID
   * @return
   */
  @Autowired
  private ISysUserService sysUserService;
  @AutoLog(value = "对弈关系-查询待接受的邀请")
  @Operation(summary = "对弈关系-查询待接受的邀请")
  @GetMapping(value = "/queryPendingInvitations")
  public Result<?> queryPendingInvitations(@RequestParam(name="userId",required=true) String userId) {
      QueryWrapper<ChessFriendPair> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("accept_user_id", userId);
      queryWrapper.eq("invite_status", 0); // 0-待接受
      List<ChessFriendPair> pendingInvitations = chessFriendPairService.list(queryWrapper);
      return Result.OK(pendingInvitations);
  }
  
  /**
   * 接受或拒绝邀请
   *
   * @param id 邀请ID
   * @param status 1-接受，2-拒绝
   * @return
   */
  @AutoLog(value = "对弈关系-接受或拒绝邀请")
  @Operation(summary = "对弈关系-接受或拒绝邀请")
  @PostMapping(value = "/respondInvitation")
  public Result<?> respondInvitation(
      @RequestParam(name="id",required=true) String id,
      @RequestParam(name="status",required=true) Integer status) {
      
      if (status != 1 && status != 2) {
          return Result.error("状态值无效，只能为1(接受)或2(拒绝)");
      }
      
      ChessFriendPair chessFriendPair = chessFriendPairService.getById(id);
      if (chessFriendPair == null) {
          return Result.error("邀请不存在");
      }
      
      if (chessFriendPair.getInviteStatus() != 0) {
          return Result.error("该邀请已被处理");
      }
      
      chessFriendPair.setInviteStatus(status);
      chessFriendPairService.updateById(chessFriendPair);
      
      String message = status == 1 ? "邀请已接受" : "邀请已拒绝";
      return Result.OK(message);
  }

    /**
     * 查询邀请状态
     * @param id 邀请ID
     * @return
     */
    @AutoLog(value = "对弈关系-查询邀请状态")
    @Operation(summary = "对弈关系-查询邀请状态")
    @GetMapping(value = "/getInvitationStatus")
    public Result<?> getInvitationStatus(
            @RequestParam(name="sourceUserId", required=true) String sourceUserId,
            @RequestParam(name="acceptUserId", required=true) String acceptUserId) {
        if (sourceUserId == null || sourceUserId.trim().isEmpty() || 
            acceptUserId == null || acceptUserId.trim().isEmpty()) {
            return Result.error("用户ID不能为空");
        }
        
        // 查询这对用户之间最新的邀请记录
        QueryWrapper<ChessFriendPair> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("source_user_id", sourceUserId);
        queryWrapper.eq("accept_user_id", acceptUserId);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");
        
        ChessFriendPair pair = chessFriendPairService.getOne(queryWrapper);
        if (pair == null) {
            return Result.error("未找到邀请信息");
        }
        
        SysUser sourceUser = sysUserService.getById(pair.getSourceUserId());
        SysUser acceptUser = sysUserService.getById(pair.getAcceptUserId());
        Map<String, Object> sourceUserInfo = sourceUser == null ? null : Map.of(
                "id", sourceUser.getId(),
                "username", sourceUser.getUsername(),
                "realname", sourceUser.getRealname()
        );
        Map<String, Object> acceptUserInfo = acceptUser == null ? null : Map.of(
                "id", acceptUser.getId(),
                "username", acceptUser.getUsername(),
                "realname", acceptUser.getRealname()
        );
        Map<String, Object> result = Map.of(
                "id", pair.getId(),
                "status", pair.getInviteStatus(),
                "sourceUserInfo", sourceUserInfo,
                "acceptUserInfo", acceptUserInfo
        );
        return Result.OK(result);
    }

    /**
     * 取消邀请
     * @param param 请求参数 {"id": "邀请ID"}
     * @return
     */
    @AutoLog(value = "对弈关系-取消邀请")
    @Operation(summary = "对弈关系-取消邀请")
    @PostMapping(value = "/cancelInvitation")
    public Result<?> cancelInvitation(@RequestBody Map<String, String> param) {
        String id = param.get("id");
        if (id == null || id.trim().isEmpty()) {
            return Result.error("邀请ID不能为空");
        }
        ChessFriendPair pair = chessFriendPairService.getById(id);
        if (pair == null) {
            return Result.error("未找到邀请信息，请检查ID是否正确");
        }
        // 只有待处理状态才能取消
        if (pair.getInviteStatus() != 0) {
            return Result.error("当前邀请不可取消");
        }
        pair.setInviteStatus(2); // 2-已拒绝/取消
        chessFriendPairService.updateById(pair);
        return Result.OK("邀请已取消");
    }
}
