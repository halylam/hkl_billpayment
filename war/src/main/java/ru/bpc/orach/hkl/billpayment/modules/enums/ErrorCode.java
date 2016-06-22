package ru.bpc.orach.hkl.billpayment.modules.enums;

public enum ErrorCode {
    SUCCESSFUL(0), INVALID_ACCOUNT(2), SERVICE_NOT_REACHABLE(4), NOT_AUTHORIZED(5), 
    SYSTEM_ERROR(6), SYNTAX_ERROR(7), DUPLICATE_REQUEST(12), TRANSACTION_UNSUCCESSFUL(15);

    private ErrorCode(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }

    @Override
    public String toString() {
	return format(code);
    }

    public static String format(int code) {
	return String.format("%02d", code);
    }

    private int code;
}
