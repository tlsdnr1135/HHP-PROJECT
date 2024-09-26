package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPointTest {

    @Test
    @DisplayName("생성자_생성_실패_Point가_음수")
    void constructorTestIdNullFail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new UserPoint(1L, -100L, System.currentTimeMillis()));
        assertEquals("포인트는 음수가 될 수 없습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("포인트_초기화_성공")
    void emptyTestSuccess() {
        //given
        long userId = 1L;

        //when
        UserPoint userPoint = UserPoint.empty(userId);

        //then
        assertNotNull(userPoint, "userPoint 객체가 null이어서는 안됩니다.");
        assertEquals(userId, userPoint.id(), "ID가 일치해야 합니다.");
        assertEquals(0, userPoint.point(), "포인트는 0이어야 합니다.");
        assertTrue(userPoint.updateMillis() <= System.currentTimeMillis(), "업데이트 시간이 현재 시간 이하이어야 합니다.");
    }

    @Test
    @DisplayName("포인트_충전_성공")
    void chargePointTestSuccess() {
        //given
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
        Long amount = 400L;

        //when
        Long result = userPoint.chargePoint(amount);

        //then
        assertEquals(500L, result, "포인트는 500이어야 합니다.");
    }
    @Test
    @DisplayName("포인트_충전_실패_최대금액초과")
    void chargePointTestMaxAmountFail() {
        //given
        UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());
        Long amount = 400L;

        //when
        RuntimeException exception = assertThrows(IllegalArgumentException.class, () -> userPoint.chargePoint(amount));

        //then
        assertEquals("최대 금액 초과! 충전금액을 줄여야 해요.", exception.getMessage());
    }

    @Test
    @DisplayName("포인트_사용_성공")
    void usePointTestSuccess() {
        //given
        UserPoint userPoint = new UserPoint(1L, 500L, System.currentTimeMillis());
        Long amount = 400L;

        //when
        Long result = userPoint.usePoint(amount);

        //then
        assertEquals(100L, result, "포인트는 100이어야 합니다.");
    }

    @Test
    @DisplayName("포인트_사용_실패_잔액부족")
    void usePointTestMinAmountFail() {
        //given
        UserPoint userPoint = new UserPoint(1L, 300L, System.currentTimeMillis());
        Long amount = 400L;

        //when
        RuntimeException exception = assertThrows(IllegalArgumentException.class, () -> userPoint.usePoint(amount));

        //then
        assertEquals("잔액 부족!", exception.getMessage());
    }
}