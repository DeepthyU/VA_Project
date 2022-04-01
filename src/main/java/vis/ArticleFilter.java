package vis;

import java.util.ArrayList;
import java.util.List;

public class ArticleFilter {

    ArticleField field;
    List<String> selectedValues = new ArrayList<>();
    List<String> unselectedValues = new ArrayList<>();
    long startDate;
    long endDate;
    boolean keepEmptyValue;

    public boolean isKeepEmptyValue() {
        return keepEmptyValue;
    }

    public void setKeepEmptyValue(boolean keepEmptyValue) {
        this.keepEmptyValue = keepEmptyValue;
    }

    public ArticleField getField() {
        return field;
    }

    public void setField(ArticleField field) {
        this.field = field;
    }

    public List<String> getSelectedValues() {
        return selectedValues;
    }

    public void setSelectedValues(List<String> selectedValues) {
        this.selectedValues = selectedValues;
    }

    public List<String> getUnselectedValues() {
        return unselectedValues;
    }

    public void setUnselectedValues(List<String> unselectedValues) {
        this.unselectedValues = unselectedValues;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
