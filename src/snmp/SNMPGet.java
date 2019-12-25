package snmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ireasoning.protocol.snmp.SnmpConst;
import com.ireasoning.protocol.snmp.SnmpInt;
import com.ireasoning.protocol.snmp.SnmpSession;
import com.ireasoning.protocol.snmp.SnmpTarget;
import com.ireasoning.protocol.snmp.SnmpUInt;
import com.ireasoning.protocol.snmp.SnmpVarBind;

import internal.Router;

/*
 * ifIndex (.1.3.6.1.2.1.2.2.1.1) - Integer
 * ifDescr (.1.3.6.1.2.1.2.2.1.2) - OctetString
 * ifOperStatus (.1.3.6.1.2.1.2.2.1.8) - Integer (up=1, down=2)
 * ifInOctets (.1.3.6.1.2.1.2.2.1.10) - Counter32
 * ifOutOctets (.1.3.6.1.2.1.2.2.1.16) - Coutner32
 */

public class SNMPGet extends Thread {
	private static final String IF_INDEX = ".1.3.6.1.2.1.2.2.1.1";
	private static final String IF_DESCR = ".1.3.6.1.2.1.2.2.1.2";
	private static final String IF_OPER_STATUS = ".1.3.6.1.2.1.2.2.1.8";
	private static final String IF_IN_OCTETS = ".1.3.6.1.2.1.2.2.1.10";
	private static final String IF_OUT_OCTETS = ".1.3.6.1.2.1.2.2.1.16";
	private static final int SNMP_PORT = 161;
	private static final String SNMP_USERNAME = "si2019";
	private static final String SNMP_PASSWORD = "si2019";
	
	private String target;
	private SNMPDataReceiver receiver;
	private int refreshRate;
	
	private SnmpSession session;
	private boolean timeout;
	private List<Integer> index;
	private List<String> description;
	private List<Integer> up;
	private List<Long> inPackets;
	private List<Long> outPackets;
	private boolean end = false;
	
	public SNMPGet(String target, SNMPDataReceiver receiver, int refreshRate) {
		this.target = target;
		this.receiver = receiver;
		this.refreshRate = refreshRate;
		start();
	}
	
	public void stopSNMPGet() {
		end = true;
		interrupt();
	}
	
	private void finished() {
		interrupt();
	}
	
	@Override
	public void run() {
		while (!end) {
			SnmpTarget snmpTarget = new SnmpTarget(target, SNMP_PORT, SNMP_USERNAME, SNMP_PASSWORD, SnmpConst.SNMPV2);
			try {
				session = new SnmpSession(snmpTarget);
				
				timeout = true;
				Thread timeoutThread =  new Thread() {
					@Override
					public void run() {
						try {
							index = getIntegerValues(session.snmpGetTableColumn(IF_INDEX));
							description = getStringValues(session.snmpGetTableColumn(IF_DESCR));
							up = getIntegerValues(session.snmpGetTableColumn(IF_OPER_STATUS));
							inPackets = getCounter32Values(session.snmpGetTableColumn(IF_IN_OCTETS));
							outPackets = getCounter32Values(session.snmpGetTableColumn(IF_OUT_OCTETS));
							
							if (index.size() == description.size() &&
								index.size() == up.size() &&
								index.size() == inPackets.size() &&
								index.size() == outPackets.size())
								timeout = false;
							
							finished();
						} catch (IOException e) {
						}
					}
				};
				
				timeoutThread.start();
				try {
					sleep((int)(0.8*Router.REFRESH_RATE));
				} catch (InterruptedException e) {
					timeoutThread.interrupt();
				}
				session.close();
			} catch (IOException e) {}
			
			receiver.receivedData(timeout, index, description, up, inPackets, outPackets);
			
			try {
				sleep(refreshRate);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	private static List<String> getStringValues(SnmpVarBind[] result) {
		List<String> values = new ArrayList<String>();
		for (int i= 0; i < result.length; i++)
			values.add(result[i].getValue().toString());
		return values;
	}
	
	private static List<Integer> getIntegerValues(SnmpVarBind[] result) {
		List<Integer> values = new ArrayList<Integer>();
		for (int i= 0; i < result.length; i++)
			values.add(((SnmpInt)result[i].getValue()).getValue());
		return values;
	}
	
	private static List<Long> getCounter32Values(SnmpVarBind[] result) {
		List<Long> values = new ArrayList<Long>();
		for (int i= 0; i < result.length; i++)
			values.add(((SnmpUInt)result[i].getValue()).getValue());
		return values;
	}
}
