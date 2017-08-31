package ru.bpc.orach.hkl.billpayment.modules;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;

import org.apache.commons.lang.StringUtils;
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
	private int requestTimeout;
	private int connectTimeout;
	private int amountExp;

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

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	protected void internalProcess(IDataMessage msg) {
		IDataPacket dp = msg.getDataPacket();
		MessageType message = (MessageType) dp.getObject(IMessageFields.ISO_MESSAGE);
		logger.debug("CALL WsModule: internalProcess with MessageType=" + message.getType());
		IDataPacketUpdateContext uc = dp.getUpdateContext(this);
		int responseCode = ErrorCode.SUCCESSFUL.getCode();
		String prcode = "";
		try {
			if ("800".equals(message.getType())) {
				logger.debug("CALL internalProcess: EchoTest at:" + IsoUtils.getFieldValue(message, IsoField.ECHO_TEST_TIME, null)
						+ " -NETWORK_CODE=" + IsoUtils.getFieldValue(message, IsoField.NETWORK_CODE, null));
			} else {
				prcode = IsoUtils.getFieldValue(message, IsoField.PROCESSING_CODE, null);
				logger.debug("prcode = " + prcode);
				if (prcode.equals("830000")) { // ex: case getBillInfo
					String billNumber = "PPWSA" + IsoUtils.getTagValue(message, IsoTag.BILL_NUMBER, null);

					LoginInfo loginInfo = new LoginInfo();
					loginInfo.setBankCode(username);
					loginInfo.setPassword(password);

					logger.debug("INPUT getBillInfo: billNumber=" + billNumber);

					BillInfo response = port.getBillInfo(billNumber, loginInfo);

					if (response != null) {
						logger.debug("OUTPUT getBillInfo: STATUS_MESSAGE : " + getValue(response.getStatusMessage()));
						logger.debug("OUTPUT getBillInfo: CUSTOMER_NAME : " + getValue(response.getCustomerName()));
						logger.debug("OUTPUT getBillInfo: CUSTOMER_NUM : " + getValue(response.getCustomerNumber()));
						logger.debug("OUTPUT getBillInfo: CUSTOMER_ADDR : " + getValue(response.getCustomerAddress()));
						logger.debug("OUTPUT getBillInfo: CUSTOMER_PHONE : " + getValue(response.getPhone()));
						String strAmount = getValue(response.getBillAmount());
						logger.debug("OUTPUT getBillInfo: STRING_AMOUNT : " + strAmount);
						BigDecimal bgAmount = null;
						try {
							bgAmount = new BigDecimal(strAmount).movePointRight(amountExp);
							logger.debug("OUTPUT getBillInfo: BILL_AMOUNT : " + bgAmount);
						} catch (Exception e) {
						}
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
							IsoUtils.setFieldObject(message, IsoField.AMOUNT_TRANSACTION, bgAmount);
							IsoUtils.setTagObject(message, IsoTag.BILLING_DATE, getValue(response.getBillingDate()));
							IsoUtils.setTagObject(message, IsoTag.BILLING_PERIOD, getValue(response.getBillingPeriod()));
						}
					} else {
						responseCode = ErrorCode.TRANSACTION_UNSUCCESSFUL.getCode();
						logger.error("getBillInfo Response NULL: responseCode=" + responseCode);
					}
				} else if (prcode.equals("840000")) {// ex: case UpdateBill
					BillPaymentInfo billPaymentInfo = new BillPaymentInfo();

					String billNumber = "PPWSA" + IsoUtils.getTagValue(message, IsoTag.BILL_NUMBER, null);
					String strAmount = IsoUtils.getFieldValue(message, IsoField.AMOUNT_TRANSACTION, null);
					logger.debug("INPUT UpdateBill: strAmount=" + strAmount);
					BigDecimal bgAmount = null;
					try {
						bgAmount = new BigDecimal(strAmount).movePointLeft(amountExp);
					} catch (Exception e) {
					}
					String paymentDate = IsoUtils.getFieldValue(message, IsoField.PAYMENT_DATE, null);
					if (paymentDate != null && paymentDate.length() > 5) {
						paymentDate = paymentDate.substring(4, 6) + "/" + paymentDate.substring(2, 4) + "/"
								+ String.valueOf(new Date().getYear() + 1900).substring(0, 2) + paymentDate.substring(0, 2);
					}

					String customerNum = IsoUtils.getTagValue(message, IsoTag.CUSTOMER_NUM_RESQUEST, null);

					billPaymentInfo.setBillNumber(billNumber);
					billPaymentInfo.setBillAmount(bgAmount.toString());
					billPaymentInfo.setPaymentDate(paymentDate);
					billPaymentInfo.setCustomerNumber(customerNum);

					logger.debug("INPUT UpdateBill: billNumber=" + billNumber + " - billAmount=" + bgAmount + " -paymentDate=" + paymentDate
							+ " -customerNum=" + customerNum);

					String checkSum = "";
					if (customerNum != null && customerNum.length() > 4 && bgAmount != null && bgAmount.toString().length() > 3) {
						try {
							int cusNum = Integer.parseInt(customerNum.substring(customerNum.length() - 4));
							int amount = Integer.parseInt(bgAmount.toString().substring(0, 3));
							checkSum = String.valueOf(cusNum * amount);
							logger.debug("INPUT UpdateBill: checkSum=" + checkSum);
						} catch (Exception e) {
							logger.error("Exception checkSum: customerNum=" + customerNum + " - billAmount=" + bgAmount);
							onError(e, uc);
						}
					}
					LoginInfo loginInfo = new LoginInfo();
					loginInfo.setBankCode(username);
					loginInfo.setPassword(password);

					logger.debug("INPUT UpdateBill: checkSum=" + checkSum);

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
						} else { // For other status message not mention in spec.
							responseCode = ErrorCode.TRANSACTION_UNSUCCESSFUL.getCode();
							logger.debug("OUTPUT UpdateBill:  Status message is not defined in spec: responseCode=" + responseCode);
						}
					} else {
						responseCode = ErrorCode.TRANSACTION_UNSUCCESSFUL.getCode();
						logger.error("OUTPUT UpdateBill: NULL: responseCode=" + responseCode);
					}
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
