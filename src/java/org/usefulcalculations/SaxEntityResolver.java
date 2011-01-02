package org.usefulcalculations;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SaxEntityResolver implements EntityResolver {
	public InputSource resolveEntity(String publicID, String sysId)
			throws SAXException {
		if (sysId.equals("http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd")) {
			return new InputSource("xhtml-math11-f.dtd");
		} else {
			System.out.println("got entity: " + publicID + ":" + sysId);
		}
		//  null signals process to continue normally
		return null;
	}
}