package dwabajty.drukarenka;


import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.io.*;
import org.fit.cssbox.layout.BrowserCanvas;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;


public class Browser {

    int cnt = 0;

    public void loadStyle(String urlString) throws IOException {
        if (!urlString.startsWith("http:") &&
                !urlString.startsWith("ftp:") &&
                !urlString.startsWith("file:"))
            urlString = "http://" + urlString;

        DocumentSource docSource = new DefaultDocumentSource(urlString);
    }

    public BufferedImage makeImage(InputStream is, String urlstring , boolean loadImages, boolean loadBackgroundImages) throws IOException, SAXException {


        if (!urlstring.startsWith("http:") &&
                !urlstring.startsWith("ftp:") &&
                !urlstring.startsWith("file:"))
            urlstring = "http://" + urlstring;

        //Open the network connection
//; charset=utf-8
        DocumentSource docSource = new StreamDocumentSource(is, new URL(urlstring), "text/html; charset=utf-8");
//        DocumentSource docSource = new DefaultDocumentSource(urlstring);

        //Parse the input document
        DOMSource parser = new DefaultDOMSource(docSource);
        Document doc = parser.parse();

        //Create the CSS analyzer
        DOMAnalyzer da = new DOMAnalyzer(doc, docSource.getURL());
        da.attributesToStyles(); //convert the HTML presentation attributes to inline styles
        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the standard style sheet
        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the additional style sheet
        da.addStyleSheet(null, CSSNorm.formsStyleSheet(), DOMAnalyzer.Origin.AGENT); //render form fields using css
        da.getStyleSheets(); //load the author style sheets



//        if (type == TYPE_PNG)
//        {

            BrowserCanvas contentCanvas = new BrowserCanvas(da.getRoot(), da, docSource.getURL());
            contentCanvas.getConfig().setLoadImages(loadImages);
            contentCanvas.getConfig().setLoadBackgroundImages(loadBackgroundImages);
            contentCanvas.createLayout(new java.awt.Dimension(384, 400));
            contentCanvas.setSize(384, 400);
            contentCanvas.setAutoSizeUpdate(false);

            BufferedImage image = contentCanvas.getImage();

//            ImageIO.write(contentCanvas.getImage(), "png", out);

        docSource.close();

//        BufferedImage image = ImageIO.read(new File("stronka.png"));

        int lastYNotWhite = 0;

        for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) {
            for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) {
                int color = image.getRGB(xPixel, yPixel);
                if(color != -328966 && color != Color.WHITE.getRGB()) {  // white?
                    lastYNotWhite = yPixel;
                }
            }
        }

        BufferedImage dest = image.getSubimage(0, 0, 384, lastYNotWhite + 1);
        ImageIO.write(dest, "png", new File("test-result-" + cnt + ".png"));
        cnt++;

        return dest;

//        }
//        else if (type == TYPE_SVG)
//        {
//            BrowserCanvas contentCanvas = new BrowserCanvas(da.getRoot(), da, docSource.getURL());
//            contentCanvas.getConfig().setLoadImages(loadImages);
//            contentCanvas.getConfig().setLoadBackgroundImages(loadBackgroundImages);
//            setDefaultFonts(contentCanvas.getConfig());
//            contentCanvas.createLayout(new java.awt.Dimension(1200, 600));
//            Writer w = new OutputStreamWriter(out, "utf-8");
//            writeSVG(contentCanvas.getViewport(), w);
//            w.close();
//        }



//        return true;
    }

    public BufferedImage makeImage(String html, String url, boolean loadImages, boolean loadBackgrounds) throws IOException, SAXException {
        InputStream is = new ByteArrayInputStream(html.getBytes("UTF-8"));
        return makeImage(is, url, loadImages, loadBackgrounds);
    }
}
