package org.jeecg.modules.chess.game.service.impl;

import org.jeecg.modules.chess.game.entity.ChessPieces;
import org.jeecg.modules.chess.game.mapper.ChessPiecesMapper;
import org.jeecg.modules.chess.game.service.ChessPiecesPositionService;
import org.jeecg.modules.chess.game.service.IChessPiecesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.UUIDGenerator;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 棋子位置
 * @Author: jeecg-boot
 * @Date: 2025-04-27
 * @Version: V1.0
 */
@Slf4j
@Service
public class ChessPiecesServiceImpl extends ServiceImpl<ChessPiecesMapper, ChessPieces> implements IChessPiecesService {
    @Autowired
    private ChessPiecesPositionService chessPiecesPositionService;

    @Transactional
    @Override
    public List<ChessPieces> initPosition(String chessGameId) {
        log.info("开始初始化游戏{}的棋子", chessGameId);

        // 查询现有棋子，检查是否已经初始化过
        QueryWrapper<ChessPieces> existingQuery = new QueryWrapper<>();
        existingQuery.eq("chess_game_id", chessGameId);
        existingQuery.eq("del_flag", 0);
        List<ChessPieces> existingPieces = this.list(existingQuery);

        // 如果已有棋子，则处理现有棋子
        if (existingPieces != null && !existingPieces.isEmpty()) {
            log.info("游戏{}已存在{}个棋子", chessGameId, existingPieces.size());

            // 找出活跃棋子
            List<ChessPieces> activePieces = existingPieces.stream()
                    .filter(p -> p.getPiecesState() == 1)
                    .collect(Collectors.toList());

            if (!activePieces.isEmpty()) {
                log.info("游戏{}已存在{}个活跃棋子，跳过初始化", chessGameId, activePieces.size());
                return activePieces;
            }

            // 如果没有活跃棋子但有非活跃棋子，激活所有棋子而不是清理它们
            log.info("游戏{}存在{}个非活跃棋子，激活它们", chessGameId, existingPieces.size());
            for (ChessPieces piece : existingPieces) {
                piece.setPiecesState(1);
                this.updateById(piece);
            }
            return existingPieces;
        }

        // 如果没有任何棋子，则创建新棋子
        log.info("为游戏{}生成新棋子", chessGameId);
        // 初始化黑棋
        List<ChessPieces> lstResult = new ArrayList<>();
        // 初始化白棋
        // 王
        ChessPieces objBlackKing = new ChessPieces();
        objBlackKing.setId(UUIDGenerator.generate());
        objBlackKing.setChessGameId(chessGameId);
        objBlackKing.setChessPiecesName("King");
        objBlackKing.setPiecesType(1);// 黑棋
        objBlackKing.setPositionX("E");
        objBlackKing.setPositionY("8");
        objBlackKing.setPiecesState(1);// 1 active
        lstResult.add(objBlackKing);

        ChessPieces objWhiteKing = new ChessPieces();
        objWhiteKing.setId(UUIDGenerator.generate());
        objWhiteKing.setChessGameId(chessGameId);
        objWhiteKing.setChessPiecesName("King");
        objWhiteKing.setPiecesType(2);// 白棋
        objWhiteKing.setPositionX("E");
        objWhiteKing.setPositionY("1");
        objWhiteKing.setPiecesState(1);// 1 active
        lstResult.add(objWhiteKing);

        // 后
        ChessPieces objBlackQueen = new ChessPieces();
        objBlackQueen.setId(UUIDGenerator.generate());
        objBlackQueen.setChessGameId(chessGameId);
        objBlackQueen.setChessPiecesName("Queen");
        objBlackQueen.setPiecesType(1);// 黑棋
        objBlackQueen.setPositionX("D");
        objBlackQueen.setPositionY("8");
        objBlackQueen.setPiecesState(1);// 1 active
        lstResult.add(objBlackQueen);

        ChessPieces objWhiteQueen = new ChessPieces();
        objWhiteQueen.setId(UUIDGenerator.generate());
        objWhiteQueen.setChessGameId(chessGameId);
        objWhiteQueen.setChessPiecesName("Queen");
        objWhiteQueen.setPiecesType(2);// 白棋
        objWhiteQueen.setPositionX("D");
        objWhiteQueen.setPositionY("1");
        objWhiteQueen.setPiecesState(1);// 1 active
        lstResult.add(objWhiteQueen);

        // 车
        ChessPieces objBlackRookA = new ChessPieces();
        objBlackRookA.setId(UUIDGenerator.generate());
        objBlackRookA.setChessGameId(chessGameId);
        objBlackRookA.setChessPiecesName("Rook");
        objBlackRookA.setPiecesType(1);// 黑棋
        objBlackRookA.setPositionX("A");
        objBlackRookA.setPositionY("8");
        objBlackRookA.setPiecesState(1);// 1 active
        lstResult.add(objBlackRookA);

        ChessPieces objBlackRookH = new ChessPieces();
        objBlackRookH.setId(UUIDGenerator.generate());
        objBlackRookH.setChessGameId(chessGameId);
        objBlackRookH.setChessPiecesName("Rook");
        objBlackRookH.setPiecesType(1);// 黑棋
        objBlackRookH.setPositionX("H");
        objBlackRookH.setPositionY("8");
        objBlackRookH.setPiecesState(1);// 1 active
        lstResult.add(objBlackRookH);

        ChessPieces objWhiteRookA = new ChessPieces();
        objWhiteRookA.setId(UUIDGenerator.generate());
        objWhiteRookA.setChessGameId(chessGameId);
        objWhiteRookA.setChessPiecesName("Rook");
        objWhiteRookA.setPiecesType(2);// 白棋
        objWhiteRookA.setPositionX("A");
        objWhiteRookA.setPositionY("1");
        objWhiteRookA.setPiecesState(1);// 1 active
        lstResult.add(objWhiteRookA);

        ChessPieces objWhiteRookH = new ChessPieces();
        objWhiteRookH.setId(UUIDGenerator.generate());
        objWhiteRookH.setChessGameId(chessGameId);
        objWhiteRookH.setChessPiecesName("Rook");
        objWhiteRookH.setPiecesType(2);// 白棋
        objWhiteRookH.setPositionX("H");
        objWhiteRookH.setPositionY("1");
        objWhiteRookH.setPiecesState(1);// 1 active
        lstResult.add(objWhiteRookH);

        // 象
        ChessPieces objBlackBishopC = new ChessPieces();
        objBlackBishopC.setId(UUIDGenerator.generate());
        objBlackBishopC.setChessGameId(chessGameId);
        objBlackBishopC.setChessPiecesName("Bishop");
        objBlackBishopC.setPiecesType(1);// 黑棋
        objBlackBishopC.setPositionX("C");
        objBlackBishopC.setPositionY("8");
        objBlackBishopC.setPiecesState(1);// 1 active
        lstResult.add(objBlackBishopC);

        ChessPieces objBlackBishopF = new ChessPieces();
        objBlackBishopF.setId(UUIDGenerator.generate());
        objBlackBishopF.setChessGameId(chessGameId);
        objBlackBishopF.setChessPiecesName("Bishop");
        objBlackBishopF.setPiecesType(1);// 黑棋
        objBlackBishopF.setPositionX("F");
        objBlackBishopF.setPositionY("8");
        objBlackBishopF.setPiecesState(1);// 1 active
        lstResult.add(objBlackBishopF);

        ChessPieces objWhiteBishopC = new ChessPieces();
        objWhiteBishopC.setId(UUIDGenerator.generate());
        objWhiteBishopC.setChessGameId(chessGameId);
        objWhiteBishopC.setChessPiecesName("Bishop");
        objWhiteBishopC.setPiecesType(2);// 白棋
        objWhiteBishopC.setPositionX("C");
        objWhiteBishopC.setPositionY("1");
        objWhiteBishopC.setPiecesState(1);// 1 active
        lstResult.add(objWhiteBishopC);

        ChessPieces objWhiteBishopF = new ChessPieces();
        objWhiteBishopF.setId(UUIDGenerator.generate());
        objWhiteBishopF.setChessGameId(chessGameId);
        objWhiteBishopF.setChessPiecesName("Bishop");
        objWhiteBishopF.setPiecesType(2);// 白棋
        objWhiteBishopF.setPositionX("F");
        objWhiteBishopF.setPositionY("1");
        objWhiteBishopF.setPiecesState(1);// 1 active
        lstResult.add(objWhiteBishopF);

        // 马
        ChessPieces objBlackKnightB = new ChessPieces();
        objBlackKnightB.setId(UUIDGenerator.generate());
        objBlackKnightB.setChessGameId(chessGameId);
        objBlackKnightB.setChessPiecesName("Knight");
        objBlackKnightB.setPiecesType(1);// 黑棋
        objBlackKnightB.setPositionX("B");
        objBlackKnightB.setPositionY("8");
        objBlackKnightB.setPiecesState(1);// 1 active
        lstResult.add(objBlackKnightB);

        ChessPieces objBlackKnightG = new ChessPieces();
        objBlackKnightG.setId(UUIDGenerator.generate());
        objBlackKnightG.setChessGameId(chessGameId);
        objBlackKnightG.setChessPiecesName("Knight");
        objBlackKnightG.setPiecesType(1);// 黑棋
        objBlackKnightG.setPositionX("G");
        objBlackKnightG.setPositionY("8");
        objBlackKnightG.setPiecesState(1);// 1 active
        lstResult.add(objBlackKnightG);

        ChessPieces objWhiteKnightB = new ChessPieces();
        objWhiteKnightB.setId(UUIDGenerator.generate());
        objWhiteKnightB.setChessGameId(chessGameId);
        objWhiteKnightB.setChessPiecesName("Knight");
        objWhiteKnightB.setPiecesType(2);// 白棋
        objWhiteKnightB.setPositionX("B");
        objWhiteKnightB.setPositionY("1");
        objWhiteKnightB.setPiecesState(1);// 1 active
        lstResult.add(objWhiteKnightB);

        ChessPieces objWhiteKnightG = new ChessPieces();
        objWhiteKnightG.setId(UUIDGenerator.generate());
        objWhiteKnightG.setChessGameId(chessGameId);
        objWhiteKnightG.setChessPiecesName("Knight");
        objWhiteKnightG.setPiecesType(2);// 白棋
        objWhiteKnightG.setPositionX("G");
        objWhiteKnightG.setPositionY("1");
        objWhiteKnightG.setPiecesState(1);// 1 active
        lstResult.add(objWhiteKnightG);

        // 黑兵 A-H 7
        String[] strX = new String[] { "A", "B", "C", "D", "E", "F", "G", "H" };
        for (String x : strX) {
            ChessPieces objBlackPawn = new ChessPieces();
            objBlackPawn.setId(UUIDGenerator.generate());
            objBlackPawn.setChessGameId(chessGameId);
            objBlackPawn.setChessPiecesName("Pawn");
            objBlackPawn.setPiecesType(1);// 黑棋
            objBlackPawn.setPositionX(x);
            objBlackPawn.setPositionY("7");
            objBlackPawn.setPiecesState(1);// 1 active
            lstResult.add(objBlackPawn);
        }
        // 白兵 A-H 2
        for (String x : strX) {
            ChessPieces objWhitePawn = new ChessPieces();
            objWhitePawn.setId(UUIDGenerator.generate());
            objWhitePawn.setChessGameId(chessGameId);
            objWhitePawn.setChessPiecesName("Pawn");
            objWhitePawn.setPiecesType(2);// 白棋
            objWhitePawn.setPositionX(x);
            objWhitePawn.setPositionY("2");
            objWhitePawn.setPiecesState(1);// 1 active
            lstResult.add(objWhitePawn);
        }

        try {
            this.saveBatch(lstResult);
            log.info("为游戏{}生成了{}个新棋子", chessGameId, lstResult.size());
        } catch (Exception e) {
            log.error("保存棋子时出错", e);
        }

        log.info("游戏{}的棋子初始化完成", chessGameId);
        return lstResult;
    }
}
