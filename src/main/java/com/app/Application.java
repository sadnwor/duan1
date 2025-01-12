package com.app;

import com.app.common.controller.ApplicationController;
import com.app.common.helper.MessageBox;
import com.app.common.helper.TestConnection;
import com.app.core.inuha.views.guest.LoginView;
import com.app.utils.ResourceUtils;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import io.github.cdimascio.dotenv.Dotenv;
import raven.popup.GlassPanePopup;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author InuHa
 */
public class Application extends JFrame {

    public static Application app;

    public final static int MIN_WIDTH = 1400;

    public final static int MIN_HEIGHT = 800;
    
    public static void main(String[] args) {

        Dotenv.configure().systemProperties().load();

        SwingUtilities.invokeLater(() -> {
            try {
                FlatRobotoFont.install();
                FlatLaf.registerCustomDefaultsSource("themes");
                UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
                
            } catch (UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }

	    app = new Application();
	    
	    ApplicationController.getInstance().setContext(app);
	    ApplicationController.getInstance().show(new LoginView());

	    app = ApplicationController.getInstance().getContext();
	    app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    app.setTitle(System.getProperty("APP_NAME"));
	    app.setIconImage(ResourceUtils.getImageAssets("/icons/logo.png").getImage());
	    app.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
	    app.setLocationRelativeTo(null);
	    app.setVisible(true);
	    app.pack();

	    GlassPanePopup.install(app);
	    Notifications.getInstance().setJFrame(app);
	    
	    ExecutorService executorService = Executors.newSingleThreadExecutor();
	    executorService.submit(() -> {
		if (TestConnection.test() == false) {
		    MessageBox.error(null, "Không thể kết nối tới cơ sở dữ liệu!!!");
		    System.exit(1);
		}
		executorService.shutdown();
	    });
	    
        });

    }

}
