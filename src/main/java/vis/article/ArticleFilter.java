package vis.article;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArticleFilter {

    private ArticleField field;
    private List<String> selectedValues = new ArrayList<>();
    private List<String> unselectedValues = new ArrayList<>();
    private long startDate;
    private long endDate;
    private boolean keepEmptyValue;

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
        for (String selectedVal : selectedValues){
            this.selectedValues.add(selectedVal.toLowerCase(Locale.ROOT));
        }
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
