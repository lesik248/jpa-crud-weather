package org.yarmosh.model;

import jakarta.persistence.*;

@Entity
@Table(name="region")
@NamedQueries({
        @NamedQuery(
                name = "Region.findById",
                query = "SELECT r FROM Region r WHERE r.id = :id"
        ),
        @NamedQuery(
                name = "Region.findAll",
                query = "SELECT r FROM Region r"
        )
})
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int square;

    @ManyToOne
    @JoinColumn(name = "citizen_type")
    private CitizenType citizenType;

    public Region() {}

    public Region(int id, String name, int square, CitizenType citizenType) {
        this.id = id;
        this.name = name;
        this.square = square;
        this.citizenType = citizenType;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getSquare() {
        return square;
    }
    public CitizenType getCitizenType() {
        return citizenType;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSquare(int square) {
        this.square = square;
    }
    public void setCitizenType(CitizenType citizenType) {
        this.citizenType = citizenType;
    }
}
