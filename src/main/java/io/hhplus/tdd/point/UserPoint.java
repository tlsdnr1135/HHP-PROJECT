package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    private static final long MAX_AMOUNT = 1000L;

    public UserPoint {
        if (point < 0) {
            throw new IllegalArgumentException("포인트는 음수가 될 수 없습니다!");
        }
    }

    // 초기화
    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    // 포인트 충전
    public Long chargePoint(long amount) {
        if(this.point + amount > MAX_AMOUNT) {
            throw new IllegalArgumentException("최대 금액 초과! 충전금액을 줄여야 해요.");
        }
        return this.point + amount;
    }

    // 포인트 사용
    public Long usePoint(long amount) {
        if(this.point - amount <= 0) {
            throw new IllegalArgumentException("잔액 부족!");
        }
        return this.point - amount;
    }
}