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

            String fileName = handler.getFileName();

            double timePivot = 0;
            int leaderLap = 0;

            FileWriter writer;
            
            try{
                writer = new FileWriter(fileName,true);
                writer.append("Pos.,");
                writer.append("No.,");
                writer.append("Name,");
                writer.append("Team,");
                writer.append("Laps,");
                writer.append("Time/Gap");
                writer.append("\r\n");

                int i = 0;
                int j = 1;
                
                while( j < driverList.size()+1){
                    while(j != driverList.get(i).getPos()){
                        i++;
                    }

                    Driver tmpDriver = driverList.get(i);
                    writer.append(String.valueOf(j));
                    writer.append(',');

                    writer.append(String.valueOf(tmpDriver.getNumber()));
                    writer.append(',');

                    writer.append(tmpDriver.getName());
                    writer.append(',');

                    writer.append(tmpDriver.getTeam().getName());
                    writer.append(',');

                    writer.append(String.valueOf(tmpDriver.getLap()));
                    writer.append(',');
                    
                    if(j == 1){
                        timePivot = tmpDriver.getTime();
                        leaderLap = tmpDriver.getLap();
                        writer.append(timeFormat(timePivot));
                        writer.append("\r\n");
                    }
                    else if(leaderLap == tmpDriver.getLap()){
                        writer.append("+" + String.format("%.3f",tmpDriver.getTime() - timePivot));
                        writer.append("\r\n");
                    }
                    else if(tmpDriver.getTime() != 0){
                        writer.append("+" + String.valueOf(leaderLap - tmpDriver.getLap()) + " Lap(s)");
                        writer.append("\r\n");
                    }
                    else{
                        writer.append("DNF");
                        writer.append("\r\n");
                    }

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

    public static String timeFormat(double rawTime){
        String formattedTime;
        int minute = (int) Math.floor(rawTime / 60);
        double second = rawTime - minute*60;

        formattedTime = String.valueOf(minute) + ":" + String.format("%.3f",second);
        return formattedTime;
    }
}
