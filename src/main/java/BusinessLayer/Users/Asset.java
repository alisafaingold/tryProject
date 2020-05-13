package BusinessLayer.Users;

import java.util.HashMap;

public interface Asset {
    boolean editAsset(HashMap<String, String> changes) throws Exception;
}
