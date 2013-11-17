package org.usefulcalculations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.EscapeStrategy;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 * @author joe
 * 
 *         Simple Anakia knockoff that enables setting an entity resolver
 * 
 */
public class SiteMaker {

	private SAXBuilder saxBuilder;
	private Template template;
	private XMLOutputter xmlOutputter;

	public static void main(String[] argv) throws JDOMException, IOException {
		new SiteMaker().run();

	}

	private void run() throws JDOMException, IOException {
		// start up velocity
		VelocityEngine ve = new VelocityEngine();
		template = ve.getTemplate("src/style/site.vsl.html");
		// set up sax parser
		saxBuilder = new SAXBuilder();
		saxBuilder.setEntityResolver(new SaxEntityResolver());
		// set up xml output format
		Format outputFormat = Format.getRawFormat();
		outputFormat.setEscapeStrategy(new SevenBitEscapeStrategy());
		xmlOutputter = new XMLOutputter(outputFormat);

		File[] sourceFiles = new File("./src/edited").listFiles();
		for (File sourceFile : sourceFiles) {
			if (sourceFile.getName().endsWith(".html")) {
				File outputFile = new File("./web/" + sourceFile.getName());
				if (template.getLastModified() >= outputFile.lastModified() || sourceFile.lastModified() >= outputFile.lastModified()) {
					processFile(sourceFile, outputFile);
				} else {
					System.out.println("up-to-date, skipping: " + sourceFile.getName());
				}
			} else {
				System.out.println("non-html, ignoring: " + sourceFile.getName());
			}
		}
	}

	private void processFile(File sourceFile, File outputFile) throws JDOMException, IOException {
		System.out.println("processing: " + sourceFile.getName());
		// parse xml input
		Document doc = saxBuilder.build(sourceFile);
		// strip namespaces off all elements for easier templating
		stripNamespace(doc.getRootElement());
		// run thru velocity
		VelocityContext context = new VelocityContext();
		context.put("root", doc.getRootElement());
		context.put("xmlout", xmlOutputter);
		Writer writer = new FileWriter(outputFile);
		template.merge(context, writer);
		writer.close();
	}

	private static void stripNamespace(Element elem) {
		elem.setNamespace(null);
		for (Object obj : elem.getChildren()) {
			stripNamespace((Element) obj);
		}
	}

	class SevenBitEscapeStrategy implements EscapeStrategy {

		@Override
		public boolean shouldEscape(char c) {
			return ((int) c) > 127;
		}

	}
}
