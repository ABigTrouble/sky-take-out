package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 统计销售额
     *
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("营业额统计")
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        // 把日期组建起来
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 把销售额组建起来
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        //组装
        return TurnoverReportVO.builder()
                .dateList(StringUtil.join(dateList.toArray(), ","))
                .turnoverList(StringUtil.join(turnoverList.toArray(), ",")).build();
    }

    /**
     * 根据日期区间查询
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 把日期组建起来
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 把销售额组建起来
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            // 先放end来查询总共的用户数
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map);
            totalUser = totalUser == null ? 0 : totalUser;
            totalUserList.add(totalUser);

            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);
            totalUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
        }
        //组装
        return UserReportVO.builder()
                .dateList(StringUtil.join(dateList.toArray(), ","))
                .totalUserList(StringUtil.join(totalUserList.toArray(), ","))
                .newUserList(StringUtil.join(newUserList.toArray(), ","))
                .build();
    }

    /**
     * 根据日期区间查询订单情况
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 把日期组建起来
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 查询所有订单的数量
        List<Integer> totalOrderList = new ArrayList<>();
        List<Integer> completedOrderList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            // 获取所有订单的数量
            Integer totalCount = getOrderCount(beginTime, endTime, null);
            totalOrderList.add(totalCount);

            // 获取已完成的订单数量
            Integer completedCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            completedOrderList.add(completedCount);
        }
        // 获取总数量
        Integer total = totalOrderList.stream().reduce(Integer::sum).get();
        Integer completed = completedOrderList.stream().reduce(Integer::sum).get();
        Double ratio = total == 0 ? 0.0 : completed.doubleValue() / total;

        return OrderReportVO.builder()
                .dateList(StringUtil.join(dateList.toArray(), ","))
                .orderCountList(StringUtil.join(totalOrderList.toArray(), ","))
                .validOrderCountList(StringUtil.join(completedOrderList.toArray(), ","))
                .totalOrderCount(total)
                .validOrderCount(completed)
                .orderCompletionRate(ratio)
                .build();
    }

    /**
     * 根据日期区间和状态订单的数量
     *
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {
        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }

    /**
     * 查询销售top10的菜品
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime, Orders.COMPLETED);

        // 取出数据组装成list
        String names = StringUtil.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()).toArray(), ",");
        String numbers = StringUtil.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()).toArray(), ",");

        return SalesTop10ReportVO.builder()
                .nameList(names)
                .numberList(numbers)
                .build();
    }
}
