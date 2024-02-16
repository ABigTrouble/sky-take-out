package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 根据日期区间查询营业额
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation(value = "根据日期区间查询营业额")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern="yyyy-MM-dd")LocalDate begin,
                                                       @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate end){
        return Result.success(reportService.getTurnover(begin,end));
    }

    /**
     * 根据日期区间查询用户
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation(value = "根据日期区间查询用户")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate end){
        log.info("查询用户总数");
        return Result.success(reportService.getUserStatistics(begin,end));
    }

    /**
     * 根据日期区间查询订单完成情况
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计接口")
    public Result<OrderReportVO> orderStatistics(@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin,
                                                 @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate end){
        return Result.success(reportService.getOrderStatistics(begin,end));
    }

    /**
     * 查询销售top10的菜品
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("销售排名top10")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin,
                                            @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate end){
        log.info("查询销售top10的菜品");
        return Result.success(reportService.getSalesTop10(begin,end));
    }
}
