package com.ecnu.trivia.common.exception;

/**
 * Created by Jack Chen on 31/8/2017.
 */

public class IRCloudException extends RuntimeException {
  public IRCloudException() {
  }

  public IRCloudException(String message) {
    super(message);
  }

  public IRCloudException(String message, Throwable cause) {
    super(message, cause);
  }
}
