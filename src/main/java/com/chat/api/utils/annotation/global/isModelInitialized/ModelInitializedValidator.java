package com.chat.api.utils.annotation.global.isModelInitialized;

import com.chat.api.utils.base.models.BaseModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ModelInitializedValidator implements ConstraintValidator<IsModelInitialized, BaseModel> {

    @Override
    public boolean isValid(BaseModel value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return value.getId() != null;
    }
}