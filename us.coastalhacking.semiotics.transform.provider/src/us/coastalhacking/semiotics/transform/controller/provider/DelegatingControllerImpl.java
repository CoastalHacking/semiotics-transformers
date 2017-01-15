package us.coastalhacking.semiotics.transform.controller.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import us.coastalhacking.semiotics.xcore.model.transformation.Controller;
import us.coastalhacking.semiotics.xcore.model.transformation.Service;

@Component
public class DelegatingControllerImpl implements Controller {

	final ConcurrentLinkedQueue<Service> serviceQueue = new ConcurrentLinkedQueue<>();
	
	@Reference(
		cardinality=ReferenceCardinality.MULTIPLE,
		policy=ReferencePolicy.DYNAMIC
	)
	void setService(Service service) {
		serviceQueue.add(service);
	}
	void unsetService(Service service) {
		serviceQueue.remove(service);
	}
	
	@Override
	public Collection<?> transform(Object source, Object target) {
		final Collection<Object> results = new ArrayList<>();
		for (Service service: serviceQueue) {
			if (service.isTransformable(source, target)) {
				results.addAll(service.transform(source, target));
			}
		}
		return results;
	}

	@Override
	public boolean isSourceSupported(Object source) {
		for (Service service: serviceQueue) {
			if (service.isSourceSupported(source))
				return true;
		}
		return false;
	}

}
