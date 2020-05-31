import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private String cloudStoragePath = "server\\cloud_storage/";

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        try {
            if (message instanceof FileMessage) {
                FileMessage fileMessage = (FileMessage) message;
                Files.write(Paths.get(cloudStoragePath + fileMessage.getFilename()), fileMessage.getData(), StandardOpenOption.CREATE);
            }
        } finally {
            ReferenceCountUtil.release(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
