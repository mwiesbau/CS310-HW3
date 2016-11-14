import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Application {

    public static void writeFileToDisk(Disk disk, String filename) {
        int currentSector = 0;


        // TRY TO LOAD FILE
        try {
            Scanner sc = new Scanner(new File(filename)).useDelimiter(System.getProperty("line.separator"));

            // CREATE BUFFER TO STORE RECORDS BEFORE WRITING TO DISK
            Buffer buffer = new Buffer(disk.getSectorSize(), 60);

            // LOAD EACHLINE
            while (sc.hasNextLine()) {
                String line = sc.next();

                // PARSE LINE AND CREATE CHARACTER ARRAY
                MountainRecord m = new MountainRecord(line);
                char[] record = m.getMountainRecordAsChar();

                // ADD THE RECORD TO THE BUFFER OR WRITE BUFFFER TO DISK IF FULL
                if (buffer.isFull()) {
                    System.out.println("Writing sector " + currentSector + " to disk.");
                    disk.writeSector(currentSector, buffer.getBuffer());
                    currentSector++;
                    buffer.emptyBuffer();
                    buffer.addRecord(record);
                } else {
                    buffer.addRecord(record);
                } // end if
            } // end while

            // WRITE BUFFER TO DISK AT THE END
            System.out.println("Writing sector " + currentSector + " to disk.");
            disk.writeSector(currentSector, buffer.getBuffer());
            currentSector++;
            buffer.emptyBuffer();

        // CATCH EXCEPTION IF FILE NOT FOUND
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file " + filename);
        } // end try catch
    } // end write file to disk


    public static void printSector(Disk disk, int sectorNumber) {

        char[] buff = new char[512];
        disk.readSector(sectorNumber, buff);

        for (int i = 0; i < buff.length; i++) {
            // PRINT NEW LINE IF AT THE END OF RECORD
            if ((i) % 60 == 0) {
                System.out.println();
            } // end if

            // REPLACE EMPTY CARACTERS WITH UNDERLINE
            if (buff[i] == '\000') {
                System.out.print("_");
            } else {
                System.out.print(buff[i]);
            } // end if else
        } // end for
    } // end print sector

    public static void main(String args[]) {
        int sectorSize = 512;
        int sectors = 10000;

        String filename = "mountains.txt";
        Disk disk = new Disk(sectors, sectorSize);

        writeFileToDisk(disk, filename);
        printSector(disk, 5);





    } // end main
} // end application