import java.util.ArrayList;

public class Index {
    private int nodeSize;
    private int firstIndexSector;
    private int sectorSize;
    private int keySize;
    private int sectorNumberSize;
    private int firstDataSector;
    private int lastDataSector;
    private int lastIndexSector;
    private int rootNode;                   // INDEX OF ROOT NODE
    private int currentIndexLevelStart;
    private Disk disk;
    private int recordSize;
    private Node[] nodes;
    private int lastNode;
    private int indexLevels;


    private class Node {
        String key;
        int sectorNumber;

        Node(String key, int sectorNumber) {
            this.key = key;
            this.sectorNumber = sectorNumber;
        } // end constructor

        Node(char[] charArray) {
            this.key = new String(charArray).substring(0, nodeSize);
        } // end constructor


        char[] toCharArray() {
            // CONVERTS A NODE TO A CHAR ARRAY
            char[] nodeArr = new char[keySize + sectorNumberSize];
            char[] keyArr = this.key.toCharArray();
            char[] sectorNumberArr = String.valueOf(this.sectorNumber).toCharArray();

            // ADD THE KEY FILED TO THE CHARACTER ARRAY
            for (int i = 0; i < keyArr.length; i++) {
                nodeArr[i] = keyArr[i];
            } // end for

            // ADD THE SECTOR NUMBER TO CHARACTER ARRAY
            for (int i = 0; i < sectorNumberArr.length; i++) {
                nodeArr[i + keySize] = sectorNumberArr[i];
            } // end for
            return nodeArr;
        } // end to charArray




        String toStringDebug() {
        // METHOD USED TO DEBUG THE CHAR ARRAY FOR THE NODE
            StringBuilder output = new StringBuilder();

            char[] nodeCharArr = this.toCharArray();

            for (int i = 0; i < nodeCharArr.length; i++) {
                if (nodeCharArr[i] == '\000') {
                    output.append("_");
                }
                output.append(nodeCharArr[i]);

            } // end for

            return output.toString();
        } // end


    }

    public Index(Disk disk, int sectorSize, int keySize, int firstDataSector, int lastDataSector, int recordSize, int sectorNumberSize) {
        this.nodeSize = keySize + sectorNumberSize;
        this.firstIndexSector = lastDataSector + 1;
        this.lastIndexSector = firstIndexSector;
        this.sectorSize = sectorSize;
        this.keySize = keySize;
        this.sectorNumberSize = sectorNumberSize;
        this.firstDataSector = firstDataSector;
        this.lastDataSector = lastDataSector;
        this.disk = disk;
        this.recordSize = recordSize;
        this.nodes = new Node[10];
        this.lastNode = 0;
        this.indexLevels = 0;

    } // end constructor


    private void addNode(Node node) {
    // ADDS A NODE TO THE NODE ARRAY
        if (lastNode == nodes.length -1 ) {
            growNodes();
        } // end if

        nodes[lastNode] = node;
        lastNode += 1;
    } // end add node

    private void emptyNodeArray() {
    // EMPTIES THE NODE ARRAY
        Node[] temp = new Node[10];
        nodes = temp;
        lastNode = 0;
    }

    private void growNodes() {
    // INCREASES THE SIZE OF THE NODE ARRAY BY FACTOR 2
        Node[] temp = new Node[nodes.length*2];

        // COPY EACH NODE TO NEW ARRAY
        for (int i = 0; i < nodes.length; i++) {
            temp[i] = nodes[i];
        } // end for

        nodes = temp;
    } // end growNodes

    public void buildIndex() {
    // CONSTRUCTS THE INDEX FOR THE FILE WRITEN TO DISK

        // 1st BUILD THE NODES FOR THE DATA SECTORS
        buildIndexForDataSectors();
        ArrayList<Integer> sectorsWritten = writeNodesToDisk();

        // 2nd BUILD THE INDEX SECTORS
        // KEEP REPEATING UNTIL ONLY THE ROOT NODE IS LEFT
        while (sectorsWritten.size() > 1) {
            indexLevels++;
            // DETERMINE SECTOR RANGE
            int firstSector = sectorsWritten.get(0);
            int lastSector = sectorsWritten.get(sectorsWritten.size()-1);
            rootNode = lastSector + 1; // KEEP TRACK OF THE ROOT NODE

            // BUILD THE TREE NODES
            buildIndexTree(firstSector, lastSector);

            // WRITE THE TREE NODES TO DISK
            sectorsWritten = writeNodesToDisk();
        } // end while

        indexLevels++;   // ONE ADDITIONAL INCREMENT FOR THE ROOT LEVEL

    } // end build index

    private void buildIndexTree(int startsector, int endsector) {
    // BUILD THE INDEX FROM THE NODES WRITTEN TO DISK

        char[] bufferArray = new char[this.sectorSize];
        emptyNodeArray();

        // ITERATE OVER ALL SECTORS AND CREATE NODES
        for (int i = startsector; i <= endsector; i++) {

            // READ THE CURRENT SECTOR INTO BUFFER
            disk.readSector(i, bufferArray);
            Buffer buffer = new Buffer(bufferArray, nodeSize);

            // GET THE FIRST RECORD FROM THE BUFFER AND ADD
            Node n = new Node(buffer.removeRecord());
            n.sectorNumber = i;
            addNode(n);
        } // end for
    } // end buildIndexTree

    private ArrayList<Integer> writeNodesToDisk() {
    // WRITES THE NODES TO DISK AND RETURNS AN ARRAY OF SECTORS WRITTEN

        ArrayList<Integer> sectorArray = new ArrayList<Integer>();
        Buffer buffer = new Buffer(sectorSize, keySize + sectorNumberSize, 0);

        // ITERATE OVER NODES
        for (int i = 0; i < lastNode; i++) {

            // CHECK IF BUFFER IS FULL AND WRITE TO DISK IS SO
            if (buffer.isFull()) {
                sectorArray.add(lastIndexSector);
                disk.writeSector(lastIndexSector, buffer.getBuffer());
                lastIndexSector++;
                buffer.emptyBuffer();
            } // end if

            // ADD NEW ENTRY TO BUFFER
            buffer.addRecord(nodes[i].toCharArray());
        } // end for

        // WHEN DONE CHECK IF THERE ARE ITEMS IN THE BUFFER AND WRITE THEM TO DISK
        if (!buffer.isEmpty())
            sectorArray.add(lastIndexSector);
            disk.writeSector(lastIndexSector, buffer.getBuffer());
            lastIndexSector++;
            buffer.emptyBuffer();

        return sectorArray;
    } // end writeNodesToDisk


    private void buildIndexForDataSectors() {
    // CREATES THE FIRST LEVEL OF THE INDEX

        char[] bufferArray = new char[this.sectorSize];

        // ITERATE OVER ALL SECTORS AND CREATE NODES
        for (int i = this.firstDataSector; i <= this.lastDataSector; i++) {

            // READ THE CURRENT SECTOR TO BUFFER
            disk.readSector(i, bufferArray);
            Buffer buffer = new Buffer(bufferArray, recordSize);

            // GET THE FIRST RECORD
            MountainRecord m = new MountainRecord();
            m.charArrayToRecord(buffer.removeRecord());

            // CREATE NODE
            Node n = new Node(m.getName(), i);
            addNode(n);
        } // end for
    } // end buildIndexForDataSectors

    public int getRootSectorNumber() {
    // RETURNS THE SECTOR NUMBER OF THE ROOT INDEX NODE
        return rootNode;
    } // end getRootSectorNumber

    public int getIndexLevels() {
        return indexLevels;
    }

    public String toString() {
    // PRINTS ALL THE NODES IN THE INDEX
        StringBuilder output = new StringBuilder();

        // ITERATE OVER ALL NODES AT THE CURRENT INDEX LEVEL
        for (int i = 0; i < lastNode; i++) {
            output.append("[" + i + "] Key: " + nodes[i].key + " Sector: " + nodes[i].sectorNumber + "\n");
        } // end for
        return output.toString();
    } // end toString


} // end class