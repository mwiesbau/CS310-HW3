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

        // CATCH EXCEPTION IF FILE NOT FOUND
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file " + filename);
        } // end try catch
    } // end write file to disk


    public static void main(String args[]) {
        int sectorSize = 512;
        int sectors = 10000;

        String filename = "mountains.txt";
        Disk disk = new Disk(sectors, sectorSize);

        writeFileToDisk(disk, filename);





    } // end main
} // end application