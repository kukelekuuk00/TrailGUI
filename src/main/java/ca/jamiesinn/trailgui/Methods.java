package ca.jamiesinn.trailgui;

import ca.jamiesinn.trailgui.files.Userdata;
import ca.jamiesinn.trailgui.trails.Trail;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Methods
{
    public static void clearTrails(Player player)
    {
        if(Main.enabledTrails.containsKey(player.getUniqueId()))
        {
            Main.enabledTrails.remove(player.getUniqueId());
        }
    }

    public static void saveTrails()
    {
        FileConfiguration config = Userdata.getInstance().getConfig();
        for(String key : config.getKeys(false))
        {
            config.set(key, null);
        }

        for(UUID key : Main.enabledTrails.keySet())
        {
            List<String> trailNames = new ArrayList<String>();
            for(Trail trail : Main.enabledTrails.get(key))
            {
                trailNames.add(trail.getName());
            }
            config.set(key.toString(), trailNames);
        }
        Userdata.getInstance().saveConfig();
    }

    public static void restoreTrails()
    {
        FileConfiguration config = Userdata.getInstance().getConfig();
        for(String key : config.getKeys(false))
        {
            List<String> trails = config.getStringList(key);
            if(!trails.isEmpty())
            {
                List<Trail> trailTypes = new ArrayList<Trail>();
                for(String trail : trails)
                {
                    trailTypes.add(Main.trailTypes.get(trail));
                }
                Main.enabledTrails.put(UUID.fromString(key), trailTypes);
            }
        }
    }

    public static void openGUI(Player player)
    {
        openGUI(player, 1);
    }

    public static void openGUI(Player player, int currentPage)
    {

        int pageSize = 27;
        List<Trail> sortedList = new ArrayList<Trail>(Main.trailTypes.values());
        Collections.sort(sortedList, new orderComparator());
        int maxPages = (int) Math.ceil((double) sortedList.size() / pageSize);
        currentPage = currentPage > maxPages ? 1 : currentPage;
        int begin = (pageSize * currentPage) - pageSize;
        int end = begin + pageSize;
        if(end > sortedList.size())
        {
            end = sortedList.size();
        }
        List<Trail> subList = sortedList.subList(begin, end);

        Inventory inv = Bukkit.createInventory(null, 45, Main.getPlugin().getConfig().getString("inventoryName").replaceAll("&", "\u00A7") + " " + currentPage + " / " + maxPages);
        int i = 0;
        for(Trail trail : subList)
        {
            if(trail.canUseInventory(player))
            {
                inv.setItem(i, trail.getItem());
            }
            else
            {
                inv.setItem(i, itemNoPerms());
            }
            i++;
        }

        if(currentPage > 1)
        {
            inv.setItem(Main.getPlugin().getConfig().getInt("PreviousPage-inventorySlot"), getItemPreviousPage());
        }
        inv.setItem(Main.getPlugin().getConfig().getInt("RemoveTrails-inventorySlot"), getItemRemoveTrails());
        if(currentPage < maxPages)
        {
            inv.setItem(Main.getPlugin().getConfig().getInt("NextPage-inventorySlot"), getItemNextPage());
        }
        player.openInventory(inv);

    }

    //region Item's for Inventory Slots
    public static ItemStack itemNoPerms()
    {
        ItemStack i = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "You do not have this trail!");
        i.setItemMeta(meta);
        return i;

    }

    private static ItemStack itemNextPage;

    public static ItemStack getItemNextPage()
    {
        if(itemNextPage == null)
        {
            itemNextPage = createItemNextPage();
        }
        return itemNextPage;
    }

    public static ItemStack createItemNextPage()
    {
        ItemStack itemNextPage = new ItemStack(Material.valueOf(Main.getPlugin().getConfig().getString("NextPage-itemType").toUpperCase()), 1);
        ItemMeta metaNextPage = itemNextPage.getItemMeta();

        String name1 = Main.getPlugin().getConfig().getString("NextPage-itemName");
        String name2 = name1.replaceAll("&", "\u00A7");
        if(Main.getPlugin().getConfig().getBoolean("NextPage-loreEnabled"))
        {
            List<String> loreNextPage = new ArrayList<String>();

            String loreLine1 = Main.getPlugin().getConfig().getString("NextPage-loreLineOne").replaceAll("&", "\u00A7");
            String loreLine2 = Main.getPlugin().getConfig().getString("NextPage-loreLineTwo").replaceAll("&", "\u00A7");
            String loreLine3 = Main.getPlugin().getConfig().getString("NextPage-loreLineThree").replaceAll("&", "\u00A7");

            loreNextPage.add(loreLine1);
            loreNextPage.add(loreLine2);
            loreNextPage.add(loreLine3);

            metaNextPage.setLore(loreNextPage);
        }
        metaNextPage.setDisplayName(name2);
        itemNextPage.setItemMeta(metaNextPage);
        return itemNextPage;
    }

    private static ItemStack itemPreviousPage;

    public static ItemStack getItemPreviousPage()
    {
        if(itemPreviousPage == null)
        {
            itemPreviousPage = createItemPreviousPage();
        }
        return itemPreviousPage;
    }

    public static ItemStack createItemPreviousPage()
    {
        ItemStack itemPreviousPage = new ItemStack(Material.valueOf(Main.getPlugin().getConfig().getString("PreviousPage-itemType").toUpperCase()), 1);
        ItemMeta metaPreviousPage = itemPreviousPage.getItemMeta();

        String name1 = Main.getPlugin().getConfig().getString("PreviousPage-itemName");
        String name2 = name1.replaceAll("&", "\u00A7");
        if(Main.getPlugin().getConfig().getBoolean("PreviousPage-loreEnabled"))
        {
            List<String> lorePreviousPage = new ArrayList<String>();

            String loreLine1 = Main.getPlugin().getConfig().getString("PreviousPage-loreLineOne").replaceAll("&", "\u00A7");
            String loreLine2 = Main.getPlugin().getConfig().getString("PreviousPage-loreLineTwo").replaceAll("&", "\u00A7");
            String loreLine3 = Main.getPlugin().getConfig().getString("PreviousPage-loreLineThree").replaceAll("&", "\u00A7");

            lorePreviousPage.add(loreLine1);
            lorePreviousPage.add(loreLine2);
            lorePreviousPage.add(loreLine3);

            metaPreviousPage.setLore(lorePreviousPage);
        }
        metaPreviousPage.setDisplayName(name2);
        itemPreviousPage.setItemMeta(metaPreviousPage);
        return itemPreviousPage;
    }

    private static ItemStack itemRemoveTrails;

    public static ItemStack getItemRemoveTrails()
    {
        if(itemRemoveTrails == null)
        {
            itemRemoveTrails = createItemRemoveTrails();
        }
        return itemRemoveTrails;
    }

    private static ItemStack createItemRemoveTrails()
    {
        ItemStack itemRemoveTrails = new ItemStack(Material.valueOf(Main.getPlugin().getConfig().getString("RemoveTrails-itemType").toUpperCase()), 1);
        ItemMeta metaRemoveTrails = itemRemoveTrails.getItemMeta();

        String name1 = Main.getPlugin().getConfig().getString("RemoveTrails-itemName");
        String name2 = name1.replaceAll("&", "\u00A7");
        if(Main.getPlugin().getConfig().getBoolean("RemoveTrails-loreEnabled"))
        {
            List<String> loreRemoveTrail = new ArrayList<String>();

            String loreLine1 = Main.getPlugin().getConfig().getString("RemoveTrails-loreLineOne").replaceAll("&", "\u00A7");
            String loreLine2 = Main.getPlugin().getConfig().getString("RemoveTrails-loreLineTwo").replaceAll("&", "\u00A7");
            String loreLine3 = Main.getPlugin().getConfig().getString("RemoveTrails-loreLineThree").replaceAll("&", "\u00A7");

            loreRemoveTrail.add(loreLine1);
            loreRemoveTrail.add(loreLine2);
            loreRemoveTrail.add(loreLine3);

            metaRemoveTrails.setLore(loreRemoveTrail);
        }
        metaRemoveTrails.setDisplayName(name2);
        itemRemoveTrails.setItemMeta(metaRemoveTrails);
        return itemRemoveTrails;
    }

    public static List<Trail> getSubList(int currentPage)
    {
        int pageSize = 27;
        List<Trail> sortedList = new ArrayList<Trail>(Main.trailTypes.values());
        Collections.sort(sortedList, new orderComparator());
        int maxPages = (int) Math.ceil((double) sortedList.size() / pageSize);
        currentPage = currentPage > maxPages ? 1 : currentPage;
        int begin = (pageSize * currentPage) - pageSize;
        int end = begin + pageSize;
        if(end > sortedList.size())
        {
            end = sortedList.size();
        }
        return sortedList.subList(begin, end);
    }

    //endregion

    private static class orderComparator implements Comparator<Trail>
    {
        public int compare(Trail o1, Trail o2)
        {
            return o1.getOrder() - o2.getOrder();
        }
    }

}
