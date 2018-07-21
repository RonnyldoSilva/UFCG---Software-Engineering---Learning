package com.systematictesting.media.quicktime;

import static com.systematictesting.media.BufferFlag.DISCARD;
import static com.systematictesting.media.BufferFlag.KEYFRAME;
import static com.systematictesting.media.FormatKeys.EncodingKey;
import static com.systematictesting.media.FormatKeys.MIME_JAVA;
import static com.systematictesting.media.FormatKeys.MIME_QUICKTIME;
import static com.systematictesting.media.FormatKeys.MediaTypeKey;
import static com.systematictesting.media.FormatKeys.MimeTypeKey;
import static com.systematictesting.media.VideoFormatKeys.DataClassKey;
import static com.systematictesting.media.VideoFormatKeys.DepthKey;
import static com.systematictesting.media.VideoFormatKeys.ENCODING_BUFFERED_IMAGE;
import static com.systematictesting.media.VideoFormatKeys.ENCODING_QUICKTIME_RAW;
import static com.systematictesting.media.VideoFormatKeys.HeightKey;
import static com.systematictesting.media.VideoFormatKeys.WidthKey;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;

import com.systematictesting.media.AbstractVideoCodec;
import com.systematictesting.media.Buffer;
import com.systematictesting.media.Format;
import com.systematictesting.media.FormatKeys.MediaType;
import com.systematictesting.media.io.SeekableByteArrayOutputStream;

public class RawCodec extends AbstractVideoCodec {

	public RawCodec() {
		super(new Format[] {
				new Format(MediaTypeKey, MediaType.VIDEO, MimeTypeKey, MIME_JAVA, EncodingKey, ENCODING_BUFFERED_IMAGE), //
		}, new Format[] {
				new Format(MediaTypeKey, MediaType.VIDEO, MimeTypeKey, MIME_QUICKTIME, EncodingKey,
						ENCODING_QUICKTIME_RAW, DataClassKey, byte[].class, DepthKey, 8), //
				new Format(MediaTypeKey, MediaType.VIDEO, MimeTypeKey, MIME_QUICKTIME, EncodingKey,
						ENCODING_QUICKTIME_RAW, DataClassKey, byte[].class, DepthKey, 16), //
				new Format(MediaTypeKey, MediaType.VIDEO, MimeTypeKey, MIME_QUICKTIME, EncodingKey,
						ENCODING_QUICKTIME_RAW, DataClassKey, byte[].class, DepthKey, 24), //
				new Format(MediaTypeKey, MediaType.VIDEO, MimeTypeKey, MIME_QUICKTIME, EncodingKey,
						ENCODING_QUICKTIME_RAW, DataClassKey, byte[].class, DepthKey, 32), //
		});
	}

	public void writeKey8(OutputStream out, byte[] data, int width, int height, int offset, int scanlineStride)
			throws IOException {
		for (int xy = offset, ymax = offset + height * scanlineStride; xy < ymax; xy += scanlineStride) {
			out.write(data, xy, width);
		}
	}

	public void writeKey16(OutputStream out, short[] data, int width, int height, int offset, int scanlineStride)
			throws IOException {

		byte[] bytes = new byte[width * 2];
		for (int xy = offset, ymax = offset + height * scanlineStride; xy < ymax; xy += scanlineStride) {
			for (int x = 0, i = 0; x < width; x++, i += 2) {
				int pixel = data[xy + x];
				bytes[i] = (byte) (pixel >> 8);
				bytes[i + 1] = (byte) (pixel);
			}
			out.write(bytes, 0, bytes.length);
		}
	}

	public void writeKey24(OutputStream out, int[] data, int width, int height, int offset, int scanlineStride)
			throws IOException {
		byte[] bytes = new byte[width * 3];
		for (int xy = offset, ymax = offset + height * scanlineStride; xy < ymax; xy += scanlineStride) {
			for (int x = 0, i = 0; x < width; x++, i += 3) {
				int pixel = data[xy + x];
				bytes[i] = (byte) (pixel >> 16);
				bytes[i + 1] = (byte) (pixel >> 8);
				bytes[i + 2] = (byte) (pixel);
			}
			out.write(bytes, 0, bytes.length);
		}
	}

	public void writeKey32(OutputStream out, int[] data, int width, int height, int offset, int scanlineStride)
			throws IOException {
		byte[] bytes = new byte[width * 4];
		for (int xy = offset, ymax = offset + height * scanlineStride; xy < ymax; xy += scanlineStride) {
			for (int x = 0, i = 0; x < width; x++, i += 4) {
				int pixel = data[xy + x];
				bytes[i] = (byte) (pixel >> 24);
				bytes[i + 1] = (byte) (pixel >> 16);
				bytes[i + 2] = (byte) (pixel >> 8);
				bytes[i + 3] = (byte) (pixel);
			}
			out.write(bytes, 0, bytes.length);
		}
	}

	public void writeKey24(OutputStream out, BufferedImage image) throws IOException {

		int width = image.getWidth();
		int height = image.getHeight();
		WritableRaster raster = image.getRaster();
		int[] rgb = new int[width * 3];
		byte[] bytes = new byte[width * 3];
		for (int y = 0; y < height; y++) {
			rgb = raster.getPixels(0, y, width, 1, rgb);
			for (int k = 0, n = width * 3; k < n; k++) {
				bytes[k] = (byte) rgb[k];
			}
			out.write(bytes);
		}
	}

	@Override
	public int process(Buffer in, Buffer out) {
		out.setMetaTo(in);
		if (in.isFlag(DISCARD)) {
			return CODEC_OK;
		}
		out.format = outputFormat;

		SeekableByteArrayOutputStream tmp;
		if (out.data instanceof byte[]) {
			tmp = new SeekableByteArrayOutputStream((byte[]) out.data);
		} else {
			tmp = new SeekableByteArrayOutputStream();
		}
		Format vf = outputFormat;

		Rectangle r;
		int scanlineStride;
		if (in.data instanceof BufferedImage) {
			BufferedImage image = (BufferedImage) in.data;
			WritableRaster raster = image.getRaster();
			scanlineStride = raster.getSampleModel().getWidth();
			r = raster.getBounds();
			r.x -= raster.getSampleModelTranslateX();
			r.y -= raster.getSampleModelTranslateY();
		} else {
			r = new Rectangle(0, 0, vf.get(WidthKey), vf.get(HeightKey));
			scanlineStride = vf.get(WidthKey);
		}

		try {
			switch (vf.get(DepthKey)) {
			case 8: {
				writeKey8(tmp, getIndexed8(in), r.width, r.height, r.x + r.y * scanlineStride, scanlineStride);
				break;
			}
			case 16: {
				writeKey16(tmp, getRGB15(in), r.width, r.height, r.x + r.y * scanlineStride, scanlineStride);
				break;
			}
			case 24: {
				writeKey24(tmp, getRGB24(in), r.width, r.height, r.x + r.y * scanlineStride, scanlineStride);
				break;
			}
			case 32: {
				writeKey24(tmp, getARGB32(in), r.width, r.height, r.x + r.y * scanlineStride, scanlineStride);
				break;
			}
			default: {
				out.setFlag(DISCARD);
				return CODEC_FAILED;
			}
			}

			out.format = outputFormat;
			out.sampleCount = 1;
			out.setFlag(KEYFRAME);
			out.data = tmp.getBuffer();
			out.offset = 0;
			out.length = (int) tmp.getStreamPosition();
			return CODEC_OK;
		} catch (IOException ex) {
			ex.printStackTrace();
			out.setFlag(DISCARD);
			return CODEC_FAILED;
		}
	}
}
