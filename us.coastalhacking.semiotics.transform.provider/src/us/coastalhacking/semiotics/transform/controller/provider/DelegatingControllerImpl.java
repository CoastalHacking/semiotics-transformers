package us.coastalhacking.semiotics.transform.controller.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import us.coastalhacking.semiotics.xcore.model.service.Controller;
import us.coastalhacking.semiotics.xcore.model.service.Converter;
import us.coastalhacking.semiotics.xcore.model.service.Transformer;

@Component
public class DelegatingControllerImpl implements Controller {

	final ConcurrentLinkedQueue<Transformer> transformers = new ConcurrentLinkedQueue<>();
	final ConcurrentLinkedQueue<Converter> converters = new ConcurrentLinkedQueue<>();
	
	@Reference(
		cardinality=ReferenceCardinality.MULTIPLE,
		policy=ReferencePolicy.DYNAMIC
	)
	void setTransformer(Transformer service) {
		transformers.add(service);
	}
	void unsetTransformer(Transformer service) {
		transformers.remove(service);
	}

	@Reference(
			cardinality=ReferenceCardinality.MULTIPLE,
			policy=ReferencePolicy.DYNAMIC
		)
	void setConverter(Converter service) {
		converters.add(service);
	}
	void unsetConverter(Converter service) {
		converters.remove(service);
	}
	
	@Override
	public Collection<?> transform(Object source, Object target) {
		Collection<?> sources = convertSource(source);

		final Collection<Object> results = new ArrayList<>();
		for (Transformer service: transformers) {
			if (service.isTransformable(sources, target)) {
				results.addAll(service.transform(sources, target));
			}
		}

		return results;
	}

	@Override
	public boolean isSourceSupported(Object source) {
		Collection<?> sources = convertSource(source);

		for (Transformer service: transformers) {
			if (service.isSourceSupported(sources))
				return true;
		}
		return false;
	}

	private Collection<?> convertSource(Object source) {
		Collection<Object> sources = new ArrayList<>();
		for (Converter converter: converters) {
			if (converter.isConvertible(source)) {
				sources.addAll(converter.convert(source));
			}
		}

		// add if empty
		if (sources.isEmpty())
			sources.add(source);

		return sources;

	}
	@Override
	public boolean isTargetSupported(Object target) {
		for (Transformer service: transformers) {
			if (service.isTargetSupported(target))
				return true;
		}
		return false;
	}
}
