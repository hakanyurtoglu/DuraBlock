package me.raisy.durablock.database.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "blocks")
public class CustomBlocksEntity {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(unique = true)
    private String location;

    @DatabaseField(columnName = "current_durability")
    private int currentDurability;

    @DatabaseField(columnName = "block_type")
    private String blockType;


    public CustomBlocksEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCurrentDurability() {
        return currentDurability;
    }

    public void setCurrentDurability(int currentDurability) {
        this.currentDurability = currentDurability;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }
}
