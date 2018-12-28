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
	List<Integer> lapList = null;
	List<Integer> posList = null;
	List<Double> timeList = null;
	Driver currentDriver = null;
	StringBuilder data = null;
	String fileName = null;
	
	public List<Driver> getDriverList(){
		return driverList;
	}

	public List<Integer> getLapList(){
		return lapList;
	}

	public List<Integer> getPosList(){
		return posList;
	}

	public List<Double> getTimeList(){
		return timeList;
	}

	public String getFileName(){
		return fileName + ".csv";
	}

	boolean dName = false;
	boolean dNumber = false;
	boolean dTeam = false;
	boolean dSession = false;
	boolean dLaps = false;
	boolean dPos = false;
	boolean dTime = false;

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
		else if(qName.equalsIgnoreCase("Position")){
			dPos = true;
			if(posList == null){
				posList = new ArrayList<>();
			}
		}
		else if (qName.equalsIgnoreCase("TeamName"))
			dTeam = true;
		else if (qName.equalsIgnoreCase("FinishTime")){
			dTime = true;
			if(timeList == null){
				timeList = new ArrayList<>();
			}
		}
		else if (qName.equalsIgnoreCase("Laps")){
			if(lapList == null){
				lapList = new ArrayList<>();
			}
			dLaps = true;
		}
			
		data = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if(dSession){
			createCSVFile(data.toString());
			fileName = data.toString();
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
		else if(dPos){
			posList.add(Integer.parseInt(data.toString()));
			dPos = false;
		}
		else if(dTeam){
			Team tmpTeam = new Team(data.toString());
			currentDriver.setTeam(tmpTeam);
			dTeam = false;
		}
		else if(dTime){
			timeList.add(Double.parseDouble(data.toString()));
			dTime = false;
		}
		else if(dLaps){
			lapList.add(Integer.parseInt(data.toString()));
			dLaps = false;
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