package com.example.wordle.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler implements CommandExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public CommandHandlingResult resolve(Exception e){
        if(e instanceof WordLoadingException ex){
            logger.warn("Error occurred while loading word list: {}", ex.getMessage());
            return CommandHandlingResult.of("[Error] " + ex.getMessage());
        }

        if(e instanceof IllegalArgumentException ex){
            logger.warn("Illegal argument provided: {}", ex.getMessage());
            return CommandHandlingResult.of("[Warning] " + ex.getMessage());
        }

        if(e instanceof IllegalStateException ex){
            logger.warn("Illegal state happened: {}", ex.getMessage());
            return CommandHandlingResult.of("[Warning] " + ex.getMessage());
        }

        logger.error("An unexpected error occured: {}", e.getMessage());
        return CommandHandlingResult.of("[Error] Unexpected error occurred: " + e.getMessage());
    }
}