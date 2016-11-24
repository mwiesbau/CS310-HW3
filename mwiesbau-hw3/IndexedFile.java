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
   } // end constructor



   public boolean insertRecord(char[] record)
   {
    return false;


   }
   public boolean findRecord(char[] record)
   {

        String recordString = new String(record);
        int sector = getSector(recordString.substring(0, keySize - 1).toCharArray());

        char[] chars = new char[disk.getSectorSize()];
        disk.readSector(sector, chars);
        Buffer buff = new Buffer(chars, recordSize);

        System.out.println(buff.toString());

        return true;

   }

   // there is no delete operation
   private int getSector(char[] key)   // returns sector number indicated by key
   {
        // KEEP TRACK OF SMALLEST RELATIVE KEY DIFFERENCE
       // START AT ROOT NODE
       int nextNodeIndex = indexRoot;

       // CONVERT SEARCH KEY TO STRING
       String keyString = new String(key);
       //System.out.println("Key: " + keyString);

       for (int i = 0; i < indexLevels; i++) {
           int smallestRelativeSize = 1000;
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
               //System.out.print(" | Node: " + nodeKey);
               // SPLIT OFF POOINTER AND CONVERT TO NUMBER
               String numberString = nodeString.substring(keySize, indexRecordSize-1);
               numberString = numberString.replaceAll("[^\\d]", "");    // REMOVE ALL NON NUMERICAL CHARACTERS
               Integer nodeIndex = Integer.parseInt(numberString, 10);

               // COMPARE SEARCH KEY WITH RECORD KEY
               int relativeSize = keyString.compareTo(nodeKey);
               //System.out.println(" | " + relativeSize);
               // IF THE CURRENT KEY IS CLOSER TO THE SEARCH KEY UPDATE REFERENCES
               if (relativeSize < smallestRelativeSize && relativeSize >= 0) {
                   smallestRelativeSize = relativeSize;
                   nextNodeIndex = nodeIndex;
               } // end if
           } // end while
           //System.out.println("Smallest Node = " + nextNodeIndex);
       } // end for
    return nextNodeIndex;
   } // end get sector
   
}