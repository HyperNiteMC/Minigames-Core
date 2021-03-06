package com.ericlam.mc.minigames.core.implement;

import com.ericlam.mc.minigames.core.commands.TpsRecordCommand;
import com.hypernite.mc.hnmc.core.main.HyperNiteMC;
import com.hypernite.mc.hnmc.core.managers.SQLDataSource;
import com.hypernite.mc.hnmc.core.misc.permission.Perm;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimingsSender implements CommandSender {

    private static final Pattern timingsURLPattern = Pattern.compile("(https)\\S+");
    private final String createTable = "CREATE TABLE IF NOT EXISTS `Timings` (`id` INT PRIMARY KEY AUTO_INCREMENT , `link` TINYTEXT NOT NULL, `timestamp` LONG NOT NULL )";
    private final String insertInto = "INSERT IGNORE INTO `Timings` (`link`, `timestamp`) VALUES (?, ?)";


    private final SQLDataSource sqlDataSource;

    public TimingsSender() {
        this.sqlDataSource = HyperNiteMC.getAPI().getSQLDataSource();
        CompletableFuture.runAsync(() -> {
            try (Connection connection = sqlDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(createTable)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void sendMessage(@Nonnull String s) {
        Matcher matcher = timingsURLPattern.matcher(s);
        if (matcher.find()) {
            String url = matcher.group();
            this.uploadToMySQL(url).whenComplete((id, ex) -> {
                if (ex != null) {
                    TpsRecordCommand.reportTo.stream().filter(sender -> sender instanceof TimingsSender).forEach(commandSender -> commandSender.sendMessage(ex.getMessage()));
                    ex.printStackTrace();
                    return;
                }
                TpsRecordCommand.reportTo.forEach(sender -> {
                    if (sender.hasPermission(Perm.MOD)) {
                        sender.sendMessage(s);
                    } else {
                        if (id == -1) {
                            sender.sendMessage("??c??????????????? Timings ??????????????????????????????");
                            return;
                        }
                        sender.sendMessage("??aTimings ?????? Id: " + id + ", ?????? Id ?????? ?????????????????????????????????");
                    }
                });
            });
        } else {
            TpsRecordCommand.reportTo.forEach(sender -> sender.sendMessage(s));
        }
    }

    private CompletableFuture<Integer> uploadToMySQL(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = sqlDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertInto, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, url);
                statement.setLong(2, System.currentTimeMillis());
                if (statement.executeUpdate() > 0) {
                    ResultSet set = statement.getGeneratedKeys();
                    if (set.next()) {
                        return set.getInt(1);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1;
        });
    }

    @Override
    public void sendMessage(@Nonnull String[] strings) {
        for (String string : strings) {
            this.sendMessage(string);
        }
    }

    @Nonnull
    @Override
    public Server getServer() {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Nonnull
    @Override
    public String getName() {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Nonnull
    @Override
    public Spigot spigot() {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public boolean isPermissionSet(String s) {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public boolean hasPermission(String s) {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public boolean hasPermission(Permission permission) {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public void recalculatePermissions() {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public boolean isOp() {
        throw new UnsupportedOperationException("Only for timings");
    }

    @Override
    public void setOp(boolean b) {
        throw new UnsupportedOperationException("Only for timings");
    }
}
