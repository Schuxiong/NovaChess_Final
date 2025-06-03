package org.jeecg.modules.chess.pair.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.modules.chess.pair.vo.PairUserVO;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.chess.score.entity.ChessPlayerScore;
import org.jeecg.modules.chess.score.service.IChessPlayerScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Tag(name = "朋友选择操作")
@RestController
@RequestMapping("/pair")
public class FriendController {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private IChessPlayerScoreService chessPlayerScoreService;

    @Operation(summary = "获取所有可选队友数据列表")
    @PostMapping(value = "/chooselist")
    public Result<?> chooseList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, 
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                @RequestParam(name = "depart_ids") String departIds,
                                HttpServletRequest req) {
        log.info("进入chooseList");
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<PairUserVO> lstPariUserVO = new LinkedList<>();
        
        if(departIds == null || departIds.isEmpty()) {
            return Result.error("部门ID不能为空");
        }
        // 直接查询sys_user表，depart_ids相同且排除当前用户
        List<SysUser> userList = sysUserService.lambdaQuery()
                .eq(SysUser::getDepartIds, departIds)
                .ne(SysUser::getId, sysUser.getId())
                .list();
        if(userList == null || userList.isEmpty()) {
            return Result.ok();
        }
        for (SysUser temp : userList) {
            PairUserVO objPairUserVO = new PairUserVO();
            objPairUserVO.setId(temp.getId());
            objPairUserVO.setUserName(temp.getUsername());
            objPairUserVO.setAvatar(temp.getAvatar());
            
            // 查询用户积分
            ChessPlayerScore playerScore = chessPlayerScoreService.lambdaQuery()
                    .eq(ChessPlayerScore::getUserId, temp.getId())
                    .one();
            if (playerScore != null) {
                objPairUserVO.setScore(playerScore.getScore());
            } else {
                objPairUserVO.setScore(600); // 默认积分
            }
            
            lstPariUserVO.add(objPairUserVO);
        }
        return Result.OK(lstPariUserVO);
    }
}
