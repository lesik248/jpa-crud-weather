package org.yarmosh.model;

import jakarta.persistence.*;

@Entity
@Table(name = "citizen_type")
@NamedQueries({
        @NamedQuery(
                name = "CitizenType.findById",
                query = "SELECT c FROM CitizenType c WHERE c.id = :id"
        ),
        @NamedQuery(
                name = "CitizenType.findAll",
                query = "SELECT c FROM CitizenType c"
        )
})
public class CitizenType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String language;

    private int number;

    public CitizenType() {}

    public CitizenType(int id, String name, String language, int number) {
        this.id = id;
        this.name = name;
        this.language = language;
        this.number = number;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getLanguage() {
        return language;
    }
    public int getNumber() {
        return number;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public void setNumber(int number) {
        this.number = number;
    }
}
