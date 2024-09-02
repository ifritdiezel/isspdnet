package com.shatteredpixel.shatteredpixeldungeon.net.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.net.Settings;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.NetWindow;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.utils.PlatformSupport;

import java.net.URI;
import java.net.URISyntaxException;

import static com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon.net;
import static com.watabou.noosa.Game.platform;

public class NetBtn extends StyledButton {
    public static final int HEIGHT = 24;
    public static final int MIN_WIDTH = 30;

    private ShatteredPixelDungeon instance = ((ShatteredPixelDungeon) ShatteredPixelDungeon.instance);

    public NetBtn() {
        super(Chrome.Type.GREY_BUTTON_TR, "");
        icon(NetIcons.get(NetIcons.GLOBE));
    }

    @Override
    public void update() {
        super.update();
        icon.brightness(instance.net.connected() ? 0.8f : 0.2f );
    }

    @Override
    protected void onClick() {
        super.onClick();
        NetWindow.showServerInfo();
    }

    @Override
    protected boolean onLongClick() {
        ShatteredPixelDungeon.scene().addToFront( new WndTextInput("Enter hostname",
                null,
                "http://ifritserv.zapto.org:9090",
                40,
                false,
                "Set",
                "Cancel"){
            @Override
            public void onSelect(boolean positive, String text) {
                if (!positive) return;
                URI url = null;
                try {
                    url = new URI(text);
                    Settings.scheme(url.getScheme());
                    Settings.address(url.getHost());
                    Settings.port(url.getPort());
                    ShatteredPixelDungeon.scene().addToFront( new WndTextInput("Enter key", "Ask the server owner for one!", Settings.auth_key(), 50, false, "Set", "Cancel" ){
                        @Override
                        public void onSelect(boolean positive, String text) {
                            if(positive){
                                Settings.auth_key(text);
                                net().reset();
                            }
                        }
                    });
                } catch (URISyntaxException e) {}
            }
        });

        return true;
    }
}
