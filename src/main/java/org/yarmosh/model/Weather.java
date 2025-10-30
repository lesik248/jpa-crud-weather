package org.yarmosh.model;

import jakarta.persistence.*;

@Entity
@Table(name="weather")
@NamedQueries({
        @NamedQuery(
                name = "Weather.findById",
                query = "SELECT w FROM Weather w WHERE w.id = :id"
        ),
        @NamedQuery(
                name = "Weather.findAll",
                query = "SELECT w FROM Weather w"
        )
})
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="region")
    private Region region;

    private String date;

    private int temperature;

    private String precipitation;

    public Weather() {}

    public Weather(int id, Region region, String date, int temperature, String precipitation) {
        this.id = id;
        this.region = region;
        this.date = date;
        this.temperature = temperature;
        this.precipitation = precipitation;
    }
    public int getId() {
        return id;
    }
    public int getRegion() {
        return region.getId();
    }
    public String getDate() {
        return date;
    }
    public int getTemperature() {
        return temperature;
    }
    public String getPrecipitation() {
        return precipitation;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }
    public String toString() {
        return "Погода в " + region.getName() + " " + date + ": " + temperature + " C, " + precipitation;
    }
}
