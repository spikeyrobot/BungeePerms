package net.alpenblock.bungeeperms;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.alpenblock.bungeeperms.Lang.MessageType;
import net.alpenblock.bungeeperms.platform.MessageEncoder;
import net.alpenblock.bungeeperms.platform.MessageEncoder.ClickEvent;
import net.alpenblock.bungeeperms.platform.MessageEncoder.HoverEvent;
import net.alpenblock.bungeeperms.platform.PlatformPlugin;
import net.alpenblock.bungeeperms.platform.Sender;

public class HelpProvider
{

    private static final int PAGE_SIZE = 7;
    private static final List<HelpEntry> helpentries = new ArrayList<>();

    static
    {
        helpentries.add(new HelpEntry(null,/*                                   */ makeClickCommand("/bp", Lang.translate(MessageType.HELP_WELCOME))));
        helpentries.add(new HelpEntry("bungeeperms.help",/*                     */ makeSuggestCommand("/bp help [page]", Lang.translate(MessageType.HELP_HELP))));
        helpentries.add(new HelpEntry("bungeeperms.reload",/*                   */ makeClickCommand("/bp reload", Lang.translate(MessageType.HELP_RELOAD))));
        helpentries.add(new HelpEntry("bungeeperms.debug",/*                    */ makeSuggestCommand("/bp debug <true|false>", Lang.translate(MessageType.HELP_DEBUG))));
        helpentries.add(new HelpEntry("bungeeperms.users",/*                    */ makeSuggestCommand("/bp users [-c]", Lang.translate(MessageType.HELP_USERS))));
        helpentries.add(new HelpEntry("bungeeperms.user.info",/*                */ makeSuggestCommand("/bp user <user> info", Lang.translate(MessageType.HELP_USER_INFO))));
        helpentries.add(new HelpEntry("bungeeperms.user.delete",/*              */ makeSuggestCommand("/bp user <user> delete", Lang.translate(MessageType.HELP_USER_DELETE))));
        helpentries.add(new HelpEntry("bungeeperms.user.display",/*             */ makeSuggestCommand("/bp user <user> display [displayname [server [world]]]", Lang.translate(MessageType.HELP_USER_DISPLAY))));
        helpentries.add(new HelpEntry("bungeeperms.user.prefix",/*              */ makeSuggestCommand("/bp user <user> prefix [prefix [server [world]]]", Lang.translate(MessageType.HELP_USER_PREFIX))));
        helpentries.add(new HelpEntry("bungeeperms.user.suffix",/*              */ makeSuggestCommand("/bp user <user> suffix [suffix [server [world]]]", Lang.translate(MessageType.HELP_USER_SUFFIX))));
        helpentries.add(new HelpEntry("bungeeperms.user.perms.add",/*           */ makeSuggestCommand("/bp user <user> addperm <perm> [server [world]]", Lang.translate(MessageType.HELP_USER_ADDPERM))));
        helpentries.add(new HelpEntry("bungeeperms.user.perms.remove",/*        */ makeSuggestCommand("/bp user <user> removeperm <perm> [server [world]]", Lang.translate(MessageType.HELP_USER_REMOVEPERM))));
        helpentries.add(new HelpEntry("bungeeperms.user.perms.has",/*           */ makeSuggestCommand("/bp user <user> has <perm> [server [world]]",Lang.translate(MessageType.HELP_USER_HAS))));
        helpentries.add(new HelpEntry("bungeeperms.user.perms.list",/*          */ makeSuggestCommand("/bp user <user> list", Lang.translate(MessageType.HELP_USER_LIST))));
        helpentries.add(new HelpEntry("bungeeperms.user.group.add",/*           */ makeSuggestCommand("/bp user <user> addgroup <group>",Lang.translate(MessageType.HELP_USER_ADDGROUP))));
        helpentries.add(new HelpEntry("bungeeperms.user.group.remove",/*        */ makeSuggestCommand("/bp user <user> removegroup <group>", Lang.translate(MessageType.HELP_USER_REMOVEGROUP))));
        helpentries.add(new HelpEntry("bungeeperms.user.group.set",/*           */ makeSuggestCommand("/bp user <user> setgroup <group>", Lang.translate(MessageType.HELP_USER_SETGROUP))));
        helpentries.add(new HelpEntry("bungeeperms.user.groups",/*              */ makeSuggestCommand("/bp user <user> groups", Lang.translate(MessageType.HELP_USER_GROUPS))));
        helpentries.add(new HelpEntry("bungeeperms.groups",/*                   */ makeClickCommand("/bp groups", Lang.translate(MessageType.HELP_GROUPS))));
        helpentries.add(new HelpEntry("bungeeperms.group.info",/*               */ makeSuggestCommand("/bp group <group> info", Lang.translate(MessageType.HELP_GROUP_INFO))));
        helpentries.add(new HelpEntry("bungeeperms.group.users",/*              */ makeSuggestCommand("/bp group <group> users [-c]", Lang.translate(MessageType.HELP_GROUP_USERS))));
        helpentries.add(new HelpEntry("bungeeperms.group.create",/*             */ makeSuggestCommand("/bp group <group> create", Lang.translate(MessageType.HELP_GROUP_CREATE))));
        helpentries.add(new HelpEntry("bungeeperms.group.delete",/*             */ makeSuggestCommand("/bp group <group> delete", Lang.translate(MessageType.HELP_GROUP_DELETE))));
        helpentries.add(new HelpEntry("bungeeperms.group.inheritances.add",/*   */ makeSuggestCommand("/bp group <group> addinherit <addgroup>", Lang.translate(MessageType.HELP_GROUP_ADDINHERIT))));
        helpentries.add(new HelpEntry("bungeeperms.group.inheritances.remove",/**/ makeSuggestCommand("/bp group <group> removeinherit <removegroup>", Lang.translate(MessageType.HELP_GROUP_REMOVEINHERIT))));
        helpentries.add(new HelpEntry("bungeeperms.group.rank",/*               */ makeSuggestCommand("/bp group <group> rank <new rank>", Lang.translate(MessageType.HELP_GROUP_RANK))));
        helpentries.add(new HelpEntry("bungeeperms.group.weight",/*             */ makeSuggestCommand("/bp group <group> weight <new weight>",Lang.translate(MessageType.HELP_GROUP_WEIGHT))));
        helpentries.add(new HelpEntry("bungeeperms.group.ladder",/*             */ makeSuggestCommand("/bp group <group> ladder <new ladder>", Lang.translate(MessageType.HELP_GROUP_LADDER))));
        helpentries.add(new HelpEntry("bungeeperms.group.default",/*            */ makeSuggestCommand("/bp group <group> default <true|false>", Lang.translate(MessageType.HELP_GROUP_DEFAULT))));
        helpentries.add(new HelpEntry("bungeeperms.group.display",/*            */ makeSuggestCommand("/bp group <group> display [displayname [server [world]]]",Lang.translate(MessageType.HELP_GROUP_DISPLAY))));
        helpentries.add(new HelpEntry("bungeeperms.group.prefix",/*             */ makeSuggestCommand("/bp group <group> prefix [prefix [server [world]]]", Lang.translate(MessageType.HELP_GROUP_PREFIX))));
        helpentries.add(new HelpEntry("bungeeperms.group.suffix",/*             */ makeSuggestCommand("/bp group <group> suffix [suffix [server [world]]]", Lang.translate(MessageType.HELP_GROUP_SUFFIX))));
        helpentries.add(new HelpEntry("bungeeperms.group.perms.add",/*          */ makeSuggestCommand("/bp group <group> addperm <perm> [server [world]]", Lang.translate(MessageType.HELP_GROUP_ADDPERM))));
        helpentries.add(new HelpEntry("bungeeperms.group.perms.remove",/*       */ makeSuggestCommand("/bp group <group> removeperm <perm> [server [world]]", Lang.translate(MessageType.HELP_GROUP_REMOVEPERM))));
        helpentries.add(new HelpEntry("bungeeperms.group.perms.has",/*          */ makeSuggestCommand("/bp group <group> has <perm> [server [world]]", Lang.translate(MessageType.HELP_GROUP_HAS))));
        helpentries.add(new HelpEntry("bungeeperms.group.perms.list",/*         */ makeSuggestCommand("/bp group <group> list",Lang.translate(MessageType.HELP_GROUP_LIST))));
        helpentries.add(new HelpEntry("bungeeperms.promote",/*                  */ makeSuggestCommand("/bp promote <user> [ladder]",Lang.translate(MessageType.HELP_PROMOTE))));
        helpentries.add(new HelpEntry("bungeeperms.demote",/*                   */ makeSuggestCommand("/bp demote <user> [ladder]", Lang.translate(MessageType.HELP_DEMOTE))));
        helpentries.add(new HelpEntry("bungeeperms.format",/*                   */ makeClickCommand("/bp format", Lang.translate(MessageType.HELP_FORMAT))));
        helpentries.add(new HelpEntry("bungeeperms.cleanup",/*                  */ makeClickCommand("/bp cleanup",Lang.translate(MessageType.HELP_CLEANUP))));
        helpentries.add(new HelpEntry("bungeeperms.migrate",/*                  */ makeSuggestCommand("/bp migrate <backend> [yaml|mysql|mysql2]", Lang.translate(MessageType.HELP_MIGRATE_BACKEND))));
        helpentries.add(new HelpEntry("bungeeperms.migrate",/*                  */ makeSuggestCommand("/bp migrate <useuuid> [true|false]", Lang.translate(MessageType.HELP_MIGRATE_USEUUID))));
        helpentries.add(new HelpEntry("bungeeperms.migrate",/*                  */ makeSuggestCommand("/bp migrate <uuidplayerdb> [None|YAML|MySQL]",Lang.translate(MessageType.HELP_MIGRATE_UUIDPLAYERDB))));
        helpentries.add(new HelpEntry("bungeeperms.uuid",/*                     */ makeSuggestCommand("/bp uuid <player|uuid> [-rm]", Lang.translate(MessageType.HELP_UUID))));
// template        helpentries.add(new HelpEntry(null, makeClickCommand("/bp help", "Shows").color(ChatColor.GRAY)));
    }

    private static MessageEncoder makeClickCommand(String cmd, String help)
    {
        return enc()
                //cmd
                .append(cmd)
                .color(ChatColor.GOLD)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, enc().append("Click to execute")))
                //reset
                .append("")
                .reset()
                //seperator
                .append(" - ")
                .color(ChatColor.WHITE)
                //help
                .append(help)
                .color(ChatColor.GRAY);
    }

    private static MessageEncoder makeSuggestCommand(String cmd, String help)
    {
        return enc()
                //cmd
                .append(cmd)
                .color(ChatColor.GOLD)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, enc().append("Click to suggest")))
                //reset
                .append("")
                .reset()
                //seperator
                .append(" - ")
                .color(ChatColor.WHITE)
                //help
                .append(help)
                .color(ChatColor.GRAY);
    }

    private static PlatformPlugin plugin()
    {
        return BungeePerms.getInstance().getPlugin();
    }

    private static MessageEncoder enc()
    {
        return plugin().newMessageEncoder();
    }

    public static void sendHelpHeader(Sender sender, int page)
    {
        sender.sendMessage(enc().append("                  ------ BungeePerms - Help - Page " + (page + 1) + " -----").color(ChatColor.GOLD));
        sender.sendMessage(enc().append("Aliases: ").color(ChatColor.GRAY).append("/bp").color(ChatColor.GOLD)
                .append("       ").color(ChatColor.GRAY).append("<required>").color(ChatColor.GOLD)
                .append("       ").color(ChatColor.GRAY).append("[optional]").color(ChatColor.GOLD));
    }

    public static void sendHelpPage(Sender sender, int page)
    {
        sendHelpHeader(sender, page);

        int index = -1;
        for (HelpEntry he : helpentries)
        {
            if (he.getPermission() != null && !BungeePerms.getInstance().getPermissionsChecker().hasPermOrConsole(sender, he.getPermission()))
            {
                continue;
            }

            index++;
            if (index < page * PAGE_SIZE)
            {
                continue;
            }
            else if (index < (page + 1) * PAGE_SIZE)
            {
                sender.sendMessage(he.getMessage());
            }
            else
            {
                break;
            }
        }
    }

    @Getter
    @AllArgsConstructor
    private static class HelpEntry
    {

        private final String permission;
        private final MessageEncoder message;
    }
}
