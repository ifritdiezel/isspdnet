package com.shatteredpixel.shatteredpixeldungeon.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Events;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.NetWindow;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.WndServerInfo;
import com.watabou.noosa.Game;

import org.json.JSONObject;

import java.net.URI;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static java.util.Collections.singletonMap;

public class Net {
    public static String DEFAULT_SCHEME = "http";
    public static String DEFAULT_HOST = "ifritserv.zapto.org";
    public static int DEFAULT_PORT = 9090;
    public static String DEFAULT_KEY = "none";

    private Socket socket;
    private Receiver receiver;
    private Sender sender;
    private ObjectMapper mapper;
    private long seed;

    private NetWindow w;

    public Net(String address, String key){
        URI url = URI.create(address);
        Settings.scheme(url.getScheme());
        Settings.address(url.getHost());
        Settings.port(url.getPort());
        Settings.auth_key(key);
        session(Settings.uri(), key);
    }

    public Net(String key){
        Settings.auth_key(key);
        session(Settings.uri(), key);
    }

    public Net(){
        session(Settings.uri(), Settings.auth_key());
    }

    public void reset(){
        session(Settings.uri(), Settings.auth_key());
    }

    public void session(URI url, String key){
        IO.Options options = IO.Options.builder()
                .setAuth(singletonMap("token", key))
                .setForceNew(true)
                .setReconnection(false)
                .build();
        socket = IO.socket(url, options);
        mapper = new ObjectMapper();
        receiver = new Receiver(this, mapper);
        sender = new Sender(this, mapper);
        setupEvents();
    }

    public void setupEvents(){
        Emitter.Listener onConnected = args -> {
            receiver.startAll();
            if(w != null) {
                Game.runOnRenderThread( () -> w.destroy());
            }
        };

        Emitter.Listener onDisconnected = args -> {
            receiver.cancelAll();
        };

        Emitter.Listener onConnectionError = args -> {
            try {

                JSONObject json = (JSONObject)args[0];
                Events.Error e = mapper().readValue(json.toString(), Events.Error.class);
                NetWindow.error(e.message);
            }catch(Exception e){
                e.printStackTrace();
                NetWindow.error("Connection could not be established!");
            }
            receiver.cancelAll();
            disconnect();
        };

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectionError);
        socket.on(Socket.EVENT_CONNECT, onConnected);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnected);
    }

    public void endEvents(){
        socket.off();
    }

    public void connect() {
        socket.connect();
    }
    public void disconnect(){
        receiver.cancelAll();
        socket.disconnect();
    }

    public void toggle(WndServerInfo w) {
        this.w = w;
        if(!socket.connected())
            connect();
        else
            disconnect();
    }

    public void die(){
        if (socket != null) {
            if(socket.connected()) disconnect();
            endEvents();
            socket = null;
        }
        receiver = null;
        sender = null;
    }

    public void seed(long seed) { this.seed = seed; }
    public long seed() { return this.seed; }

    public Boolean connected() { return socket != null && socket.connected(); }
    public Socket socket(){ return this.socket; }
    public ObjectMapper mapper() { return this.mapper;}
    public Sender sender() { return sender; }
    public URI uri(){ return Settings.uri(); }
}
