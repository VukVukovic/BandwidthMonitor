package internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import snmp.SNMPDataReceiver;
import snmp.SNMPGet;

public class Router implements SNMPDataReceiver, Iterable<RouterInterface> {
	public static final int REFRESH_RATE = 10000;
	
	private List<RouterInterface> interfaces = new ArrayList<>();
	private Map<Integer, RouterInterface> interfacesMap = new HashMap<>();
	private SNMPGet snmpGetter;
	private String ip;
	
	private Routers routers;
	
	public Router(Routers routers, String ip) {
		this.routers = routers;
		this.ip = ip;
		this.snmpGetter = new SNMPGet(ip, this, REFRESH_RATE);
	}
	
	public String getIp() {
		return ip;
	}
	
	public void stopGettingData() {
		snmpGetter.stopSNMPGet();
	}
	
	private void checkConsistent(List<Integer> index, List<String> description) {
		boolean clear = false;
		if (index.size() != interfaces.size())
			clear = true;
		else {
			for (int i=0;i<index.size();i++) {
				int id = index.get(i);
				if (!interfacesMap.containsKey(id) || !interfacesMap.get(id).getName().equals(description.get(i)))
					clear = true;
			}
		}
		
		if (clear) {
			interfaces.clear();
			interfacesMap.clear();
		}
	}

	@Override
	public void receivedData(boolean timeout, 
			List<Integer> index, 
			List<String> description, 
			List<Integer> up,
			List<Long> inPackets, 
			List<Long> outPackets) {
		
		if (timeout) {
			System.out.println("Router " + ip + " timeout!");
			for (RouterInterface ri : interfaces)
				ri.timeout();
		}
		else {
			boolean newInterfaces = false;
			checkConsistent(index, description);
			int n = index.size();
			for (int i=0;i<n;i++) {
				int id = index.get(i);
				String name = description.get(i);
				
				RouterInterface routerInterface;
				
				if (!interfacesMap.containsKey(id)) {
					routerInterface = new RouterInterface(this, id, name);
					interfacesMap.put(id, routerInterface);
					interfaces.add(routerInterface);
					newInterfaces = true;
				} else {
					routerInterface = interfacesMap.get(id);
				}
				
				routerInterface.setUp(up.get(i)==1);
				routerInterface.newData(inPackets.get(i), outPackets.get(i));
			}
			if (newInterfaces)
				routers.notifyRoutersChangeListeners();
		}
	}

	@Override
	public Iterator<RouterInterface> iterator() {
		return interfaces.iterator();
	}
	
}
