package mshttp.utilities;

/**
 * Created by mvalencia on 3/6/17.
 */

public class Vin {
    public String id;
    public String date;
    public String vin;
    public CarDetails details;

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getVin() {
        return vin;
    }

    public CarDetails getDetails() {
        return details;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setDetails(CarDetails details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Vin{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", vin='" + vin + '\'' +
                ", details=" + details +
                '}';
    }
}