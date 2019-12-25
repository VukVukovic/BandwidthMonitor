package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import internal.Router;
import internal.RouterInterface;
import internal.Routers;
import internal.RoutersChangeListener;

public class RoutersPanel extends JPanel implements RoutersChangeListener {
	private volatile DefaultListModel<RouterListItem> model =  new DefaultListModel<>();
	private JList<RouterListItem> list = new JList<>(model);
	private Routers routers;
	
	private Router selectedRouter = null;
	
	public RoutersPanel(Routers routers, InfoPanel infoPanel) {
		this.routers = routers;
		list.setCellRenderer(new RouterListCellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setOpaque(false);
		
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (arg0.getValueIsAdjusting()) return;
				selectedRouter = null;
				
				RouterListItem item = list.getSelectedValue();
				if (item == null) return;
				if (item instanceof InterfaceItem) {
					InterfaceItem intItem = (InterfaceItem)item;
					RouterInterface routerInterface = intItem.getRouterInterface();
					infoPanel.setInterface(routerInterface);
				} else {
					selectedRouter = ((RouterItem)item).getRouter();					
					infoPanel.clearInterface();
				}
			}
		});
		
		JScrollPane scrollBar = new JScrollPane(list,
	            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		
		setLayout(new BorderLayout());
		add(scrollBar, BorderLayout.CENTER);
	}

	@Override
	public void routersChanged() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				model.clear();
				
				for (Router router : routers)  {
					model.addElement(new RouterItem(router));
					
					for (RouterInterface routerInterface : router)
						model.addElement(new InterfaceItem(routerInterface));
				}
				list.setModel(model);
				selectedRouter = null;
			}
		});
	}
	
	public Router getSelectedRouter() {
		return selectedRouter;
	}
}

abstract class RouterListItem extends JLabel {
	public RouterListItem(String labelText, Icon icon) {
		setText(labelText);
		setIcon(icon);
	}
	
	public abstract boolean isRouter();
}

class RouterItem extends RouterListItem {
	private static final Icon routerIcon = new ImageIcon("res/router.png");
	private Router router;
	
	public RouterItem(Router router) {
		super(router.getIp(), routerIcon);
		this.router = router;
	}
	
	@Override
	public boolean isRouter() {
		return true;
	}
	
	public Router getRouter() {
		return router;
	}
	
}

class InterfaceItem extends RouterListItem {
	private static final Icon interfaceIcon = new ImageIcon("res/interface.png");
	private RouterInterface routerInterface;
	
	public InterfaceItem(RouterInterface routerInterface) {
		super(routerInterface.getName(), interfaceIcon);
		this.routerInterface = routerInterface;
		this.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
	}

	@Override
	public boolean isRouter() {
		return false;
	}
	
	public RouterInterface getRouterInterface() {
		return routerInterface;
	}
}

class RouterListCellRenderer extends DefaultListCellRenderer {	
	@Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        RouterListItem item = (RouterListItem)value;
        
        item.setOpaque(isSelected);
        item.setBackground(Color.LIGHT_GRAY);
        
        return item;
    }
}