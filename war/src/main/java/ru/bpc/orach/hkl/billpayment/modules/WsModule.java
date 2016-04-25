package ru.bpc.orach.hkl.billpayment.modules;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bpc.iso8583.ISO8583Exception;
import ru.bpc.iso8583.IsoField;
import ru.bpc.iso8583.IsoTag;
import ru.bpc.iso8583.IsoUtils;
import ru.bpc.iso8583.xml.MessageType;
import ru.bpc.orach.IDataMessage;
import ru.bpc.orach.IDataPacket;
import ru.bpc.orach.IDataPacketUpdateContext;
import ru.bpc.orach.InitializationException;
import ru.bpc.orach.hkl.billpayment.modules.constants.IMessageFields;
import ru.bpc.orach.hkl.billpayment.modules.enums.ErrorCode;
import ru.bpc.orach.hkl.billpayment.ws.BillInfo;
import ru.bpc.orach.hkl.billpayment.ws.BillPaymentInfo;
import ru.bpc.orach.hkl.billpayment.ws.LoginInfo;
import ru.bpc.orach.hkl.billpayment.ws.Service;
import ru.bpc.orach.hkl.billpayment.ws.ServiceSoap;
import ru.bpc.orach.hkl.billpayment.ws.UpdateStatus;
import ru.bpc.orach.module.AThreadingModule;

public class WsModule extends AThreadingModule {
    private static final Logger logger = LoggerFactory.getLogger(WsModule.class);
    private static final String REQUEST_TIMEOUT_PROPERTY_1 = "com.sun.xml.internal.ws.request.timeout";
    private static final String CONNECT_TIMEOUT_PROPERTY_1 = "com.sun.xml.internal.ws.connect.timeout";
    private static final String REQUEST_TIMEOUT_PROPERTY_2 = "com.sun.xml.ws.request.timeout";
    private static final String CONNECT_TIMEOUT_PROPERTY_2 = "com.sun.xml.ws.connect.timeout";
    private static final String REQUEST_TIMEOUT_PROPERTY_3 = "javax.xml.ws.client.receiveTimeout";
    private static final String CONNECT_TIMEOUT_PROPERTY_3 = "javax.xml.ws.client.connectionTimeout";

    private ServiceSoap port;
    private String username;
    private String password;
//    private String serviceIdRegexp;//NOT USE FOR HKL
//    private boolean testing;//NOT USE FOR HKL
//    private int payeeIndex;//NOT USE FOR HKL
//    private int domainIndex;//NOT USE FOR HKL
    private int requestTimeout;
    private int connectTimeout;
    private int amountExp;//NOT USE FOR HKL

    private final URL wsdlLocation;

    public WsModule(int moduleType, String moduleId, URL wsdlLocation) {
	this(moduleType, moduleId, wsdlLocation, 3, 8);
    }

    public WsModule(int moduleType, String moduleId, URL wsdlLocation, int corePoolSize, int maxPoolSize) {
	super(moduleType, moduleId, corePoolSize, maxPoolSize);
	this.wsdlLocation = wsdlLocation;
    }

    public void setAmountExp(int amountExp) {
	this.amountExp = amountExp;
    }

    public void setConnectTimeout(int connectTimeout) {
	this.connectTimeout = connectTimeout;
    }

//    public void setDomainIndex(int domainIndex) {
//	this.domainIndex = domainIndex;
//    }

    public void setPassword(String password) {
	this.password = password;
    }

//    public void setPayeeIndex(int payeeIndex) {
//	this.payeeIndex = payeeIndex;
//    }

    public void setRequestTimeout(int requestTimeout) {
	this.requestTimeout = requestTimeout;
    }

//    public void setServiceIdRegexp(String serviceIdRegexp) {
//	this.serviceIdRegexp = serviceIdRegexp;
//    }

//    public void setTesting(boolean testing) {
//	this.testing = testing;
//    }

    public void setUsername(String username) {
	this.username = username;
    }

    @Override
    protected void internalProcess(IDataMessage msg) {
	logger.debug("CALL WsModule: internalProcess");
	IDataPacket dp = msg.getDataPacket();
	MessageType message = (MessageType) dp.getObject(IMessageFields.ISO_MESSAGE);
	IDataPacketUpdateContext uc = dp.getUpdateContext(this);
	int responseCode = ErrorCode.SUCCESSFUL.getCode();
	String prcode = "";
	try {
	    prcode = IsoUtils.getFieldValue(message, IsoField.PROCESSING_CODE, null);
	    if (prcode.equals(830000L)) { // ex: case getBillInfo
		String billNumber = IsoUtils.getTagValue(message, IsoTag.BILL_NUMBER, null);

		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setBankCode(username);
		loginInfo.setPassword(password);

		logger.debug("INPUT getBillInfo: billNumber=" + billNumber + " - username=" + username + " - password=" + password);

		BillInfo response = port.getBillInfo(billNumber, loginInfo);

		if (response != null) {
		    logger.debug("OUTPUT getBillInfo: STATUS_MESSAGE : " + getValue(response.getStatusMessage()));
		    logger.debug("OUTPUT getBillInfo: CUSTOMER_NAME : " + getValue(response.getCustomerName()));
		    logger.debug("OUTPUT getBillInfo: CUSTOMER_NUM : " + getValue(response.getCustomerNumber()));
		    logger.debug("OUTPUT getBillInfo: CUSTOMER_ADDR : " + getValue(response.getCustomerAddress()));
		    logger.debug("OUTPUT getBillInfo: CUSTOMER_PHONE : " + getValue(response.getPhone()));
		    logger.debug("OUTPUT getBillInfo: BILL_AMOUNT : " + getValue(response.getBillAmount()));
		    logger.debug("OUTPUT getBillInfo: BILL_DATE : " + getValue(response.getBillingDate()));
		    logger.debug("OUTPUT getBillInfo: BILL_PERIOD : " + getValue(response.getBillingPeriod()));

		    if ("No Record Found".equalsIgnoreCase(response.getStatusMessage())) {
			responseCode = ErrorCode.TRANSACTION_UNSUCCESSFUL.getCode();
			logger.debug("OUTPUT getBillInfo:  Record Not Found - bill number is wrong or bill is expired: responseCode="
				+ responseCode);
		    } else {
			IsoUtils.setTagObject(message, IsoTag.CUSTOMER_NAME, getValue(response.getCustomerName()));
			IsoUtils.setTagObject(message, IsoTag.CUSTOMER_NUM_RESPONSE, getValue(response.getCustomerNumber()));
			IsoUtils.setTagObject(message, IsoTag.CUSTOMER_ADDR, getValue(response.getCustomerAddress()));
			IsoUtils.setTagObject(message, IsoTag.CUSTOMER_PHONE, getValue(response.getPhone()));
			IsoUtils.setFieldObject(message, IsoField.AMOUNT_TRANSACTION, getValue(response.getBillAmount()));
			IsoUtils.setTagObject(message, IsoTag.BILLING_DATE, getValue(response.getBillingDate()));
			IsoUtils.setTagObject(message, IsoTag.BILLING_PERIOD, getValue(response.getBillingPeriod()));
		    }
		} else {
		    responseCode = ErrorCode.TRANSACTION_UNSUCCESSFUL.getCode();
		    logger.error("getBillInfo Response NULL: responseCode=" + responseCode);
		}

	    } else if (prcode.equals(840000L)) {// ex: case UpdateBill
		BillPaymentInfo billPaymentInfo = new BillPaymentInfo();

		String billNumber = IsoUtils.getTagValue(message, IsoTag.BILL_NUMBER, null);
		String billAmount = IsoUtils.getFieldValue(message, IsoField.AMOUNT_TRANSACTION, null);
		String paymentDate = IsoUtils.getFieldValue(message, IsoField.PAYMENT_DATE, null);
		String customerNum = IsoUtils.getTagValue(message, IsoTag.CUSTOMER_NUM_RESQUEST, null);

		billPaymentInfo.setBillNumber(billNumber);
		billPaymentInfo.setBillAmount(billAmount);
		billPaymentInfo.setPaymentDate(paymentDate);
		billPaymentInfo.setCustomerNumber(customerNum);

		logger.debug("INPUT UpdateBill: billNumber=" + billNumber + " - billAmount=" + billAmount + " -paymentDate" + paymentDate
			+ " -customerNum" + customerNum);

		String checkSum = "";
		if (customerNum != null && customerNum.length() > 4) {
		    ;
		    try {
			int cusNum = Integer.parseInt(customerNum.substring(customerNum.length() - 4));
			int amount = Integer.parseInt(billAmount.substring(0, 3));
			checkSum = String.valueOf(cusNum * amount);
		    } catch (Exception e) {
			logger.error("Exception checkSum: customerNum=" + customerNum + " - billAmount=" + billAmount);
			onError(e, uc);
		    }
		}
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setBankCode(username);
		loginInfo.setPassword(password);

		logger.debug("INPUT UpdateBill: checkSum=" + checkSum + " - username=" + username + " -password" + password);

		UpdateStatus response = port.updateBill(billPaymentInfo, loginInfo, checkSum);
		if (response != null && response.getStatusMessage() != null) {
		    String statuMess = getValue(response.getStatusMessage());
		    logger.debug("OUTPUT UpdateBill: STATUS_MESSAGE : " + statuMess);
		    if ("Success".equalsIgnoreCase(statuMess)) {
			logger.debug("OUTPUT UpdateBill:  Payment is succeed: responseCode=" + responseCode);
		    } else if ("Record Not Found".equalsIgnoreCase(statuMess)) {
			responseCode = ErrorCode.TRANSACTION_UNSUCCESSFUL.getCode();
			logger.debug("OUTPUT UpdateBill:  Record Not Found - send wrong information: responseCode=" + responseCode);
		    } else if ("Bill has been paid previously".equalsIgnoreCase(statuMess)) {
			responseCode = ErrorCode.TRANSACTION_UNSUCCESSFUL.getCode();
			logger.debug("OUTPUT UpdateBill:  Bill was paid: responseCode=" + responseCode);
		    }
		} else {
		    responseCode = ErrorCode.TRANSACTION_UNSUCCESSFUL.getCode();
		    logger.error("OUTPUT UpdateBill: NULL: responseCode=" + responseCode);
		}
	    }
	} catch (Exception e) {
	    responseCode = onError(e, uc);
	}

	try {
	    IsoUtils.setFieldObject(message, IsoField.RESPONSE_CODE, ErrorCode.format(responseCode));
	    IsoUtils.setFieldObject(message, IsoField.PROCESSING_CODE, prcode);
	    uc.setObject(IMessageFields.ISO_RESPONSE, message);
	} catch (ISO8583Exception e) {
	    responseCode = onError(e, uc);
	}

	uc.apply();

	if (responseCode == ErrorCode.SUCCESSFUL.getCode()) {
	    getMessageBus().sentOk(msg, this);
	} else {
	    getMessageBus().sentErrors(msg, this, 1);
	}
    }

    @Override
    protected void internalStart() throws InitializationException {
	logger.debug("CALL WsModule: internalStart");
	if (wsdlLocation == null) {
	    port = new Service().getServiceSoap();
	} else {
	    port = new Service(wsdlLocation).getServiceSoap();
	}
	logger.debug("CALL WsModule: FOUND WEBSERVICE =" + wsdlLocation);
	Map<String, Object> ctx = ((BindingProvider) port).getRequestContext();
	ctx.put(REQUEST_TIMEOUT_PROPERTY_1, requestTimeout);
	ctx.put(REQUEST_TIMEOUT_PROPERTY_2, requestTimeout);
	ctx.put(REQUEST_TIMEOUT_PROPERTY_3, requestTimeout);
	ctx.put(CONNECT_TIMEOUT_PROPERTY_1, connectTimeout);
	ctx.put(CONNECT_TIMEOUT_PROPERTY_2, connectTimeout);
	ctx.put(CONNECT_TIMEOUT_PROPERTY_3, connectTimeout);
    }

    @SuppressWarnings("unused")
    private String getValue(String value) {
	return value;
    }

    private String getValue(JAXBElement<String> value) {
	return value.getValue();
    }

    private int onError(Exception e, IDataPacketUpdateContext uc) {
	logger.error("", e);
	uc.setObject(IMessageFields.EXCEPTION, e.getMessage());
	return ErrorCode.SYSTEM_ERROR.getCode();
    }
}
