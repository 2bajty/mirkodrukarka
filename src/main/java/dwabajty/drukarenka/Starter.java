package dwabajty.drukarenka;

import dwabajty.drukarenka.atk.tools.handler.Looper;
import dwabajty.drukarenka.wykop.Entry;
import dwabajty.drukarenka.wykop.NewEntryListener;
import dwabajty.drukarenka.wykop.WykopMonitor;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.commons.cli.*;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Starter {
    public static void main(String[] args) throws InterruptedException, IOException, SerialPortException {

        Options options = new Options();
        options.addOption("p", "port", true, "Com port to be used");
        HelpFormatter formatter = new HelpFormatter();

        CommandLineParser parser = new GnuParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp("eprint", options );
            return;
        }

        if(!cmd.hasOption("p")) {
            String[] portNames = SerialPortList.getPortNames();
            System.out.println("Serial ports: ");
            for(int i = 0; i < portNames.length; i++){
                System.out.println(portNames[i]);
            }
            System.out.println("");
            formatter.printHelp("eprint", options );
        }

        String portName = cmd.getOptionValue("p");
        System.out.println("Opening port: " + portName);

        Looper.prepare();

        final Printer printer = Printer.openPort(portName);
        final WykopMonitor wykopMonitor = new WykopMonitor();
        final LayoutBuilder layoutBuilder = new LayoutBuilder();
        final Browser browser = new Browser();

        printer.setParams(12, 255, 10);
        layoutBuilder.loadLayout("entry.html");

        printer.print("START\n");
        printer.printBitmap(ImageIO.read(new File("test.bmp")));
        System.out.println("sdf\n");

        wykopMonitor.start(true, new NewEntryListener() {
            @Override
            public void onNewEntry(List<Entry> entryList) {
                for (Entry e : entryList) {
                    System.out.println(e.author);
                    System.out.println(e.body);

                    String embedUrl = null;
                    if (e.embed != null) {
                        embedUrl = e.embed.preview;
                        System.out.println(e.embed.preview);
                    }
                    System.out.println(" ");

                    String html = layoutBuilder.buildHtml(e.author, e.body, embedUrl, e.author_avatar_med);
                    try {
                        BufferedImage image = browser.makeImage(html, "http://www.wykop.pl/mikroblog/", true, true);
                        printer.printBitmap(image);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (SAXException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        Looper.loop();
        printer.close();

    }
}
