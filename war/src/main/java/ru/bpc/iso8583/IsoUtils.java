package ru.bpc.iso8583;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import ru.bpc.iso8583.field.IField;
import ru.bpc.iso8583.field.types.TagsetField;
import ru.bpc.iso8583.field.types.tag.Tag;
import ru.bpc.iso8583.text.internal.FieldConfig;
import ru.bpc.iso8583.xml.FieldType;
import ru.bpc.iso8583.xml.MessageType;
import ru.bpc.iso8583.xml.ObjectFactory;
import ru.bpc.iso8583.xml.TagType;
import ru.bpc.iso8583.xml.TagsType;
import ru.bpc.orach.modules.iso8583.Iso8583Utils;
import ru.bpc.orach.modules.iso8583.TagInfo;

public class IsoUtils {

	private static final int[] ISO_STATE_FIELDS = getIds(IsoField.values());
	private static final String DEFAULT_ENCODING = "UTF-8";

	public static Object[] saveIsoState(ObjectMessage objectMessage) throws ISO8583Exception {
		return Iso8583Utils.saveIsoState(objectMessage, ISO_STATE_FIELDS);
	}

	public static ObjectMessage restoreIsoState(ObjectMessage isoMsg, Object[] objs) throws ISO8583Exception {
		Iso8583Utils.restoreIsoState(isoMsg, objs, ISO_STATE_FIELDS);
		return isoMsg;
	}

	public static MessageType saveIsoState(ObjectMessage objectMessage, String outputEncoding) throws ISO8583Exception {
		ObjectFactory factory = new ObjectFactory();
		MessageType message = factory.createMessageType();
		message.setType(String.valueOf(objectMessage.getType()));
		message.setFields(factory.createFieldsType());
		List<FieldType> list = message.getFields().getField();
		for (IField field : objectMessage.getFields().values()) {
			FieldConfig fieldConfig = field.getConfig();
			FieldType curField = factory.createFieldType();
			curField.setId(field.getId());

			if ("tagset".equals(fieldConfig.getObjType())) {
				TagsType tags = factory.createTagsType();
				TagsetField tagset = new TagsetField(field);
				List<Tag> tagData = tagset.getAllTags();
				for (Tag item : tagData) {
					if (item.getBytes().length > 0) {
						TagType curTag = factory.createTagType();
						curTag.setId(item.getConfig().getFieldId());
						addValue(curTag, item.getBytes(), item.getConfig(), outputEncoding);
						tags.getTag().add(curTag);
					}
				}
				curField.setTags(tags);
			} else {
				addValue(curField, field.getData(), fieldConfig, outputEncoding);
			}
			list.add(curField);
		}
		return message;
	}

	public static ObjectMessage restoreIsoState(ObjectMessage isoMsg, MessageType message) throws ISO8583Exception {
		for (FieldType item : message.getFields().getField()) {
			if (item.getValue() != null) {
				isoMsg.setObjectField(item.getId(), item.getValue());
			} else if (item.getTags() != null) {
				List<TagInfo> list = new ArrayList<TagInfo>();
				for (TagType tag : item.getTags().getTag()) {
					Object obj = (tag.getValue() == null ? tag.getBase64BinaryValue() : tag.getValue());
					list.add(new TagInfo(tag.getId(), obj));
				}
				Iso8583Utils.fillTags(isoMsg, item.getId(), list);
			}
		}
		return isoMsg;
	}

	public static String getFieldValue(MessageType message, IsoField field, String encoding) throws ISO8583Exception {
		return getValue(getField(message, field, false), encoding);
	}

	public static void setFieldObject(MessageType message, IsoField field, Object obj) throws ISO8583Exception {
		addObject(getField(message, field, true), obj);
	}

	public static String getTagValue(MessageType message, IsoTag tag, String encoding) throws ISO8583Exception {
		return getValue(getTag(message, tag, false), encoding);
	}

	public static String getTagValue(MessageType message, IsoTag tag, String encoding, String sDefault) throws ISO8583Exception {
		try {
			return getValue(getTag(message, tag, false), encoding);
		} catch (ISO8583Exception e) {
			return sDefault;
		}
	}

	public static void setTagObject(MessageType message, IsoTag tag, Object obj) throws ISO8583Exception {
		addObject(getTag(message, tag, true), obj);
	}

	public static void setTagObject(FieldType field, IsoTag tag, Object obj) throws ISO8583Exception {
		addObject(getTag(field, tag, true), obj);
	}

	public static int[] getIds(IsoField... fields) {
		int[] ids = new int[fields.length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = fields[i].getId();
		}
		return ids;
	}

	private static void addValue(FieldType field, byte[] data, FieldConfig fieldConfig, String encoding) throws ISO8583Exception {
		if (encoding == null) {
			encoding = DEFAULT_ENCODING;
		}
		try {
			switch (fieldConfig.getSymbolSet()) {
				case Alpha:
				case Numeric:
				case AlphaNumeric:
					field.setValue(new String(data, encoding));
					break;
				default:
					field.setBase64BinaryValue(data);
					break;
			}
		} catch (UnsupportedEncodingException e) {
			throw new ISO8583Exception(e);
		}
	}

	private static String getValue(FieldType field, String encoding) throws ISO8583Exception {
		if (encoding == null) {
			encoding = DEFAULT_ENCODING;
		}
		try {
			return ((field.getValue() == null) ? new String(field.getBase64BinaryValue(), encoding) : field.getValue());
		} catch (UnsupportedEncodingException e) {
			throw new ISO8583Exception(e);
		}
	}

	private static void addObject(FieldType field, Object obj) throws ISO8583Exception {
		if (obj == null) {
			field.setValue(null);
			field.setBase64BinaryValue(null);
		} else if (obj instanceof JAXBElement<?>) {
			obj = ((JAXBElement<?>) obj).getValue();
			addObject(field, obj);
		} else if (obj instanceof byte[]) {
			field.setBase64BinaryValue((byte[]) obj);
		} else if ((obj instanceof String) || (obj instanceof Number)) {
			field.setValue(obj.toString());
		} else {
			throw new ISO8583Exception("Unsupported type of value " + obj.getClass());
		}
	}

	private static TagType getTag(MessageType message, IsoTag tag, boolean create) throws ISO8583Exception {
		FieldType field = getField(message, tag.getField(), create);
		return getTag(field, tag, create);
	}

	private static FieldType getField(MessageType message, IsoField field, boolean create) throws ISO8583Exception {
		for (FieldType item : message.getFields().getField()) {
			if (item.getId() == field.getId()) {
				return item;
			}
		}
		if (create) {
			FieldType item = new ObjectFactory().createFieldType();
			item.setId(field.getId());
			message.getFields().getField().add(item);
			return item;
		}
		throw new ISO8583Exception("No field " + field + "(" + field.getId() + ") found");
	}

	private static TagType getTag(FieldType field, IsoTag tag, boolean create) throws ISO8583Exception {
		for (TagType item : field.getTags().getTag()) {
			if (item.getId() == tag.getId()) {
				return item;
			}
		}
		if (create) {
			TagType item = new ObjectFactory().createTagType();
			item.setId(tag.getId());
			field.getTags().getTag().add(item);
			return item;
		}
		throw new ISO8583Exception("No tag " + tag + "(" + tag.getId() + ") found");
	}
}
