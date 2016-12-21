package ru.bpc.orach.hkl.billpayment.modules;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bpc.iso8583.ISO8583;
import ru.bpc.iso8583.ISO8583Exception;
import ru.bpc.iso8583.IsoField;
import ru.bpc.iso8583.IsoUtils;
import ru.bpc.iso8583.ObjectMessage;
import ru.bpc.iso8583.xml.MessageType;
import ru.bpc.orach.IDataMessage;
import ru.bpc.orach.IDataPacket;
import ru.bpc.orach.IDataPacketUpdateContext;
import ru.bpc.orach.hkl.billpayment.modules.constants.IErrors;
import ru.bpc.orach.hkl.billpayment.modules.constants.IMessageFields;
import ru.bpc.orach.modules.iso8583.AFrontEndModule;
import ru.bpc.orach.modules.iso8583.AtmIdentification;

public class Iso8583Module extends AFrontEndModule {
	private static final Logger logger = LoggerFactory.getLogger(Iso8583Module.class);

	private AtmIdentification originator;
	private String elements;
	private String messages;

	public Iso8583Module(int moduleType, String moduleId, String elements, String messages) throws ISO8583Exception {
		super(moduleType, moduleId);
		this.elements = elements;
		this.messages = messages;
	}

	public Iso8583Module(int moduleType, String moduleId, int corePoolSize, int maxPoolSize) throws ISO8583Exception {
		super(moduleType, moduleId, corePoolSize, maxPoolSize);
	}

	public void setOriginator(AtmIdentification originator) {
		this.originator = originator;
	}

	@Override
	protected ISO8583 createIsoProcessor() throws ISO8583Exception {
		return new ISO8583(openInputStream(elements), openInputStream(messages));
	}

	@Override
	protected void processRequest(ObjectMessage objectMessage) throws ISO8583Exception {
		logger.debug("CALL Iso8583Module: processRequest");
		if (objectMessage.getType() == 100 || objectMessage.getType() == 200) {
			Object prcode = objectMessage.getObjectField(IsoField.PROCESSING_CODE.getId()).getAsObject();
			if (prcode != null) {
				if (prcode.equals(830000L) || prcode.equals(840000L)) {
					MessageType message = IsoUtils.saveIsoState(objectMessage, null);
					IDataMessage msg = getMessageStore().createMessage();
					IDataPacketUpdateContext uc = msg.getDataPacket().getUpdateContext(this);
					uc.setObject(IMessageFields.ISO_MESSAGE, message);
					uc.apply();
					getMessageBus().receivedNew(msg, this);
				}
			}
		}
	}

	@Override
	protected void processResponse(ObjectMessage objectMessage, IDataMessage originator) {
		// replies are not expected
	}

	@Override
	protected void internalProcess(IDataMessage msg) {
		logger.debug("CALL Iso8583Module: internalProcess");
		IDataPacket dp = msg.getDataPacket();
		try {
			ObjectMessage isoMsg = null;
			Object response = dp.getObject(IMessageFields.ISO_RESPONSE);
			if (response instanceof MessageType) {
				String prcode = IsoUtils.getFieldValue((MessageType) response, IsoField.PROCESSING_CODE, null);
				logger.debug("CALL Iso8583Module: internalProcess: prcode=" + prcode);
				if (prcode.equals("830000")) {
					isoMsg = createMessage(110);
					logger.debug("createMessage(110) successful");
				} else if (prcode.equals("840000")) {
					isoMsg = createMessage(210);
					logger.debug("createMessage(210) successful");
				}
				if (isoMsg != null) {
					IsoUtils.restoreIsoState(isoMsg, (MessageType) response);
				} else {
					logger.debug("isoMsg NULL");
				}
			}
			sendMessage(isoMsg, originator, Long.MAX_VALUE);
			logger.debug("Message [id={}] sent to FE", msg.getId());

			getMessageBus().sentOk(msg, this);
		} catch (ISO8583Exception isoe) {
			logger.error("", isoe);
			reportError(msg, IErrors.RUNTIME);
		} catch (Exception e) {
			logger.error("", e);
			reportError(msg, IErrors.RUNTIME);
		}
	}

	@Override
	protected void handleErrorReply(ObjectMessage objectMessage, String isoErrorCode, IDataMessage originator) throws ISO8583Exception {
		IDataPacketUpdateContext uc = originator.getDataPacket().getUpdateContext(this);
		uc.setString(IMessageFields.FE_ERROR_CODE, isoErrorCode);
		uc.setNumber(IMessageFields.ERROR, IErrors.FE_ERROR);
		uc.apply();

		getMessageBus().sentErrors(originator, this, IErrors.FE_ERROR);
	}

	private void reportError(IDataMessage msg, int errCode) {
		IDataPacket dpck = msg.getDataPacket();

		IDataPacketUpdateContext uc = dpck.getUpdateContext(this);
		uc.setNumber(IMessageFields.ERROR, errCode);
		uc.apply();

		getMessageBus().sentErrors(msg, this, errCode);
	}

	private InputStream openInputStream(String path) {
		InputStream is = null;
		if (path != null) {
			try {
				File file = new File(path);
				if (file.exists()) {
					is = FileUtils.openInputStream(file);
				} else {
					is = getClass().getClassLoader().getResourceAsStream(path);
				}
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		return is;
	}
}
