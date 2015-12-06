package net.alpenblock.bungeeperms.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.alpenblock.bungeeperms.BPConfig;
import net.alpenblock.bungeeperms.BungeePerms;
import net.alpenblock.bungeeperms.Config;
import net.alpenblock.bungeeperms.Group;
import net.alpenblock.bungeeperms.Lang;
import net.alpenblock.bungeeperms.Server;
import net.alpenblock.bungeeperms.Statics;
import net.alpenblock.bungeeperms.User;
import net.alpenblock.bungeeperms.World;
import net.alpenblock.bungeeperms.platform.PlatformPlugin;

public class YAMLBackEnd implements BackEnd
{

    private final String permspath;
    private Config permsconf;

    private final PlatformPlugin plugin;
    private final BPConfig config;

    public YAMLBackEnd()
    {
        plugin = BungeePerms.getInstance().getPlugin();
        config = BungeePerms.getInstance().getConfig();

        permspath = "/permissions.yml";

        checkPermFile();

        permsconf = new Config(plugin, permspath);
    }

    @Override
    public BackEndType getType()
    {
        return BackEndType.YAML;
    }

    @Override
    public void load()
    {
        //load from table
        permsconf.load();
    }

    @Override
    public List<Group> loadGroups()
    {
        List<Group> ret = new ArrayList<>();

        List<String> groups = permsconf.getSubNodes("groups");
        for (String g : groups)
        {
            ret.add(loadGroup(g));
        }
        Collections.sort(ret);

        return ret;
    }

    @Override
    public List<User> loadUsers()
    {
        List<User> ret = new ArrayList<>();

        List<String> users = permsconf.getSubNodes("users");
        for (String u : users)
        {
            User user = BungeePerms.getInstance().getConfig().isUseUUIDs() ? loadUser(UUID.fromString(u)) : loadUser(u);
            ret.add(user);
        }

        return ret;
    }

    @Override
    public Group loadGroup(String group)
    {
        List<String> inheritances = permsconf.getListString("groups." + group + ".inheritances", new ArrayList<String>());
        List<String> permissions = permsconf.getListString("groups." + group + ".permissions", new ArrayList<String>());
        boolean isdefault = permsconf.getBoolean("groups." + group + ".default", false);
        int rank = permsconf.getInt("groups." + group + ".rank", 1000);
        int weight = permsconf.getInt("groups." + group + ".weight", 1000);
        String ladder = permsconf.getString("groups." + group + ".ladder", "default");
        String display = permsconf.getString("groups." + group + ".display", "");
        String prefix = permsconf.getString("groups." + group + ".prefix", "");
        String suffix = permsconf.getString("groups." + group + ".suffix", "");

        //per server perms
        Map<String, Server> servers = new HashMap<>();
        for (String server : permsconf.getSubNodes("groups." + group + ".servers"))
        {
            List<String> serverperms = permsconf.getListString("groups." + group + ".servers." + server + ".permissions", new ArrayList<String>());
            String sdisplay = permsconf.getString("groups." + group + ".servers." + server + ".display", "");
            String sprefix = permsconf.getString("groups." + group + ".servers." + server + ".prefix", "");
            String ssuffix = permsconf.getString("groups." + group + ".servers." + server + ".suffix", "");

            //per server world perms
            Map<String, World> worlds = new HashMap<>();
            for (String world : permsconf.getSubNodes("groups." + group + ".servers." + server + ".worlds"))
            {
                List<String> worldperms = permsconf.getListString("groups." + group + ".servers." + server + ".worlds." + world + ".permissions", new ArrayList<String>());
                String wdisplay = permsconf.getString("groups." + group + ".servers." + server + ".worlds." + world + ".display", "");
                String wprefix = permsconf.getString("groups." + group + ".servers." + server + ".worlds." + world + ".prefix", "");
                String wsuffix = permsconf.getString("groups." + group + ".servers." + server + ".worlds." + world + ".suffix", "");

                World w = new World(Statics.toLower(world), worldperms, wdisplay, wprefix, wsuffix);
                worlds.put(Statics.toLower(world), w);
            }

            servers.put(Statics.toLower(server), new Server(Statics.toLower(server), serverperms, worlds, sdisplay, sprefix, ssuffix));
        }

        Group g = new Group(group, inheritances, permissions, servers, rank, weight, ladder, isdefault, display, prefix, suffix);
        return g;
    }

    @Override
    public User loadUser(String user)
    {
        if (!permsconf.keyExists("users." + user))
        {
            return null;
        }

        //load user from database
        List<String> sgroups = permsconf.getListString("users." + user + ".groups", new ArrayList<String>());
        List<String> perms = permsconf.getListString("users." + user + ".permissions", new ArrayList<String>());
        String display = permsconf.getString("users." + user + ".display", "");
        String prefix = permsconf.getString("users." + user + ".prefix", "");
        String suffix = permsconf.getString("users." + user + ".suffix", "");

        List<Group> lgroups = new ArrayList<>();
        for (String s : sgroups)
        {
            Group g = BungeePerms.getInstance().getPermissionsManager().getGroup(s);
            if (g != null)
            {
                lgroups.add(g);
            }
        }

        //per server perms
        Map<String, Server> servers = new HashMap<>();
        for (String server : permsconf.getSubNodes("users." + user + ".servers"))
        {
            List<String> serverperms = permsconf.getListString("users." + user + ".servers." + server + ".permissions", new ArrayList<String>());
            String sdisplay = permsconf.getString("users." + user + ".servers." + server + ".display", "");
            String sprefix = permsconf.getString("users." + user + ".servers." + server + ".prefix", "");
            String ssuffix = permsconf.getString("users." + user + ".servers." + server + ".suffix", "");

            //per server world perms
            Map<String, World> worlds = new HashMap<>();
            for (String world : permsconf.getSubNodes("users." + user + ".servers." + server + ".worlds"))
            {
                List<String> worldperms = permsconf.getListString("users." + user + ".servers." + server + ".worlds." + world + ".permissions", new ArrayList<String>());
                String wdisplay = permsconf.getString("users." + user + ".servers." + server + ".worlds." + world + ".display", "");
                String wprefix = permsconf.getString("users." + user + ".servers." + server + ".worlds." + world + ".prefix", "");
                String wsuffix = permsconf.getString("users." + user + ".servers." + server + ".worlds." + world + ".suffix", "");

                World w = new World(Statics.toLower(world), worldperms, wdisplay, wprefix, wsuffix);
                worlds.put(Statics.toLower(world), w);
            }

            servers.put(Statics.toLower(server), new Server(Statics.toLower(server), serverperms, worlds, sdisplay, sprefix, ssuffix));
        }

        UUID uuid = BungeePerms.getInstance().getPermissionsManager().getUUIDPlayerDB().getUUID(user);
        User u = new User(user, uuid, lgroups, perms, servers, display, prefix, suffix);
        return u;
    }

    @Override
    public User loadUser(UUID user)
    {
        if (!permsconf.keyExists("users." + user))
        {
            return null;
        }

        //load user from database
        List<String> sgroups = permsconf.getListString("users." + user + ".groups", new ArrayList<String>());
        List<String> perms = permsconf.getListString("users." + user + ".permissions", new ArrayList<String>());
        String display = permsconf.getString("users." + user + ".display", "");
        String prefix = permsconf.getString("users." + user + ".prefix", "");
        String suffix = permsconf.getString("users." + user + ".suffix", "");

        List<Group> lgroups = new ArrayList<>();
        for (String s : sgroups)
        {
            Group g = BungeePerms.getInstance().getPermissionsManager().getGroup(s);
            if (g != null)
            {
                lgroups.add(g);
            }
        }

        //per server perms
        Map<String, Server> servers = new HashMap<>();
        for (String server : permsconf.getSubNodes("users." + user + ".servers"))
        {
            List<String> serverperms = permsconf.getListString("users." + user + ".servers." + server + ".permissions", new ArrayList<String>());
            String sdisplay = permsconf.getString("users." + user + ".servers." + server + ".display", "");
            String sprefix = permsconf.getString("users." + user + ".servers." + server + ".prefix", "");
            String ssuffix = permsconf.getString("users." + user + ".servers." + server + ".suffix", "");

            //per server world perms
            Map<String, World> worlds = new HashMap<>();
            for (String world : permsconf.getSubNodes("users." + user + ".servers." + server + ".worlds"))
            {
                List<String> worldperms = permsconf.getListString("users." + user + ".servers." + server + ".worlds." + world + ".permissions", new ArrayList<String>());
                String wdisplay = permsconf.getString("users." + user + ".servers." + server + ".worlds." + world + ".display", "");
                String wprefix = permsconf.getString("users." + user + ".servers." + server + ".worlds." + world + ".prefix", "");
                String wsuffix = permsconf.getString("users." + user + ".servers." + server + ".worlds." + world + ".suffix", "");

                World w = new World(Statics.toLower(world), worldperms, wdisplay, wprefix, wsuffix);
                worlds.put(Statics.toLower(world), w);
            }

            servers.put(Statics.toLower(server), new Server(Statics.toLower(server), serverperms, worlds, sdisplay, sprefix, ssuffix));
        }

        String username = BungeePerms.getInstance().getPermissionsManager().getUUIDPlayerDB().getPlayerName(user);
        User u = new User(username, user, lgroups, perms, servers, display, prefix, suffix);
        return u;
    }

    @Override
    public int loadVersion()
    {
        return permsconf.getInt("version", 1);
    }

    @Override
    public void saveVersion(int version, boolean savetodisk)
    {
        permsconf.setInt("version", version);

        if (savetodisk)
        {
            permsconf.save();
        }
    }

    @Override
    public boolean isUserInDatabase(User user)
    {
        return permsconf.keyExists("users." + (BungeePerms.getInstance().getConfig().isUseUUIDs() ? user.getUUID().toString() : user.getName()));
    }

    private void checkPermFile()
    {
        File f = new File(plugin.getPluginFolder(), permspath);
        if (!f.exists() | !f.isFile())
        {
            BungeePerms.getLogger().info(Lang.translate(Lang.MessageType.NO_PERM_FILE));
        }
    }

    @Override
    public List<String> getRegisteredUsers()
    {
        return permsconf.getSubNodes("users");
    }

    @Override
    public List<String> getGroupUsers(Group group)
    {
        List<String> users = new ArrayList<>();

        for (String user : permsconf.getSubNodes("users"))
        {
            if (permsconf.getListString("users." + user + ".groups", new ArrayList<String>()).contains(group.getName()))
            {
                users.add(user);
            }
        }

        return users;
    }

    @Override
    public synchronized void saveUser(User user, boolean savetodisk)
    {
        if (BungeePerms.getInstance().getConfig().isSaveAllUsers() ? true : !user.isNothingSpecial())
        {
            List<String> groups = new ArrayList<>();
            for (Group g : user.getGroups())
            {
                groups.add(g.getName());
            }

            String uname = BungeePerms.getInstance().getConfig().isUseUUIDs() ? user.getUUID().toString() : user.getName();

            permsconf.setListString("users." + uname + ".groups", groups);
            permsconf.setListString("users." + uname + ".permissions", user.getExtraPerms());

            for (Map.Entry<String, Server> se : user.getServers().entrySet())
            {
                permsconf.setListString("users." + uname + ".servers." + se.getKey() + ".permissions", se.getValue().getPerms());
                permsconf.setString("users." + uname + ".servers." + se.getKey() + ".display", se.getValue().getDisplay());
                permsconf.setString("users." + uname + ".servers." + se.getKey() + ".prefix", se.getValue().getPrefix());
                permsconf.setString("users." + uname + ".servers." + se.getKey() + ".suffix", se.getValue().getSuffix());

                for (Map.Entry<String, World> we : se.getValue().getWorlds().entrySet())
                {
                    permsconf.setListString("users." + uname + ".servers." + se.getKey() + ".worlds." + we.getKey() + ".permissions", we.getValue().getPerms());
                    permsconf.setString("users." + uname + ".servers." + se.getKey() + ".worlds." + we.getKey() + ".display", we.getValue().getDisplay());
                    permsconf.setString("users." + uname + ".servers." + se.getKey() + ".worlds." + we.getKey() + ".prefix", we.getValue().getPrefix());
                    permsconf.setString("users." + uname + ".servers." + se.getKey() + ".worlds." + we.getKey() + ".suffix", we.getValue().getSuffix());
                }
            }
        }
    }

    @Override
    public synchronized void saveGroup(Group group, boolean savetodisk)
    {
        permsconf.setListString("groups." + group.getName() + ".inheritances", group.getInheritances());
        permsconf.setListString("groups." + group.getName() + ".permissions", group.getPerms());
        permsconf.setInt("groups." + group.getName() + ".rank", group.getRank());
        permsconf.setString("groups." + group.getName() + ".ladder", group.getLadder());
        permsconf.setBool("groups." + group.getName() + ".default", group.isDefault());
        permsconf.setString("groups." + group.getName() + ".display", group.getDisplay());
        permsconf.setString("groups." + group.getName() + ".prefix", group.getPrefix());
        permsconf.setString("groups." + group.getName() + ".suffix", group.getSuffix());

        for (Map.Entry<String, Server> se : group.getServers().entrySet())
        {
            permsconf.setListString("groups." + group.getName() + ".servers." + se.getKey() + ".permissions", se.getValue().getPerms());
            permsconf.setString("groups." + group.getName() + ".servers." + se.getKey() + ".display", se.getValue().getDisplay());
            permsconf.setString("groups." + group.getName() + ".servers." + se.getKey() + ".prefix", se.getValue().getPrefix());
            permsconf.setString("groups." + group.getName() + ".servers." + se.getKey() + ".suffix", se.getValue().getSuffix());

            for (Map.Entry<String, World> we : se.getValue().getWorlds().entrySet())
            {
                permsconf.setListString("groups." + group.getName() + ".servers." + se.getKey() + ".worlds." + we.getKey() + ".permissions", we.getValue().getPerms());
                permsconf.setString("groups." + group.getName() + ".servers." + se.getKey() + ".worlds." + we.getKey() + ".display", we.getValue().getDisplay());
                permsconf.setString("groups." + group.getName() + ".servers." + se.getKey() + ".worlds." + we.getKey() + ".prefix", we.getValue().getPrefix());
                permsconf.setString("groups." + group.getName() + ".servers." + se.getKey() + ".worlds." + we.getKey() + ".suffix", we.getValue().getSuffix());
            }
        }

        if (savetodisk)
        {
            permsconf.save();
        }
    }

    @Override
    public synchronized void deleteUser(User user)
    {
        permsconf.deleteNode("users." + (BungeePerms.getInstance().getConfig().isUseUUIDs() ? user.getUUID().toString() : user.getName()));
    }

    @Override
    public synchronized void deleteGroup(Group group)
    {
        permsconf.deleteNode("groups." + group.getName());
    }

    @Override
    public synchronized void saveUserGroups(User user)
    {
        List<String> savegroups = new ArrayList<>();
        for (Group g : user.getGroups())
        {
            savegroups.add(g.getName());
        }

        permsconf.setListStringAndSave("users." + (BungeePerms.getInstance().getConfig().isUseUUIDs() ? user.getUUID().toString() : user.getName()) + ".groups", savegroups);
    }

    @Override
    public synchronized void saveUserPerms(User user)
    {
        permsconf.setListStringAndSave("users." + (BungeePerms.getInstance().getConfig().isUseUUIDs() ? user.getUUID().toString() : user.getName()) + ".permissions", user.getExtraPerms());
    }

    @Override
    public synchronized void saveUserPerServerPerms(User user, String server)
    {
        server = Statics.toLower(server);

        permsconf.setListStringAndSave("users." + (BungeePerms.getInstance().getConfig().isUseUUIDs() ? user.getUUID().toString() : user.getName()) + ".servers." + server + ".permissions", user.getServer(server).getPerms());
    }

    @Override
    public synchronized void saveUserPerServerWorldPerms(User user, String server, String world)
    {
        server = Statics.toLower(server);
        world = Statics.toLower(world);
        
        permsconf.setListStringAndSave("users." + (BungeePerms.getInstance().getConfig().isUseUUIDs() ? user.getUUID().toString() : user.getName()) + ".servers." + server + ".worlds." + world + ".permissions", user.getServer(server).getWorld(world).getPerms());
    }

    @Override
    public synchronized void saveUserDisplay(User user, String server, String world)
    {
        server = Statics.toLower(server);
        world = Statics.toLower(world);
        
        permsconf.setStringAndSave("users." + (BungeePerms.getInstance().getConfig().isUseUUIDs() ? user.getUUID().toString() : user.getName()) + (server != null ? ".servers." + server + (world != null ? ".worlds." + world : "") : "") + ".display", user.getDisplay());
    }

    @Override
    public synchronized void saveUserPrefix(User user, String server, String world)
    {
        server = Statics.toLower(server);
        world = Statics.toLower(world);
        
        permsconf.setStringAndSave("users." + (BungeePerms.getInstance().getConfig().isUseUUIDs() ? user.getUUID().toString() : user.getName()) + (server != null ? ".servers." + server + (world != null ? ".worlds." + world : "") : "") + ".prefix", user.getPrefix());
    }

    @Override
    public synchronized void saveUserSuffix(User user, String server, String world)
    {
        server = Statics.toLower(server);
        world = Statics.toLower(world);
        
        permsconf.setStringAndSave("users." + (BungeePerms.getInstance().getConfig().isUseUUIDs() ? user.getUUID().toString() : user.getName()) + (server != null ? ".servers." + server + (world != null ? ".worlds." + world : "") : "") + ".suffix", user.getSuffix());
    }

    @Override
    public synchronized void saveGroupPerms(Group group)
    {
        permsconf.setListStringAndSave("groups." + group.getName() + ".permissions", group.getPerms());
    }

    @Override
    public synchronized void saveGroupPerServerPerms(Group group, String server)
    {
        server = Statics.toLower(server);

        permsconf.setListStringAndSave("groups." + group.getName() + ".servers." + server + ".permissions", group.getServer(server).getPerms());
    }

    @Override
    public synchronized void saveGroupPerServerWorldPerms(Group group, String server, String world)
    {
        server = Statics.toLower(server);
        world = Statics.toLower(world);
        
        permsconf.setListStringAndSave("groups." + group.getName() + ".servers." + server + ".worlds." + world + ".permissions", group.getServer(server).getWorld(world).getPerms());
    }

    @Override
    public synchronized void saveGroupInheritances(Group group)
    {
        permsconf.setListStringAndSave("groups." + group.getName() + ".inheritances", group.getInheritances());
    }

    @Override
    public synchronized void saveGroupLadder(Group group)
    {
        permsconf.setStringAndSave("groups." + group.getName() + ".ladder", group.getLadder());
    }

    @Override
    public synchronized void saveGroupRank(Group group)
    {
        permsconf.setIntAndSave("groups." + group.getName() + ".rank", group.getRank());
    }

    @Override
    public synchronized void saveGroupWeight(Group group)
    {
        permsconf.setIntAndSave("groups." + group.getName() + ".weight", group.getWeight());
    }

    @Override
    public synchronized void saveGroupDefault(Group group)
    {
        permsconf.setBoolAndSave("groups." + group.getName() + ".default", group.isDefault());
    }

    @Override
    public synchronized void saveGroupDisplay(Group group, String server, String world)
    {
        server = Statics.toLower(server);
        world = Statics.toLower(world);
        
        permsconf.setStringAndSave("groups." + group.getName() + (server != null ? ".servers." + server + (world != null ? ".worlds." + world : "") : "") + ".display", group.getDisplay());
    }

    @Override
    public synchronized void saveGroupPrefix(Group group, String server, String world)
    {
        server = Statics.toLower(server);
        world = Statics.toLower(world);
        
        permsconf.setStringAndSave("groups." + group.getName() + (server != null ? ".servers." + server + (world != null ? ".worlds." + world : "") : "") + ".prefix", group.getPrefix());
    }

    @Override
    public synchronized void saveGroupSuffix(Group group, String server, String world)
    {
        server = Statics.toLower(server);
        world = Statics.toLower(world);
        
        permsconf.setStringAndSave("groups." + group.getName() + (server != null ? ".servers." + server + (world != null ? ".worlds." + world : "") : "") + ".suffix", group.getSuffix());
    }

    @Override
    public synchronized void format(List<Group> groups, List<User> users, int version)
    {
        clearDatabase();
        for (int i = 0; i < groups.size(); i++)
        {
            saveGroup(groups.get(i), false);
        }
        for (int i = 0; i < users.size(); i++)
        {
            saveUser(users.get(i), false);
        }
        saveVersion(version, false);

        permsconf.save();
    }

    @Override
    public synchronized int cleanup(List<Group> groups, List<User> users, int version)
    {
        int deleted = 0;

        clearDatabase();
        for (int i = 0; i < groups.size(); i++)
        {
            saveGroup(groups.get(i), false);
        }
        for (int i = 0; i < users.size(); i++)
        {
            User u = users.get(i);
            if (BungeePerms.getInstance().getConfig().isDeleteUsersOnCleanup())
            {
                //check for additional permissions and non-default groups AND onlinecheck
                if (u.isNothingSpecial()
                        && BungeePerms.getInstance().getPlugin().getPlayer(u.getName()) == null
                        && BungeePerms.getInstance().getPlugin().getPlayer(u.getUUID()) == null)
                {
                    deleted++;
                    continue;
                }
            }

            //player has to be saved
            saveUser(users.get(i), false);
        }
        saveVersion(version, false);

        permsconf.save();

        return deleted;
    }

    @Override
    public void clearDatabase()
    {
        new File(BungeePerms.getInstance().getPlugin().getPluginFolder() + permspath).delete();
        permsconf = new Config(BungeePerms.getInstance().getPlugin(), permspath);
    }

    @Override
    public void reloadGroup(Group group)
    {
        permsconf.load();

        //load group from database
        List<String> inheritances = permsconf.getListString("groups." + group.getName() + ".inheritances", new ArrayList<String>());
        List<String> permissions = permsconf.getListString("groups." + group.getName() + ".permissions", new ArrayList<String>());
        boolean isdefault = permsconf.getBoolean("groups." + group.getName() + ".default", false);
        int rank = permsconf.getInt("groups." + group.getName() + ".rank", 1000);
        int weight = permsconf.getInt("groups." + group.getName() + ".weight", 1000);
        String ladder = permsconf.getString("groups." + group.getName() + ".ladder", "default");
        String display = permsconf.getString("groups." + group.getName() + ".display", "");
        String prefix = permsconf.getString("groups." + group.getName() + ".prefix", "");
        String suffix = permsconf.getString("groups." + group.getName() + ".suffix", "");

        //per server perms
        Map<String, Server> servers = new HashMap<>();
        for (String server : permsconf.getSubNodes("groups." + group.getName() + ".servers"))
        {
            List<String> serverperms = permsconf.getListString("groups." + group.getName() + ".servers." + server + ".permissions", new ArrayList<String>());
            String sdisplay = permsconf.getString("groups." + group.getName() + ".servers." + server + ".display", "");
            String sprefix = permsconf.getString("groups." + group.getName() + ".servers." + server + ".prefix", "");
            String ssuffix = permsconf.getString("groups." + group.getName() + ".servers." + server + ".suffix", "");

            //per server world perms
            Map<String, World> worlds = new HashMap<>();
            for (String world : permsconf.getSubNodes("groups." + group.getName() + ".servers." + server + ".worlds"))
            {
                List<String> worldperms = permsconf.getListString("groups." + group.getName() + ".servers." + server + ".worlds." + world + ".permissions", new ArrayList<String>());
                String wdisplay = permsconf.getString("groups." + group.getName() + ".servers." + server + ".worlds." + world + ".display", "");
                String wprefix = permsconf.getString("groups." + group.getName() + ".servers." + server + ".worlds." + world + ".prefix", "");
                String wsuffix = permsconf.getString("groups." + group.getName() + ".servers." + server + ".worlds." + world + ".suffix", "");

                World w = new World(Statics.toLower(world), worldperms, wdisplay, wprefix, wsuffix);
                worlds.put(Statics.toLower(world), w);
            }

            servers.put(Statics.toLower(server), new Server(Statics.toLower(server), serverperms, worlds, sdisplay, sprefix, ssuffix));
        }

        group.setInheritances(inheritances);
        group.setPerms(permissions);
        group.setIsdefault(isdefault);
        group.setRank(rank);
        group.setWeight(weight);
        group.setLadder(ladder);
        group.setDisplay(display);
        group.setPrefix(prefix);
        group.setSuffix(suffix);
        group.setServers(servers);
    }

    @Override
    public void reloadUser(User user)
    {
        permsconf.load();

        String uname = config.isUseUUIDs() ? user.getUUID().toString() : user.getName();

        //load user from database
        List<String> sgroups = permsconf.getListString("users." + uname + ".groups", new ArrayList<String>());
        List<String> perms = permsconf.getListString("users." + uname + ".permissions", new ArrayList<String>());
        String display = permsconf.getString("users." + uname + ".display", "");
        String prefix = permsconf.getString("users." + uname + ".prefix", "");
        String suffix = permsconf.getString("users." + uname + ".suffix", "");

        List<Group> lgroups = new ArrayList<>();
        for (String s : sgroups)
        {
            Group g = BungeePerms.getInstance().getPermissionsManager().getGroup(s);
            if (g != null)
            {
                lgroups.add(g);
            }
        }

        //per server perms
        Map<String, Server> servers = new HashMap<>();
        for (String server : permsconf.getSubNodes("users." + uname + ".servers"))
        {
            List<String> serverperms = permsconf.getListString("users." + uname + ".servers." + server + ".permissions", new ArrayList<String>());
            String sdisplay = permsconf.getString("users." + uname + ".servers." + server + ".display", "");
            String sprefix = permsconf.getString("users." + uname + ".servers." + server + ".prefix", "");
            String ssuffix = permsconf.getString("users." + uname + ".servers." + server + ".suffix", "");

            //per server world perms
            Map<String, World> worlds = new HashMap<>();
            for (String world : permsconf.getSubNodes("users." + uname + ".servers." + server + ".worlds"))
            {
                List<String> worldperms = permsconf.getListString("users." + uname + ".servers." + server + ".worlds." + world + ".permissions", new ArrayList<String>());
                String wdisplay = permsconf.getString("users." + uname + ".servers." + server + ".worlds." + world + ".display", "");
                String wprefix = permsconf.getString("users." + uname + ".servers." + server + ".worlds." + world + ".prefix", "");
                String wsuffix = permsconf.getString("users." + uname + ".servers." + server + ".worlds." + world + ".suffix", "");

                World w = new World(Statics.toLower(world), worldperms, wdisplay, wprefix, wsuffix);
                worlds.put(Statics.toLower(world), w);
            }

            servers.put(Statics.toLower(server), new Server(Statics.toLower(server), serverperms, worlds, sdisplay, sprefix, ssuffix));
        }

        user.setGroups(lgroups);
        user.setExtraPerms(perms);
        user.setDisplay(display);
        user.setPrefix(prefix);
        user.setSuffix(suffix);
        user.setServers(servers);
    }
}
