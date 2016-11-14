public class Disk
{
   private int sectorCount;   // sectors on the disk
   private int sectorSize;    // characters in a sector
   private char[][] store;    // all disk data is stored here
   public Disk() {    // for default sectorCount and sectorSize
       this(10000, 512);
   } // end default constructor


   public Disk(int sectorCount, int sectorSize) {
      this.sectorSize = sectorSize;
      this.sectorCount = sectorCount;
      this.store = new char[sectorCount][sectorSize];
   } // end Constructor


   public void readSector(int sectorNumber, char[] buffer) {  // sector to buffer
       // COPIES THE CONTENTS OF THE SECTO TO THE BUFFER
       for (int i = 0; i < buffer.length; i++) {
           buffer[i] = this.store[sectorNumber][i];
       } // end for
   } // end readSector


   public void writeSector(int sectorNumber, char[] buffer) {  // buffer to sector
       // COPIES THE CONTENTS OF THE BUFFER TO THE SECTOR
       for (int i = 0; i < buffer.length; i++) {
           store[sectorNumber][i] = buffer[i];
       } // end for
   } // end writeSector


   public int getSectorCount() {
      return this.sectorCount;
   }


   public int getSectorSize() {
      return this.sectorSize;
   }

} // end Disk
