package com.shatteredpixel.shatteredpixeldungeon.net.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Events;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Receive;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.NetWindow;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

import static com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon.net;

public class NetIndicator extends Tag {

    public static final int COLOR	= 0xFF4C4C;

    private Image icon;

    public NetIndicator() {
        super( 0xFF4C4C );
        setSize( icon.width()+6, icon.height()+6 );
        visible = true;
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        icon = NetIcons.get(NetIcons.PLAYERS);
        icon.scale.set(0.72f);
        add( icon );
    }

    @Override
    protected void layout() {
        super.layout();
        icon.x = right()-icon.width()-2;
        icon.y = y+3;
    }

    @Override
    public void update() {
        super.update();
        bg.hardlight(net().connected() ? 0x52846b: 0x845252);
    }

    @Override
    protected void onClick() {
        if (net().connected()) {
            net().sender().sendPlayerListRequest();
        }else{
            NetWindow.error("Not connected", "You must connect before viewing players");
            return;
        }
        net().socket().once(Events.PLAYERLISTREQUEST, args -> {
            String data = (String) args[0];
            try {
                final Receive.PlayerList pl = net().mapper().readValue(data, Receive.PlayerList.class);
                Game.runOnRenderThread(() -> NetWindow.showPlayerList(pl));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }
}
