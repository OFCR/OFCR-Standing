package xiatstudio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {
	List<Driver> driverList = null;
	Driver currentDriver = null;
	StringBuilder data = null;
	
	public List<Driver> getDriverList(){
		return driverList;
	}

	boolean dName = false;
	boolean dNumber = false;
	boolean dTeam = false;
	boolean dSession = false;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		if(qName.equalsIgnoreCase("ServerName")){
			dSession = true;
		}
		else if(qName.equalsIgnoreCase("Driver")){
			currentDriver = new Driver(" ");
			
			if(driverList == null) {
				driverList = new ArrayList<>();
			}
		} 
		else if(qName.equalsIgnoreCase("Name"))
			dName = true;
		else if(qName.equalsIgnoreCase("CarNumber"))
			dNumber = true;
		else if (qName.equalsIgnoreCase("TeamName"))
			dTeam = true;

		data = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if(dSession){
			createCSVFile(data.toString());
			dSession = false;
		}
		else if(dName){
			currentDriver.setName(data.toString());
			dName = false;
		}
		else if(dNumber){
			currentDriver.setNumber(Integer.parseInt(data.toString()));
			dNumber = false;
		}
		else if(dTeam){
			Team tmpTeam = new Team(data.toString());
			currentDriver.setTeam(tmpTeam);
			dTeam = false;
		}
		
		if(qName.equalsIgnoreCase("Driver")){
			driverList.add(currentDriver);
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException{
		data.append(new String(ch,start,length));
	}

	public void createCSVFile(String fileName){
		File f = new File(fileName + ".csv");
		try{
			f.createNewFile();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}