package com.sekwah.advancedportals.bukkit.compat;

import com.sekwah.advancedportals.bukkit.AdvancedPortalsPlugin;
import com.sekwah.advancedportals.bukkit.reflection.ReflectionHelper;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CraftBukkit {

    private final AdvancedPortalsPlugin plugin;

    // Data for chat bar and json message
    private Method chatMessageTypeMethod;

    private Method serializeMessage;
    private Constructor<?> chatPacketConstructor;

    private Method playerGetHandle;
    private Field playerConnection;
    private Method sendPacket;


    public CraftBukkit(AdvancedPortalsPlugin plugin, String bukkitImpl) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {

        this.plugin = plugin;

        try {
            String craftBukkitPackage = "org.bukkit.craftbukkit." + bukkitImpl + ".";
            String minecraftPackage = "net.minecraft.server." + bukkitImpl + ".";

            this.plugin.getLogger().info("Bukkit version detected " + bukkitImpl);

            Class<?> chatBaseComponent = Class.forName(minecraftPackage + "IChatBaseComponent"); // string to packet methods
            Class<?> chatSerialClass = ReflectionHelper.findClass(chatBaseComponent, "ChatSerializer");

            Class<?> chatMessageTypeClass = Class.forName(minecraftPackage + "ChatMessageType");

            this.chatMessageTypeMethod = chatMessageTypeClass.getMethod("a", byte.class);

            this.chatPacketConstructor = Class.forName(minecraftPackage + "PacketPlayOutChat").getConstructor(chatBaseComponent, chatMessageTypeClass);


            this.serializeMessage = chatSerialClass.getMethod("a", String.class);

            this.playerGetHandle = Class.forName(craftBukkitPackage + "entity.CraftPlayer").getMethod("getHandle");
            this.playerConnection = Class.forName(minecraftPackage + "EntityPlayer").getField("playerConnection"); // get player connection
            Class<?> packet = Class.forName(minecraftPackage + "Packet");
            this.sendPacket = playerConnection.getType().getMethod("sendPacket", packet);

        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().warning("Attempting to use backup porekit locations");
            // Fall back on your Porekit
            Class<?> textBaseComponent = Class.forName("net.minecraft.util.text.ITextComponent"); // string to packet methods
            this.serializeMessage = ReflectionHelper.findClass(textBaseComponent, "Serializer").getMethod("func_150699_a", String.class); // md: jsonToComponent
            this.chatPacketConstructor = Class.forName("net.minecraft.network.play.server.SPacketChat").getConstructor(textBaseComponent, byte.class);

            this.playerGetHandle = Class.forName("blue.lapis.pore.impl.entity.PorePlayer").getMethod("getHandle");
            this.playerConnection = Class.forName("net.minecraft.entity.player.EntityPlayerMP").getField("field_71135_a"); // get player connection fd: connection
            Class<?> packet = Class.forName("net.minecraft.network.Packet");
            this.sendPacket = playerConnection.getType().getMethod("func_147359_a", packet); //md: sendPacket
        }
    }

    public void sendRawMessage(String rawMessage, Player player) {
        this.sendMessage(rawMessage,player, (byte) 1);
    }

    public void sendActionBarMessage(String rawMessage, Player player) {
        this.sendMessage("{\"text\":\"" + rawMessage + "\"}",player, (byte) 2);
    }

    public void sendMessage(String rawMessage, Player player, byte msgType) {
        try {
            Object comp = this.serializeMessage.invoke(null, rawMessage);
            Object packet = this.chatPacketConstructor.newInstance(comp, this.chatMessageTypeMethod.invoke(null,msgType)); // convert bytes into packet

            Object handle = this.playerGetHandle.invoke(player);
            Object playerConnection = this.playerConnection.get(handle); // get players connection
            sendPacket.invoke(playerConnection, packet); // send packet
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            this.plugin.getLogger().warning("Error creating raw message, something must be wrong with reflection");
            e.printStackTrace();
        }
    }


    /**
     * Blocks the beacon from showing
     * @param block
     */
    /*public void setGatewayAgeHigh(Block block) {
        if(block.getWorld().getEnvironment() != World.Environment.THE_END &&
                this.endGatewayClass.isAssignableFrom(block.getState().getClass())) {
            try {
                Object tileEntity = this.getTileEntityMethod.invoke(this.getWorldHandleMethod.invoke(block.getWorld()),
                        this.blockPositionConstructor.newInstance(block.getX(), block.getY(), block.getZ()));
                if(this.tileEntityEndGatewayClass.isAssignableFrom(tileEntity.getClass())) {
                    getEntityTimeoutField.set(tileEntity, Integer.MAX_VALUE);
                }
            } catch (IllegalAccessException| InvocationTargetException | InstantiationException e) {
                this.plugin.getLogger().warning("Error setting gateway time");
                e.printStackTrace();
            }
        }
    }*/
}
