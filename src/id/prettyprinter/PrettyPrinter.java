package id.prettyprinter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import argo.format.PrettyJsonFormatter;
import argo.jdom.JdomParser;

public class PrettyPrinter {

    @SuppressWarnings("resource")
    static void usage() throws IOException {
        Scanner scanner = new Scanner(PrettyPrinter.class.getResource("README.org").openStream())
                .useDelimiter("\n");
        while (scanner.hasNext())
            System.out.println(scanner.next());
    }
    
    static enum Printer {
        XML {
            @Override
            void run(InputStream is) throws Exception {
                runSgml(is, Parser.xmlParser());
            }
        },
        HTML {
            @Override
            void run(InputStream is) throws Exception {
                runSgml(is, Parser.htmlParser());
            }
        },
        JSON {
            @Override
            void run(InputStream is) throws Exception {
                String json =  new PrettyJsonFormatter().format(new JdomParser().parse(new BufferedReader(new InputStreamReader(is))));
                Scanner scanner = new Scanner(json);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    int i = 0;
                    while (i < line.length() && line.charAt(i) == '\t') {
                        System.out.print(" ");
                        i++;
                    }
                    System.out.println(line.substring(i));
                }
                scanner.close();
            }
        };
        abstract void run(InputStream is) throws Exception;
        void runSgml(InputStream is, Parser p) throws Exception {
            Document doc = Jsoup.parse(is, "UTF-8", "http://example.com/", p);
            System.out.println(doc.toString());
        }
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            usage();
            return;
        }
        InputStream is = System.in;
        if (args.length > 1)
            is = new BufferedInputStream(new FileInputStream(new File(args[1])));
        switch (args[0]) {
        case "-xml":
            Printer.XML.run(is);
            break;
        case "-html":
            Printer.HTML.run(is);
            break;
        case "-json":
            Printer.JSON.run(is);
            break;
        default:
            System.out.println("Unknown format: " + args[0]);
        }
        is.close();
    }

}
