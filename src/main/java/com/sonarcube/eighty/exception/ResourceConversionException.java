package com.sonarcube.eighty.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ResourceConversionException extends RuntimeException
{
    public ResourceConversionException(String resourceName, String targetName)
    {
        super("Error while serializing " + resourceName + " to " + targetName);
    }
}
