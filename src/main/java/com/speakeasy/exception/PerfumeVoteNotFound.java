package com.speakeasy.exception;

public class PerfumeVoteNotFound extends RuntimeException{
    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public PerfumeVoteNotFound() {
        super(MESSAGE);
    }
}
