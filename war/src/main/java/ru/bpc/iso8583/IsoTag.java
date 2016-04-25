package ru.bpc.iso8583;

public enum IsoTag {
    BILL_NUMBER(51, IsoField.ADDITIONAL_DATA),
    CUSTOMER_NAME(57, IsoField.ADDITIONAL_DATA),
    CUSTOMER_NUM_RESQUEST(07, IsoField.ADDITIONAL_DATA),
    CUSTOMER_NUM_RESPONSE(58, IsoField.ADDITIONAL_DATA),
    CUSTOMER_ADDR(59, IsoField.ADDITIONAL_DATA),
    CUSTOMER_PHONE(60, IsoField.ADDITIONAL_DATA),
    BILLING_DATE(61, IsoField.ADDITIONAL_DATA),
    BILLING_PERIOD(62, IsoField.ADDITIONAL_DATA);

    public int getId() {
	return id;
    }

    public IsoField getField() {
	return field;
    }

    private IsoTag(int id, IsoField field) {
	this.id = id;
	this.field = field;
    }

    private int id;
    private IsoField field;
}
