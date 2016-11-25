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


    public MountainRecord() {
        this.name = "";
        this.country = "";
        this.elevation = "";
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;

    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }


    public int getKeySize() {
        return nameSize;
    }


    public char[] recordToCharArray() {
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


    public void charArrayToRecord(char[] record) {

        String recordString = new String(record);
        this.name = recordString.substring(0, nameSize);
        this.country = recordString.substring(nameSize, nameSize + countrySize);
        this.elevation = recordString.substring(nameSize + countrySize, record.length);

    } // end charArrayToRecord


    public String toString() {
        String output = "Name: " + this.name + " | Country: " + this.country + " | Elevation : " + this.elevation;
        return output;
    }

} // end class