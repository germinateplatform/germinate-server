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
	public static Date getCreatedOnOrClosest(File imageFile)
	{
		try
		{
			Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

			Iterable<Directory> directories = metadata.getDirectories();
			Iterator<Directory> iterator = directories.iterator();

			Date result = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

			while (iterator.hasNext())
			{
				Directory dir = iterator.next();

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
