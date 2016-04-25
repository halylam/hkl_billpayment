package ru.bpc.orach.hkl.billpayment.webstart;

import javax.servlet.ServletContextEvent;
import ru.bpc.orach.webstart.AbstractServletContextListener;
import ru.bpc.orach.webstart.logging.ILoggingConfigurator;
import ru.bpc.orach.webstart.logging.LogbackLoggingConfigurator;

public class ContextInitializedListener extends AbstractServletContextListener {
	private ILoggingConfigurator _configurator;

	public void contextInitialized(ServletContextEvent context) {
		this._configurator = new LogbackLoggingConfigurator(context);
		super.contextInitialized(context);
	}

	protected String getApplicationHomeEnvironmentVarName() {
		return "billpayment.home";
	}

	protected ILoggingConfigurator getLoggingConfigurator() {
		return this._configurator;
	}
}
