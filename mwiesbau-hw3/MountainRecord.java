class MountainRecord {

    private String name;
    private String country;
    private String elevation;

    private int nameSize = 27;
    private int countrySize = 27;
    private int elevationSize = 6;


    public MountainRecord(String line) {
        line.replace("\n", "");
        String[] items = line.split("#");

        this.name = items[0];
        this.country = items[1];
        this.elevation = items[2];
    } // end constructor



    public char[] getMountainRecordAsChar() {
    /// THIS METHOD RETURNS THE MOUNTAIN RECORD AS AN ARRAY OF CHARACTERS

        char[] record = new char[nameSize + countrySize + elevationSize];

        // COPY NAME TO RECORD
        char[] name = this.name.toCharArray();
        for (int i = 0; i < name.length; i++) {
            record[i] = name[i];
        } // end for

        // COPY COUNTRY TO RECORD
        char[] country = this.country.toCharArray();
        for (int i = 0; i < country.length; i++) {
            record[i + nameSize] = country[i];
         } // end for

        // COPY ELEVATION TO RECORD
        char[] elevation = this.elevation.toCharArray();
        for (int i = 0; i < elevation.length; i++) {
            record[i + nameSize + countrySize] = elevation[i];
        } // end for


        return record;
    }



} // end class