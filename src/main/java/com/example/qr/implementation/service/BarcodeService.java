package com.example.qr.implementation.service;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;

@Service
public class BarcodeService {

    /**
     * Декодирует штрих-код из предоставленного файла изображения.
     * Включает предварительную обработку изображения для повышения точности.
     *
     * @param imageFile Файл изображения.
     * @return Расшифрованный текст из штрих-кода.
     * @throws IOException Если возникает ошибка при чтении файла.
     * @throws NotFoundException Если штрих-код не найден на изображении.
     */
    public String decodeBarcode(MultipartFile imageFile) throws IOException, NotFoundException {
        BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
        if (bufferedImage == null) {
            throw new IOException("Не удалось прочитать изображение. Возможно, формат не поддерживается.");
        }

        // 1. Фильтрация и повышение четкости изображения
        BufferedImage sharpenedImage = sharpenImage(bufferedImage);

        // Автоматическое определение масштаба не требуется, т.к. ZXing
        // самостоятельно находит штрих-коды разного размера.

        // 2. Распознавание и расшифровка кода с помощью ZXing
        LuminanceSource source = new BufferedImageLuminanceSource(sharpenedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        // MultiFormatReader пытается декодировать все известные ему форматы
        Result result = new MultiFormatReader().decode(bitmap);

        return result.getText();
    }

    /**
     * Применяет фильтр повышения резкости (sharpen) к изображению.
     * Это может помочь декодеру распознать нечеткие или смазанные штрих-коды.
     *
     * @param image Входное изображение.
     * @return Изображение с повышенной четкостью.
     */
    private BufferedImage sharpenImage(BufferedImage image) {
        // Ядро свертки для повышения резкости
        float[] sharpenMatrix = {
            0, -1,  0,
            -1,  5, -1,
            0, -1,  0
        };
        Kernel kernel = new Kernel(3, 3, sharpenMatrix);

        // Операция свертки, которая применяет ядро к изображению
        ConvolveOp convolveOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        return convolveOp.filter(image, null);
    }
}