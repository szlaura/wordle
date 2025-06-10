package com.example.wordle.handler;

public class NoGameStartedException extends RuntimeException{
    public NoGameStartedException(String message, Throwable cause) {super(message, cause);}
}
