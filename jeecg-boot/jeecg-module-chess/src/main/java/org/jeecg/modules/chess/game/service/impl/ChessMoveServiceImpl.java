package org.jeecg.modules.chess.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.chess.game.entity.ChessMove;
import org.jeecg.modules.chess.game.entity.ChessPieces;
import org.jeecg.modules.chess.game.mapper.ChessMoveMapper;
import org.jeecg.modules.chess.game.service.IChessMoveService;
import org.jeecg.modules.chess.game.service.IChessPiecesService;
import org.jeecg.modules.chess.game.vo.ChessPiecesVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 行棋
 * @Author: jeecg-boot
 * @Date:   2025-04-27
 * @Version: V1.0
 */
@Slf4j
@Service
public class ChessMoveServiceImpl extends ServiceImpl<ChessMoveMapper, ChessMove> implements IChessMoveService {

    @Autowired
    private IChessPiecesService chessPiecesService;

    @Transactional
    @Override
    public ChessPiecesVO movePieces(ChessMove chessMove) {
        ChessPiecesVO objChessPiecesVO = new ChessPiecesVO();
        try{
            ChessPieces objqueryChessPieces = new ChessPieces();
            Map<String,String[]> objParamMap = new HashMap<>();
            objParamMap.put("chessGameId",new String[]{chessMove.getChessGameId()});
            objParamMap.put("positionX",new String[]{chessMove.getToPositionX()});
            objParamMap.put("positionY",new String[]{chessMove.getToPositionY()});
            QueryWrapper<ChessPieces> queryWrapper = QueryGenerator.initQueryWrapper(objqueryChessPieces, objParamMap);
            Page<ChessPieces> page = new Page<ChessPieces>(1, 1);
            IPage<ChessPieces> pageList = chessPiecesService.page(page, queryWrapper);
            chessMove.setChessPiecesId(UUIDGenerator.generate());
            objParamMap.clear();
            objParamMap.put("chessGameId",new String[]{chessMove.getChessGameId()});
            objParamMap.put("id",new String[]{chessMove.getChessPiecesId()});
            queryWrapper = QueryGenerator.initQueryWrapper(objqueryChessPieces, objParamMap);
            ChessPieces objChessPieces = chessPiecesService.getOne(queryWrapper);
            objChessPieces.setPositionX(chessMove.getToPositionX());
            objChessPieces.setPositionY(chessMove.getToPositionY());
            chessPiecesService.saveOrUpdate(objChessPieces);//更新棋子在棋盘位置
            objParamMap.clear();
            if(pageList == null && pageList.getTotal() < 1){
                this.saveOrUpdate(chessMove);
            }else{
                ChessPieces objTarget = pageList.getRecords().get(0);
                objParamMap.put("chessGameId",new String[]{chessMove.getChessGameId()});
                objParamMap.put("id",new String[]{objTarget.getId()});
                queryWrapper = QueryGenerator.initQueryWrapper(objqueryChessPieces, objParamMap);
                ChessPieces objTargetChessPieces = chessPiecesService.getOne(queryWrapper);
                objTargetChessPieces.setPiecesState(2); //被吃了
                chessPiecesService.saveOrUpdate(objTargetChessPieces);//更新棋子在棋盘位置
                objChessPiecesVO.setTargetChessPiecesId(objTargetChessPieces.getId());
                chessMove.setTookPiecesId(objTargetChessPieces.getId());
                this.saveOrUpdate(chessMove);
            }
            objChessPiecesVO.setChessGameId(chessMove.getChessGameId());
            objChessPiecesVO.setPositionX(chessMove.getToPositionX());
            objChessPiecesVO.setPositionY(chessMove.getToPositionY());

        }catch (Exception e){
            log.error("错误为：",e);
        }
        return  objChessPiecesVO;


    }
}
