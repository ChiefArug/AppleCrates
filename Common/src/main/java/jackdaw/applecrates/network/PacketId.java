package jackdaw.applecrates.network;

import jackdaw.applecrates.Constants;
import net.minecraft.resources.ResourceLocation;

public class PacketId {

    public static final ResourceLocation CHANNEL = new ResourceLocation(Constants.MODID, "paintingchannelnetwork");

    public static final byte SPACKET_TRADE = 1;
    public static final byte SPACKET_SALE = 2;


}
