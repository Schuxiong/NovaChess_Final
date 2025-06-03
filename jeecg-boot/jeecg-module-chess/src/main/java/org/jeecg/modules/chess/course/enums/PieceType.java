package org.jeecg.modules.chess.course.enums;

/**
 * 棋子类型枚举
 */
public enum PieceType {
    WHITE_KING("white_king", "白王"),
    WHITE_QUEEN("white_queen", "白后"),
    WHITE_ROOK("white_rook", "白车"),
    WHITE_BISHOP("white_bishop", "白象"),
    WHITE_KNIGHT("white_knight", "白马"),
    WHITE_PAWN("white_pawn", "白兵"),
    BLACK_KING("black_king", "黑王"),
    BLACK_QUEEN("black_queen", "黑后"),
    BLACK_ROOK("black_rook", "黑车"),
    BLACK_BISHOP("black_bishop", "黑象"),
    BLACK_KNIGHT("black_knight", "黑马"),
    BLACK_PAWN("black_pawn", "黑兵");

    private final String code;
    private final String description;

    PieceType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     */
    public static PieceType fromCode(String code) {
        for (PieceType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown piece type code: " + code);
    }

    /**
     * 验证棋子类型代码是否有效
     */
    public static boolean isValidCode(String code) {
        for (PieceType type : values()) {
            if (type.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}