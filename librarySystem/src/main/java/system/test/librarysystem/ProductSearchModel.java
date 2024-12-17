package system.test.librarysystem;

public class ProductSearchModel {
    Integer ID;
    String Name, Creator, image_path;

    public ProductSearchModel(Integer ID, String Name, String Creator, String image_path) {
        this.ID = ID;
        this.Name = Name;
        this.Creator = Creator;
        this.image_path = image_path;

    }

    public Integer getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getCreator() {
        return Creator;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setCreator(String creator) {
        Creator = creator;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}