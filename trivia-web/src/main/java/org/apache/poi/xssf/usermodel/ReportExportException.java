package org.apache.poi.xssf.usermodel;

import com.ecnu.trivia.common.exception.BusinessException;

public class ReportExportException extends BusinessException
{
    public ReportExportException() {
    }

    public ReportExportException(String message) {
        super(message);
    }

    public ReportExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
