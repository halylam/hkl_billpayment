package ru.bpc.iso8583;

public enum IsoField {
    PROCESSING_CODE(3), AMOUNT_TRANSACTION(4), RESPONSE_CODE(39), ADDITIONAL_DATA(48), PAYMENT_DATE(12), ECHO_TEST_TIME(7), NETWORK_CODE(70);

    private int id;

    public int getId() {
	return this.id;
    }

    private IsoField(int id) {
	this.id = id;
    }
}
