package com.springsecurity.tweet.Exceptions;

public class ForbiddenException extends Throwable {
    public ForbiddenException(String s) {
        super(s);
    }
}
