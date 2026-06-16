package com.pigeonkim.paymentshop.board.exception;

public class NicknameRequiredException extends RuntimeException {

    public NicknameRequiredException(String message) {
        super(message);
    }
}