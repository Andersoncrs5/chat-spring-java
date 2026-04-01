package com.chat.api.utils.result;

import lombok.Getter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class Result<T> {

    private final T value;
    private final boolean isSuccess;
    private final List<String> errors;

    private Result(T value, boolean isSuccess, List<String> errors) {
        this.value = value;
        this.isSuccess = isSuccess;
        this.errors = Collections.unmodifiableList(errors);
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, true, new ArrayList<>());
    }

    public static <T> Result<T> failure(String error) {
        return new Result<>(null, false, List.of(error));
    }

    public static <T> Result<T> failure(List<String> errors) {
        return new Result<>(null, false, errors);
    }

    public boolean isFailure() {
        return !isSuccess;
    }
    public <U> Result<U> map(Function<T, U> mapper) {
        if (isSuccess) {
            return Result.success(mapper.apply(value));
        }
        return Result.failure(this.errors);
    }

    public Result<T> ifSuccess(Consumer<T> action) {
        if (isSuccess) {
            action.accept(value);
        }
        return this;
    }

    public Result<T> ifFailure(Consumer<List<String>> action) {
        if (isFailure()) {
            action.accept(errors);
        }
        return this;
    }

    public T orElse(T defaultValue) {
        return isSuccess ? value : defaultValue;
    }
}