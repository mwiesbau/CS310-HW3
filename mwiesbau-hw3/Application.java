import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Application {

    public static int writeFileToDisk(Disk disk, String filename, int startSector, int recordSize) {
        int currentSector = startSector;

        // TRY TO LOAD FILE
        try {
            Scanner sc = new Scanner(new File(filename)).useDelimiter(System.getProperty("line.separator"));

            // CREATE BUFFER TO STORE RECORDS BEFORE WRITING TO DISK
            Buffer buffer = new Buffer(disk.getSectorSize(), recordSize);

            // LOAD EACHLINE
            while (sc.hasNextLine()) {
                String line = sc.next();

                // PARSE LINE AND CREATE CHARACTER ARRAY
                MountainRecord m = new MountainRecord(line);
                char[] record = m.recordToCharArray();

                // ADD THE RECORD TO THE BUFFER OR WRITE BUFFFER TO DISK IF FULL
                if (buffer.isFull()) {
                    //System.out.println("Writing sector " + currentSector + " to disk.");
                    disk.writeSector(currentSector, buffer.getBuffer());
                    currentSector++;
                    buffer.emptyBuffer();
                    buffer.addRecord(record);
                } else {
                    buffer.addRecord(record);
                } // end if
            } // end while

            // WRITE BUFFER TO DISK AT THE END
            //System.out.println("Writing sector " + currentSector + " to disk.");
            disk.writeSector(currentSector, buffer.getBuffer());
            //currentSector++;
            buffer.emptyBuffer();

        // CATCH EXCEPTION IF FILE NOT FOUND
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file " + filename);
        } // end try catch

        // RETURNS THE LAST SECTOR ON THE DISK
        return currentSector;
    } // end write file to disk



    public static void main(String args[]) {
        int sectorSize = 512;
        int sectors = 10000;
        int keySize = 27;
        int firstDataSector = 1000;
        int lastDataSector;
        int recordSize = 60;
        int indexRecordSize = 34;
        Disk disk = new Disk(sectors, sectorSize);


        // WRITE FILE TO DISK AND BUILD INDEX
        String filename = "mountains.txt";
        lastDataSector = writeFileToDisk(disk, filename, 1000, recordSize);
        Index index = new Index(disk, sectorSize, keySize, firstDataSector, lastDataSector, recordSize, 7);
        index.buildIndex();
        int indexRoot = index.getRootSectorNumber();
        int indexStart = lastDataSector + 1;
        int indexSectors = indexRoot - indexStart;
        int indexLevels = index.getIndexLevels();

        IndexedFile iFile = new IndexedFile(disk, recordSize, keySize, indexRecordSize, firstDataSector,
                                            indexStart, indexSectors, indexRoot, indexLevels);



        MountainRecord rec = new MountainRecord();
        rec.setName("Agassiz Peak");
        boolean found = iFile.findRecord(rec.recordToCharArray());

        System.out.println(found);

        /*
        char[] carr4 = new char[sectorSize];
        disk.readSector(1631, carr4);
        Buffer b4 = new Buffer(carr4, 34);
        System.out.println(b4.toString());

        char[] carr5 = new char[sectorSize];
        disk.readSector(1630, carr5);
        Buffer b5 = new Buffer(carr5, 34);
        System.out.println(b5.toString());

        char[] carr6 = new char[sectorSize];
        disk.readSector(1618, carr6);
        Buffer b6 = new Buffer(carr6, 34);
        System.out.println(b6.toString());

        char[] carr1 = new char[sectorSize];
        disk.readSector(1456, carr1);
        Buffer b1 = new Buffer(carr1, recordSize);
        System.out.println(b1.toString());
        */
    } // end main
} // end application