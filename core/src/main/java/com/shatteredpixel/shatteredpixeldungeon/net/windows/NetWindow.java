package com.shatteredpixel.shatteredpixeldungeon.net.windows;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Receive;
import com.shatteredpixel.shatteredpixeldungeon.net.ui.NetIcons;
import com.shatteredpixel.shatteredpixeldungeon.net.ui.UI;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

public class NetWindow extends Window {
    public NetWindow(int width, int height){
        super(width, height, UI.get(UI.Type.WINDOW));
    }
    public NetWindow(){
        super(0, 0, UI.get(UI.Type.WINDOW));
    }

    public static void message(Image i, String title, String message){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndMessage(i, title, message)));
    }

    public static void message(String title, String message){
        message(NetIcons.get(NetIcons.GLOBE), title, message);
    }

    public static void message(String message){
        message(NetIcons.get(NetIcons.GLOBE), "Server Message", message);
    }

    public static void error(String message){
        message(NetIcons.get(NetIcons.ALERT), "Connection Error", message);
    }

    public static void error(String title, String message){
        message(NetIcons.get(NetIcons.ALERT), title, message);
    }

    public static void runWindow(Window w){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(w));
    }

    public static void showSettings(){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndNetSettings()));
    }

    public static void showServerInfo(){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndServerInfo()));
    }

    public static void motd(String motd, long seed){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndMotd(motd,seed)));
    }

    public static void showPlayerList(Receive.PlayerList p){
        //Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndPlayerList(p)));
    }
}
