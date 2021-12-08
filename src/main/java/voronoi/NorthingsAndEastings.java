package voronoi;

public class NorthingsAndEastings {
    private String postcode;
    private int easting;
    private int northing;

    public NorthingsAndEastings(String postcode, int easting, int northing) {

        this.postcode = postcode;
        this.easting = easting;
        this.northing = northing;
    }

    public int getEasting() {
        return easting;
    }

    public int getNorthing() {
        return northing;
    }
}
