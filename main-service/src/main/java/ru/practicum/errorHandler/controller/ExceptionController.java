package ru.practicum.errorHandler.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.errorHandler.DataTimeException;
import ru.practicum.errorHandler.IntegrityViolationException;
import ru.practicum.errorHandler.NotFoundException;
import ru.practicum.errorHandler.RestrictionsViolationException;
import ru.practicum.errorHandler.model.ApiError;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleCommonException(Exception e) {
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .reason("Внутренняя ошибка сервера")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Ошибка в запросе пользователя")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleDataTimeException(DataTimeException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Ошибка в запросе пользователя с указанием даты и времени")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .reason("Запрашиваемый объект не найден")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIntegrityViolationException(IntegrityViolationException e) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason("Целостность данных была нарушена")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleRestrictionsViolationException(RestrictionsViolationException e) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason("Конфликт данных")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String violations = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Ошибка в запросе пользователя")
                .message(violations)
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Ошибка в запросе пользователя")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }
}
