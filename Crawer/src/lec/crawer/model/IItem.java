package lec.crawer.model;

import java.io.IOException;
import java.io.Serializable;

public interface IItem extends Serializable {
   public String getKey();
   public void save() throws IOException;
}
