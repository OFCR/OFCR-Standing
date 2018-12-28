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
            List<Integer> posList = handler.getPosList();

            String fileName = handler.getFileName();

            FileWriter writer;
            
            try{
                writer = new FileWriter(fileName,true);
                writer.append("Pos.,");
                writer.append("No.,");
                writer.append("Name,");
                writer.append("Team,");
                writer.append("Laps");
                writer.append("\r\n");

                int i = 0;
                int j = 1;
                
                while( j < driverList.size()+1){
                    while(j != posList.get(i)){
                        i++;
                    }
                    writer.append(String.valueOf(j));
                    writer.append(',');

                    writer.append(String.valueOf(driverList.get(i).getNumber()));
                    writer.append(',');

                    writer.append(driverList.get(i).getName());
                    writer.append(',');

                    writer.append(driverList.get(i).getTeam().getName());
                    writer.append(',');

                    writer.append(String.valueOf(lapList.get(i)));
                    writer.append("\r\n");

                    i = 0;
                    j++;
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
