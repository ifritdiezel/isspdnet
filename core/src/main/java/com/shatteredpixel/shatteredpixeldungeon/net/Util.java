package com.shatteredpixel.shatteredpixeldungeon.net;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.WndNetSettings;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.WndServerInfo;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;


public class Util {

    public static void error(String message){
        message(Icons.get(Icons.ALERT), "Connection Error", message);
    }

    public static void message(Image i, String title, String message){
        Game.runOnRenderThread(new Callback() {
            @Override
            public void call() {
                ShatteredPixelDungeon.scene().add(new WndMessage(i, title, message));
            }
        });
    }

    public static void message(String title, String message){
        message(Icons.get(Icons.NET), title, message);
    }
    public static void message(String message){
        message(Icons.get(Icons.NET), "Server Message", message);
    }

    public static void showSettings(){
        Game.runOnRenderThread(new Callback() {
            @Override
            public void call() {
                ShatteredPixelDungeon.scene().add(new WndNetSettings());
            }
        });
    }

    public static void showServerInfo(){
        Game.runOnRenderThread(new Callback() {
            @Override
            public void call() {
                ShatteredPixelDungeon.scene().add(new WndServerInfo());
            }
        });
    }

    public static void motd(String message){
        message(Icons.get(Icons.NEWS), "Motd", message);
    }
}