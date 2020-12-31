package com.pro100kryto.server.modules;

import com.pro100kryto.server.Server;
import com.pro100kryto.server.module.Module;
import com.pro100kryto.server.modules.apacheftpserver.DigestPasswordEncryptor;
import com.pro100kryto.server.modules.apacheftpserver.FtpletLogger;
import com.pro100kryto.server.modules.apacheftpserver.NoPasswordEncryptor;
import com.pro100kryto.server.service.IServiceControl;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.message.MessageResourceFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.io.File;
import java.security.Permissions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ApacheFtpServerModule extends Module {
    private FtpServer ftpServer;
    private UserManager userManager;

    public ApacheFtpServerModule(IServiceControl service, String name) {
        super(service, name);
    }

    @Override
    protected void startAction() throws Throwable {

        // settings

        final String address = settings.getOrDefault("ftp-address", "");
        if (address.equals(""))
            throw new Exception("ftp-address not specified");
        final int port = Integer.parseInt(settings.getOrDefault("ftp-port", "21"));
        final String fileUsersPath = settings.getOrDefault("ftp-users-file",
                Server.getInstance().getWorkingPath() + File.separator + "users.properties");
        final String encryptAlgorithm = settings.getOrDefault("ftp-pass-encrypt-algorithm", "SHA-256");
        final String homeDirPath = settings.getOrDefault("ftp-directory", "");
        if (homeDirPath.equals(""))
            throw new Exception("ftp-directory not specified");
        final boolean userAnonymEnabled = settings.getOrDefault("ftp-users-anonymous", "").equals("true");
        final int loginFailureDelay = Integer.parseInt(settings.getOrDefault("ftp-login-fail-delay", "500"));
        final int loginMax = Integer.parseInt(settings.getOrDefault("ftp-login-max", "15"));
        final int threadsMax = Integer.parseInt(settings.getOrDefault("ftp-threads-max", "0"));
        final boolean verboseEnables = !settings.getOrDefault("logger-verbose", "").equals("false");

        // new

        final FtpServerFactory serverFactory = new FtpServerFactory();
        final ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(port);
        listenerFactory.setServerAddress(address);
        serverFactory.addListener("main", listenerFactory.createListener());

        // users

        final PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        final File fileUsers = new File(fileUsersPath);
        if (!fileUsers.exists()) fileUsers.createNewFile();
        userManagerFactory.setFile(fileUsers);

        if (encryptAlgorithm.isEmpty() || encryptAlgorithm.equalsIgnoreCase("raw")){
            userManagerFactory.setPasswordEncryptor(new NoPasswordEncryptor());
        } else {
            final String encryptSalt = settings.getOrDefault("ftp-pass-encrypt-salt", UUID.randomUUID().toString());
            userManagerFactory.setPasswordEncryptor(new DigestPasswordEncryptor(encryptAlgorithm, encryptSalt));
        }

        userManager = userManagerFactory.createUserManager();
        serverFactory.setUserManager(userManager);

        // conn

        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(userAnonymEnabled);
        connectionConfigFactory.setLoginFailureDelay(loginFailureDelay);
        connectionConfigFactory.setMaxLogins(loginMax);
        connectionConfigFactory.setMaxThreads(threadsMax);
        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());

        // ftplet

        final Map<String, Ftplet> ftpletMap = new HashMap<String, Ftplet>();
        if (verboseEnables)
            ftpletMap.put("logger", new FtpletLogger(logger));
        serverFactory.setFtplets(ftpletMap);

        /*
        // -------

        MessageResourceFactory messageResourceFactory = new MessageResourceFactory();
        messageResourceFactory.se
        serverFactory.setMessageResource();

        */

        // anonymous

        if (userAnonymEnabled){
            final BaseUser userAnonym = new BaseUser();
            userAnonym.setName("anonymous");
            userAnonym.setHomeDirectory(new File(homeDirPath).getAbsolutePath());
            userManager.save(userAnonym);
        }

        // start

        ftpServer = serverFactory.createServer();
        ftpServer.start();
    }

    @Override
    protected void stopAction(boolean force) throws Throwable {
        ftpServer.stop();
    }

    @Override
    public void tick() throws Throwable {
        Thread.yield();
    }
}
