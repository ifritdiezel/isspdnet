package com.shatteredpixel.shatteredpixeldungeon.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.net.actor.Player;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Events;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Receive;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Send;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.NetWindow;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Reflection;

import io.socket.emitter.Emitter;

public class Receiver {
        private ObjectMapper mapper;
        private Net net;

        public Receiver(Net net, ObjectMapper mapper) {
                this.net = net;
                this.mapper = mapper;
        }

        public void startAll() {
                Emitter.Listener onMessage = args -> {
                        String data = (String) args[0];
                        handleMessage(data);
                };
                Emitter.Listener onChat = args -> {
                        String data = (String) args[1];
                        handleChat(data);
                };
                Emitter.Listener onAction = args -> {
                        int type = (int) args[0];
                        String data = (String) args[1];
                        handleAction(type, data);
                };
                Emitter.Listener onTransfer = args -> {
                        String data = (String) args[0];
                        handleTransfer(data);
                };
                Emitter.Listener onMotd = args -> {
                        String data = (String) args[0];
                        handleMotd(data);
                };
                net.socket().on(Events.ACTION, onAction);
                net.socket().on(Events.CHAT, onChat);
                net.socket().on(Events.TRANSFER, onTransfer);
                net.socket().on(Events.MESSAGE, onMessage);
                net.socket().once(Events.MOTD, onMotd);
        }

        public void cancelAll() {
                net.socket().off(Events.ACTION);
                net.socket().off(Events.TRANSFER);
                net.socket().off(Events.MESSAGE);
                net.socket().off(Events.CHAT);
                net.socket().off(Events.MOTD);
        }

        public void handleMessage(String json) {
                try {
                        Send.Message message = mapper.readValue(json, Send.Message.class);
                        NetWindow.message(message.data);
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                }
        }

        public void handleChat(String json){
                try{
                        Receive.Chat message = mapper.readValue(json, Receive.Chat.class);
                        GLog.e(message.nick + ": " + message.data);
                        GLog.newLine();
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                }
        }

        public void handleTransfer(String json) {
                try {
                        Receive.Transfer item = mapper.readValue(json, Receive.Transfer.class);
                        Class<?> k = Reflection.forNameUnhandled(addPkgName(item.className));
                        Item i = (Item) Reflection.newInstance(k);
                        Dungeon.hero.belongings.backpack.items.add(i);
                } catch (Exception ignored) { }

        }

        public void handleAction(int type, String json) {
                Player player;
                Receive.Join join;
                try {
                        switch (type) {
                                case Receive.MOVE:
                                        Receive.Move m = mapper.readValue(json, Receive.Move.class);
                                        Player.movePlayer(Player.getPlayer(m.id), m.pos, m.playerClass);
                                        break;
                                case Receive.JOIN:
                                        join = mapper.readValue(json, Receive.Join.class);
                                        Player.addPlayer(join.id, join.nick, join.playerClass, join.pos, join.depth, join.items);
                                        break;
                                case Receive.JOIN_LIST:
                                        Receive.JoinList jl = mapper.readValue(json, Receive.JoinList.class);
                                        for (int i = 0; i < jl.players.length; i++) {
                                                Receive.Join j = jl.players[i];
                                                Player.addPlayer(j.id, j.nick, j.playerClass, j.pos, j.depth, j.items);
                                        }
                                        break;
                                case Receive.LEAVE:
                                        Receive.Leave l = mapper.readValue(json, Receive.Leave.class);
                                        player = Player.getPlayer(l.id);
                                        if (player != null) player.leave();
                                        break;
                                case Receive.DEATH:
                                        Receive.Death d = mapper.readValue(json, Receive.Death.class);
                                        GLog.n(d.nick + " " + d.cause);
                                        GLog.newLine();
                                        break;
                                default:
                                        DeviceCompat.log("Unknown Action", json);
                        }
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                }
        }

        public void handleMotd(String json) {
                try {
                        Receive.Motd motd = mapper.readValue(json, Receive.Motd.class);
                        NetWindow.motd(motd.motd, motd.seed);
                        net.seed(motd.seed);
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                }
        }

        public static String addPkgName(String c) {
                return Game.pkgName + ".items." + c;
        }
}
