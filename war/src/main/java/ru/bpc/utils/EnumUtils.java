package ru.bpc.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ru.bpc.Id;
import ru.bpc.commons.exception.NotFoundEnumElementException;

public class EnumUtils extends org.apache.commons.lang3.EnumUtils {

	public static <E extends Enum<E> & Id> E getEnum(Class<E> clazz, int id) {
		for (E value : org.apache.commons.lang3.EnumUtils.getEnumList(clazz)) {
			if (value.getId() == id) {
				return value;
			}
		}
		return null;
	}

	public static <E extends Enum<E> & Id> E getEnumEx(Class<E> clazz, Integer id) {
		E value = ((id == null) ? null : getEnum(clazz, id));
		if (value == null) {
			throw new NotFoundEnumElementException(clazz.getName(), id);
		}
		return value;
	}

	public static Integer[] getIds(Id... ids) {
		if (ids == null) {
			return null;
		}
		return getIds(Arrays.asList(ids));
	}

	public static Integer[] getIds(Collection<Id> idList) {
		int i = 0;
		Integer[] ids = new Integer[idList.size()];
		for (Id id : idList) {
			ids[i++] = getId(id);
		}
		return ids;
	}

	public static Integer getId(Id item) {
		if (item == null) {
			return null;
		}
		return item.getId();
	}

	public static List<Integer> getIdList(Id... ids) {
		if (ids == null) {
			return null;
		} else {
			return getIdList(Arrays.asList(ids));
		}
	}

	public static List<Integer> getIdList(Collection<Id> items) {
		if (items == null) {
			return null;
		} else {
			return Arrays.asList(getIds(items));
		}
	}
}
