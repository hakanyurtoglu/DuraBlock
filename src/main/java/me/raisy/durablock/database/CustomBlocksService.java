package me.raisy.durablock.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import me.raisy.durablock.model.CustomBlock;
import me.raisy.durablock.util.LocationUtil;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.List;

public class CustomBlocksService {
    private final Dao<CustomBlocksEntity, Integer> customBlocksDao;
    private final DuraBlockPlugin plugin;

    public CustomBlocksService(String path, DuraBlockPlugin plugin) throws SQLException {
        this.plugin = plugin;

        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);
        Logger.setGlobalLogLevel(Level.ERROR);

        TableUtils.createTableIfNotExists(connectionSource, CustomBlocksEntity.class);
        this.customBlocksDao = DaoManager.createDao(connectionSource, CustomBlocksEntity.class);
    }

    public List<CustomBlocksEntity> getAllCustomBlocks() throws SQLException {
        return customBlocksDao.queryForAll();
    }

    public void addCustomBlock(CustomBlock customBlock) throws SQLException {
        String locationString = LocationUtil.locationToString(customBlock.getLocation());
        CustomBlocksEntity customBlocks = new CustomBlocksEntity();

        customBlocks.setBlockType(customBlock.getBlockType().getName());
        customBlocks.setLocation(locationString);
        customBlocks.setCurrentDurability(customBlock.getBlockType().getDefaultDurability());

        customBlocksDao.create(customBlocks);
    }

    public void addCustomBlock(CustomBlocksEntity customBlocksEntity) throws SQLException {
        customBlocksDao.create(customBlocksEntity);
    }

    public void restoreCustomBlock(CustomBlocksEntity customBlocksEntity, int defaultDurability) throws SQLException {
        customBlocksEntity.setStatus("enabled");
        customBlocksEntity.setCurrentDurability(defaultDurability);
        customBlocksDao.update(customBlocksEntity);
    }

    public void restoreAllCustomBlocks() throws SQLException {
        List<CustomBlocksEntity> allCustomBlocks = getAllCustomBlocks();

        for (CustomBlocksEntity customBlocksEntity : allCustomBlocks) {
            int defaultDurability = plugin.getBlockTypes().get(customBlocksEntity.getBlockType()).getDefaultDurability();

            customBlocksEntity.setStatus("enabled");
            customBlocksEntity.setCurrentDurability(defaultDurability);
            customBlocksDao.update(customBlocksEntity);
        }
    }

    public boolean isBlockExists(Location blockLocation) throws SQLException {
        String locationString = LocationUtil.locationToString(blockLocation);
        List<CustomBlocksEntity> blockExists = customBlocksDao.queryForEq("location", locationString);
        return !blockExists.isEmpty();
    }

    public CustomBlocksEntity getBlock(Location blockLocation) throws SQLException {
        String locationString = LocationUtil.locationToString(blockLocation);
        return customBlocksDao.queryBuilder().where().eq("location", locationString).queryForFirst();
    }

    public CustomBlocksEntity getBlockById(int blockId) throws SQLException {
        return customBlocksDao.queryForId(blockId);
    }

    public void updateCustomBlock(CustomBlocksEntity customBlocksEntity) throws SQLException {
        customBlocksDao.update(customBlocksEntity);
    }

    public boolean removeCustomBlockById(int blockId) throws SQLException {
        return customBlocksDao.deleteById(blockId) > 0;
    }

    public void removeCustomBlock(CustomBlocksEntity customBlocks) throws SQLException {
        customBlocksDao.delete(customBlocks);
    }

}
