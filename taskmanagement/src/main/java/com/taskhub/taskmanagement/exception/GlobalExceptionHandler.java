package com.taskhub.taskmanagement.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TaskNotFoundException.class)
    public Object handleTaskNotFoundException(TaskNotFoundException ex, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api/")) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } else {
            ModelAndView modelAndView = new ModelAndView("error-page");
            modelAndView.addObject("errorMessage", ex.getMessage());
            return modelAndView;
        }
    }

    @ExceptionHandler(TaskAlreadyExistsException.class)
    public Object handleTaskAlreadyExistsException(TaskAlreadyExistsException ex, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api/")) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } else {
            ModelAndView modelAndView = new ModelAndView("error-page");
            modelAndView.addObject("errorMessage", ex.getMessage());
            return modelAndView;
        }
    }

    @ExceptionHandler(InvalidTaskException.class)
    public Object handleInvalidTaskException(InvalidTaskException ex, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api/")) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } else {
            ModelAndView modelAndView = new ModelAndView("error-page");
            modelAndView.addObject("errorMessage", ex.getMessage());
            return modelAndView;
        }
    }
    @ExceptionHandler(TaskValidationException.class)
    public Object handleTaskValidationException(TaskValidationException ex, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api/")) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } else {
            ModelAndView modelAndView = new ModelAndView("error-page");
            modelAndView.addObject("errorMessage", ex.getMessage());
            return modelAndView;
        }

    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex, Model model,HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api/")) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            ModelAndView modelAndView = new ModelAndView("error-page");
            modelAndView.addObject("errorMessage","An unexpected error occurred: " +  ex.getMessage());
            return modelAndView;

        }
    }
    @ExceptionHandler(NoSuchElementException.class)
    public Object handleNoSuchElementException(NoSuchElementException ex, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api")) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            ModelAndView modelAndView = new ModelAndView("error-page");
            modelAndView.addObject("errorMessage", ex.getMessage());
            return modelAndView;

        }
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        if (e.getMessage().contains("Value too long for column")) {
            return ResponseEntity.badRequest().body("Task description is too long. Please shorten it.");
        }
        return ResponseEntity.badRequest().body("Data integrity violation");
    }


}
