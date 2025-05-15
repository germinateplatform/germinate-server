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

import static com.drew.metadata.exif.ExifDirectoryBase.*;
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

			if (!file.exists())
				return null;

			try
			{
				// Auto rotate the image in place
				String[] commands = {"mogrify", "-auto-orient", file.getAbsolutePath()};
				Process p = new ProcessBuilder(commands).start();
				p.waitFor();
			}
			catch (Exception e)
			{
				// Ignore errors, it means mogrify isn't available or can't auto rotate the image
			}

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
						case TAG_IMAGE_WIDTH:
						case 0xbc80:
							exif.setImageWidth(tag.getDescription());
							break;
						case TAG_IMAGE_HEIGHT:
						case 0xbc81:
							exif.setImageHeight(tag.getDescription());
							break;
						case TAG_COMPRESSION:
							exif.setCompression(tag.getDescription());
							break;
						case TAG_PHOTOMETRIC_INTERPRETATION:
							exif.setPhotometricInterpretation(tag.getDescription());
							break;
						case TAG_MAKE:
							exif.setCameraMake(tag.getDescription());
							break;
						case TAG_MODEL:
							exif.setCameraModel(tag.getDescription());
							break;
						case TAG_ORIENTATION:
							try
							{
								Integer code = dir.getInt(TAG_ORIENTATION);
								exif.setOrientationCode(code);
							}
							catch (Exception e)
							{
								// Do nothing here
							}
							exif.setOrientation(tag.getDescription());
							break;
						case TAG_SAMPLES_PER_PIXEL:
							exif.setSamplesPerPixel(tag.getDescription());
							break;
						case TAG_X_RESOLUTION:
							exif.setxResolution(tag.getDescription());
							break;
						case TAG_Y_RESOLUTION:
							exif.setyResolution(tag.getDescription());
							break;
						case TAG_EXPOSURE_TIME:
							exif.setExposureTime(tag.getDescription());
							break;
						case TAG_FNUMBER:
							exif.setfNumber(tag.getDescription());
							break;
						case TAG_ISO_EQUIVALENT:
							exif.setIsoSpeedRatings(tag.getDescription());
							break;
						case TAG_EXIF_VERSION:
							exif.setExifVersion(tag.getDescription());
							break;
						case TAG_DATETIME_ORIGINAL:
							exif.setDateTimeOriginal(sdf.parse(tag.getDescription()));
							break;
						case TAG_DATETIME:
							exif.setDateTime(sdf.parse(tag.getDescription()));
							break;
						case TAG_DATETIME_DIGITIZED:
							exif.setDateTimeDigitized(sdf.parse(tag.getDescription()));
							break;
						case TAG_SHUTTER_SPEED:
							exif.setShutterSpeedValue(tag.getDescription());
							break;
						case TAG_APERTURE:
							exif.setApertureValue(tag.getDescription());
							break;
						case TAG_METERING_MODE:
							exif.setMeteringMode(tag.getDescription());
							break;
						case TAG_FLASH:
							exif.setFlash(tag.getDescription());
							break;
						case TAG_FOCAL_LENGTH:
							exif.setFocalLength(tag.getDescription());
							break;
						case 0x9217:
						case TAG_SENSING_METHOD:
							exif.setSensingMethod(tag.getDescription());
							break;
						case TAG_COLOR_SPACE:
							exif.setColorSpace(tag.getDescription());
							break;
						case TAG_EXIF_IMAGE_WIDTH:
							exif.setExifImageWidth(tag.getDescription());
							break;
						case TAG_EXIF_IMAGE_HEIGHT:
							exif.setExifImageHeight(tag.getDescription());
							break;
						case TAG_EXPOSURE_MODE:
							exif.setExposureMode(tag.getDescription());
							break;
						case TAG_WHITE_BALANCE_MODE:
							exif.setWhiteBalanceMode(tag.getDescription());
							break;
						case TAG_DIGITAL_ZOOM_RATIO:
							exif.setDigitalZoomRatio(tag.getDescription());
							break;
						case TAG_SCENE_CAPTURE_TYPE:
							exif.setSceneCaptureType(tag.getDescription());
							break;
						case TAG_GAIN_CONTROL:
							exif.setGainControl(tag.getDescription());
							break;
						case TAG_CONTRAST:
						case 0xfe54:
							exif.setContrast(tag.getDescription());
							break;
						case TAG_SATURATION:
						case 0xfe55:
							exif.setSaturation(tag.getDescription());
							break;
						case TAG_SHARPNESS:
						case 0xfe56:
							exif.setSharpness(tag.getDescription());
							break;
						case TAG_LENS_MAKE:
							exif.setLensMake(tag.getDescription());
							break;
						case TAG_LENS_MODEL:
							exif.setLensModel(tag.getDescription());
							break;
						case TAG_WHITE_BALANCE:
						case 0xfe4e:
							exif.setWhiteBalance(tag.getDescription());
							break;
						case 0xfe51:
							exif.setExposure(tag.getDescription());
							break;
						case TAG_EXPOSURE_BIAS:
							exif.setExposureBiasValue(tag.getDescription());
							break;
						case TAG_EXPOSURE_PROGRAM:
							exif.setExposureProgram(tag.getDescription());
							break;
						case TAG_FILE_SOURCE:
							exif.setFileSource(tag.getDescription());
							break;
						case TAG_SCENE_TYPE:
							exif.setSceneType(tag.getDescription());
							break;
						case TAG_USER_COMMENT:
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
