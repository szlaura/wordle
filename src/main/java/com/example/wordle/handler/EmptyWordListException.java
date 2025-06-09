package com.example.wordle.handler;

public class EmptyWordListException extends RuntimeException{
    public EmptyWordListException(String message){
        super(message);
    }
}
