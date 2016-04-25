package ru.bpc.orach;

import com.codahale.metrics.JmxReporter;

public class MetricsReporter {

	final JmxReporter reporter;

	public MetricsReporter(IServiceProvider service) {
		reporter = JmxReporter.forRegistry(service.getMetricRegistry()).build();
	}

	public void start() {
		reporter.start();
	}

	public void stop() {
		reporter.stop();
	}
}
