package internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Routers implements Iterable<Router> {
	private List<Router> routers = new ArrayList<>();
	private Map<String, Router> routersMap = new HashMap<>();
	
	private List<RoutersChangeListener> listeners = new ArrayList<>();
	
	void notifyRoutersChangeListeners() {
		for (RoutersChangeListener rcl : listeners)
			rcl.routersChanged();
	}
	
	public void addRoutersChangeListener(RoutersChangeListener listener) {
		listeners.add(listener);
	}
	
	public synchronized void add(String ip) {
		if (!routersMap.containsKey(ip)) {
			Router newRouter = new Router(this, ip);
			routersMap.put(ip, newRouter);
			routers.add(newRouter);
			
			notifyRoutersChangeListeners();
		}
	}
	
	public synchronized void remove(String ip) {
		if (routersMap.containsKey(ip)) {
			Router routerRemove = routersMap.get(ip);
			
			routerRemove.stopGettingData();
			
			for (RouterInterface ri : routerRemove)
				ri.removeListener();
			
			routers.remove(routerRemove);
			routersMap.remove(ip);
			notifyRoutersChangeListeners();
		}
	}
	
	public void stopRouters() {
		for (Router router : routers) 
			router.stopGettingData();
	}

	@Override
	public Iterator<Router> iterator() {
		return routers.iterator();
	}
}
