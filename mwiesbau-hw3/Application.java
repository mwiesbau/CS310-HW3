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


    public static void showMenu() {
        System.out.println();
        System.out.format("----------------------------------------\n");
        System.out.format("|                  MENU                |\n");
        System.out.format("----------------------------------------\n");
        System.out.format("|%20s", "Insert Record [i]");
        System.out.format("%20s", "Quit [q]  |\n");
        System.out.format("|%20s", "Search record [s]");
        System.out.format("%19s", "|");
        System.out.format("\n----------------------------------------\n");
    } // end show menu


    /* GETS INPUT FROM USER AND RETURNS INPUT STRING */
    public static String getInput(String prompt) {
        Scanner scan = new Scanner(System.in);
        System.out.print(prompt);
        String input = scan.next();
        return input;
    } // end getInput()

    public static void main(String args[]) {
        String userInput;
        Boolean quit = false;
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
        lastDataSector = writeFileToDisk(disk, filename, firstDataSector, recordSize);
        Index index = new Index(disk, sectorSize, keySize, firstDataSector, lastDataSector, recordSize, 7);
        index.buildIndex();
        int indexRoot = index.getRootSectorNumber();
        int indexStart = lastDataSector + 1;
        int indexSectors = indexRoot - indexStart;
        int indexLevels = index.getIndexLevels();

        IndexedFile iFile = new IndexedFile(disk, recordSize, keySize, indexRecordSize, firstDataSector,
                                            indexStart, indexSectors, indexRoot, indexLevels);


        while (quit == false) {
            showMenu();
            userInput = getInput("-> ");

            switch (userInput) {
                case "q":
                    quit = true;
                    break;

                case "i":
                    //String name = getInput("Mountain Name -> ");
                    //String county = getInput("Country -> ");
                    //String elevation = getInput("Elevation -> ");

                    MountainRecord newRec = new MountainRecord();
                    newRec.setName(getInput("Mountain Name -> "));
                    newRec.setCountry(getInput("Country -> "));
                    newRec.setElevation(getInput("Elevation -> "));
                    boolean added = iFile.insertRecord(newRec.recordToCharArray());

                    if (added) {
                        System.out.println("Record added Successfully.");
                    } else {
                        System.out.println("Record with name: '" + newRec.getName() + "' already exists");
                    }

                    break;

                case "s":
                    String key = getInput("Mountain Name -> ");
                    MountainRecord rec = new MountainRecord();
                    rec.setName(key);
                    boolean found = iFile.findRecord(rec.recordToCharArray());
                    if (found) {
                        //System.out.println("'" + key + "' FOUND in file.");
                        break;
                    } // end if
                    System.out.println("'" + key + "' NOT FOUND in file.");
                    break;



            } // end switch

        } // end while

        //for (int i = 0; i < 26; i ++) {
        //    MountainRecord rec1 = new MountainRecord();
        //    rec1.setName("A" + i);
        //    rec1.setCountry("Austria");
        //    rec1.setElevation("12000");
        //    iFile.insertRecord(rec1.recordToCharArray());
        //}

        //MountainRecord rec = new MountainRecord();
        //rec.setName("A26");
        //boolean found = iFile.findRecord(rec.recordToCharArray());
        //System.out.println("Record found :" + found);


    } // end main
} // end application