package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import internal.Router;
import internal.Routers;

public class AppWindow extends JFrame {
	private static final double APP_WIDTH = 0.5, APP_HEIGHT = 0.6;
	private static final String APP_NAME = "Bandwith Monitor";
	
	private JTextField tfIp = new JTextField(12);
	private JButton btnAddRouter = new JButton("Add");
	private JButton btnRemoveRouter = new JButton("Remove");
	
	private Routers routers = new Routers();
	private InfoPanel ip = new InfoPanel();
	private RoutersPanel rp = new RoutersPanel(routers, ip);
	
	public AppWindow() {
		setTitle(APP_NAME);
		setNativeLookFeel();
		setCloseOperation();
		setSize();
		setLocation();
		createLayout();
		setButtonActions();
		setVisible(true);
	}

	private void setNativeLookFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}
	}

	private void setCloseOperation() {
		routers.stopRouters();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void setSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int appWidth = (int)(APP_WIDTH * screenSize.width);
		int appHeight = (int)(APP_HEIGHT * screenSize.height);
		setPreferredSize(new Dimension(appWidth, appHeight));
		pack();
	}
	
	private void setLocation() {
		setLocationRelativeTo(null);
	}
	
	private void createLayout() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(tfIp);
		Icon addIcon = new ImageIcon("res/plus.png");
		btnAddRouter.setIcon(addIcon);
		Icon removeIcon = new ImageIcon("res/cross.png");
		btnRemoveRouter.setIcon(removeIcon);
		btnAddRouter.setFocusPainted(false);
		btnRemoveRouter.setFocusPainted(false);
		topPanel.add(btnAddRouter);
		topPanel.add(btnRemoveRouter);
		
		routers.addRoutersChangeListener(rp);
		
		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		add(rp, BorderLayout.WEST);
		add(ip, BorderLayout.CENTER);
	}
	
	private void setButtonActions() {
		btnAddRouter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String ip = tfIp.getText();
				if (!ip.isEmpty())
					routers.add(ip);
			}
		});
		
		btnRemoveRouter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			    Router remove = rp.getSelectedRouter();
			    if (remove != null) {
			    	routers.remove(remove.getIp());
			    }
			}
		});
	}

}
