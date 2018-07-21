package com.systematictesting.screenrecorder;

import static com.systematictesting.media.BufferFlag.SAME_DATA;
import static com.systematictesting.media.FormatKeys.EncodingKey;
import static com.systematictesting.media.FormatKeys.FrameRateKey;
import static com.systematictesting.media.FormatKeys.MIME_QUICKTIME;
import static com.systematictesting.media.FormatKeys.MediaTypeKey;
import static com.systematictesting.media.FormatKeys.MimeTypeKey;
import static com.systematictesting.media.VideoFormatKeys.COMPRESSOR_NAME_QUICKTIME_ANIMATION;
import static com.systematictesting.media.VideoFormatKeys.CompressorNameKey;
import static com.systematictesting.media.VideoFormatKeys.DepthKey;
import static com.systematictesting.media.VideoFormatKeys.ENCODING_BUFFERED_IMAGE;
import static com.systematictesting.media.VideoFormatKeys.ENCODING_QUICKTIME_ANIMATION;
import static com.systematictesting.media.VideoFormatKeys.FixedFrameRateKey;
import static com.systematictesting.media.VideoFormatKeys.HeightKey;
import static com.systematictesting.media.VideoFormatKeys.WidthKey;
import static java.lang.Math.max;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.Mixer;
import javax.swing.SwingUtilities;

import com.systematictesting.media.Buffer;
import com.systematictesting.media.BufferFlag;
import com.systematictesting.media.Codec;
import com.systematictesting.media.Format;
import com.systematictesting.media.FormatKeys.MediaType;
import com.systematictesting.media.MovieWriter;
import com.systematictesting.media.Registry;
import com.systematictesting.media.avi.AVIWriter;
import com.systematictesting.media.beans.AbstractStateModel;
import com.systematictesting.media.color.Colors;
import com.systematictesting.media.converter.CodecChain;
import com.systematictesting.media.converter.ScaleImageCodec;
import com.systematictesting.media.image.Images;
import com.systematictesting.media.math.Rational;
import com.systematictesting.media.quicktime.QuickTimeWriter;

@SuppressWarnings("rawtypes")
public class ScreenRecorder extends AbstractStateModel {

    public enum State {
        DONE, FAILED, RECORDING
    }
    private State state = State.DONE;
    private String stateMessage = null;
    public final static String ENCODING_BLACK_CURSOR = "black";
    public final static String ENCODING_WHITE_CURSOR = "white";
    public final static String ENCODING_YELLOW_CURSOR = "yellow";
    private Format fileFormat;
    protected Format mouseFormat;
    private Format screenFormat;
    private Rectangle captureArea;
    private MovieWriter w;
    protected long recordingStartTime;
    protected volatile long recordingStopTime;
    private long fileStartTime;
    private ArrayBlockingQueue<Buffer> mouseCaptures;
    private ScheduledThreadPoolExecutor screenCaptureTimer;
    protected ScheduledThreadPoolExecutor mouseCaptureTimer;
    private volatile Thread writerThread;
    private BufferedImage cursorImg;
    private BufferedImage cursorImgPressed;
    private Point cursorOffset;
    private ArrayBlockingQueue<Buffer> writerQueue;
    private Codec frameEncoder;
    private Rational outputTime;
    private Rational ffrDuration;
    private ArrayList<File> recordedFiles;
    protected int videoTrack = 0;
    protected int audioTrack = 1;
    private GraphicsDevice captureDevice;
    private ScreenGrabber screenGrabber;
    protected MouseGrabber mouseGrabber;
	private ScheduledFuture screenFuture;
    protected ScheduledFuture mouseFuture;
    protected File movieFolder;
    protected String videoFileName;
    private AWTEventListener awtEventListener;
    private long maxRecordingTime = 60 * 60 * 1000;
    private long maxFileSize = Long.MAX_VALUE;
    private Mixer mixer;

    public File getMovieFolder() {
		return movieFolder;
	}

	public String getVideoFileName() {
		return videoFileName;
	}

	public ScreenRecorder(GraphicsConfiguration cfg) throws IOException, AWTException {
        this(cfg, null,
                new Format(MediaTypeKey, MediaType.FILE,MimeTypeKey, MIME_QUICKTIME),
                new Format(MediaTypeKey, MediaType.VIDEO,EncodingKey, ENCODING_QUICKTIME_ANIMATION,CompressorNameKey, COMPRESSOR_NAME_QUICKTIME_ANIMATION, DepthKey, 24, FrameRateKey, new Rational(15, 1)), 
                new Format(MediaTypeKey, MediaType.VIDEO,EncodingKey, ENCODING_BLACK_CURSOR,FrameRateKey, new Rational(30, 1))
            );
    }

    public ScreenRecorder(GraphicsConfiguration cfg,
            Format fileFormat,
            Format screenFormat,
            Format mouseFormat) throws IOException, AWTException {
        this(cfg, null, fileFormat, screenFormat, mouseFormat);
    }

    public ScreenRecorder(GraphicsConfiguration cfg,
            Rectangle captureArea,
            Format fileFormat,
            Format screenFormat,
            Format mouseFormat) throws IOException, AWTException {
        this(cfg, null, fileFormat, screenFormat, mouseFormat, null, null);
    }

    public ScreenRecorder(GraphicsConfiguration cfg,
            Rectangle captureArea,
            Format fileFormat,
            Format screenFormat,
            Format mouseFormat,
            File movieFolder, String videoFileName) throws IOException, AWTException {

        this.fileFormat = fileFormat;
        this.screenFormat = screenFormat;
        this.mouseFormat = mouseFormat;
        if (this.mouseFormat == null) {
            this.mouseFormat = new Format(FrameRateKey, new Rational(0, 0), EncodingKey, ENCODING_BLACK_CURSOR);
        }
        //this.audioFormat = null;
        this.recordedFiles = new ArrayList<File>();
        this.captureDevice = cfg.getDevice();
        this.captureArea = (captureArea == null) ? cfg.getBounds() : captureArea;
        if (mouseFormat != null && mouseFormat.get(FrameRateKey).intValue() > 0) {
            mouseCaptures = new ArrayBlockingQueue<Buffer>(mouseFormat.get(FrameRateKey).intValue() * 2);
            if (this.mouseFormat.get(EncodingKey).equals(ENCODING_BLACK_CURSOR)) {
                cursorImg = Images.toBufferedImage(Images.createImage(ScreenRecorder.class, "images/Cursor.black.png"));
                cursorImgPressed = Images.toBufferedImage(Images.createImage(ScreenRecorder.class, "images/Cursor.black.pressed.png"));
            } else if (this.mouseFormat.get(EncodingKey).equals(ENCODING_YELLOW_CURSOR)) {
                cursorImg = Images.toBufferedImage(Images.createImage(ScreenRecorder.class, "images/Cursor.yellow.png"));
                cursorImgPressed = Images.toBufferedImage(Images.createImage(ScreenRecorder.class, "images/Cursor.yellow.pressed.png"));
            } else {
                cursorImg = Images.toBufferedImage(Images.createImage(ScreenRecorder.class, "images/Cursor.white.png"));
                cursorImgPressed = Images.toBufferedImage(Images.createImage(ScreenRecorder.class, "images/Cursor.white.pressed.png"));
            }
            cursorOffset = new Point(cursorImg.getWidth() / -2, cursorImg.getHeight() / -2);
        }
        this.movieFolder = movieFolder;
        this.videoFileName = videoFileName;
        if (this.movieFolder == null) {
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                this.movieFolder = new File(System.getProperty("user.home") + File.separator + "Videos");
            } else {
                this.movieFolder = new File(System.getProperty("user.home") + File.separator + "Movies");
            }
        }

    }

    protected MovieWriter createMovieWriter() throws IOException {
        File f = createMovieFile(fileFormat);
        recordedFiles.add(f);
        MovieWriter mw = w = Registry.getInstance().getWriter(fileFormat, f);
        Rational videoRate = Rational.max(screenFormat.get(FrameRateKey), mouseFormat.get(FrameRateKey));
        ffrDuration = videoRate.inverse();
        Format videoInputFormat = screenFormat.prepend(MediaTypeKey, MediaType.VIDEO,EncodingKey, ENCODING_BUFFERED_IMAGE,WidthKey, captureArea.width,HeightKey, captureArea.height,FrameRateKey, videoRate);
        Format videoOutputFormat = screenFormat.prepend(FrameRateKey, videoRate,MimeTypeKey, fileFormat.get(MimeTypeKey)).append(WidthKey, captureArea.width,HeightKey, captureArea.height);

        videoTrack = w.addTrack(videoOutputFormat);
//        if (audioFormat != null) {
//            audioTrack = w.addTrack(audioFormat);
//        }

        Codec encoder = Registry.getInstance().getEncoder(w.getFormat(videoTrack));
        if (encoder == null) {
            throw new IOException("No encoder for format " + w.getFormat(videoTrack));
        }
        frameEncoder = encoder;
        frameEncoder.setInputFormat(videoInputFormat);
        frameEncoder.setOutputFormat(videoOutputFormat);
        if (frameEncoder.getOutputFormat() == null) {
            throw new IOException("Unable to encode video frames in this output format:\n" + videoOutputFormat);
        }

        if (!videoInputFormat.intersectKeys(WidthKey, HeightKey).matches(
                videoOutputFormat.intersectKeys(WidthKey, HeightKey))) {
            ScaleImageCodec sic = new ScaleImageCodec();
            sic.setInputFormat(videoInputFormat);
            sic.setOutputFormat(videoOutputFormat.intersectKeys(WidthKey, HeightKey).append(videoInputFormat));
            frameEncoder = new CodecChain(sic, frameEncoder);
        }

        if (screenFormat.get(DepthKey) == 8) {
            if (w instanceof AVIWriter) {
                AVIWriter aviw = (AVIWriter) w;
                aviw.setPalette(videoTrack, Colors.createMacColors());
            } else if (w instanceof QuickTimeWriter) {
                QuickTimeWriter qtw = (QuickTimeWriter) w;
                qtw.setVideoColorTable(videoTrack, Colors.createMacColors());
            }
        }

        fileStartTime = System.currentTimeMillis();
        return mw;
    }

    protected File createMovieFile(Format fileFormat) throws IOException {
        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        } else if (!movieFolder.isDirectory()) {
            throw new IOException("\"" + movieFolder + "\" is not a directory.");
        }

        if (videoFileName==null){
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH.mm.ss");
	        File f = new File(movieFolder,"ScreenRecording " + dateFormat.format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat));
	        return f;
        } else {
        	File f = new File(movieFolder,videoFileName);
	        return f;
        }
    }
    
    public State getState() {
        return state;
    }

    public String getStateMessage() {
        return stateMessage;
    }

    private void setState(State newValue, String msg) {
        state = newValue;
        stateMessage = msg;
        fireStateChanged();
    }

    public long getStartTime() {
        return recordingStartTime;
    }

    public void start() throws IOException {
        stop();
        recordedFiles.clear();
        createMovieWriter();
        try {
            recordingStartTime = System.currentTimeMillis();
            recordingStopTime = Long.MAX_VALUE;

            outputTime = new Rational(0, 0);
            startWriter();
            try {
                startScreenCapture();
            } catch (AWTException e) {
                IOException ioe = new IOException("Start screen capture failed");
                ioe.initCause(e);
                stop();
                throw ioe;
            } catch (IOException ioe) {
                stop();
                throw ioe;
            }
            if (mouseFormat != null && mouseFormat.get(FrameRateKey).intValue() > 0) {
                startMouseCapture();
            }
//            if (audioFormat != null) {
//                try {
//                    startAudioCapture();
//                } catch (LineUnavailableException e) {
//                    IOException ioe = new IOException("Start audio capture failed");
//                    ioe.initCause(e);
//                    stop();
//                    throw ioe;
//                }
//            }
            setState(State.RECORDING, null);
        } catch (IOException e) {
            stop();
            throw e;
        }
    }

    private void startScreenCapture() throws AWTException, IOException {
        screenCaptureTimer = new ScheduledThreadPoolExecutor(1);
        int delay = max(1, (int) (1000 / screenFormat.get(FrameRateKey).doubleValue()));
        screenGrabber = new ScreenGrabber(this, recordingStartTime);
        screenFuture = screenCaptureTimer.scheduleAtFixedRate(screenGrabber, delay, delay, TimeUnit.MILLISECONDS);
        screenGrabber.setFuture(screenFuture);
    }

    private static class ScreenGrabber implements Runnable {

        private Point prevDrawnMouseLocation = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        private boolean prevMousePressed = false;
        private BufferedImage screenCapture;
        private ScreenRecorder recorder;
        private ScheduledThreadPoolExecutor screenTimer;
        private Robot robot;
        private Rectangle captureArea;
        private BufferedImage videoImg;
        private Graphics2D videoGraphics;
        private final Format mouseFormat;
        private ArrayBlockingQueue<Buffer> mouseCaptures;
        private Rational prevScreenCaptureTime;
        private BufferedImage cursorImg, cursorImgPressed;
        private Point cursorOffset;
        private int videoTrack;
        private long startTime;
        private volatile long stopTime = Long.MAX_VALUE;
        private ScheduledFuture future;
        private long sequenceNumber;

        public void setFuture(ScheduledFuture future) {
            this.future = future;
        }

        public synchronized void setStopTime(long newValue) {
            this.stopTime = newValue;
        }

        public synchronized long getStopTime() {
            return this.stopTime;
        }

        public ScreenGrabber(ScreenRecorder recorder, long startTime) throws AWTException, IOException {
            this.recorder = recorder;
            this.captureArea = recorder.captureArea;
            this.robot = new Robot(recorder.captureDevice);
            this.mouseFormat = recorder.mouseFormat;
            this.mouseCaptures = recorder.mouseCaptures;
            this.cursorImg = recorder.cursorImg;
            this.cursorImgPressed = recorder.cursorImgPressed;
            this.cursorOffset = recorder.cursorOffset;
            this.videoTrack = recorder.videoTrack;
            this.prevScreenCaptureTime = new Rational(startTime, 1000);
            this.startTime = startTime;

            Format screenFormat = recorder.screenFormat;
            if (screenFormat.get(DepthKey, 24) == 24) {
                videoImg = new BufferedImage(this.captureArea.width, this.captureArea.height, BufferedImage.TYPE_INT_RGB);
            } else if (screenFormat.get(DepthKey) == 16) {
                videoImg = new BufferedImage(this.captureArea.width, this.captureArea.height, BufferedImage.TYPE_USHORT_555_RGB);
            } else if (screenFormat.get(DepthKey) == 8) {
                videoImg = new BufferedImage(this.captureArea.width, this.captureArea.height, BufferedImage.TYPE_BYTE_INDEXED, Colors.createMacColors());
            } else {
                throw new IOException("Unsupported color depth " + screenFormat.get(DepthKey));
            }
            videoGraphics = videoImg.createGraphics();
            videoGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            videoGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
            videoGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        }

        @Override
        public void run() {
            try {
                grabScreen();
            } catch (Throwable ex) {
                ex.printStackTrace();
                screenTimer.shutdown();
                recorder.recordingFailed(ex.getMessage());
            }
        }

        private void grabScreen() throws IOException, InterruptedException {
            BufferedImage previousScreenCapture = screenCapture;
            long timeBeforeCapture = System.currentTimeMillis();
            try {
                screenCapture = robot.createScreenCapture(captureArea);
            } catch (IllegalMonitorStateException e) {
                return;
            }
            long timeAfterCapture = System.currentTimeMillis();
            if (previousScreenCapture == null) {
                previousScreenCapture = screenCapture;
            }
            videoGraphics.drawImage(previousScreenCapture, 0, 0, null);

            Buffer buf = new Buffer();
            buf.format = new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_BUFFERED_IMAGE);
            boolean hasMouseCapture = false;
            if (mouseFormat != null && mouseFormat.get(FrameRateKey).intValue() > 0) {
                while (!mouseCaptures.isEmpty() && mouseCaptures.peek().timeStamp.compareTo(new Rational(timeAfterCapture, 1000)) < 0) {
                    Buffer mouseCapture = mouseCaptures.poll();
                    if (mouseCapture.timeStamp.compareTo(prevScreenCaptureTime) > 0) {
                        if (mouseCapture.timeStamp.compareTo(new Rational(timeBeforeCapture, 1000)) < 0) {
                            previousScreenCapture = screenCapture;
                            videoGraphics.drawImage(previousScreenCapture, 0, 0, null);
                        }

                        Point mcp = (Point) mouseCapture.data;
                        prevMousePressed = (Boolean) mouseCapture.header;
                        prevDrawnMouseLocation.setLocation(mcp.x - captureArea.x, mcp.y - captureArea.y);
                        Point p = prevDrawnMouseLocation;

                        long localStopTime = getStopTime();
                        if (mouseCapture.timeStamp.compareTo(new Rational(localStopTime, 1000)) > 0) {
                            break;
                        }
                        {
                            hasMouseCapture = true;

                            if (prevMousePressed) {
                                videoGraphics.drawImage(cursorImgPressed, p.x + cursorOffset.x, p.y + cursorOffset.y, null);
                            } else {
                                videoGraphics.drawImage(cursorImg, p.x + cursorOffset.x, p.y + cursorOffset.y, null);
                            }
                            buf.clearFlags();
                            buf.data = videoImg;
                            buf.sampleDuration = mouseCapture.timeStamp.subtract(prevScreenCaptureTime);
                            buf.timeStamp = prevScreenCaptureTime.subtract(new Rational(startTime, 1000));
                            buf.track = videoTrack;
                            buf.sequenceNumber = sequenceNumber++;

                            buf.header = p.x == Integer.MAX_VALUE ? null : p;
                            recorder.write(buf);
                            prevScreenCaptureTime = mouseCapture.timeStamp;

                            videoGraphics.drawImage(previousScreenCapture, //
                                    p.x + cursorOffset.x, p.y + cursorOffset.y,//
                                    p.x + cursorOffset.x + cursorImg.getWidth() - 1, p.y + cursorOffset.y + cursorImg.getHeight() - 1,//
                                    p.x + cursorOffset.x, p.y + cursorOffset.y,//
                                    p.x + cursorOffset.x + cursorImg.getWidth() - 1, p.y + cursorOffset.y + cursorImg.getHeight() - 1,//
                                    null);
                        }

                    }
                }

                if (!hasMouseCapture && prevScreenCaptureTime.compareTo(new Rational(getStopTime(), 1000)) < 0) {
                    Point p = prevDrawnMouseLocation;
                    if (p != null) {
                        if (prevMousePressed) {
                            videoGraphics.drawImage(cursorImgPressed, p.x + cursorOffset.x, p.y + cursorOffset.y, null);
                        } else {
                            videoGraphics.drawImage(cursorImg, p.x + cursorOffset.x, p.y + cursorOffset.y, null);
                        }
                    }

                    buf.data = videoImg;
                    buf.sampleDuration = new Rational(timeAfterCapture, 1000).subtract(prevScreenCaptureTime);
                    buf.timeStamp = prevScreenCaptureTime.subtract(new Rational(startTime, 1000));
                    buf.track = videoTrack;
                    buf.sequenceNumber = sequenceNumber++;
                    buf.header = p.x == Integer.MAX_VALUE ? null : p;
                    recorder.write(buf);
                    prevScreenCaptureTime = new Rational(timeAfterCapture, 1000);
                    if (p != null) {
                        videoGraphics.drawImage(previousScreenCapture, p.x + cursorOffset.x, p.y + cursorOffset.y,p.x + cursorOffset.x + cursorImg.getWidth() - 1, p.y + cursorOffset.y + cursorImg.getHeight() - 1,//
                                p.x + cursorOffset.x, p.y + cursorOffset.y,p.x + cursorOffset.x + cursorImg.getWidth() - 1, p.y + cursorOffset.y + cursorImg.getHeight() - 1,null);
                    }
                }
            } else if (prevScreenCaptureTime.compareTo(new Rational(getStopTime(), 1000)) < 0) {
                buf.data = videoImg;
                buf.sampleDuration = new Rational(timeAfterCapture, 1000).subtract(prevScreenCaptureTime);
                buf.timeStamp = prevScreenCaptureTime.subtract(new Rational(startTime, 1000));
                buf.track = videoTrack;
                buf.sequenceNumber = sequenceNumber++;
                buf.header = null;
                recorder.write(buf);
                prevScreenCaptureTime = new Rational(timeAfterCapture, 1000);
            }

            if (timeBeforeCapture > getStopTime()) {
                future.cancel(false);
            }
        }

        public void close() {
            videoGraphics.dispose();
            videoImg.flush();
        }
    }

    protected void startMouseCapture() throws IOException {
        mouseCaptureTimer = new ScheduledThreadPoolExecutor(1);
        int delay = max(1, (int) (1000 / mouseFormat.get(FrameRateKey).doubleValue()));
        mouseGrabber = new MouseGrabber(this, recordingStartTime, mouseCaptureTimer);
        mouseFuture = mouseCaptureTimer.scheduleAtFixedRate(mouseGrabber, delay, delay, TimeUnit.MILLISECONDS);
        final MouseGrabber mouseGrabberF = mouseGrabber;
        awtEventListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event.getID() == MouseEvent.MOUSE_PRESSED) {
                    mouseGrabberF.setMousePressed(true);
                } else if (event.getID() == MouseEvent.MOUSE_RELEASED) {
                    mouseGrabberF.setMousePressed(false);
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(awtEventListener, AWTEvent.MOUSE_EVENT_MASK);
        mouseGrabber.setFuture(mouseFuture);
    }

    protected void stopMouseCapture() {
        if (mouseCaptureTimer != null) {
            mouseGrabber.setStopTime(recordingStopTime);
        }
        if (awtEventListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(awtEventListener);
            awtEventListener = null;
        }
    }

    protected void waitUntilMouseCaptureStopped() throws InterruptedException {
        if (mouseCaptureTimer != null) {
            try {
                mouseFuture.get();
            } catch (InterruptedException ex) {
            } catch (CancellationException ex) {
            } catch (ExecutionException ex) {
            }
            mouseCaptureTimer.shutdown();
            mouseCaptureTimer.awaitTermination(5000, TimeUnit.MILLISECONDS);
            mouseCaptureTimer = null;
            mouseGrabber.close();
            mouseGrabber = null;
        }
    }

    protected static class MouseGrabber implements Runnable {
        private Point prevCapturedMouseLocation = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        private ScheduledThreadPoolExecutor timer;
        private ScreenRecorder recorder;
        private GraphicsDevice captureDevice;
        private Rectangle captureArea;
        private BlockingQueue<Buffer> mouseCaptures;
        private volatile long stopTime = Long.MAX_VALUE;
        private Format format;
        private ScheduledFuture future;
        private volatile boolean mousePressed;
        private volatile boolean mouseWasPressed;
        private volatile boolean mousePressedRecorded;

        public MouseGrabber(ScreenRecorder recorder, long startTime, ScheduledThreadPoolExecutor timer) {
            this.timer = timer;
            this.format = recorder.mouseFormat;
            this.captureDevice = recorder.captureDevice;
            this.captureArea = recorder.captureArea;
            this.mouseCaptures = recorder.mouseCaptures;
        }

        public void setFuture(ScheduledFuture future) {
            this.future = future;
        }

        public void setMousePressed(boolean newValue) {
            if (newValue) {
                mouseWasPressed = true;
            }
            mousePressed = newValue;
        }

        @Override
        public void run() {
            try {
                grabMouse();
            } catch (Throwable ex) {
                ex.printStackTrace();
                timer.shutdown();
                recorder.recordingFailed(ex.getMessage());
            }
        }

        public synchronized void setStopTime(long newValue) {
            this.stopTime = newValue;
        }

        public synchronized long getStopTime() {
            return this.stopTime;
        }

        /**
         * Captures the mouse cursor.
         */
        private void grabMouse() throws InterruptedException {
            long now = System.currentTimeMillis();
            if (now > getStopTime()) {
                future.cancel(false);
                return;
            }
            PointerInfo info = MouseInfo.getPointerInfo();
            Point p = info.getLocation();
            if (!info.getDevice().equals(captureDevice)
                    || !captureArea.contains(p)) {
                p.setLocation(Integer.MAX_VALUE, Integer.MAX_VALUE);
            }

            if (!p.equals(prevCapturedMouseLocation) || mouseWasPressed != mousePressedRecorded) {
                Buffer buf = new Buffer();
                buf.format = format;
                buf.timeStamp = new Rational(now, 1000);
                buf.data = p;
                buf.header = mouseWasPressed;
                mousePressedRecorded = mouseWasPressed;
                mouseCaptures.put(buf);
                prevCapturedMouseLocation.setLocation(p);
            }
            if (!mousePressed) {
                mouseWasPressed = false;
            }
        }

        public void close() {
        }
    }

    private void startWriter() {
        writerQueue = new ArrayBlockingQueue<Buffer>(max(screenFormat.get(FrameRateKey).intValue(), mouseFormat.get(FrameRateKey).intValue()) + 1);
        writerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (writerThread == this || !writerQueue.isEmpty()) {
                        try {
                            Buffer buf = writerQueue.take();
                            doWrite(buf);
                        } catch (InterruptedException ex) {
                            break;
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    recordingFailed(e.getMessage()==null?e.toString():e.getMessage());
                }
            }
        };
        writerThread.start();
    }

    private void recordingFailed(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    stop();
                    setState(State.FAILED, msg);
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            }
        });
    }

    public void stop() throws IOException {
        if (state == State.RECORDING) {
            recordingStopTime = System.currentTimeMillis();
            stopMouseCapture();
            if (screenCaptureTimer != null) {
                screenGrabber.setStopTime(recordingStopTime);
            }
            try {
                waitUntilMouseCaptureStopped();
                if (screenCaptureTimer != null) {
                    try {
                        screenFuture.get();
                    } catch (InterruptedException ex) {
                    } catch (CancellationException ex) {
                    } catch (ExecutionException ex) {
                    }
                    screenCaptureTimer.shutdown();
                    screenCaptureTimer.awaitTermination(5000, TimeUnit.MILLISECONDS);
                    screenCaptureTimer = null;
                    screenGrabber.close();
                    screenGrabber = null;
                }
            } catch (InterruptedException ex) {
            }
            stopWriter();
            setState(State.DONE, null);
        }
    }

    private void stopWriter() throws IOException {
        Thread pendingWriterThread = writerThread;
        writerThread = null;

        try {
            if (pendingWriterThread != null) {
                pendingWriterThread.interrupt();
                pendingWriterThread.join();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (w != null) {
            w.close();
            w = null;
        }
    }
    long counter = 0;

    protected void write(Buffer buf) throws IOException, InterruptedException {
        MovieWriter writer = this.w;
        if (writer == null) {
            return;
        }
        if (buf.track == videoTrack) {
            if (writer.getFormat(videoTrack).get(FixedFrameRateKey, false) == false) {
                Buffer wbuf = new Buffer();
                frameEncoder.process(buf, wbuf);
                writerQueue.put(wbuf);
            } else {
                Rational inputTime = buf.timeStamp.add(buf.sampleDuration);
                boolean isFirst = true;
                while (outputTime.compareTo(inputTime) < 0) {
                    buf.timeStamp = outputTime;
                    buf.sampleDuration = ffrDuration;
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        buf.setFlag(SAME_DATA);
                    }
                    Buffer wbuf = new Buffer();
                    if (frameEncoder.process(buf, wbuf) != Codec.CODEC_OK) {
                        throw new IOException("Codec failed or could not process frame in a single step.");
                    }
                    writerQueue.put(wbuf);
                    outputTime = outputTime.add(ffrDuration);
                }
            }
        } else {
            Buffer wbuf = new Buffer();
            wbuf.setMetaTo(buf);
            wbuf.data = ((byte[]) buf.data).clone();
            wbuf.length = buf.length;
            wbuf.offset = buf.offset;
            writerQueue.put(wbuf);
        }
    }

    private void doWrite(Buffer buf) throws IOException {
        MovieWriter mw = w;
        long now = System.currentTimeMillis();
        if (buf.track == videoTrack && buf.isFlag(BufferFlag.KEYFRAME)
                && (mw.isDataLimitReached() || now - fileStartTime > maxRecordingTime)) {
            final MovieWriter closingWriter = mw;
            new Thread() {
                @Override
                public void run() {
                    try {
                        closingWriter.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();
            mw = createMovieWriter();

        }
        mw.write(buf.track, buf);
    }

    public long getMaxRecordingTime() {
        return maxRecordingTime;
    }

    public void setMaxRecordingTime(long maxRecordingTime) {
        this.maxRecordingTime = maxRecordingTime;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Mixer getAudioMixer() {
        return mixer;
    }

    public void setAudioMixer(Mixer mixer) {
        this.mixer = mixer;
    }
}
