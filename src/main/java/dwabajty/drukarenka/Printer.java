package dwabajty.drukarenka;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;

public class Printer {

    SerialPort serialPort;

    private Printer(String portName) throws SerialPortException {
        serialPort = new SerialPort(portName);
        serialPort.openPort();//Open serial port
        serialPort.setParams(19200, 8, 1, 0);
        serialPort.addEventListener(new SerialPortEventListener() {
            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                System.out.println("EVENT: " + serialPortEvent.toString());
            }
        });
    }

    public static Printer openPort(String portName) throws SerialPortException {
        return new Printer(portName);
    }

    public void print(byte[] data) {
        try {
            serialPort.writeBytes(data);

        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void print(int... data) {
        byte[] bytes = new byte[data.length];
        for(int i = 0; i < data.length; i++) {
            if(data[i] > 255) {
                throw new RuntimeException("Invalid data");
            }

            bytes[i] = (byte)data[i];
        }
        print(bytes);
    }

    public void print(String text) {
        print(text.getBytes());
    }

    public void reversePrintingMode(boolean enable) {
        print(29, 66, 1);
    }

    public void setParams(int printingDots, int heatingTime, int heatingInterval) {
        print(27, 55, printingDots, heatingTime, heatingInterval);
    }

    public void printBitmap(BufferedImage image) {

        int[] array2D;
        array2D = new int[image.getWidth() * image.getHeight()];

        for (int xPixel = 0; xPixel < image.getWidth(); xPixel++)
        {
            for (int yPixel = 0; yPixel < image.getHeight(); yPixel++)
            {
                int color = image.getRGB(xPixel, yPixel);
                Color c = new Color(color);

                double maxValue = 255 * 0.2989 + 255 * 0.5870 + 255 * 0.1140;
                double value = c.getRed() * 0.2989 + c.getGreen() * 0.5870 + c.getBlue() * 0.1140;

                if (value > maxValue * 0.5) {
                    array2D[xPixel + yPixel * image.getWidth()] = 0;
                } else {
                    array2D[xPixel + yPixel * image.getWidth()] = 1;
                }
            }
        }

        int nL = image.getHeight() % 255;
        int nH = image.getHeight() / 255;

        if(nH > 3)
            nH = 3;
        int k = nL + nH * 256;

        byte[] data = new byte[4 + k * 48];
        data[0] = 18;
        data[1] = 86;
        data[2] = (byte)nL;
        data[3] = (byte)nH;

        int cnt = 4;
        int b = 0;

        for(int i = 0; i < array2D.length; i++) {
            b += ((int)Math.pow(2, 7-(i % 8))) * array2D[i];
            if(i % 8 == 7 && i > 0) {
                data[cnt] = (byte)b;
                b = 0;
                cnt++;
            }

            if(cnt >= data.length)
                break;
        }

        System.out.println("START");
        print(data);
        System.out.println("PRINTED");
    }

    public void close() {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }
}
