import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class LabClient extends WebSocketClient {

    String msg;

    public LabClient(URI serverURI){
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handShakeData) {
        System.out.println( "opened connection" );
    }

    @Override
    public void onMessage(String message) {
        //System.out.println("message: " + message);
        msg = new String(message);
        synchronized(this) {
            this.notify();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println( "Connection closed by " +
                ( remote ? "remote peer" : "us" ) + " Code: " + code + " Reason: " + reason );
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public String responseFromServer() {
        return this.msg;
    }

}
