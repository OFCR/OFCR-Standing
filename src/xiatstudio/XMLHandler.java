package xiatstudio;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {
	List<Driver> driverList = null;
	
	public List<Driver> getDriverList(){
		return driverList;
	}

	@Override
	public void startElement(String uri, String localElement, String qName, Attributes attributes) throws SAXException{

	}
}
