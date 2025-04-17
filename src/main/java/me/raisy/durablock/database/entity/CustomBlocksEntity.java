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

    @DatabaseField(columnName = "status", defaultValue = "enabled")
    private String status;

    @DatabaseField(columnName = "last_player", canBeNull = true)
    private String lastPlayer;

    @DatabaseField(columnName = "last_broken_date", canBeNull = true)
    private Long lastBrokenDate;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastPlayer() {
        return lastPlayer;
    }

    public void setLastPlayer(String lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    public Long getLastBrokenDate() {
        return lastBrokenDate;
    }

    public void setLastBrokenDate(long lastBrokenDate) {
        this.lastBrokenDate = lastBrokenDate;
    }
}
