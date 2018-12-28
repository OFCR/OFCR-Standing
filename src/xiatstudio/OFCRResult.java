package xiatstudio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class OFCRResult {
    public static void main(String args[]) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLHandler handler = new XMLHandler();
            saxParser.parse(new File("08AUTR.xml"), handler);

            List<Driver> driverList = handler.getDriverList();

            String fileName = handler.getFileName();

            double timePivot = 0;
            int leaderLap = 0;

            FileWriter writer;
            FileWriter mdWriter;

            try {
                mdWriter = new FileWriter(handler.getMDFile(), true);
                mdWriter.append("<table style=\"width:100%\">");
                mdWriter.append("\r\n");
                String title[] = { "Pos.", "No.", "Name", "Team", "Laps", "Time/Gap", "Personal Best",
                        "Position Diff" };
                createHTMLRow(mdWriter, title);

                int i = 0;
                int j = 1;
                while (j < driverList.size()+1){
                    while(j != driverList.get(i).getPos()){
                        i++;
                    }

                    Driver tmpDriver = driverList.get(i);
                    String time_gap;
                    String pb;
                    String posDiff;

                    if (j == 1) {
                        timePivot = tmpDriver.getTime();
                        leaderLap = tmpDriver.getLap();
                        time_gap = timeFormat(tmpDriver.getTime());
                    } else if (leaderLap == tmpDriver.getLap()) {
                        time_gap = "+" + String.format("%.3f",(tmpDriver.getTime() - timePivot));
                    } else if (tmpDriver.getTime() != 0) {
                        int lapGap = leaderLap - tmpDriver.getLap();
                        if (lapGap == 1)
                            time_gap = ("+1 Lap");
                        else
                            time_gap = ("+" + lapGap + " Laps");
                    } else {
                        time_gap = "DNF";
                    }
                  
                    if (tmpDriver.getLap() == 0) {
                        pb = "N/A";
                    } else {
                       pb = timeFormat(tmpDriver.getPB());
                    }
                  

                    if (tmpDriver.getPosGain() > 0)
                       posDiff = "+" + tmpDriver.getPosGain();
                    else
                       posDiff = String.valueOf(tmpDriver.getPosGain());

                    String singleRow[] = {String.valueOf(tmpDriver.getPos()),String.valueOf(tmpDriver.getNumber()),tmpDriver.getName(),tmpDriver.getTeam().getName(),String.valueOf(tmpDriver.getNumber()),time_gap,pb,posDiff};
                    createHTMLRow(mdWriter, singleRow);

                    i = 0;
                    j++;
                }
                mdWriter.append("</table>");
                mdWriter.flush();
                mdWriter.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            try {
                writer = new FileWriter(fileName, true);
                writer.append("Pos.,");
                writer.append("No.,");
                writer.append("Name,");
                writer.append("Team,");
                writer.append("Laps,");
                writer.append("Time/Gap,");
                writer.append("Personal Best,");
                writer.append("Position Diff");
                writer.append("\r\n");

                int i = 0;
                int j = 1;

                while (j < driverList.size() + 1) {
                    while (j != driverList.get(i).getPos()) {
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

                    if (j == 1) {
                        timePivot = tmpDriver.getTime();
                        leaderLap = tmpDriver.getLap();
                        writer.append(timeFormat(timePivot));
                    } else if (leaderLap == tmpDriver.getLap()) {
                        writer.append("+" + String.format("%.3f", tmpDriver.getTime() - timePivot));
                    } else if (tmpDriver.getTime() != 0) {
                        int lapGap = leaderLap - tmpDriver.getLap();
                        writer.append("+" + String.valueOf(lapGap));
                        if (lapGap == 1)
                            writer.append(" Lap");
                        else
                            writer.append(" Laps");
                    } else {
                        writer.append("DNF");
                    }
                    writer.append(',');

                    if (tmpDriver.getLap() == 0) {
                        writer.append("N/A");
                    } else {
                        writer.append(timeFormat(tmpDriver.getPB()));
                    }
                    writer.append(',');

                    if (tmpDriver.getPosGain() > 0)
                        writer.append("+" + String.valueOf(tmpDriver.getPosGain()));
                    else
                        writer.append(String.valueOf(tmpDriver.getPosGain()));

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

    public static String timeFormat(double rawTime) {
        String formattedTime;

        int minute = (int) Math.floor(rawTime / 60);
        double second = rawTime - minute * 60;

        DecimalFormat df = new DecimalFormat("00.000");
        String formattedSecond = df.format(second);

        formattedTime = String.valueOf(minute) + ":" + formattedSecond;
        return formattedTime;
    }

    public static void createHTMLRow(FileWriter fw, String[] content) {
        try {
            fw.append("<tr>");
            fw.append("\r\n");

            for (int i = 0; i < content.length; i++) {
                HTMLTableCell(fw, content[i]);
            }
            fw.append("</tr>");
            fw.append("\r\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public static void HTMLTableCell(FileWriter fw, String content) {
        try {
            fw.append("<th>");
            fw.append(content);
            fw.append("</th>");
            fw.append("\r\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
