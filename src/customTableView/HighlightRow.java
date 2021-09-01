package customTableView;

import com.company.Service;
import javafx.scene.control.TableRow;

public class HighlightRow extends TableRow<Service> {
  @Override
  public void updateSelected(boolean selected) {
    super.updateSelected(selected);
    if (isSelected()) {
      setStyle("");
    } else if (getItem() != null) {
      if (getItem().getQuantity() < 1000) {
        setStyle("-fx-background-color: #FFFDD0;");
      } else {
        setStyle("");
      }
    }
  }
}
