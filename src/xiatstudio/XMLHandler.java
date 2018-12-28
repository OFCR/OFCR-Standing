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
	String fileName = null;
	
	public List<Driver> getDriverList(){
		return driverList;
	}

	public String getFileName(){
		return fileName + ".csv";
	}

	public String getMDFile(){
		return fileName+".MD";
	}

	boolean dName = false;
	boolean dNumber = false;
	boolean dTeam = false;
	boolean dSession = false;
	boolean dLaps = false;
	boolean dPos = false;
	boolean dTime = false;
	boolean dPB = false;
	boolean dPosdiff = false;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		if(qName.equalsIgnoreCase("ServerName")){
			dSession = true;
			driverList = null;
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
		else if(qName.equalsIgnoreCase("Position"))
			dPos = true;
		else if (qName.equalsIgnoreCase("TeamName"))
			dTeam = true;
		else if (qName.equalsIgnoreCase("GridPos"))
			dPosdiff = true;
		else if (qName.equalsIgnoreCase("BestLapTime"))
			dPB = true;
		else if (qName.equalsIgnoreCase("FinishTime"))
			dTime = true;
		else if (qName.equalsIgnoreCase("Laps")){
			if(currentDriver != null)
				dLaps = true;
		}
				
			
		data = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if(dSession){
			createCSVFile(".\\2018_Result_CSV\\"+data.toString(),true);
			createMDFile(".\\2018_Result_MD\\"+data.toString(),true);
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
			currentDriver.setPos(Integer.parseInt(data.toString()));
			dPos = false;
		}
		else if(dTeam){
			Team tmpTeam = new Team(data.toString());
			currentDriver.setTeam(tmpTeam);
			dTeam = false;
		}
		else if(dPosdiff){
			currentDriver.setPosGain(Integer.parseInt(data.toString()));
			dPosdiff = false;
		}
		else if(dPB){
			currentDriver.setPB(Double.parseDouble(data.toString()));
			dPB = false;
		}
		else if(dTime){
			currentDriver.setTime(Double.parseDouble(data.toString()));
			dTime = false;
		}
		else if(dLaps){
			currentDriver.setLap(Integer.parseInt(data.toString()));
			dLaps = false;
		}
		
		if(qName.equalsIgnoreCase("Driver")){
			currentDriver.setPosGain(currentDriver.getPosGain()-currentDriver.getPos());
			driverList.add(currentDriver);
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException{
		data.append(new String(ch,start,length));
	}

	public void createCSVFile(String fileName, boolean overwrite){
		File f = new File(fileName + ".csv");

		if(overwrite == true)
			f.delete();

		try{
			f.createNewFile();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void createMDFile(String fileName, boolean overwrite){
		File f = new File(fileName + ".MD");

		if(overwrite == true)
			f.delete();

		try{
			f.createNewFile();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}