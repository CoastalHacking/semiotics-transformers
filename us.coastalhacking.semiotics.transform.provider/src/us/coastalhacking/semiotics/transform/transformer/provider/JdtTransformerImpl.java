package us.coastalhacking.semiotics.transform.transformer.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IMethod;
import org.osgi.service.component.annotations.Component;

import us.coastalhacking.semiotics.xcore.model.SemioticsFactory;
import us.coastalhacking.semiotics.xcore.model.Sink;
import us.coastalhacking.semiotics.xcore.model.SinkCatalog;
import us.coastalhacking.semiotics.xcore.model.SinkCategory;
import us.coastalhacking.semiotics.xcore.model.service.Transformer;

@Component
public class JdtTransformerImpl implements Transformer {

	@Override
	public Collection<?> transform(Collection<?> sources, Object target) {
		List<EObject> results = new ArrayList<>();
		if (target instanceof SinkCatalog)
			results.addAll(atSinkCatalog(sources, target));
		else if (target instanceof SinkCategory)
			results.addAll(atSinkCategory(sources, target));
		return results;
	}

	@Override
	public boolean isTransformable(Collection<?> sources, Object target) {
		if (!isSourceSupported(sources)) return false;
		if (!isTargetSupported(target)) return false;
		return true;
	}

	@Override
	public boolean isSourceSupported(Collection<?> sources) {
		for (Object source: sources) {
			if (source instanceof IMethod)
				return true;
		}
		return false;
	}

	
	private List<EObject> atSinkCategory(Collection<?> sources, Object target) {
		final List<EObject> results = new ArrayList<>();
		for (Object source: sources) {
			if (source instanceof IMethod) {
				final Sink sink = createSink((IMethod)source);
				results.add(sink);
			}
		}
		return results;
	}

	private List<EObject> atSinkCatalog(Collection<?> sources, Object target) {
		final List<EObject> results = new ArrayList<>();
		SinkCategory sinkCategory = SemioticsFactory.eINSTANCE.createSinkCategory();
		sinkCategory.setLabel("Generic Category");
		results.add(sinkCategory);
		for (Object source: sources) {
			if (source instanceof IMethod) {
				final Sink sink = createSink((IMethod)source);
				sinkCategory.getSinks().add(sink);
			}
		}
		return results;
	}

	private Sink createSink(IMethod iMethod) {
		Sink sink = SemioticsFactory.eINSTANCE.createSink();
		sink.setLabel(iMethod.getElementName());
		return sink;
	}

	@Override
	public boolean isTargetSupported(Object target) {
		if (target instanceof SinkCatalog) return true;
		else if (target instanceof SinkCategory) return true;
		return false;
	}

}
