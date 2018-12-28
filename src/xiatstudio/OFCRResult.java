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

            File folder = new File(".\\2018_Result_RAW\\");
            File[] listOfFiles = folder.listFiles();
            String[] fileList = new String[listOfFiles.length];

            for(int i = 0; i < listOfFiles.length; i++){
                fileList[i] = (".\\2018_Result_RAW\\"+listOfFiles[i].getName());
                saxParser.parse(new File(fileList[i]),handler);
                List<Driver> driverList = handler.getDriverList();
                exportCSV(handler.getFileName(), driverList);
                exportMD(handler.getMDFile(), driverList);
            }
            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String PBParser(Driver d) {
        if (d.getLap() == 0)
            return "N/A";
        else
            return timeFormat(d.getPB());
    }

    public static String posDiffParser(Driver d) {
        if (d.getPosGain() > 0)
            return ("+" + d.getPosGain());
        else
            return String.valueOf(d.getPosGain());
    }

    public static void exportMD(String fileName, List<Driver> driverList) {
        FileWriter mdWriter;
        double timePivot = 0;
        int leaderLap = 0;
        try {
            mdWriter = new FileWriter(".\\2018_Result_MD\\"+fileName, true);
            mdWriter.append("<table style=\"width:100%\">");
            mdWriter.append("\r\n");
            String title[] = { "Pos.", "No.", "Name", "Team", "Laps", "Time/Gap", "Personal Best", "Position Diff" };
            createHTMLRow(mdWriter, title);

            int i = 0;
            int j = 1;
            while (j < driverList.size() + 1) {
                while (j != driverList.get(i).getPos()) {
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
                    time_gap = "+" + String.format("%.3f", (tmpDriver.getTime() - timePivot));
                } else if (tmpDriver.getTime() != 0) {
                    int lapGap = leaderLap - tmpDriver.getLap();
                    if (lapGap == 1)
                        time_gap = ("+1 Lap");
                    else
                        time_gap = ("+" + lapGap + " Laps");
                } else {
                    time_gap = "DNF";
                }

                pb = PBParser(tmpDriver);

                posDiff = posDiffParser(tmpDriver);

                String singleRow[] = { String.valueOf(tmpDriver.getPos()), String.valueOf(tmpDriver.getNumber()),
                        tmpDriver.getName(), tmpDriver.getTeam().getName(), String.valueOf(tmpDriver.getLap()),
                        time_gap, pb, posDiff };
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
    }

    public static void exportCSV(String fileName, List<Driver> driverList) {
        FileWriter writer;
        double timePivot = 0;
        int leaderLap = 0;
        try {
            writer = new FileWriter(".\\2018_Result_CSV\\"+fileName, true);
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

                writer.append(String.valueOf(j) + ',');

                writer.append(String.valueOf(tmpDriver.getNumber()) + ',');

                writer.append(tmpDriver.getName() + ',');

                writer.append(tmpDriver.getTeam().getName() + ',');

                writer.append(String.valueOf(tmpDriver.getLap()) + ',');

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

                writer.append(PBParser(tmpDriver) + ',');

                writer.append(posDiffParser(tmpDriver));

                writer.append("\r\n");

                i = 0;
                j++;
            }

            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
            fw.append("\t<tr>");
            fw.append("\r\n");

            for (int i = 0; i < content.length; i++) {
                HTMLTableCell(fw, content[i]);
            }
            fw.append("\t</tr>");
            fw.append("\r\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public static void HTMLTableCell(FileWriter fw, String content) {
        try {
            fw.append("\t\t<th>");
            fw.append(content);
            fw.append("</th>");
            fw.append("\r\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
