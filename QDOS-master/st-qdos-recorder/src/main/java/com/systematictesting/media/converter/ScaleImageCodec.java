package com.systematictesting.media.converter;

import static com.systematictesting.media.BufferFlag.DISCARD;
import static com.systematictesting.media.FormatKeys.EncodingKey;
import static com.systematictesting.media.FormatKeys.MIME_JAVA;
import static com.systematictesting.media.FormatKeys.MediaTypeKey;
import static com.systematictesting.media.FormatKeys.MimeTypeKey;
import static com.systematictesting.media.VideoFormatKeys.DepthKey;
import static com.systematictesting.media.VideoFormatKeys.ENCODING_BUFFERED_IMAGE;
import static com.systematictesting.media.VideoFormatKeys.HeightKey;
import static com.systematictesting.media.VideoFormatKeys.WidthKey;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

import com.systematictesting.media.AbstractVideoCodec;
import com.systematictesting.media.Buffer;
import com.systematictesting.media.Format;
import com.systematictesting.media.FormatKeys.MediaType;

public class ScaleImageCodec extends AbstractVideoCodec {

	public ScaleImageCodec() {
		super(new Format[] {
				new Format(MediaTypeKey, MediaType.VIDEO, MimeTypeKey, MIME_JAVA, EncodingKey, ENCODING_BUFFERED_IMAGE), //
		}, new Format[] {
				new Format(MediaTypeKey, MediaType.VIDEO, MimeTypeKey, MIME_JAVA, EncodingKey, ENCODING_BUFFERED_IMAGE), //
		});
		name = "Scale Image";
	}

	@Override
	public Format setOutputFormat(Format f) {
		if (!f.containsKey(WidthKey) || !f.containsKey(HeightKey)) {
			throw new IllegalArgumentException("Output format must specify width and height.");
		}
		Format fNew = super.setOutputFormat(f.prepend(DepthKey, 24));
		return fNew;
	}

	@Override
	public int process(Buffer in, Buffer out) {
		out.setMetaTo(in);
		out.format = outputFormat;

		if (in.isFlag(DISCARD)) {
			return CODEC_OK;
		}
		BufferedImage imgIn = (BufferedImage) in.data;
		if (imgIn == null) {
			out.setFlag(DISCARD);
			return CODEC_FAILED;
		}

		BufferedImage imgOut = null;
		if (out.data instanceof BufferedImage) {
			imgOut = (BufferedImage) out.data;
			if (imgOut.getWidth() != outputFormat.get(WidthKey) || imgOut.getHeight() != outputFormat.get(HeightKey)//
					|| imgOut.getType() != imgIn.getType()) {
				imgOut = null;
			}
		}
		if (imgOut == null) {
			if (imgIn.getColorModel() instanceof IndexColorModel) {
				imgOut = new BufferedImage(outputFormat.get(WidthKey), outputFormat.get(HeightKey), imgIn.getType(),
						(IndexColorModel) imgIn.getColorModel());
			} else {
				imgOut = new BufferedImage(outputFormat.get(WidthKey), outputFormat.get(HeightKey), imgIn.getType());
			}

		}
		Graphics2D g = imgOut.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(imgIn, 0, 0, imgOut.getWidth() - 1, imgOut.getHeight() - 1, 0, 0, imgIn.getWidth() - 1,
				imgIn.getHeight() - 1, null);
		g.dispose();

		out.data = imgOut;

		return CODEC_OK;
	}
}
