package jhi.germinate.server.resource;

import jakarta.servlet.http.*;
import jakarta.ws.rs.core.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;

public class ContextResource
{
	@Context
	protected SecurityContext     securityContext;
	@Context
	protected HttpServletRequest  req;

	protected File toFileResult (File file, String type, HttpServletResponse response) {
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
		response.setContentType(type);
		return file;
	}

	protected File toFileResult (File file, String type, String filename, HttpServletResponse response) {
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		response.setContentType(type);
		return file;
	}

	protected StreamingOutput toDiretoryStreamingResult (File result, File folder, String type, HttpServletResponse response) {
		response.setHeader("Content-Disposition", "attachment; filename=\"" + result.getName() + "\"");
		response.setHeader("Content-Length", String.valueOf(result.length()));
		response.setContentType(type);

		java.nio.file.Path filePath = result.toPath();
		return output -> {
			try {
				Files.copy(filePath, output);
			} finally {
				Files.deleteIfExists(filePath);
				FileUtils.deleteDirectory(folder);
			}
		};
	}

	protected StreamingOutput toStreamingResult (File result, String type, HttpServletResponse response) {
		response.setHeader("Content-Disposition", "attachment; filename=\"" + result.getName() + "\"");
		response.setHeader("Content-Length", String.valueOf(result.length()));
		response.setContentType(type);

		java.nio.file.Path filePath = result.toPath();
		return output -> {
			try {
				Files.copy(filePath, output);
			} finally {
				Files.deleteIfExists(filePath);
			}
		};
	}
}
