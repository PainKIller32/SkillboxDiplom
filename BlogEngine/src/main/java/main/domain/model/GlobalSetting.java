package main.domain.model;

import javax.persistence.*;

@Entity
@Table(name = "global_settings")
public class GlobalSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String code;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}