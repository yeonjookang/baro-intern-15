package org.example.baro.exception;

public interface ErrorCode {
	int getStatus();

	String getCode();

	String getMessage();
}
