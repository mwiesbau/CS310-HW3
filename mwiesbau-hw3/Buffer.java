public class Buffer {
    private char[] buffer;
    private int recordSize;
    private int bufferFillLevel;
    private int roomForAdditionalRecords = 3;

    public Buffer(int bufferSize, int recordSize) {
        this.buffer = new char[bufferSize];
        this.bufferFillLevel = 0;
        this.recordSize = recordSize;
    } // end constructor


    public Buffer(int bufferSize, int recordSize, int roomForAdditionalRecords) {
        this(bufferSize, recordSize);
        this.roomForAdditionalRecords = roomForAdditionalRecords;
    }


    public Buffer(char[] sector, int recordSize) {
        this.buffer = sector;
        this.recordSize = recordSize;
        int lastCharInBuffer = buffer.length - 1;


        // FIND THE LAST RECORD NON EMPTY RECORD
        while (buffer[lastCharInBuffer] == '\000' && lastCharInBuffer > 0) {
            lastCharInBuffer -= 1;
        } // end while

        bufferFillLevel = ((lastCharInBuffer / recordSize) + 1) * recordSize;
    }

    public char[] removeRecord() {

        int recordStart = 0;

        while (buffer[recordStart] == '\000') {
            recordStart += recordSize;
        } // end while


        String record = new String(buffer).substring(recordStart, recordStart+recordSize);

        for (int i = recordStart; i < recordStart + recordSize; i++) {
            buffer[i] = '\000';
        } // end for

        bufferFillLevel -= recordSize;
        return record.toCharArray();
    } // end remove Record

    public void addRecord(char[] record) {
    // ADDS RECORDS IN SUCCESSIVE ORDER TO BUFFER

        // SETS THE RECORD SIZE TO THE SIZE OF THE FIRST RECORD RECEIVED
        if (this.recordSize == 0 ) {
            this.recordSize = record.length;
        } // end if

        // ADDS THE RECORD TO THE BUFFER
        for (int i = 0; i < record.length; i ++) {
            buffer[bufferFillLevel + i] = record[i];
        } // end for
        bufferFillLevel += record.length;
    } // end addRecord


    public void emptyBuffer() {
    // CLEARS THE BUFFER
        bufferFillLevel = 0;
        char[] newBuffer = new char[buffer.length];
        buffer = newBuffer;
    } // end emptyBuffer


    public boolean isFull() {
    // IS FULL IF ONLY THREE MORE RECORDS CAN BE STORED
        if ((buffer.length - bufferFillLevel) > ((roomForAdditionalRecords + 1) * recordSize)) {
            return false;
        } // end if
        return true;
    } // end is full


    public boolean isEmpty() {

        if (bufferFillLevel == 0) {
            return true;
        }
        return false;
    }

    public char[] getBuffer() {
        return buffer;
    }


    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < buffer.length; i++) {
            // PRINT NEW LINE IF AT THE END OF RECORD
            if ((i) % this.recordSize == 0) {
                output.append("\n");
                //System.out.println();
            } // end if

            // REPLACE EMPTY CARACTERS WITH UNDERLINE
            if (buffer[i] == '\000') {
                output.append("_");
                //System.out.print("_");
            } else {
                output.append(buffer[i]);
                //System.out.print(buffer[i]);
            } // end if else
        } // end for
    return output.toString();
    } // end print sector




} // end buffer