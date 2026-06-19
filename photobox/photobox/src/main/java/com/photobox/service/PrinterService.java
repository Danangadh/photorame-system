// service/PrinterService.java
package com.photobox.service;

import org.springframework.stereotype.Service;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.io.FileInputStream;

@Service
public class PrinterService {
    
    public boolean printPhoto(String imagePath) {
        try {
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new Copies(1));
            
            DocFlavor flavor = DocFlavor.INPUT_STREAM.JPEG;
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, attributes);
            
            if (printServices.length > 0) {
                // Pilih printer default atau printer pertama yang tersedia
                PrintService printService = printServices[0];
                DocPrintJob printJob = printService.createPrintJob();
                
                try (FileInputStream fis = new FileInputStream(imagePath)) {
                    Doc doc = new SimpleDoc(fis, flavor, null);
                    printJob.print(doc, attributes);
                    return true;
                }
            } else {
                System.out.println("No printer found!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String[] getAvailablePrinters() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        String[] printerNames = new String[printServices.length];
        for (int i = 0; i < printServices.length; i++) {
            printerNames[i] = printServices[i].getName();
        }
        return printerNames;
    }
}