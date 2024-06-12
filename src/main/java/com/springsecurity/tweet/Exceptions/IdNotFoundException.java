package com.springsecurity.tweet.Exceptions;

public class IdNotFoundException extends RuntimeException {
    public IdNotFoundException(String s) {
        super(s);
    }
}
