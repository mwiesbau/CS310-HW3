public class IndexedFile
{
   private Disk disk;             // disk on which the file will be written
   private char[] buffer;         // disk buffer
   private int recordSize;        // in characters
   private int keySize;           // in characters
   private int indexRecordSize;       // in characters
   // fields describing data portion of file
   private int recordsPerSector;  // sectorSize/recordSize
   private int firstAllocated;    // sector number where data begins
   private int sectorsAllocated;  // sectors originally allocated for data
   private int overflowStart;     // sector number where overflow begins
   private int overflowSectors;   // count of overflow sectors in use
   // fields describing index portion of file
   private int indexStart;        // sector number where index begins
   private int indexSectors;      // number of sectors allocated for index
   private int indexRoot;         // sector number of root of index
   private int indexLevels;       // number of levels of index
   
   public IndexedFile(Disk disk, int recordSize, int keySize, int indexRecordSize,
                      int firstAllocated, int indexStart, int indexSectors,
                      int indexRoot, int indexLevels)
   {
       this.disk = disk;
       this.recordSize = recordSize;
       this.keySize = keySize;
       this.indexRecordSize = indexRecordSize;
       this.firstAllocated = firstAllocated;
       this.indexStart = indexStart;
       this.indexSectors = indexSectors;
       this.indexRoot = indexRoot;
       this.indexLevels = indexLevels;
       this.overflowStart = indexRoot + 1;
       this.overflowSectors = 0;
       this.buffer = new char[disk.getSectorSize()];
   } // end constructor



   public boolean insertRecord(char[] record) {

       // 1st CASE
       // CHECK IF RECORD EXISTS
       if (findRecord(record) == true) {
           return false;
       } // end if

       // 2nd CASE
       // FIND THE DATA RECORD WHERE THE RECORD BELONGS ACCORDING TO INDEX
       // AND TRY TO ADD THERE
       String searchRecordString = new String(record);
       String searchRecordKeyString = searchRecordString.substring(0, keySize - 1);
       int dataSector = getSector(searchRecordKeyString.toCharArray());

       // READ DATA SECTOR INTO BUFFER
       char[] chars = new char[disk.getSectorSize()];
       disk.readSector(dataSector, chars);
       Buffer buff = new Buffer(chars, recordSize);
       buff.setRoomForAdditionalRecords(0); // ALLOW THE SECTOR/BUFFER TO BE FILLED
       // IF THERE IS ROOM IN THE DATA SECTOR ADD THE RECORD
       if (!buff.isFull()) {
           buff.addRecord(record);
           disk.writeSector(dataSector, buff.getBuffer());
           return true;
       } // endif

       // 3rd CASE
       // ADD TO OVERFLOW SECTOR
       addToOverFlow(record);
       return true;
   }

   // ADDS DATA TO OVER FLOW SECTOR
   private void addToOverFlow(char[] record) {
   // ADDS RECORD TO LAST NOT FULL OVERFLOW
   // OTHERWISE CREATES NEW OVERFLOW SECTOR

       // READ CURRENT OVERFLOW SECTOR INTO BUFFER
       if (overflowSectors == 0) {
           overflowSectors = 1;
       } // end if

       int currentOverFlowSector = indexRoot + overflowSectors;
       //char[] chars = new char[disk.getSectorSize()];
       disk.readSector(currentOverFlowSector, buffer);
       Buffer buff = new Buffer(buffer, recordSize);
       buff.setRoomForAdditionalRecords(0);

       // IF THE BUFFER IS NOT FULL ADD RECORD AND SAVE TO DISK
       if (!buff.isFull()) {
           buff.addRecord(record);
           disk.writeSector(currentOverFlowSector, buff.getBuffer());
       // IF THE BUFFER IS FULL, CREATE NEW BUFFER ADD RECORD AND SAVE TO DISK
       } else {
           overflowSectors += 1;
           currentOverFlowSector += 1;
           buff.emptyBuffer();
           buff.addRecord(record);
           disk.writeSector(currentOverFlowSector, buff.getBuffer());
       } // end if else
   } // end add to overflow



   // RETURNS TRUE IF THE RECORD WAS FOUND IN EITHER THE DATA OR OVERFLOW PORTION
   public boolean findRecord(char[] record) {
       String searchRecordString = new String(record);
       String searchRecordKeyString = searchRecordString.substring(0, keySize - 1);
       boolean foundRecord = false;

        // 1st CASE
        // GET THE DATA SECTOR THE RECORD SHOULD BE IN
        int sector = getSector(searchRecordKeyString.toCharArray());

        // READ SECTOR INTO BUFFER
        //char[] chars = new char[disk.getSectorSize()];
        disk.readSector(sector, buffer);
        Buffer buff = new Buffer(buffer, recordSize);

        foundRecord = buff.findKeyInBuffer(record, keySize);


        if (foundRecord == true) {
            // REASSIGN THE FOUND RECORD TO THE PASSED CHARACTER ARRAY
            char[] foundRec = buff.removeRecord();
            java.lang.System.arraycopy(foundRec, 0, record, 0, record.length-1);
            return foundRecord;
        } // END IF

       // 2nd CASE
       // LOOK IN THE OVERFLOW SECTORS IF NO RECORDS WERE FOUND IN THE DATA SECTORS
       // IF OVERFLOW SECTORS EXIST
       if (overflowSectors > 0) {
           // ITERATE OVER ALL OVERFLOW SECTORS
           for (int  i = overflowStart; i < (overflowStart + overflowSectors); i++) {
                // READ EACH SECTOR INTO BUFFER
               disk.readSector(i, buffer);
               Buffer buffO = new Buffer(buffer, recordSize);
               foundRecord = buffO.findKeyInBuffer(record, keySize);

               if (foundRecord) {
                    MountainRecord result = new MountainRecord();
                    result.charArrayToRecord(buff.removeRecord());
                    System.out.println(result.toString());
               } // end if
           } // end for
       } // end if

       // 3rd CASE RECORD NOT FOUND
        return foundRecord;
   } // end findRecord

   // there is no delete operation
   // Returns sector number for data sector only
   private int getSector(char[] key)  {
       // START AT ROOT NODE
       // VARIABLE TO TRAVERSE THROUGH INDEX TREE
       int nextNodeIndex = indexRoot;

       // CONVERT SEARCH KEY TO STRING
       String keyString = new String(key);

       // ITERATE THROUGH THE TREE LEVELS
       for (int i = 0; i < indexLevels; i++) {
           int smallestRelativeSize = 0;

           // READ INDEX NODE SECTOR INTO BUFFER
           char[] chars = new char[disk.getSectorSize()];
           disk.readSector(nextNodeIndex, chars);
           Buffer buff = new Buffer(chars, indexRecordSize);

           // ITERATE OVER NODE ENTRIES
           while (!buff.isEmpty()) {

               // GET A RECORD FROM BUFFER
               char[] node = buff.removeRecord();
               // CONVERT RECORD TO STRING
               String nodeString = new String(node);
               // SPLIT OFF KEY FIELD
               String nodeKey = nodeString.substring(0, keySize - 1);

               // SPLIT OFF POOINTER AND CONVERT TO NUMBER
               String numberString = nodeString.substring(keySize, indexRecordSize-1);
               numberString = numberString.replaceAll("[^\\d]", "");    // REMOVE ALL NON NUMERICAL CHARACTERS
               Integer nodeIndex = Integer.parseInt(numberString, 10);

               // COMPARE SEARCH KEY WITH RECORD KEY
               int relativeSize = keyString.compareTo(nodeKey);
               // IF THE CURRENT KEY IS CLOSER TO THE SEARCH KEY UPDATE REFERENCES
               if (relativeSize >= 0 ) {
                   smallestRelativeSize = relativeSize;
                   nextNodeIndex = nodeIndex;
               } // end if
           } // end while
       } // end for
    return nextNodeIndex;
   } // end get sector
} // end class