public class Buffer {
    char[] buffer;
    int recordSize;
    int bufferFillLevel;

    public Buffer(int bufferSize, int recordSize) {
        this.buffer = new char[bufferSize];
        this.recordSize = recordSize;
        this.bufferFillLevel = 0;
    } // end constructor


    public void addRecord(char[] record) {
    // ADDS RECORDS IN SUCCESSIVE ORDER TO BUFFER
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
        if ((buffer.length - bufferFillLevel) > (4 * recordSize)) {
            return false;
        } // end if
        return true;
    } // end is full


    public char[] getBuffer() {
        return buffer;
    }

} // end buffer