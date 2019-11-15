//SendMessage.java - Sample application.
//
// This application shows you the basic procedure for sending messages.
// You will find how to send synchronous and asynchronous messages.
//
// For asynchronous dispatch, the example application sets a callback
// notification, to see what's happened with messages.

package smsapp;

import java.util.List;
import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.Library;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;


public class SendMessage
{
        public void doIt() throws Exception
        {
                
            
            
            OutboundNotification outboundNotification = new OutboundNotification();
                System.out.println("Example: Send message from a serial gsm modem.");
                System.out.println(Library.getLibraryDescription());
                System.out.println("Version: " + Library.getLibraryVersion());
                SerialModemGateway gateway1 = new SerialModemGateway("modem.com4", "COM8", 115200, "", "");
                gateway1.setInbound(true);
                gateway1.setOutbound(true);
                gateway1.setSmscNumber("+966560100000");
                Service.getInstance().setOutboundMessageNotification(outboundNotification);
                Service.getInstance().addGateway(gateway1);
                Service.getInstance().startService();
                System.out.println();
                System.out.println("Modem Information:");
                System.out.println("  Manufacturer: " + gateway1.getManufacturer());
                System.out.println("  Model: " + gateway1.getModel());
                System.out.println("  Serial No: " + gateway1.getSerialNo());
                System.out.println("  SIM IMSI: " + gateway1.getImsi());
                System.out.println("  Signal Level: " + gateway1.getSignalLevel() + " dBm");
                System.out.println("  Battery Level: " + gateway1.getBatteryLevel() + "%");
                System.out.println();
                OutboundMessage msg = new OutboundMessage("+966550726540", "Hello from SMSLib!");
                Service.getInstance().sendMessage(msg);
                System.out.println(msg);
                System.out.println("Now Sleeping - Hit <enter> to terminate.");
                System.in.read();
                Service.getInstance().stopService();
        }

        public class OutboundNotification implements IOutboundMessageNotification
        {
                public void process(AGateway gateway, OutboundMessage msg)
                {
                        System.out.println("Outbound handler called from Gateway: " + gateway.getGatewayId());
                        System.out.println(msg);
                }
        }

        public static void main(String args[])
        {
                SendMessage app = new SendMessage();
                try
                {
                        app.doIt();
                }
                catch (Exception e)
                {
                        e.printStackTrace();
                }
        }
}