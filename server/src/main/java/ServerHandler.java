import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

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
                updateCloudListView(ctx);
            }
            if (message instanceof DownloadRequest) {
                DownloadRequest downloadRequest = (DownloadRequest) message;
                if (Files.exists(Paths.get(cloudStoragePath + downloadRequest.getFilename()))) {
                    FileMessage fileMessage = new FileMessage(Paths.get(cloudStoragePath + downloadRequest.getFilename()));
                    ctx.writeAndFlush(fileMessage);
                }
            }
            if (message instanceof UpdateCloudMessage) {
                updateCloudListView(ctx);
            }
            if (message instanceof DeleteRequest) {
                DeleteRequest deleteRequest = (DeleteRequest) message;
                Files.delete(Paths.get(cloudStoragePath + deleteRequest.getFilename()));
                updateCloudListView(ctx);
            }
        } finally {
            ReferenceCountUtil.release(message);
        }
    }

    private void updateCloudListView(ChannelHandlerContext ctx) {
        try {
            ArrayList<String> serverFileList = new ArrayList<>();
            Files.list(Paths.get(cloudStoragePath)).map(p -> p.getFileName().toString()).forEach(serverFileList::add);
            ctx.writeAndFlush(new UpdateCloudMessage(serverFileList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
