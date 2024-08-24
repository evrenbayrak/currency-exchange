package org.project.currencyexchange.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

@JsonFormat
public record InvalidFieldInfo(String field, String message, String rejectedValue) implements Serializable {
}
