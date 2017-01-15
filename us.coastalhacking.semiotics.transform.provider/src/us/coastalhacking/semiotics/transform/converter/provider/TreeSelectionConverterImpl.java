package us.coastalhacking.semiotics.transform.converter.provider;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeSelection;
import org.osgi.service.component.annotations.Component;

import us.coastalhacking.semiotics.xcore.model.service.Converter;

@Component
public class TreeSelectionConverterImpl implements Converter {

	@Override
	public Collection<?> convert(Object source) {
		TreeSelection treeSelection = (TreeSelection)source;
		return treeSelection.toList();
	}

	@Override
	public boolean isConvertible(Object source) {
		if (source instanceof TreeSelection) return true;
		return false;
	}


}
