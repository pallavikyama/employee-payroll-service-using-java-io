package com.blz.employeepayrolljavaio;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import org.junit.Test;

public class NIOFileAPITest {
	private static String HOME = System.getProperty("user.home");
	private static String PLAY_WITH_NIO = "\\Desktop\\Java BridgeLabz Demo\\TempPlayGround";

	@Test
	public void givenPathWhenCheckedThenConfirm() throws IOException {
		// CHECK FILE EXISTS
		Path homePath = Paths.get(HOME);
		assertTrue(Files.exists(homePath));

		// DELETE FILE AND CHECK FILE NOT EXISTS
		Path playPath = Paths.get(HOME + PLAY_WITH_NIO);
		if (Files.exists(playPath))
			FileUtils.deleteFiles(playPath.toFile());
		assertTrue(Files.notExists(playPath));

		// CREATE DIRECTORY
		Files.createDirectory(playPath);
		assertTrue(Files.exists(playPath));

		// CREATE FILE
		IntStream.range(1, 10).forEach(cntr -> {
			Path tempFile = Paths.get(playPath + "/temp" + cntr);
			assertTrue(Files.notExists(tempFile));
			try {
				Files.createFile(tempFile);
			} catch (IOException e) {
			}
			assertTrue(Files.exists(tempFile));
		});

		// LIST FILES, DIRECTORIES AS WELL AS FILES WITH EXTENSION
		Files.list(playPath).filter(Files::isRegularFile).forEach(System.out::println);
		Files.newDirectoryStream(playPath).forEach(System.out::println);
		Files.newDirectoryStream(playPath, path -> path.toFile().isFile() && path.toString().startsWith("temp"))
				.forEach(System.out::println);
	}
}