package indi.liyi.example.utils.glide;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 带进度的
 */
public class ProgressResponseBody extends ResponseBody {
    private ResponseBody mDelegate;
    //  BufferedSource 是 okio 库中的输入流，这里就当作 inputStream 来使用。
    private BufferedSource mBufferedSource;
    // 下载进度监听
    private OnProgressListener mProgressListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public ProgressResponseBody(String url, ResponseBody responseBody) {
        this.mDelegate = responseBody;
        mProgressListener = ProgressController.getListener(url);
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mDelegate.contentType();
    }

    @Override
    public long contentLength() {
        return mDelegate.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(new ProgressSource(mDelegate.source()));
        }
        return mBufferedSource;
    }

    private class ProgressSource extends ForwardingSource {
        private long totalBytesRead = 0;
        private long totalSize = 0;

        public ProgressSource(Source delegate) {
            super(delegate);
        }

        @Override
        public long read(Buffer sink, long byteCount) throws IOException {
            long bytesRead = super.read(sink, byteCount);
            if (totalSize == 0) {
                totalSize = contentLength();
            }
            // 增加当前读取的字节数，如果读取完成了 bytesRead 会返回-1
            this.totalBytesRead += (bytesRead != -1 ? bytesRead : 0);
            if (mProgressListener != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 实时发送读取进度、当前已读取的字节和总字节
                        mProgressListener.onProgress(totalBytesRead * 100f / totalSize, totalSize);
                    }
                });
            }
            return bytesRead;
        }
    }
}
