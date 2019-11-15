
//
// This application shows you the basic procedure needed for reading
// SMS messages from your GSM modem, in synchronous mode.
//
// Operation description:
// The application setup the necessary objects and connects to the phone.
// As a first step, it reads all messages found in the phone.
// Then, it goes to sleep, allowing the asynchronous callback handlers to
// be called. Furthermore, for callback demonstration purposes, it responds
// to each received message with a "Got It!" reply.
//
// Tasks:
// 1) Setup Service object.
// 2) Setup one or more Gateway objects.
// 3) Attach Gateway objects to Service object.
// 4) Setup callback notifications.
// 5) Run
package smsapp;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;
import org.smslib.AGateway;
import org.smslib.AGateway.GatewayStatuses;
import org.smslib.AGateway.Protocols;
import org.smslib.GatewayException;
import org.smslib.ICallNotification;
import org.smslib.IGatewayStatusNotification;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOrphanedMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.Library;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.crypto.AESKey;
import org.smslib.modem.SerialModemGateway;

public class ReadMessages {
Formatter x;
    public void doIt() throws Exception {
        // Define a list which will hold the read messages.
        List<InboundMessage> msgList;
                // Create the notification callback method for inbound & status report
        // messages.
        InboundNotification inboundNotification = new InboundNotification();
        ReadMessages.OutboundNotification outboundNotification = new ReadMessages.OutboundNotification();
        // Create the notification callback method for inbound voice calls.
        CallNotification callNotification = new CallNotification();
        //Create the notification callback method for gateway statuses.
        GatewayStatusNotification statusNotification = new GatewayStatusNotification();
        OrphanedMessageNotification orphanedMessageNotification = new OrphanedMessageNotification();
        try {
            System.out.println("Example: Read messages from a serial gsm modem.");
            System.out.println(Library.getLibraryDescription());
            System.out.println("Version: " + Library.getLibraryVersion());
            // Create the Gateway representing the serial GSM modem.
            SerialModemGateway gateway = new SerialModemGateway("modem.com8", "COM15", 115200 , "", "");
            // Set the modem protocol to PDU (alternative is TEXT). PDU is the default, anyway...
            gateway.setProtocol(Protocols.PDU);
            // Do we want the Gateway to be used for Inbound messages?
            gateway.setInbound(true);
            // Do we want the Gateway to be used for Outbound messages?
            gateway.setOutbound(true);
            // Let SMSLib know which is the SIM PIN.
            gateway.setSimPin("0000");
            // Set up the notification methods.
            gateway.setSmscNumber("+966560100000");
                Service.getInstance().setOutboundMessageNotification(outboundNotification);
            Service.getInstance().setInboundMessageNotification(inboundNotification);
            Service.getInstance().setCallNotification(callNotification);
            Service.getInstance().setGatewayStatusNotification(statusNotification);
            Service.getInstance().setOrphanedMessageNotification(orphanedMessageNotification);
            // Add the Gateway to the Service object.
            Service.getInstance().addGateway(gateway);
                        // Similarly, you may define as many Gateway objects, representing
            // various GSM modems, add them in the Service object and control all of them.
            // Start! (i.e. connect to all defined Gateways)
            Service.getInstance().startService();
            // Printout some general information about the modem.
            System.out.println();
            System.out.println("Modem Information:");
            System.out.println("  Manufacturer: " + gateway.getManufacturer());
            System.out.println("  Model: " + gateway.getModel());
            System.out.println("  Serial No: " + gateway.getSerialNo());
            System.out.println("  SIM IMSI: " + gateway.getImsi());
            System.out.println("  Signal Level: " + gateway.getSignalLevel() + " dBm");
            System.out.println("  Battery Level: " + gateway.getBatteryLevel() + "%");
            System.out.println();
                        // In case you work with encrypted messages, its a good time to declare your keys.
            // Create a new AES Key with a known key value. 
            // Register it in KeyManager in order to keep it active. SMSLib will then automatically
            // encrypt / decrypt all messages send to / received from this number.
            Service.getInstance().getKeyManager().registerKey("+966560100000", new AESKey(new SecretKeySpec("0011223344556677".getBytes(), "AES")));
                        // Read Messages. The reading is done via the Service object and
            // affects all Gateway objects defined. This can also be more directed to a specific
            // Gateway - look the JavaDocs for information on the Service method calls.
            msgList = new ArrayList<>();
            Service.getInstance().readMessages(msgList, MessageClasses.ALL);
            for (InboundMessage msg : msgList) {
                
                System.out.println(msg.getText());
                x = new Formatter("C:\\Users\\toshibaa\\Desktop\\GSM_SMSLib_Tutorial\\GSM_SMSLib_Tutorial\\Msg\\"+msg.getOriginator()+" "
                        +msg.getUuid()+".txt");
                             x.format("%s\n",msg.getText());
                             x.close();
                             Service.getInstance().deleteMessage(msg);
            }
            
            //OutboundMessage msg1 = new OutboundMessage("+966550726540", "from read messages- SMSLib!");
                //Service.getInstance().sendMessage(msg1);
               // System.out.println(msg1);
                        // Sleep now. Emulate real world situation and give a chance to the notifications
            // methods to be called in the event of message or voice call reception.
            System.out.println("Now Sleeping - Hit <enter> to stop service.");
            System.in.read();
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Service.getInstance().stopService();
        }
    }

    public class InboundNotification implements IInboundMessageNotification {

        @Override
        public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg) {
            if (msgType == MessageTypes.INBOUND) {

                System.out.println(">>> New Inbound message detected from Gateway: " + gateway.getGatewayId());
                System.out.println("samyan Qayyum Wahla " + msg.getOriginator());
                
            } else if (msgType == MessageTypes.STATUSREPORT) {
                System.out.println(">>> New Inbound Status Report message detected from Gateway: " + gateway.getGatewayId());
            }
            System.out.println(msg.getText());
           /* try{
			BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\toshibaa\\Desktop\\"
                                + "GSM_SMSLib_Tutorial\\GSM_SMSLib_Tutorial\\Messages\\message.txt"));
				writer.append(msg.getText());
				writer.newLine();
                                System.out.println("ok");
			writer.close();
		}
		catch(IOException e){
                    System.out.println("not ok");
			e.getStackTrace();
		}*/


              //writerToFile(msg.getText());
            try {
                Formatter z = new Formatter("C:\\Users\\toshibaa\\Desktop\\GSM_SMSLib_Tutorial\\GSM_SMSLib_Tutorial\\Msg\\"+msg.getOriginator()+" "
                        + " "+msg.getUuid()+".txt");
                           z.format("%s%n",msg.getText());
                           z.close();
                           
                           
            } catch (FileNotFoundException ex) {
               System.out.println("Oops!!! Something gone bad 1...");
            }
                                   

            try {
    // Uncomment following line if you wish to delete the message upon arrival. 
                Service.getInstance().deleteMessage(msg);
            } catch (Exception e) {
                System.out.println("Oops!!! Something gone bad.2..");
                e.printStackTrace();
            }

           
        }
    }

    public class OutboundNotification implements IOutboundMessageNotification
        {
                public void process(AGateway gateway, OutboundMessage msg1)
                {
                        System.out.println("Outbound handler called from Gateway: " + gateway.getGatewayId());
                        System.out.println(msg1);
                }
        }
    public class CallNotification implements ICallNotification {

        @Override
        public void process(AGateway gateway, String callerId) {
            System.out.println(">>> New call detected from Gateway: " + gateway.getGatewayId() + " : " + callerId);
        }
    }

    public class GatewayStatusNotification implements IGatewayStatusNotification {

        @Override
        public void process(AGateway gateway, GatewayStatuses oldStatus, GatewayStatuses newStatus) {
            System.out.println(">>> Gateway Status change for " + gateway.getGatewayId() + ", OLD: " + oldStatus + " -> NEW: " + newStatus);
        }
    }

    public class OrphanedMessageNotification implements IOrphanedMessageNotification {

        @Override
        public boolean process(AGateway gateway, InboundMessage msg) {
            System.out.println(">>> Orphaned message part detected from " + gateway.getGatewayId());
            System.out.println(msg);
            // Since we are just testing, return FALSE and keep the orphaned message part.
            return false;
        }
    }
    
    

    public static void main(String args[]) {
        ReadMessages app = new ReadMessages();
        try {
            app.doIt();
        } catch (Exception e) {
        }
    }




public static void writerToFile (String  list){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\toshibaa\\Desktop\\"
                                + "GSM_SMSLib_Tutorial\\GSM_SMSLib_Tutorial\\Messages\\message.txt"));
				writer.append(list);
				writer.newLine();
                                System.out.println("ok");
			writer.close();
		}
		catch(IOException e){
                    System.out.println("not ok");
			e.getStackTrace();
		}
	}

}