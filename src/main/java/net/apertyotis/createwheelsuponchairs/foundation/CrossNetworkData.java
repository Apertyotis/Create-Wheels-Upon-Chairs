package net.apertyotis.createwheelsuponchairs.foundation;

import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class CrossNetworkData {
    public boolean hasOrderId = false;
    public int orderId = 0;
    public int usedLinks = 0;
    public MutableBoolean finalLinkTracker;
    public PackageOrderWithCrafts firstContextTracker;

    public static CrossNetworkData INSTANCE;
}
