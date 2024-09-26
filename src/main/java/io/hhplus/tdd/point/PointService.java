package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public Long getPoint(Long id) {

        UserPoint userPoint = userPointTable.selectById(id);

        return userPoint.point();
    }

    public List<PointHistory> getPointHistory(Long id) {

        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);

        return pointHistories;
    }

    public UserPoint charge(Long id, Long amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("충전금액이 유효하지 않습니다.");
        }

        //기존 포인트 가져오기
        UserPoint currentPoint = userPointTable.selectById(id);

        //포인트 충전
        Long chargePoint = currentPoint.chargePoint(amount);

        //기존 아이디에 포인트 업데이트
        UserPoint userPoint = userPointTable.insertOrUpdate(currentPoint.id(), chargePoint);

        //히스토리 저장
        pointHistoryTable.insert(userPoint.id(), userPoint.point(), TransactionType.CHARGE, System.currentTimeMillis());

        return userPoint;
    }

    public UserPoint use(Long id, Long amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("충전금액이 유효하지 않습니다.");
        }

        //기존 포인트 가져오기
        UserPoint currentPoint = userPointTable.selectById(id);

        //포인트 사용
        Long usePoint = currentPoint.usePoint(amount);

        //기존 아이디에 포인트 업데이트
        UserPoint userPoint = userPointTable.insertOrUpdate(currentPoint.id(), usePoint);

        //히스토리 저장
        pointHistoryTable.insert(userPoint.id(), userPoint.point(), TransactionType.USE, System.currentTimeMillis());

        return userPoint;
    }

}