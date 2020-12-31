package com.pro100kryto.server.modules.apacheftpserver;

import com.pro100kryto.server.logger.ILogger;
import org.apache.ftpserver.ftplet.*;

import java.io.IOException;

public class FtpletLogger implements Ftplet {
    private final ILogger logger;

    public FtpletLogger(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void init(FtpletContext ftpletContext) throws FtpException {
        logger.writeInfo("ftplet init"
                + System.lineSeparator()
                + "Thread #" + Thread.currentThread().getId());
    }

    @Override
    public void destroy() {
        logger.writeInfo("ftplet destroy"
                + ". Thread #" + Thread.currentThread().getId());
    }

    @Override
    public FtpletResult beforeCommand(FtpSession ftpSession, FtpRequest ftpRequest) throws FtpException, IOException {
        logger.writeInfo("ftplet command "
                + ftpSession.getUserArgument() + " : "
                + ftpSession.toString() + " | " + ftpRequest.getArgument()
                + " : " + ftpRequest.getCommand()
                + " : " + ftpRequest.getRequestLine()
                + ". Thread #" + Thread.currentThread().getId());
        return FtpletResult.DEFAULT;
    }

    @Override
    public FtpletResult afterCommand(FtpSession ftpSession, FtpRequest ftpRequest, FtpReply ftpReply) throws FtpException, IOException {
        return FtpletResult.DEFAULT;
    }

    @Override
    public FtpletResult onConnect(FtpSession ftpSession) throws FtpException, IOException {
        logger.writeInfo("ftplet onConnect " + ftpSession.getUserArgument() + " : " + ftpSession.toString()
                + ". Thread #" + Thread.currentThread().getId());
        return FtpletResult.DEFAULT;
    }

    @Override
    public FtpletResult onDisconnect(FtpSession ftpSession) throws FtpException, IOException {
        logger.writeInfo("ftplet onDisconnect " + ftpSession.getUserArgument() + " : " + ftpSession.toString()
                + ". Thread #" + Thread.currentThread().getId());
        return FtpletResult.DEFAULT;
    }
}
