package snmp;

import java.util.List;

public interface SNMPDataReceiver {
	void receivedData(boolean timeout, List<Integer> index, List<String> description, List<Integer> up, List<Long> inPackets, List<Long> outPackets);
}
