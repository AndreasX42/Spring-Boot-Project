package com.andreasx42.taskmanagerapi.exception;

public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String fieldName, String fieldValue, Class<?> entity) {
        super(String.format("The %s with %s '%s' does already exist in our records",
                entity.getSimpleName().toLowerCase(), fieldName, fieldValue));
    }

}
