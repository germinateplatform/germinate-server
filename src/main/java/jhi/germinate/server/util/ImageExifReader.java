package jhi.germinate.server.util;

import com.drew.imaging.*;
import com.drew.lang.GeoLocation;
import com.drew.metadata.*;
import com.drew.metadata.exif.GpsDirectory;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.records.ImagesRecord;
import jhi.germinate.server.database.pojo.Exif;
import jhi.germinate.server.resource.images.ImageResource;
import org.jooq.DSLContext;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

import static jhi.germinate.server.database.codegen.tables.Images.*;

/**
 * @author Sebastian Raubach
 */
public class ImageExifReader implements Callable<ImageExifReader.ExifResult>
{
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	private       ImagesRecord     image;

	public ImageExifReader(ImagesRecord image)
	{
		this.image = image;
	}

	@Override
	public ExifResult call()
	{
		ExifResult exif = null;
		Timestamp date;

		try
		{
			File parent = new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images");
			File file = new File(new File(parent, ImageResource.ImageType.database.name()), image.getPath());
			exif = getExif(file);

			if (exif.exif.getDateTimeOriginal() != null)
				date = new Timestamp(exif.exif.getDateTimeOriginal().getTime());
			else if (exif.exif.getDateTimeDigitized() != null)
				date = new Timestamp(exif.exif.getDateTimeDigitized().getTime());
			else if (exif.exif.getDateTime() != null)
				date = new Timestamp(exif.exif.getDateTime().getTime());
			else
				date = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			exif = null;
			date = null;
		}

		if (exif != null)
		{
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);
				context.update(IMAGES)
					   .set(IMAGES.EXIF, exif.exif)
					   .set(IMAGES.CREATED_ON, date)
					   .where(IMAGES.ID.eq(image.getId()))
					   .execute();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return exif;
	}

	private ExifResult getExif(File image)
		throws ImageProcessingException, IOException
	{
		Metadata metadata = ImageMetadataReader.readMetadata(image);

		Exif exif = new Exif();
		List<String> keywords = new ArrayList<>();

		// See whether it has GPS data
		Collection<GpsDirectory> gpsDirectories = metadata.getDirectoriesOfType(GpsDirectory.class);
		for (GpsDirectory gpsDirectory : gpsDirectories)
		{
			// Try to read out the location, making sure it's non-zero
			GeoLocation geoLocation = gpsDirectory.getGeoLocation();
			if (geoLocation != null && !geoLocation.isZero())
			{
				// Add to our collection for use below
				exif.setGpsLatitude(geoLocation.getLatitude())
					.setGpsLongitude(geoLocation.getLongitude())
					.setGpsTimestamp(gpsDirectory.getGpsDate());
				// TODO: How to get the altitude?
				break;
			}
		}

		Iterable<Directory> directories = metadata.getDirectories();
		for (Directory dir : directories)
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
						case 0x0100:
						case 0xbc80:
							exif.setImageWidth(tag.getDescription());
							break;
						case 0x0101:
						case 0xbc81:
							exif.setImageHeight(tag.getDescription());
							break;
						case 0x0103:
							exif.setCompression(tag.getDescription());
							break;
						case 0x0106:
							exif.setPhotometricInterpretation(tag.getDescription());
							break;
						case 0x010f:
							exif.setCameraMake(tag.getDescription());
							break;
						case 0x0110:
							exif.setCameraModel(tag.getDescription());
							break;
						case 0x0112:
							exif.setOrientation(tag.getDescription());
							break;
						case 0x0115:
							exif.setSamplesPerPixel(tag.getDescription());
							break;
						case 0x011a:
							exif.setxResolution(tag.getDescription());
							break;
						case 0x011b:
							exif.setyResolution(tag.getDescription());
							break;
						case 0x829a:
							exif.setExposureTime(tag.getDescription());
							break;
						case 0x829d:
							exif.setfNumber(tag.getDescription());
							break;
						case 0x8827:
							exif.setIsoSpeedRatings(tag.getDescription());
							break;
						case 0x9000:
							exif.setExifVersion(tag.getDescription());
							break;
						case 0x9003:
							exif.setDateTimeOriginal(sdf.parse(tag.getDescription()));
							break;
						case 0x0132:
							exif.setDateTime(sdf.parse(tag.getDescription()));
							break;
						case 0x9004:
							exif.setDateTimeDigitized(sdf.parse(tag.getDescription()));
							break;
						case 0x9201:
							exif.setShutterSpeedValue(tag.getDescription());
							break;
						case 0x9202:
							exif.setApertureValue(tag.getDescription());
							break;
						case 0x9207:
							exif.setMeteringMode(tag.getDescription());
							break;
						case 0x9209:
							exif.setFlash(tag.getDescription());
							break;
						case 0x920a:
							exif.setFocalLength(tag.getDescription());
							break;
						case 0x9217:
						case 0xa217:
							exif.setSensingMethod(tag.getDescription());
							break;
						case 0xa001:
							exif.setColorSpace(tag.getDescription());
							break;
						case 0xa002:
							exif.setExifImageWidth(tag.getDescription());
							break;
						case 0xa003:
							exif.setExifImageHeight(tag.getDescription());
							break;
						case 0xa402:
							exif.setExposureMode(tag.getDescription());
							break;
						case 0xa403:
							exif.setWhiteBalanceMode(tag.getDescription());
							break;
						case 0xa404:
							exif.setDigitalZoomRatio(tag.getDescription());
							break;
						case 0xa406:
							exif.setSceneCaptureType(tag.getDescription());
							break;
						case 0xa407:
							exif.setGainControl(tag.getDescription());
							break;
						case 0xa408:
						case 0xfe54:
							exif.setContrast(tag.getDescription());
							break;
						case 0xa409:
						case 0xfe55:
							exif.setSaturation(tag.getDescription());
							break;
						case 0xa40a:
						case 0xfe56:
							exif.setSharpness(tag.getDescription());
							break;
						case 0xa433:
							exif.setLensMake(tag.getDescription());
							break;
						case 0xa434:
							exif.setLensModel(tag.getDescription());
							break;
						case 0xfe4e:
							exif.setWhiteBalance(tag.getDescription());
							break;
						case 0xfe51:
							exif.setExposure(tag.getDescription());
							break;
						case 0x9204:
							exif.setExposureBiasValue(tag.getDescription());
							break;
						case 0x8822:
							exif.setExposureProgram(tag.getDescription());
							break;
						case 0xa300:
							exif.setFileSource(tag.getDescription());
							break;
						case 0xa301:
							exif.setSceneType(tag.getDescription());
							break;
						case 0x9286:
							exif.setUserComment(tag.getDescription());
							break;
						case 0x0219:
							String k = tag.getDescription();
							if (k != null)
								keywords.addAll(Arrays.asList(k.split(";")));
							break;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return new ExifResult(exif, keywords);
	}

	public static class ExifResult
	{
		private final Exif         exif;
		private final List<String> keyword;

		public ExifResult(Exif exif, List<String> keyword)
		{
			this.exif = exif;
			this.keyword = keyword;
		}
	}
}
