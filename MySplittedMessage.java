package test;

public class MySplittedMessage {

    private String id;

    private Integer number;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "MySplittedMessage{" +
                "id='" + id + '\'' +
                ", number=" + number +
                '}';
    }
}
