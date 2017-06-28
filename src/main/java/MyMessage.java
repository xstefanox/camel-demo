public class MyMessage {

    private String id;

    private Boolean valid;

    private Integer children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean isValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "MyMessage{" +
                "id='" + id + '\'' +
                ", valid=" + valid +
                ", children=" + children +
                '}';
    }
}
