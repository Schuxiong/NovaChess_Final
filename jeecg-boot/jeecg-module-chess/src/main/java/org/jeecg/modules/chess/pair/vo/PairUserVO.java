package org.jeecg.modules.chess.pair.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PairUserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String userName;

    /**用户头像*/
    private String avatar;

    /**用户积分*/
    private Integer score;

}
