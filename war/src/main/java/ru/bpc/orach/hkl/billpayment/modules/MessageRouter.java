package ru.bpc.orach.hkl.billpayment.modules;

import java.util.Collections;
import java.util.List;

import ru.bpc.orach.IMessageRouter;
import ru.bpc.orach.IModule;
import ru.bpc.orach.hkl.billpayment.modules.constants.ProcessingStep;
import ru.bpc.orach.message.DataMessage;
import ru.bpc.orach.module.ModuleArray;
import ru.bpc.orach.persistence.DataMessageState;

public class MessageRouter implements IMessageRouter {
    private final ModuleArray _modules;

    public MessageRouter(ModuleArray modules) {
	this._modules = modules;
    }

    @Override
    public List<IModule> route(DataMessage message, IModule module) {
	String step = message.getProcessingStep();
	if (step == null) {
	    message.setProcessingStep(ProcessingStep.WS_STEP);
	    return Collections.singletonList(getModuleByType(200));
	}
	if (step.equals(ProcessingStep.WS_STEP)) {
	    message.setProcessingStep(ProcessingStep.ISO_STEP);
	    return Collections.singletonList(getModuleByType(100));
	}
	message.setProcessingStep(null);
	message.setState(DataMessageState.finished);
	return Collections.emptyList();
    }

    @Override
    public List<IModule> routeError(DataMessage message, IModule module, int errorCode) {
	String step = message.getProcessingStep();
	if (step.equals(ProcessingStep.WS_STEP)) {
	    message.setProcessingStep(ProcessingStep.ISO_STEP);
	    return Collections.singletonList(getModuleByType(100));
	}
	message.setProcessingStep(null);
	message.setState(DataMessageState.finished);
	return Collections.emptyList();
    }

    private IModule getModuleByType(int type) {
	for (IModule mod : this._modules) {
	    if (mod.getType() == type) {
		return mod;
	    }
	}
	return null;
    }
}
