package xiatstudio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import xiatstudio.Driver;

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
            List<Driver> seasonList = new ArrayList<>();

            for (int i = 0; i < listOfFiles.length; i++) {
                fileList[i] = (".\\2018_Result_RAW\\" + listOfFiles[i].getName());
                saxParser.parse(new File(fileList[i]), handler);
                List<Driver> driverList = handler.getDriverList();
                addDriver(seasonList, driverList);
                updatePoints(seasonList, driverList);
                exportCSV(handler.getFileName(), driverList);
                exportMD(handler.getMDFile(), driverList);
            }

            createFinalStanding(seasonList);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void addDriver(List<Driver> seasonList, List<Driver> roundList) {
        int counter = 0;
        for (int i = 0; i < roundList.size(); i++) {
            if (seasonList.size() == 0) {
            	Driver tmpDriver = new Driver(" ");
            	tmpDriver.setNumber(roundList.get(0).getNumber());
            	tmpDriver.setName(roundList.get(0).getName());
            	tmpDriver.setTeam(roundList.get(0).getTeam());
                seasonList.add(tmpDriver);
            } else {
                counter = 0;
                while (counter < seasonList.size()) {
                    if (seasonList.get(counter).getName().equals(roundList.get(i).getName())) {
                        break;
                    } else {
                        counter++;
                    }
                }
                if (counter == seasonList.size()) {
                	Driver tmpDriver = new Driver(" ");
                	tmpDriver.setNumber(roundList.get(i).getNumber());
                	tmpDriver.setName(roundList.get(i).getName());
                	tmpDriver.setTeam(roundList.get(i).getTeam());
                    seasonList.add(tmpDriver);
                }
            }
        }
    }

    public static void updatePoints(List<Driver> seasonList, List<Driver> roundList) {
        int counter = 0;
        for (int i = 0; i < roundList.size(); i++) {
            counter = 0;
            while (!seasonList.get(counter).getName().equals(roundList.get(i).getName())) {
                counter++;
            }
            seasonList.get(counter).addPoints(roundList.get(i).getPoints());
            if(roundList.get(i).getDNF() == 1)
                seasonList.get(counter).regDNF();
            if(roundList.get(i).getWin() == 1)
                seasonList.get(counter).regWin();
            if(roundList.get(i).getStart() == 1) 
            	seasonList.get(counter).regStart();
            if(roundList.get(i).getPointStart() == 1)
                seasonList.get(counter).regPoint();
            if(roundList.get(i).getPodium() == 1)
                seasonList.get(counter).regPodium();
        }
        
    }

    public static void createFinalStanding(List<Driver> seasonList) {
        File f = new File("Driver Championship Standing.MD");
        f.delete();
        try {
            f.createNewFile();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        seasonList.sort(Comparator.comparingInt(Driver::getPoints).reversed());
        FileWriter writer;
        try {
            writer = new FileWriter(f, true);

            writer.append("<table style=\"width:100%\">");
            writer.append("\r\n");
            String title[] = { "Pos.", "No.", "Driver", "Team", "Points","Participation Rate","Win Rate","Podium Rate","Points Rate","DNF Rate" };
            File folder = new File(".\\2018_Result_RAW\\");
            File[] listOfFiles = folder.listFiles();
            double totalRace = listOfFiles.length;
            createHTMLRow(writer, title);
            for (int i = 0; i < seasonList.size(); i++) {
                String startRate = String.format("%.2f",seasonList.get(i).getStart()*100/totalRace) + " %";
                String winRate = String.format("%.2f",seasonList.get(i).getWin()*100/totalRace) + " %";
                String podiumRate = String.format("%.2f",seasonList.get(i).getPodium()*100/totalRace) + " %";
                String pointRate = String.format("%.2f",seasonList.get(i).getPointStart()*100/totalRace) + " %";
                String dnfRate = String.format("%.2f",seasonList.get(i).getDNF()*100/totalRace) + " %";
                String row[] = { String.valueOf(i + 1), String.valueOf(seasonList.get(i).getNumber()),
                        seasonList.get(i).getName(), seasonList.get(i).getTeam().getName(),
                        String.valueOf(seasonList.get(i).getPoints()),startRate,winRate,podiumRate,pointRate,dnfRate };
                createHTMLRow(writer, row);
            }
            writer.append("</table>");
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
            mdWriter = new FileWriter(".\\2018_Result_MD\\" + fileName, true);
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
            writer = new FileWriter(".\\2018_Result_CSV\\" + fileName, true);
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
