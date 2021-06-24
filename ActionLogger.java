package minegame159.meteorclient.systems.modules.ArtikHack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.gui.GuiTheme;
import minegame159.meteorclient.gui.widgets.WWidget;
import minegame159.meteorclient.gui.widgets.containers.WTable;
import minegame159.meteorclient.gui.widgets.input.WTextBox;
import minegame159.meteorclient.gui.widgets.pressable.WMinus;
import minegame159.meteorclient.gui.widgets.pressable.WPlus;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.BaseText;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;
import net.minecraft.world.GameMode;

public class ActionLogger extends Module {
	
    public ActionLogger() {
        super(Categories.ArtikHack, "action-logger", "Send message on player action.");
    }
    
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> joinleave = sgGeneral.add(new BoolSetting.Builder()
		    .name("join-leave")
		    .defaultValue(true)
		    .build());
    
    private final Setting<Boolean> gamemode = sgGeneral.add(new BoolSetting.Builder()
            .name("game-mode-change")
            .defaultValue(true)
            .build());
    

    private final List<String> players = new ArrayList<>();
    HashMap<String, GameMode> state = new HashMap<>();


    @Override
    public void onActivate() {
    	state.clear();
    	if(players.isEmpty()) {
    		toggle();
    		return;
    	}
    	ArrayList<PlayerListEntry> list = new ArrayList<>(mc.getNetworkHandler().getPlayerList());
    	for (PlayerListEntry p : list) {
    		if(players.contains(p.getProfile().getName())) state.put(p.getProfile().getName(), p.getGameMode());
    	}
    }
    
    private BaseText getMode(GameMode m){
    	String tmode = "Выживание";
    	int color = 16777215;
    	if(m == GameMode.CREATIVE) {
    		tmode = "Творческий";
    		color = 10053324;
    	}
    	if(m == GameMode.ADVENTURE) {
    		tmode = "Приключение";
    		color = 7855591;
    	}
    	if(m == GameMode.SPECTATOR) {
    		tmode = "Наблюдатель";
    		color = 16720896;
    	}
    	BaseText mode = new LiteralText(tmode);
    	mode.setStyle(mode.getStyle().withColor(TextColor.fromRgb(color)));
    	return mode;
    }
    
    
    private BaseText getText(String s){
    	BaseText text = new LiteralText("§8§l［§bActionLogger§8§l］ "+s);
    	text.setStyle(text.getStyle()
    			.withHoverEvent(new HoverEvent(
    					HoverEvent.Action.SHOW_TEXT,
    					new LiteralText(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date(System.currentTimeMillis())))
    					))
    			);
    	return text;
    }
    
    
    private void send(BaseText s){
    	mc.inGameHud.getChatHud().addMessage(s);
    }
    
    
    private void sayModeChange(String s, GameMode m){
    	BaseText text = getText(s);
    	text.append(getMode(m));
    	send(text);
    }
    
    
    private void sayMode(String s, GameMode m){
    	BaseText text = getText(s);
    	if(m != null){
    		text.append(" §8§l［");
    		text.append(getMode(m));
    		text.append("§8§l］");
    	}
    	send(text);
    	
    }
    
    @EventHandler
    private void onTick(TickEvent.Post e) {
    	if(players.isEmpty()) {
    		toggle();
    		return;
    	}

    	ArrayList<PlayerListEntry> list = new ArrayList<>(mc.getNetworkHandler().getPlayerList());
    	HashMap<String, GameMode> newstate = new HashMap<String, GameMode>();
    	for (PlayerListEntry p : list) {
    		if(players.contains(p.getProfile().getName())) newstate.put(p.getProfile().getName(), p.getGameMode());
    	}
    	
    	
    	for(String p : players){
    		if(joinleave.get()){
    			if(state.containsKey(p)){
    				if(!newstate.containsKey(p)) {
    					sayMode("§a§n"+p+"§a － вышел с сервера",null);
    					continue;
    				}
    			} else if(newstate.containsKey(p)) {
    				GameMode mode = null;
    				if(newstate.get(p) != GameMode.SURVIVAL) mode = newstate.get(p);
    				sayMode("§c§n"+p+"§c － зашёл на сервер",mode);
    				continue;
    			}
    		}
    		if(gamemode.get() && state.containsKey(p) && newstate.containsKey(p)){
    			if(state.get(p) != newstate.get(p)) {
    				sayModeChange("§c§n"+p+"§6 изменил режим игры на ",newstate.get(p));
    			}
    		}
    	}
    	
    	state = newstate;
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        players.removeIf(String::isEmpty);

        WTable table = theme.table();
        fillTable(theme, table);

        return table;
    }

    private void fillTable(GuiTheme theme, WTable table) {
        table.add(theme.horizontalSeparator("Players")).expandX();
        table.row();

        for (int i = 0; i < players.size(); i++) {
            int msgI = i;
            String player = players.get(i);

            WTextBox textBox = table.add(theme.textBox(player)).minWidth(100).expandX().widget();
            textBox.action = () -> players.set(msgI, textBox.get());

            WMinus delete = table.add(theme.minus()).widget();
            delete.action = () -> {
                players.remove(msgI);

                table.clear();
                fillTable(theme, table);
            };

            table.row();
        }

        WPlus add = table.add(theme.plus()).expandCellX().right().widget();
        add.action = () -> {
            players.add("");

            table.clear();
            fillTable(theme, table);
        };
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();

        players.removeIf(String::isEmpty);
        ListTag playersTag = new ListTag();

        for (String player : players) playersTag.add(StringTag.of(player));
        tag.put("players", playersTag);

        return tag;
    }

    @Override
    public Module fromTag(CompoundTag tag) {
        players.clear();

        if (tag.contains("players")) {
            ListTag playersTag = tag.getList("players", 8);
            for (Tag playerTag : playersTag) players.add(playerTag.asString());
        } else {
            players.add("nag1bator228");
        }

        return super.fromTag(tag);
    }
}
