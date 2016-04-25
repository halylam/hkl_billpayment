package ru.bpc.orach.hkl.billpayment.modules;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.bpc.commons.ejb.jdbc.query.UpdateQuery;
import ru.bpc.orach.IModule;
import ru.bpc.orach.INotificator;
import ru.bpc.orach.hkl.billpayment.modules.enums.ApplicationStatus;
import ru.bpc.utils.EnumUtils;

@Component
public class ApplicationNotificator implements INotificator {
    private final static Logger logger = LoggerFactory.getLogger(ApplicationNotificator.class);

    @Resource
    private DataSource dsFEAuthorized;
    private int pid;

    @Override
    public void notify(String summary, String details, IModule module) {
	logger.warn("{}: {}", module.getId(), summary);
    }

    @Override
    public void start() {
	updateStatus(ApplicationStatus.ONLINE);
	logger.info("Billpayment started");
    }

    @Override
    public void stop() {
	updateStatus(ApplicationStatus.OFFLINE);
	logger.info("Billpayment stopped");
    }

    public void setPid(int pid) {
	this.pid = pid;
    }

    private void updateStatus(ApplicationStatus status) {
	UpdateQuery update = new UpdateQuery();
	update.table("t_epay_stat").key("pid", pid).value("oper_stat", EnumUtils.getId(status));
	if (update.update(dsFEAuthorized).getCount() == 0) {
	    logger.warn("No epay_stat records found for pid {}", pid);
	}
    }
}
