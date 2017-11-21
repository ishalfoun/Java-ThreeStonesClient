package ThreeStone;

import ThreeStone.Opcode;
import ThreeStone.PlayerType;
import ThreeStone.Stone;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  The ClientSession object is responsible for maintaining a connection to the server.
 * It is also responsible the sending and recieving of packets between the client and the server.
 * @author Isaak
 */
public class ClientSession {
    
    
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private byte[] byteBuffer;
    private int packetSize;
    Logger l = Logger.getLogger(ClientGame.class.getName());
    
    /**
     * The Constructor recieves a socket and defines packetsize.
     * @param socket the socket to connect to
     * @throws IOException 
     */
    public ClientSession (Socket socket) throws IOException
    {
        this.socket=socket;
        this.packetSize=5;
        byteBuffer = new byte[packetSize];
    }
    
    /**
     * closes the connection with the server
     * @throws IOException 
     */
    public void closeSocket() throws IOException
    {
        socket.close();
    }
    /**
     * Waits until it receives one Packet.
     * then returns the stone and opcode as a list
     * 
     * @return List of [Stone, opcode]
     * @throws IOException 
     */
    public ArrayList<Object> receivePacket() throws IOException
    {
        in = socket.getInputStream();
        int totalBytesRcvd = 0;      // Total bytes received so far
        int bytesRcvd;        // Bytes received in last read
        while (totalBytesRcvd < packetSize)
        {
          if ( (bytesRcvd = in.read(byteBuffer, totalBytesRcvd,   packetSize - totalBytesRcvd)) == -1)
            throw new SocketException("Connection closed prematurely");
          totalBytesRcvd += bytesRcvd;
        }
        Opcode opcode= Opcode.values()[(int)byteBuffer[0]];
        int x = (int)byteBuffer[1];
        int y = (int)byteBuffer[2];
        int scoreUser = (int)byteBuffer[3];
        int scoreComp = (int)byteBuffer[4];
        //int score = (int)byteBuffer[3];
        
        //System.out.println("Received: opcode:"+opcode.getValue()+" x="+y+" y="+x);
        return new ArrayList<>(Arrays.asList(new Stone(x,y, PlayerType.COMPUTER), opcode, scoreUser, scoreComp));
    }
     
    
    
    /**
     * Sends one packet containing a stone and opcode
     * @param stone
     * @param opcode
     * @throws IOException 
     */
    public void sendPacket(Stone stone, Opcode opcode) throws IOException 
    {    
        out = socket.getOutputStream();
        switch(opcode)
        {
            case REQ_GAME_START:
            {
                byteBuffer = new byte[]{(byte)Opcode.REQ_GAME_START.getValue(), 0b0, 0b0, 0b0, 0b0};
                break;
            }
            case CLIENT_PLACE:
            {
                if (stone==null)
                    throw new IllegalArgumentException();
                byteBuffer = new byte[]{(byte)Opcode.CLIENT_PLACE.getValue(), (byte)stone.getX(), (byte)stone.getY(), 0b0, 0b0};
                break;
            }
            case ACK_PLAY_AGAIN:
            {
                byteBuffer = new byte[]{(byte)Opcode.ACK_PLAY_AGAIN.getValue(), 0b0, 0b0, 0b0, 0b0};
                break;   
            }
            default:
                throw new IllegalArgumentException();
                // TODO: add code here
        }   
        
        out.write(byteBuffer);
    }
    
    
    public static void main (String args[]) throws IOException
    {
        /*
        Scanner sc=new Scanner(System.in);  
        System.out.println("Welcome! Please enter <Server> to start game");  
        sc.next();
        String server = "192.168.12.104";
        int servPort = 7;

        ClientSession isi = new ClientSession( new Socket(server, servPort));

        System.out.println("sending a packet");
        //isi.sendPacket(new Stone(3,4, PlayerType.PLAYER), Opcode.CLIENT_PLACE);
    
        System.out.println("waiting to receive packet");
        ArrayList<Object> recvd = isi.receivePacket();
        
        */
    }
    
}