package com.vuntfx17196.controller;

import com.vuntfx17196.global.AccountResourceException;
import com.vuntfx17196.global.BadRequestAlertException;
import com.vuntfx17196.global.UserNotActivatedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(BadRequestAlertException.class)
    public ModelAndView handleBadRequestException(BadRequestAlertException ex) {
        return new ModelAndView("error", "message", ex.getMessage());
    }

    @ExceptionHandler(AccountResourceException.class)
    public ModelAndView handleAccountResourceException(AccountResourceException ex) {
        return new ModelAndView("error", "message", ex.getMessage());
    }

    @ExceptionHandler(UserNotActivatedException.class)
    public ModelAndView handleUserNotActivedException(UserNotActivatedException e) {
        return new ModelAndView("error", "message", e.getMessage());
    }

}
