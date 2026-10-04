package derekahedron.customrecords.item;

import net.minecraft.world.item.Item;

public class PortableGlobalJukeboxItem extends PortableJukeboxItem {

    public PortableGlobalJukeboxItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
