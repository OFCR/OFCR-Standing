package xiatstudio;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import xiatstudio.Driver;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class OFCRResult {
	static Font xtDefault = new Font("Microsoft Yahei", Font.PLAIN, 14);
    public static void main(String args[]) {
        GUISetup();
    }

    public static void GUISetup() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle("OFCR Result Exporter");
        mainFrame.setSize(600, 250);
        mainFrame.setFont(xtDefault);
        mainFrame.setLayout(new GridBagLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        JButton updateSeason = new JButton("Choose season folder");

        mainFrame.add(updateSeason, c);
        updateSeason.setFont(xtDefault);
        mainFrame.setVisible(true);

        updateSeason.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String xmlPath;
                String seasonIndex;
                JFileChooser fc = new JFileChooser(".\\");
                setFileChooserFont(fc.getComponents());
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                switch (fc.showOpenDialog(mainFrame)) {
                case JFileChooser.APPROVE_OPTION:
                    xmlPath = fc.getSelectedFile().getAbsolutePath();
                    xmlPath = xmlPath.replace("\\", "/");

                    String tmpStrArr[] = xmlPath.split("/");
                    seasonIndex = tmpStrArr[tmpStrArr.length - 2];

                    seasonParser(Integer.parseInt(seasonIndex), xmlPath + "/");
                    break;
                }
            }
        });
    }

    public static void setFileChooserFont(Component[] comp) {
        for (int i = 0; i < comp.length; i++) {
            if (comp[i] instanceof Container)
                setFileChooserFont(((Container) comp[i]).getComponents());
            try {
                comp[i].setFont(xtDefault);
            } catch (Exception e) {
            }
        }
    }

    public static void seasonParser(int season, String sourcePath) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLHandler handler = new XMLHandler();

            File folder = new File(sourcePath);
            File[] listOfFiles = folder.listFiles();
            String[] fileList = new String[listOfFiles.length];
            List<Driver> seasonList = new ArrayList<>();

            for (int i = 0; i < listOfFiles.length; i++) {
                fileList[i] = (sourcePath + listOfFiles[i].getName());
                saxParser.parse(new File(fileList[i]), handler);
                List<Driver> driverList = handler.getDriverList();
                addDriver(seasonList, driverList);
                updatePoints(seasonList, driverList);
                exportCSV(season, handler.getFileName(), driverList);
                exportMD(season, handler.getMDFile(), driverList);
            }

            createFinalStanding(season, sourcePath, seasonList);

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
            if (roundList.get(i).getDNF() == 1)
                seasonList.get(counter).regDNF();
            if (roundList.get(i).getWin() == 1)
                seasonList.get(counter).regWin();
            if (roundList.get(i).getStart() == 1)
                seasonList.get(counter).regStart();
            if (roundList.get(i).getPointStart() == 1)
                seasonList.get(counter).regPoint();
            if (roundList.get(i).getPodium() == 1)
                seasonList.get(counter).regPodium();
        }

    }

    public static void createFinalStanding(int season, String sourcePath, List<Driver> seasonList) {
        String seasonFolder = ".\\" + season + "\\";
        File f = new File(seasonFolder + season + " Driver Championship Standing.MD");
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
            writer.append("## "+season+" OFCR Season Driver Championship Standing  \r\n");
            writer.append("<table style=\"width:100%\">");
            writer.append("\r\n");
            String title[] = { "Pos.", "No.", "Driver", "Team", "Points", "Participation Rate", "Win Rate",
                    "Podium Rate", "Points Rate", "DNF Rate" };
            File folder = new File(sourcePath);
            File[] listOfFiles = folder.listFiles();
            double totalRace = listOfFiles.length;
            createHTMLRow(writer, title);
            for (int i = 0; i < seasonList.size(); i++) {
                double driverStarts = seasonList.get(i).getStart();
                String startRate = String.format("%.2f", seasonList.get(i).getStart() * 100 / totalRace) + " %";
                String winRate = String.format("%.2f", seasonList.get(i).getWin() * 100 / driverStarts) + " %";
                String podiumRate = String.format("%.2f", seasonList.get(i).getPodium() * 100 / driverStarts) + " %";
                String pointRate = String.format("%.2f", seasonList.get(i).getPointStart() * 100 / driverStarts) + " %";
                String dnfRate = String.format("%.2f", seasonList.get(i).getDNF() * 100 / driverStarts) + " %";
                String row[] = { String.valueOf(i + 1), String.valueOf(seasonList.get(i).getNumber()),
                        seasonList.get(i).getName(), seasonList.get(i).getTeam().getName(),
                        String.valueOf(seasonList.get(i).getPoints()), startRate, winRate, podiumRate, pointRate,
                        dnfRate };
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

    public static void exportMD(int season, String fileName, List<Driver> driverList) {
        String seasonFolder = ".\\" + String.valueOf(season) + "\\";
        new File(seasonFolder + season + "_Result_MD\\").mkdir();
        File f = new File(seasonFolder + season + "_Result_MD\\" + season + " " + fileName);
        FileWriter mdWriter;
        double timePivot = 0;
        int leaderLap = 0;
        try {
            mdWriter = new FileWriter(f.getPath(), true);
            mdWriter.append("---");
            mdWriter.append("\r\n");
            
            mdWriter.append("layout: page");
            mdWriter.append("\r\n");
            
            mdWriter.append("title: "+f.getName());
            mdWriter.append("\r\n");
            
            mdWriter.append("published: true");
            mdWriter.append("\r\n");
            
            mdWriter.append("---");
            mdWriter.append("\r\n");
            mdWriter.append("\r\n");
            
            mdWriter.append("<font size=\"2\">\r\n");
            mdWriter.append("<table style=\"width:120%\">");
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
            mdWriter.append("</table>\r\n</font>");
            mdWriter.flush();
            mdWriter.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void exportCSV(int season, String fileName, List<Driver> driverList) {
        String seasonFolder = ".\\" + season + "\\";
        new File(seasonFolder + season + "_Result_CSV\\").mkdir();
        File f = new File(seasonFolder + season + "_Result_CSV\\" + season + " " + fileName);
        FileWriter writer;
        double timePivot = 0;
        int leaderLap = 0;
        try {
            writer = new FileWriter(f.getPath(), true);
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
