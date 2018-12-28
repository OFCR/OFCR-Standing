package xiatstudio;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class OFCRResult {
    public static void main(String args[]){
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try{
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLHandler handler = new XMLHandler();
            saxParser.parse(new File("sample.xml"),handler);

            List<Driver> driverList = handler.getDriverList();
            List<Integer> lapList = handler.getLapList();

            for(int i = 0; i < driverList.size(); i++){
                System.out.print(driverList.get(i) + " ");
                System.out.println(lapList.get(i));
            }

            String fileName = handler.getFileName();

            FileWriter writer;
            
            try{
                writer = new FileWriter(fileName,true);
                writer.append("No.,");
                writer.append("Name,");
                writer.append("Team,");
                writer.append("Laps");
                writer.append("\r\n");

                for(int i = 0; i < driverList.size();i++){
                    writer.append(String.valueOf(driverList.get(i).getNumber()));
                    writer.append(',');

                    writer.append(driverList.get(i).getName());
                    writer.append(',');

                    writer.append(driverList.get(i).getTeam().getName());
                    writer.append(',');

                    writer.append(String.valueOf(lapList.get(i)));
                    writer.append("\r\n");
                }
                
                writer.flush();
                writer.close();
            } catch (IOException ioe) {
            	ioe.printStackTrace();
            }
                
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
