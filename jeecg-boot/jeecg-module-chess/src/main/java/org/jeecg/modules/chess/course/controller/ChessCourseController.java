package org.jeecg.modules.chess.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.chess.course.entity.ChessCourse;
import org.jeecg.modules.chess.course.service.IChessCourseService;
import org.jeecg.modules.chess.course.vo.CourseRequestVO;
import org.jeecg.modules.chess.course.vo.CourseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Tag(name="国际象棋课程")
@RestController
@RequestMapping("/course/chessCourse")
public class ChessCourseController extends JeecgController<ChessCourse, IChessCourseService> {
	@Autowired
	private IChessCourseService chessCourseService;
	
	/**
	 * 分页列表查询
	 *
	 * @param chessCourse
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "国际象棋课程-分页列表查询")
	@Operation(summary = "国际象棋课程-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ChessCourse chessCourse,
								@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								HttpServletRequest req) {
		QueryWrapper<ChessCourse> queryWrapper = new QueryWrapper<>(chessCourse);
		Page<ChessCourse> page = new Page<ChessCourse>(pageNo, pageSize);
		IPage<ChessCourse> pageList = chessCourseService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param courseRequestVO
	 * @return
	 */
	@AutoLog(value = "国际象棋课程-添加")
	@Operation(summary = "国际象棋课程-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CourseRequestVO courseRequestVO) {
		try {
			String courseId = chessCourseService.createCourse(courseRequestVO);
			return Result.OK("创建成功", Map.of("id", courseId));
		} catch (Exception e) {
			log.error("创建课程失败", e);
			return Result.error("创建失败：" + e.getMessage());
		}
	}
	
	/**
	 * 编辑
	 *
	 * @param courseRequestVO
	 * @return
	 */
	@AutoLog(value = "国际象棋课程-编辑")
	@Operation(summary = "国际象棋课程-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody CourseRequestVO courseRequestVO) {
		try {
			chessCourseService.updateCourse(courseRequestVO);
			return Result.OK("编辑成功!");
		} catch (Exception e) {
			log.error("编辑课程失败", e);
			return Result.error("编辑失败：" + e.getMessage());
		}
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "国际象棋课程-通过id删除")
	@Operation(summary = "国际象棋课程-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		chessCourseService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "国际象棋课程-批量删除")
	@Operation(summary = "国际象棋课程-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.chessCourseService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "国际象棋课程-通过id查询")
	@Operation(summary = "国际象棋课程-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		try {
			CourseResponseVO courseResponse = chessCourseService.getCourseById(id);
			if(courseResponse == null) {
				return Result.error("未找到对应数据");
			}
			return Result.OK(courseResponse);
		} catch (Exception e) {
			log.error("查询课程失败", e);
			return Result.error("查询失败：" + e.getMessage());
		}
	}

  /**
   * 导出excel
   *
   * @param request
   * @param chessCourse
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, ChessCourse chessCourse) {
      return super.exportXls(request, chessCourse, ChessCourse.class, "国际象棋课程");
  }

  /**
   * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
      return super.importExcel(request, response, ChessCourse.class);
  }
}