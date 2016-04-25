package ru.bpc.orach.hkl.billpayment.modules.enums;

import ru.bpc.Id;

public enum ApplicationStatus implements Id {
	ONLINE(1), OFFLINE(0);

	private int id;

	ApplicationStatus(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}
}
