package io.hhplus.tdd.point;


import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PointServiceTest {

    @Mock
    UserPointTable userPointTable;

    @Mock
    PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointService pointService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this); // 목 객체 초기화
    }

    @Test
    @DisplayName("포인트_조회_성공")
    void getPointTestSuccess() {
        //given
        Long userId = 1L;
        Long amount = 500L;

        when(userPointTable.selectById(userId)).thenReturn(new UserPoint(userId, amount, System.currentTimeMillis()));

        //when
        long result = pointService.getPoint(userId);

        //then
        assertNotNull(result);
        assertEquals(500L, result);
    }

    @Test
    @DisplayName("포인트_내역_조회_성공")
    void getPointTestHistorySuccess() {
        //given
        Long userId = 1L;

        List<PointHistory> GivenPointHistoryList = new ArrayList<>();
        GivenPointHistoryList.add(new PointHistory(1L, 97L, 500L, TransactionType.CHARGE, System.currentTimeMillis()));
        GivenPointHistoryList.add(new PointHistory(2L, 97L, 200L, TransactionType.USE, System.currentTimeMillis()));

        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(GivenPointHistoryList);

        //when
        List<PointHistory> pointHistoryList = pointService.getPointHistory(userId);

        //then
        assertNotNull(pointHistoryList);
        assertEquals(2L, pointHistoryList.get(1).id());
        assertEquals(97L, pointHistoryList.get(1).userId());
        assertEquals(200L, pointHistoryList.get(1).amount());
        assertEquals(TransactionType.USE, pointHistoryList.get(1).type());
        assertTrue(pointHistoryList.get(1).updateMillis()<System.currentTimeMillis());
    }

    @Test
    @DisplayName("충전_성공")
    void chargeSuccess() {
        //given
        Long userId = 1L;
        Long baseAmount = 300L;
        Long amount = 500L;

        when(userPointTable.selectById(userId)).thenReturn(new UserPoint(userId, baseAmount, System.currentTimeMillis()));
        when(userPointTable.insertOrUpdate(userId, baseAmount + amount)).thenReturn(new UserPoint(userId, baseAmount + amount, System.currentTimeMillis()));

        //when
        UserPoint result = pointService.charge(userId, amount);

        //then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(800L, result.point());
    }

    @Test
    @DisplayName("충전_실패_유효하지_않은_충전금액")
    void chargeTestInvalidAmountFail() {
        //given
        Long userId = 1L;
        Long amount = -1L;

        //when
        RuntimeException exception = assertThrows(IllegalArgumentException.class, () -> pointService.charge(userId, amount));

        //then
        assertEquals("충전금액이 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("사용_성공")
    void useSuccess() {
        //given
        Long userId = 1L;
        Long baseAmount = 3000L;
        Long amount = 500L;

        when(userPointTable.selectById(userId)).thenReturn(new UserPoint(userId, baseAmount, System.currentTimeMillis()));
        when(userPointTable.insertOrUpdate(userId, baseAmount - amount)).thenReturn(new UserPoint(userId, baseAmount - amount, System.currentTimeMillis()));

        //when
        UserPoint result = pointService.use(userId, amount);

        //then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(2500L, result.point());
    }

    @Test
    @DisplayName("사용_실패_유효하지_않은_충전금액")
    void useTestInvalidAmountFail() {
        //given
        Long userId = 1L;
        Long amount = -1L;

        //when
        RuntimeException exception = assertThrows(IllegalArgumentException.class, () -> pointService.charge(userId, amount));

        //then
        assertEquals("충전금액이 유효하지 않습니다.", exception.getMessage());
    }

}