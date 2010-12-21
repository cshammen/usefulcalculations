package org.usefulcalculations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * 
 * @author joe
 * 
 *         Simple Anakia clone that enables setting an entity resolver
 * 
 */
public class SiteMaker {

	public static void main(String[] argv) throws JDOMException, IOException {

		// start up velocity
		VelocityEngine ve = new VelocityEngine();
		Template template = ve.getTemplate("site.vsl");

		// set up jdom parser
		SAXBuilder saxBuilder = new SAXBuilder();
		saxBuilder.setEntityResolver(new SaxEntityResolver());

		File[] sourceFiles = new File("./src/exported").listFiles();
		for (File sourceFile : sourceFiles) {
			if (sourceFile.getName().endsWith(".html")) {
				System.out.println("processing file: " + sourceFile.getName());
				Document doc = saxBuilder.build(sourceFile);
				VelocityContext context = new VelocityContext();
				context.put("doc", doc.getRootElement());
				Writer writer = new FileWriter("./www/" + sourceFile.getName());
				template.merge(context, writer);
				writer.close();
			} else {
				System.out.println("ignoring file: " + sourceFile.getName());
			}
		}
	}
}
