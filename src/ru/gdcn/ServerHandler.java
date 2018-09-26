package ru.gdcn;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

import java.nio.charset.StandardCharsets;

public class ServerHandler extends SimpleChannelHandler {

    private static String className = "ServerHandler";

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String message = getStringFromBuffer((ChannelBuffer) e.getMessage());
        Logger.log("Получено сообщение: " + message, className);

        String[] parsedMessage = message.split("/d/");
        for (String string : parsedMessage) {
            if (!string.equals("")) ;
            ServerMessage.read(string, e.getChannel());
        }
    }

    private String getStringFromBuffer(ChannelBuffer buffer) {
        int bufSize = buffer.readableBytes();
        byte[] byteBuffer = new byte[bufSize];
        buffer.readBytes(byteBuffer);
        return new String(byteBuffer, StandardCharsets.UTF_8);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelClosed(ctx, e);
        Logger.log("Канал закрылся.", className);
        ServerMethods.disconnectReceived(e.getChannel());
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        // /d/ выступает в качестве разделителя между сообщениями
        String message = e.getMessage() + "/d/";
        Channels.write(
                ctx,
                e.getFuture(),
                ChannelBuffers.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8)),
                e.getRemoteAddress()
        );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Logger.log("Cловил Exception.", className);
        e.getCause().printStackTrace();
        ServerMethods.disconnectReceived(e.getChannel());
//        e.getChannel().close();
    }
}
