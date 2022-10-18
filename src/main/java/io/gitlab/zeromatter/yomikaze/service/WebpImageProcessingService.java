package io.gitlab.zeromatter.yomikaze.service;

import com.luciad.imageio.webp.WebPWriteParam;
import io.gitlab.zeromatter.yomikaze.snowflake.SnowflakeFactory;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.NoSuchElementException;

@Service
public class WebpImageProcessingService {
    private final ImageWriter writer;
    private final ImageWriteParam writeParam;

    private SnowflakeFactory tempIdFactory;

    private boolean inMemory = false;

    public WebpImageProcessingService() {
        try {
            writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
            writeParam = new WebPWriteParam(writer.getLocale());
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSLESS_COMPRESSION]);
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("WebP support is not available");
        }
    }

    public InputStream convertToWebp(InputStream inputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null) {
            throw new UnsupportedEncodingException("Unsupported image format");
        }
        return convertToWebp(image);
    }

    public InputStream convertToWebp(BufferedImage image) throws IOException {
        if (inMemory) {
            return convertToWebpInMemory(image);
        } else {
            return convertToWebpOnDisk(image);
        }
    }

    private InputStream convertToWebpInMemory(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writer.setOutput(ImageIO.createImageOutputStream(outputStream));
        writer.write(null, new IIOImage(image, null, null), writeParam);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private InputStream convertToWebpOnDisk(BufferedImage image) throws IOException {
        File temp = File.createTempFile(nextName(), ".webp");
        writer.setOutput(ImageIO.createImageOutputStream(temp));
        writer.write(null, new IIOImage(image, null, null), writeParam);
        return Files.newInputStream(temp.toPath());
    }

    /**
     * @param inMemory if true, images processed by this service will be stored in memory,
     *                 otherwise they will be stored as temporary files.
     *                 Default is <code>false</code>.
     */
    public void setInMemory(boolean inMemory) {
        this.inMemory = inMemory;
    }

    private String nextName() {
        if (tempIdFactory == null) {
            tempIdFactory = SnowflakeFactory.randomFactory();
        }
        return String.valueOf(tempIdFactory.nextId());
    }
}
