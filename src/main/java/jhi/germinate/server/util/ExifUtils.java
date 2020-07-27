package jhi.germinate.server.util;

import com.drew.imaging.*;
import com.drew.metadata.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class ExifUtils
{
	/**
	 * Reads the provided file and if it supports EXIF data will extract and return the closest match to the original creation date/time.
	 *
	 * @param imageFile The image file containing the EXIF information
	 * @return The closest match to the original creation date/time or null if none is found or the provided file does not support EXIF information.
	 */
	public static Date getCreatedOnOrClosest(File imageFile)
	{
		try
		{
			Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

			Date result = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

			for (Directory dir : metadata.getDirectories())
			{
				if (dir.getName().contains("PrintIM"))
					continue;

				Collection<Tag> tags = dir.getTags();

				for (Tag tag : tags)
				{
					try
					{
						switch (tag.getTagType())
						{
							case 0x9003:
								// Return this immediately, it's 'Exif.Photo.DateTimeOriginal'
								return sdf.parse(tag.getDescription());
							case 0x0132:
								// This is 'Exif.Image.DateTime', only use it if nothing else was found yet.
								if (result == null)
									result = sdf.parse(tag.getDescription());
								break;
							case 0x9004:
								// This is 'Exif.Photo.DateTimeDigitized', prefer it to the one above.
								result = sdf.parse(tag.getDescription());
								break;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}

			return result;
		}
		catch (IOException | ImageProcessingException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
