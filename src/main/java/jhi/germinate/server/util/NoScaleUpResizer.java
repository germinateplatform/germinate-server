package jhi.germinate.server.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.ImageFilter;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class NoScaleUpResizer implements ImageFilter
{
	private final int maxWidth;
	private final int maxHeight;

	public NoScaleUpResizer(int maxWidth, int maxHeight)
	{
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	@Override
	public BufferedImage apply(BufferedImage img)
	{
		if (img.getWidth() <= maxWidth && img.getHeight() <= maxHeight)
		{
			return img;
		}
		try
		{
			return Thumbnails.of(img).size(maxWidth, maxHeight).asBufferedImage();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}