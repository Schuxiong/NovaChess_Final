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

    @Operation(summary = "获取所有可选队友数据列表")
    @PostMapping(value = "/chooselist")
    //@PermissionData(pageComponent = "jeecg/JeecgDemoList")
    public Result<?> chooseList( @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                HttpServletRequest req) {
        log.info("进入chooseList");
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();//获取登录用户信息

        List<PairUserVO> lstPariUserVO = new LinkedList<>();
        Page<SysUser> page = new Page<>();
        String strDeptId = "1234";
        IPage<SysUser> lstUser = sysUserService.getUserByDepId(page,strDeptId,null);
        String strCurrentUserId = sysUser.getId();
        if( lstUser == null || lstUser.getTotal() < 1L)
                return Result.ok();

        for (SysUser temp : lstUser.getRecords()){
             if(strCurrentUserId.equals(temp.getId())){
                 lstUser.getRecords().remove(temp);
                 break;
             }else{
                 PairUserVO objPairUserVO = new PairUserVO();
                 objPairUserVO.setId(temp.getId());
                 objPairUserVO.setUserName(temp.getUsername());
             }
        }

        return Result.OK(lstPariUserVO);
    }
}
