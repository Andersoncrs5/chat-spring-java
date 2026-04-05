package com.chat.api.utils.result;

import com.chat.api.modules.user.model.UserModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class Result<T> {

    private final T value;
    private final boolean isSuccess;
    private final List<String> errors;
    private final HttpStatus status;

    private Result(T value, boolean isSuccess, List<String> errors, HttpStatus status) {
        this.value = value;
        this.isSuccess = isSuccess;
        this.errors = errors != null ? Collections.unmodifiableList(errors) : Collections.emptyList();
        this.status = status;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, true, null, HttpStatus.OK);
    }

    public static <T> Result<T> created(T value) {
        return new Result<>(value, true, null, HttpStatus.CREATED);
    }

    public static <T> Result<T> failure(String error, HttpStatus status) {
        return new Result<>(null, false, List.of(error), status);
    }

    public static <T> Result<T> failure(List<String> errors, HttpStatus status) {
        return new Result<>(null, false, errors, status);
    }

    public static <T> Result<T> conflict(String s) {
        return new Result<>(null, false, List.of(s), HttpStatus.CONFLICT);
    }

    public static <T> Result<T> notFound(String s) {
        return new Result<>(null, false, List.of(s), HttpStatus.NOT_FOUND);
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    public int getStatusCode() {
        return status.value();
    }

    public <U> Result<U> map(Function<T, U> mapper) {
        if (isSuccess) {
            return new Result<>(mapper.apply(value), true, null, this.status);
        }
        return Result.failure(this.errors, this.status);
    }

    public Result<T> ifSuccess(Consumer<T> action) {
        if (isSuccess) action.accept(value);

        return this;
    }

    public Result<T> ifFailure(Consumer<List<String>> action) {
        if (isFailure()) action.accept(errors);

        return this;
    }

    public T orElse(T defaultValue) {
        return isSuccess ? value : defaultValue;
    }

}