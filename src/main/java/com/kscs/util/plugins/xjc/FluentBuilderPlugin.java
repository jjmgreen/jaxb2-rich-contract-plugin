package com.kscs.util.plugins.xjc;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @description
 */
public class FluentBuilderPlugin extends Plugin {

	@Override
	public String getOptionName() {
		return "Xfluent-builder";
	}

	@Override
	public int parseArgument(final Options opt, final String[] args, final int i) throws BadCommandLineException, IOException {
		return 0;
	}

	@Override
	public String getUsage() {
		return " -Xfluent-builder: Generates an inner \"fluent builder\" for each of the generated classes.";
	}

	@Override
	public boolean run(final Outline outline, final Options opt, final ErrorHandler errorHandler) throws SAXException {
		final ApiConstructs apiConstructs = new ApiConstructs(outline.getCodeModel());

		final List<BuilderGenerator> builderGenerators = new ArrayList<BuilderGenerator>(outline.getClasses().size());

		for(final ClassOutline classOutline : outline.getClasses()) {
			final JDefinedClass definedClass = classOutline.implClass;
			try {
				builderGenerators.add(new BuilderGenerator(apiConstructs, classOutline, opt));
			} catch(final JClassAlreadyExistsException caex) {
				errorHandler.warning(new SAXParseException("Class \"" + definedClass.name() + "\" already contains inner class \"Builder\". Skipping generation of fluent builder.", classOutline.target.getLocator(), caex));
			}
		}

		for (final BuilderGenerator builderGenerator : builderGenerators) {
			builderGenerator.buildProperties();
		}
		return true;
	}

}