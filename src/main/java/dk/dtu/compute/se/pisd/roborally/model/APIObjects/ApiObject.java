package dk.dtu.compute.se.pisd.roborally.model.APIObjects;

public interface ApiObject {
    long id = 0;
    /**
     * @author s224804
     * @return the path of the object
     */
    public String getPath();
    public long getId();
}
