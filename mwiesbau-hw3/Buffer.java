// THIS IS A HELPER CLASS TO DEAL WITH READING AND
// WRITING THE DISK BUFFER
// CAN HANDLE BOTH THE RECORDS AND THE INDEX NODES
public class Buffer {
    private char[] buffer;
    private int recordSize;       // SIZE OF THE RECORD STORED 60 for DATA, 34 FOR INDEX NODE
    private int bufferFillLevel;
    private int roomForAdditionalRecords = 3; // SPECIFIES HOW MUCH ROOM SHOULD BE LEFT

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
        //int lastCharInBuffer = buffer.length - 1;

        if (buffer != null) {
            // DETERMINE FILL LEVEL BY FIDING THE LAST RECORD
            for (int i = 0; i < (buffer.length - recordSize); i+=this.recordSize) {
                if (!(buffer[i] == '\000')) {
                    bufferFillLevel += recordSize;
                } // end if
            } // end for
        }

    } // end constructor

    public char[] removeRecord() {
    // REMVOES FIRST RECORD IN BUFFER
        int recordStart = 0;

        // DETERMINES THE NEXT RECORD LOCATION

        while (buffer[recordStart] == '\000' && recordStart < (buffer.length - 2*recordSize)) {
            recordStart += recordSize;
        } // end while

        // CONVERT RECORD TO STRING
        String record = new String(buffer).substring(recordStart, recordStart+recordSize);

        // DELETE RECORD FROM BUFFER
        for (int i = recordStart; i < recordStart + recordSize; i++) {
            buffer[i] = '\000';
        } // end for

        // ADJUST VARIABLES AND RETURN
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
        if ((buffer.length - bufferFillLevel) >= ((roomForAdditionalRecords + 1) * recordSize)) {
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



    public boolean findKeyInBuffer(char[] record, int keySize) {

        String searchRecordString = new String(record);
        String searchRecordKeyString = searchRecordString.substring(0, keySize - 1);
        boolean foundRecord = false;

        while (!isEmpty()) {
            // GET A RECORD FROM BUFFER
            char[] recordFromBuffer = removeRecord();
            // CONVERT RECORD TO STRING
            String recordString = new String(recordFromBuffer);
            // SPLIT OFF KEY FIELD
            String recordKeyString = recordString.substring(0, keySize - 1);

            if (searchRecordKeyString.equals(recordKeyString)) {
                foundRecord = true;
                // MAKE THE FOUND RECORD THE FIRST RECORD IN THE BUFFER
                addRecord(recordFromBuffer);
                return foundRecord;
            } // end if
        } // end while

        return foundRecord;
    } // end findKeyInBuffer

    public void setRoomForAdditionalRecords(int numberOfRecords) {
        // ONLY ALLOW A REDUCTION NO INCREASE TO AVOID ISSUES
        if (numberOfRecords <= this.roomForAdditionalRecords) {
            this.roomForAdditionalRecords = numberOfRecords;
        } // end if
    } // ennd setRoomForAdditionalRecord




} // end buffer