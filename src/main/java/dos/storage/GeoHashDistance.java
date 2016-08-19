package dos.storage;

public class GeoHashDistance {

    private String origin;
    private String destination;
    private double distance;
    private double rdistance;
    
    public double getRdistance() {
        return rdistance;
    }
    public void setRdistance(double rdistance) {
        this.rdistance = rdistance;
    }
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public String getCommonPrefix() {
        int minLength = origin.length();
        if (destination.length() < minLength) {
            minLength = destination.length();
        }
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < minLength; i++) {
            if (origin.charAt(i) != destination.charAt(i)) {
                break;
            }
            prefix.append(origin.charAt(i));
        }
        if (prefix.length() > 6) {
            prefix.subSequence(0, 5);
        }
        return prefix.toString();
    }
    
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
}
