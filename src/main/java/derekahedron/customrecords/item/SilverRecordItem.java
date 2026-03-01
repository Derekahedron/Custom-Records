package derekahedron.customrecords.item;

public class SilverRecordItem extends GoldenRecordItem {

    public SilverRecordItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getModelFolder() {
        return "silver_record";
    }
}
