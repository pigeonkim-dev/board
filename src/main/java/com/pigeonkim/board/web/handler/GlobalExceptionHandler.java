package com.pigeonkim.board.web.handler;

import com.pigeonkim.board.exception.BusinessException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/business";
    }

    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException(BusinessException e) {

        ModelAndView modelAndView = new ModelAndView("error/business");
        modelAndView.setStatus(e.getStatus());
        modelAndView.addObject("errorMessage", e.getMessage());

        return modelAndView;
    }
}
