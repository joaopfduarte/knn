public class KnnResults {

    private int k;

    private double accuracy;

    private String distanceMetric;

    public KnnResults(int k, double accuracy, String distanceMetric) {
        this.k = k;
        this.accuracy = accuracy;
        this.distanceMetric = distanceMetric;
    }

    public int getK() {return k;}

    public void setK(int k) {this.k = k;}

    public double getAccuracy() {return accuracy;}

    public void setAccuracy(double accuracy) {this.accuracy = accuracy;}

    public String getDistanceMetric() {return distanceMetric;}

    public void setDistanceMetric(String distanceMetric) {this.distanceMetric = distanceMetric;}

}
